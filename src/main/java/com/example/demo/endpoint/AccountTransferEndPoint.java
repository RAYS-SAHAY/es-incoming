package com.example.demo.endpoint;

import com.example.demo.module.*;
import com.example.demo.object.*;
import com.example.demo.service.GlobalMethods;
import com.example.demo.service.ServiceLogger;
import com.example.demo.service.SmsLogging;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.java.Log;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import org.springframework.ws.soap.SoapHeader;
import org.springframework.ws.soap.SoapHeaderElement;
import org.springframework.ws.soap.saaj.SaajSoapMessage;
import org.w3c.dom.Document;

import javax.xml.bind.*;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;


@Log
@Endpoint
@SuppressWarnings("Duplicates")
public class AccountTransferEndPoint {
    private static final String SOAP_ENV_NAMESPACE = "http://schemas.xmlsoap.org/soap/envelopes/";
    private static final String PREFERRED_PREFIX = "env";
    private static final String NAMESPACE_URI = "http://ws.webgate.bpc.ru/";

    @Value(value = "${org.app.properties.ethio.sms.success.incoming}")
    private String TEMPLATE_ID;

    @Value(value = "${org.app.properties.ethio.sms.failure.balance.incoming}")
    private String FAIL_BAL_TEMP;

    private final QuerySahay querySahay;

    private final SmsLogging smsLogging;

    private final GlobalMethods globalMethods;
    private final QueryRaysCore queryRaysCore;
    private final ProcessSahay processSahay;
    private final ServiceLogger serviceLogger;
    private final ProcessRaysCore processRaysCore;
    private final TransactionValidation transactionValidation;
    private final InstitutionRepository institutionRepository;
    private final IncomingTransactionRepository incomingTransactionRepository;

    @Autowired
    public AccountTransferEndPoint(QuerySahay querySahay, SmsLogging smsLogging,
                                   GlobalMethods globalMethods,
                                   QueryRaysCore queryRaysCore, ProcessSahay processSahay,
                                   ServiceLogger serviceLogger, ProcessRaysCore processRaysCore,
                                   TransactionValidation transactionValidation,
                                   InstitutionRepository institutionRepository,
                                   IncomingTransactionRepository incomingTransactionRepository) {
        this.querySahay = querySahay;
        this.smsLogging = smsLogging;
        this.globalMethods = globalMethods;
        this.queryRaysCore = queryRaysCore;
        this.processSahay = processSahay;
        this.serviceLogger = serviceLogger;
        this.processRaysCore = processRaysCore;
        this.transactionValidation = transactionValidation;
        this.institutionRepository = institutionRepository;
        this.incomingTransactionRepository = incomingTransactionRepository;
    }

    @ResponsePayload
    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "a2aCredit")
    public void accountTransfer(@RequestPayload JAXBElement<A2ACredit> request, MessageContext messageContext,
                                @org.springframework.ws.soap.server.endpoint.annotation.SoapHeader("{http://ws.webgate.bpc.ru/}credentials") SoapHeaderElement auth) {

        A2ACreditResponse response = new A2ACreditResponse();
        A2ACreditReplyDTO dto = new A2ACreditReplyDTO();
        GregorianCalendar c = new GregorianCalendar();
        c.setTime(new Date());
        XMLGregorianCalendar formatedDate = null;
        try {
            formatedDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
        } catch (DatatypeConfigurationException e) {
            log.log(Level.WARNING, e.getMessage());
            serviceLogger.log(6, e.getMessage());
        }
        SaajSoapMessage soapResponse = (SaajSoapMessage) messageContext.getResponse();
        try {
            A2ACredit req = request.getValue();
            String stagingRef = globalMethods.generateTrans();
            String reqString = "";
            ObjectMapper obRequest = new ObjectMapper();
            try {
                reqString = obRequest.writeValueAsString(req);
                log.log(Level.INFO, "CREDIT REQ: " + reqString);
                serviceLogger.log(1, reqString);
            } catch (Exception ex) {
                log.log(Level.INFO, "ERROR : " + ex.getMessage());
                serviceLogger.log(6, ex.getMessage());
            }
            dto.setRefnum(req.getA2ACredit().getRefNum());
            IncomingTransaction trans = new IncomingTransaction();
            trans.setSahayRef(stagingRef);
            trans.setReqType("A2ACredit");
            trans.setRefNum(req.getA2ACredit().getRefNum());
            trans.setInstId(Integer.valueOf(req.getA2ACredit().getDestAccount().getInstId()));
            String account = req.getA2ACredit().getDestAccount().getAccountNumber();
            if (req.getA2ACredit().getDestAccount().getAccountNumber().startsWith("+251")
                    || req.getA2ACredit().getDestAccount().getAccountNumber().startsWith("251")
                    || req.getA2ACredit().getDestAccount().getAccountNumber().startsWith("09")
                    || req.getA2ACredit().getDestAccount().getAccountNumber().startsWith("9")) {

                Integer length = req.getA2ACredit().getDestAccount().getAccountNumber().length();
                String phone = req.getA2ACredit().getDestAccount().getAccountNumber();
                switch (length) {
                    case 13:
                        phone = req.getA2ACredit().getDestAccount().getAccountNumber().substring(length - 12);
                        break;
                    case 12:
                        phone = req.getA2ACredit().getDestAccount().getAccountNumber();
                        break;
                    case 10:
                        phone = "251" + req.getA2ACredit().getDestAccount().getAccountNumber().substring(length - 9);
                        break;
                    case 9:
                        phone = "251" + req.getA2ACredit().getDestAccount().getAccountNumber();
                        break;
                }
                trans.setAccountType("SAHAY");
                account = phone;
            } else if (req.getA2ACredit().getDestAccount().getAccountNumber().length() == 12) {
                trans.setAccountType("RAYS-CB");
                //trans.setAccountNumber(req.getA2ACredit().getDestAccount().getAccountNumber());
            } else {
                trans.setAccountType("INVALID");
                //trans.setAccountNumber(req.getA2ACredit().getDestAccount().getAccountNumber());
            }
            trans.setAccountNumber(account);
            trans.setAccCurrency("ETB");
            trans.setAmount(new BigDecimal(Double.valueOf(req.getA2ACredit().getAmount().getAmount()) / 100));
            trans.setCurrency(req.getA2ACredit().getAmount().getCurrency());
            trans.setSourceBin(Integer.valueOf(req.getA2ACredit().getSourceBin()));
            trans.setTerminalId(req.getA2ACredit().getAcqTerminalDetails().getTerminalId());
            trans.setTerminalName(req.getA2ACredit().getAcqTerminalDetails().getTerminalName());
            trans.setMerchantId(req.getA2ACredit().getAcqTerminalDetails().getMerchantId());
            trans.setMcc(req.getA2ACredit().getAcqTerminalDetails().getMcc());
            trans.setAcqInstId(Integer.valueOf(req.getA2ACredit().getAcqTerminalDetails().getAcqInstId()));
            trans.setOrigRefNum(req.getA2ACredit().getOrigRefNum());
            trans.setLocalTransactionDateTime(req.getA2ACredit().getLocalTransactionDateTime().toString());
            trans.setSttlDate(req.getA2ACredit().getSttlDate().toString());
            trans.setRequestPayload(reqString);
            trans.setRequestDate(Timestamp.from(Instant.now()));
            incomingTransactionRepository.save(trans);
            JSONObject paymentReq = new JSONObject();
            paymentReq.put("TransRef", stagingRef);
            paymentReq.put("Amount", trans.getAmount().toString());
            AtomicReference<String> insName = new AtomicReference<>("Ethio Switch");
            institutionRepository.findInstitutionByInstId(trans.getAcqInstId())
                    .ifPresent(ins -> {
                        insName.set(ins.getInstName());
                    });
            paymentReq.put("InsName", insName.get());
            paymentReq.put("InsId", req.getA2ACredit().getAcqTerminalDetails().getAcqInstId());
            paymentReq.put("AccTransRef", trans.getRefNum());
            paymentReq.put("AccountNumber", account);
            paymentReq.put("Account", account);

            Date transDate = toDate(req.getA2ACredit().getLocalTransactionDateTime());

            JSONObject validPay = new JSONObject();
            validPay.put("Amount", req.getA2ACredit().getAmount().getAmount());
            validPay.put("Currency", req.getA2ACredit().getAmount().getCurrency());
            validPay.put("Timestamp", new Timestamp(transDate.getTime()).toString());
            JSONObject validPayRes = validate(validPay);
            if (validPayRes.getString("Status").equals("00")) {
                if (trans.getAccountType().equals("SAHAY")) {
                    JSONObject acQuery = querySahay.pickAndProcess(account);
                    if (acQuery.getString("response").equals("000")) {
                        //Check Limit
                        JSONObject limPay = new JSONObject();
                        limPay.put("TransactionType", "FTFES");
                        limPay.put("Account", account);
                        limPay.put("Amount", paymentReq.getString("Amount"));
                        /*
                        JSONObject balLimit = transactionValidation.pickAndProcess(limPay);
                         */
                        JSONObject balLimit = new JSONObject();
                        balLimit.put("Status", "00");
                        balLimit.put("BalLimitExceeded", "0");
                        balLimit.put("BalLimitAmount", "0");
                        balLimit.put("CustomerName", "Test Customer");

                        if (balLimit.getString("Status").equals("00")) {
                            if (balLimit.getString("BalLimitExceeded").equals("0")) {
                                JSONObject payRes = processSahay.pickAndProcess(paymentReq);
                                if (payRes.getString("Status").equals("00")) {
                                    trans.setResponsePayload(payRes.toString());
                                    trans.setResponseStatus("000");
                                    trans.setResponseMessage("Success");
                                    dto.setStatus("1");
                                    dto.setErrorCode("000");
                                    dto.setErrorDescription("Successful");
                                    Date date = new Date();
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                                    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
                                    String strDate = dateFormat.format(date);
                                    String strTime = timeFormat.format(date);

                                    JSONObject jsonObj = new JSONObject();
                                    jsonObj.put("TemplateId", TEMPLATE_ID);
                                    jsonObj.put("TemplatePhone", payRes.getString("CustomerMsisdn"));
                                    jsonObj.put("name", payRes.getString("CustomerName")); // PENDING VERIFICATION
                                    jsonObj.put("amount", paymentReq.getString("Amount"));
                                    jsonObj.put("account", req.getA2ACredit().getDestAccount().getAccountNumber());
                                    jsonObj.put("date", strDate);
                                    jsonObj.put("time", strTime);
                                    jsonObj.put("bank_name", insName.get());
                                    jsonObj.put("balance", payRes.getString("CustomerAccountBalance"));
                                    jsonObj.put("fee", payRes.getString("TransactionCost"));
                                    jsonObj.put("refNo", stagingRef);
                                    String[] words = {"name", "amount", "account", "date", "time", "bank_name", "balance", "fee", "refNo"};
                                    String message = smsLogging.generateMessage(jsonObj, words);
                                    globalMethods.sendSMS(message, payRes.getString("CustomerMsisdn"), stagingRef);
                                } else {
                                    trans.setResponsePayload(payRes.getString("Status"));
                                    trans.setResponseStatus("999");
                                    trans.setResponseMessage(payRes.getString("Message"));
                                    dto.setStatus("0");
                                    dto.setErrorCode("WSH963");
                                    dto.setErrorDescription("Transaction Failed");
                                }
                            } else {
                                /*
                                Dear customer, a transaction of [receiving_amount?] from [bank?] failed due to balance limit of ETB [amount?] exceeded.
                                To transact more, visit any Rays MFI branch and open account
                                 */
                                JSONObject jsonObj = new JSONObject();
                                jsonObj.put("TemplateId", FAIL_BAL_TEMP);
                                jsonObj.put("TemplatePhone", paymentReq.getString("Account"));
                                jsonObj.put("customer_name", balLimit.getString("CustomerName")); // PENDING VERIFICATION
                                jsonObj.put("bank", paymentReq.getString("InsName"));
                                jsonObj.put("receiving_amount", paymentReq.getString("Amount"));
                                jsonObj.put("amount", balLimit.getString("BalLimitAmount"));
                                String[] words = {"customer_name", "amount"};
                                String message = smsLogging.generateMessage(jsonObj, words);
                                globalMethods.sendSMS(message, paymentReq.getString("Account"), stagingRef);

                                trans.setResponsePayload(balLimit.toString());
                                trans.setResponseStatus("999");
                                trans.setResponseMessage("Sahay Account has failed because of Balance Limit.");
                                dto.setStatus("0");
                                dto.setErrorCode("WSH963");
                                dto.setErrorDescription("Transaction Failed");
                            }
                        } else {
                            trans.setResponsePayload(balLimit.toString());
                            trans.setResponseStatus("999");
                            trans.setResponseMessage("Transaction Failed.");
                            dto.setStatus("0");
                            dto.setErrorCode("WSH963");
                            dto.setErrorDescription("Transaction Failed");
                        }
                    } else {
                        dto.setStatus("0");
                        dto.setErrorCode("WSH914");
                        dto.setErrorDescription("Invalid Account");
                        trans.setResponsePayload(acQuery.toString());
                        trans.setResponseStatus("999");
                        trans.setResponseMessage("Invalid Account");
                    }
                } else if (trans.getAccountType().equals("RAYS-CB")) {
                    JSONObject acQuery = queryRaysCore.pickAndProcess(req.getA2ACredit().getDestAccount().getAccountNumber());
                    if (acQuery.getString("response").equals("000")) {
                        JSONObject payRes = processRaysCore.pickAndProcess(paymentReq);
                        if (!payRes.getString("response").equals("000")) {
                            try {
                                Thread.sleep(2000);
                                payRes = processRaysCore.pickAndProcess(paymentReq);
                            } catch (Exception exception) {
                                log.log(Level.WARNING, "Thread Safe exception : " + exception.getMessage());
                            }
                        }
                        if (!payRes.getString("response").equals("000")) {
                            try {
                                Thread.sleep(2000);
                                payRes = processRaysCore.pickAndProcess(paymentReq);
                            } catch (Exception exception) {
                                log.log(Level.WARNING, "Thread Safe exception : " + exception.getMessage());
                            }
                        }
                        if (!payRes.getString("response").equals("000")) {
                            try {
                                Thread.sleep(2000);
                                payRes = processRaysCore.pickAndProcess(paymentReq);
                            } catch (Exception exception) {
                                log.log(Level.WARNING, "Thread Safe exception : " + exception.getMessage());
                            }
                        }

                        if (payRes.getString("response").equals("000")) {
                            trans.setSahayRef(payRes.getString("reference"));
                            trans.setResponsePayload(payRes.getString("response"));
                            trans.setResponseStatus("000");
                            trans.setResponseMessage("Success");
                            dto.setStatus("1");
                            dto.setErrorCode("000");
                            dto.setErrorDescription("Successful");
                        } else {
                            trans.setResponsePayload(payRes.getString("response"));
                            trans.setResponseStatus("999");
                            trans.setResponseMessage(payRes.getString("responseDescription"));
                            dto.setStatus("0");
                            dto.setErrorCode("WSH963");
                            dto.setErrorDescription("Transaction Failed");
                        }
                    } else {
                        trans.setResponsePayload(acQuery.toString());
                        trans.setResponseStatus("999");
                        trans.setResponseMessage("Invalid Account");
                        dto.setStatus("0");
                        dto.setErrorCode("WSH914");
                        dto.setErrorDescription("Invalid Account");
                    }
                } else {
                    trans.setResponsePayload("Invalid Account");
                    trans.setResponseStatus("999");
                    trans.setResponseMessage("Invalid Account");
                    dto.setStatus("0");
                    dto.setErrorCode("WSH914");
                    dto.setErrorDescription("Invalid Account");
                }
            } else {
                trans.setResponsePayload(validPayRes.getString("Error"));
                trans.setResponseStatus("999");
                trans.setResponseMessage(validPayRes.getString("Error"));
                dto.setStatus("0");
                dto.setErrorCode(validPayRes.getString("ErrorCode"));
                dto.setErrorDescription(validPayRes.getString("Error"));
            }
            trans.setResponseDate(Timestamp.from(Instant.now()));
            incomingTransactionRepository.save(trans);
            dto.setLocalTransactionDateTime(formatedDate);
            response.setReply(dto);
        } catch (Exception ex) {
            A2ACredit req = request.getValue();
            dto.setRefnum(req.getA2ACredit().getRefNum());
            dto.setStatus("0");
            dto.setErrorCode("WSH812");
            dto.setErrorDescription("Invalid Request Payload");
            dto.setLocalTransactionDateTime(formatedDate);
            response.setReply(dto);
            serviceLogger.log(6, ex.getMessage());
            log.log(Level.INFO, "AC CREDIT  ERROR : " + ex.getMessage());
        }

        String responseString = "";
        ObjectMapper ObjResponse = new ObjectMapper();
        try {
            responseString = ObjResponse.writeValueAsString(response);
            log.log(Level.INFO, "RESPONSE PAYLOAD: " + responseString);
            serviceLogger.log(7, responseString);
        } catch (Exception ex) {
            serviceLogger.log(6, ex.getMessage());
            log.log(Level.INFO, "ERROR : " + ex.getMessage());
        }
        try {
            CredentialsDTO token = getAuthentication(auth);
            SoapHeader soapResponseHeader = soapResponse.getSoapHeader();
            JAXBContext jaxbContext = JAXBContext.newInstance(CredentialsDTO.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            // To format XML
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            jaxbMarshaller.marshal(token, soapResponseHeader.getResult());
        } catch (JAXBException e) {
            log.log(Level.WARNING, e.getMessage());
        }
        SOAPMessage soapMessage = soapResponse.getSaajMessage();
        try {

            SOAPPart soapPart = soapMessage.getSOAPPart();
            SOAPEnvelope envelope = soapPart.getEnvelope();
            SOAPHeader header = soapMessage.getSOAPHeader();
            SOAPBody body = soapMessage.getSOAPBody();
            envelope.removeNamespaceDeclaration(envelope.getPrefix());
            envelope.addNamespaceDeclaration(PREFERRED_PREFIX, SOAP_ENV_NAMESPACE);
            envelope.setPrefix(PREFERRED_PREFIX);
            header.setPrefix(PREFERRED_PREFIX);
            body.setPrefix(PREFERRED_PREFIX);
            soapMessage.saveChanges();
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            Marshaller marshaller = JAXBContext.newInstance(A2ACreditResponse.class).createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.marshal(response, document);
            soapMessage.getSOAPBody().addDocument(document);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            soapMessage.writeTo(outputStream);
        } catch (SOAPException | JAXBException | IOException | ParserConfigurationException e) {
            log.log(Level.WARNING, e.getMessage());
            serviceLogger.log(6, e.getMessage());
        }
    }

    private CredentialsDTO getAuthentication(SoapHeaderElement header) {
        CredentialsDTO authentication = null;
        try {
            JAXBContext context = JAXBContext.newInstance(CredentialsDTO.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            authentication = (CredentialsDTO) unmarshaller.unmarshal(header.getSource());
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return authentication;
    }

    private JSONObject validate(JSONObject payload) {
        JSONObject response = new JSONObject();
        response.put("Status", "00")
                .put("Error", "None");
        /*
        try {
            Double amount = payload.getDouble("Amount") / 100;
            if (amount < 5) {
                response.put("Status", "99")
                        .put("Error", "Amount too small")
                        .put("ErrorCode", "WSH968")
                        .put("Type", "Amount");
                return response;
            }
        } catch (Exception ex) {
            response.put("Status", "99")
                    .put("Error", "Amount Invalid")
                    .put("ErrorCode", "WSH903")
                    .put("Type", "Amount");
            return response;
        }
        */

        try {
            Integer currency = payload.getInt("Currency");
            if (currency != 230) {
                response.put("Status", "99")
                        .put("Error", "Invalid currency")
                        .put("ErrorCode", "WSH841")
                        .put("Type", "Currency");
                return response;
            }
        } catch (Exception ex) {
            response.put("Status", "99")
                    .put("Error", "Invalid currency")
                    .put("ErrorCode", "WSH841")
                    .put("Type", "Currency");
            return response;
        }
        /*
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
            Date parsedDate = dateFormat.parse(payload.getString("Timestamp"));
            Timestamp timestamp = new java.sql.Timestamp(parsedDate.getTime());
            long minutes = TimeUnit.MILLISECONDS.toMinutes(new Timestamp(System.currentTimeMillis()).getTime() - timestamp.getTime());
            if (minutes > 5) {
                response.put("Status", "99")
                        .put("Error", "Invalid transaction timestamp")
                        .put("ErrorCode", "WSH812")
                        .put("Type", "Timestamp");
                return response;

            }
        } catch (Exception ex) {
            response.put("Status", "99")
                    .put("Error", "Invalid transaction timestamp")
                    .put("ErrorCode", "WSH812")
                    .put("Type", "Timestamp");
            return response;
        }
         */
        return response;
    }

    public static Date toDate(XMLGregorianCalendar calendar) {
        if (calendar == null) {
            return null;
        }
        return calendar.toGregorianCalendar().getTime();
    }
}
