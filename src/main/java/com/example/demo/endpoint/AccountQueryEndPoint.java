package com.example.demo.endpoint;

import com.example.demo.module.QueryRaysCore;
import com.example.demo.module.QuerySahay;
import com.example.demo.object.A2AEnquiry;
import com.example.demo.object.A2AEnquiryReplyDTO;
import com.example.demo.object.A2AEnquiryResponse;
import com.example.demo.object.CredentialsDTO;
import com.example.demo.service.ServiceLogger;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.java.Log;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.logging.Level;


@Log
@Endpoint
@SuppressWarnings("Duplicates")
public class AccountQueryEndPoint {
    private static final String SOAP_ENV_NAMESPACE = "http://schemas.xmlsoap.org/soap/envelopes/";
    private static final String PREFERRED_PREFIX = "env";
    private static final String NAMESPACE_URI = "http://ws.webgate.bpc.ru/";

    private final QuerySahay querySahay;
    private final QueryRaysCore queryRaysCore;
    private final ServiceLogger serviceLogger;

    @Autowired
    public AccountQueryEndPoint(QuerySahay querySahay, QueryRaysCore queryRaysCore,
                                ServiceLogger serviceLogger) {
        this.querySahay = querySahay;
        this.queryRaysCore = queryRaysCore;
        this.serviceLogger = serviceLogger;
    }


    @ResponsePayload
    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "a2aEnquiry")
    public void accountQuery(@RequestPayload JAXBElement<A2AEnquiry> request, MessageContext messageContext,
                             @org.springframework.ws.soap.server.endpoint.annotation.SoapHeader("{http://ws.webgate.bpc.ru/}credentials") SoapHeaderElement auth) {
        A2AEnquiryResponse response = new A2AEnquiryResponse();
        A2AEnquiryReplyDTO dto = new A2AEnquiryReplyDTO();
        GregorianCalendar c = new GregorianCalendar();
        c.setTime(new Date());
        XMLGregorianCalendar date2 = null;
        try {
            date2 = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
        } catch (DatatypeConfigurationException e) {
            e.printStackTrace();
        }
        SaajSoapMessage soapResponse = (SaajSoapMessage) messageContext.getResponse();
        try {
            A2AEnquiry req = request.getValue();
            String reqString = "";
            ObjectMapper obRequest = new ObjectMapper();
            try {
                reqString = obRequest.writeValueAsString(req);
                serviceLogger.log(1, reqString);
                log.log(Level.INFO, "REQUEST PAYLOAD" + reqString);
            } catch (Exception ex) {
                log.log(Level.INFO, "ERROR : " + ex.getMessage());
                serviceLogger.log(6, ex.getMessage());
            }
            dto.setRefnum(req.getA2AEnquiry().getRefNum());
            if (req.getA2AEnquiry().getDestAccount().getAccountNumber().startsWith("+251")
                    || req.getA2AEnquiry().getDestAccount().getAccountNumber().startsWith("251")
                    || req.getA2AEnquiry().getDestAccount().getAccountNumber().startsWith("09")
                    || req.getA2AEnquiry().getDestAccount().getAccountNumber().startsWith("9")) {

                Integer length = req.getA2AEnquiry().getDestAccount().getAccountNumber().length();
                String phone = req.getA2AEnquiry().getDestAccount().getAccountNumber();
                switch (length) {
                    case 13:
                        phone = req.getA2AEnquiry().getDestAccount().getAccountNumber().substring(length - 12);
                        break;
                    case 12:
                        phone = req.getA2AEnquiry().getDestAccount().getAccountNumber();
                        break;
                    case 10:
                        phone = "251" + req.getA2AEnquiry().getDestAccount().getAccountNumber().substring(length - 9);
                        break;
                    case 9:
                        phone = "251" + req.getA2AEnquiry().getDestAccount().getAccountNumber();
                        break;
                }
                JSONObject acQuery = querySahay.pickAndProcess(phone);
                if (acQuery.getString("response").equals("000")) {
                    dto.setStatus("1");
                    dto.setBeneficiaryName(acQuery.getString("customerName"));
                    dto.setErrorCode("000");
                    dto.setErrorDescription("Successful");
                } else {
                    dto.setStatus("0");
                    dto.setErrorCode("WSH914");
                    dto.setErrorDescription("Invalid Account");
                }
            } else if (req.getA2AEnquiry().getDestAccount().getAccountNumber().length() == 12) {
                JSONObject acQuery = queryRaysCore.pickAndProcess(req.getA2AEnquiry().getDestAccount().getAccountNumber());
                if (acQuery.getString("response").equals("000")) {
                    dto.setStatus("1");
                    dto.setBeneficiaryName(acQuery.getString("accountName"));
                    dto.setErrorCode("000");
                    dto.setErrorDescription("Successful");
                } else {
                    dto.setStatus("0");
                    dto.setErrorCode("WSH914");
                    dto.setErrorDescription(acQuery.getString("responseDescription"));
                }
            } else {
                dto.setStatus("0");
                dto.setErrorCode("WSH914");
                dto.setErrorDescription("Invalid Account");
            }
            dto.setLocalTransactionDateTime(date2);
            response.setReply(dto);
        } catch (Exception ex) {
            dto.setRefnum("");
            dto.setStatus("0");
            dto.setErrorCode("WSH802");
            dto.setErrorDescription("System not available");
            dto.setLocalTransactionDateTime(date2);
            response.setReply(dto);
            log.log(Level.INFO, "AC QUERY  ERROR : " + ex.getMessage());
            serviceLogger.log(6, ex.getMessage());
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
            serviceLogger.log(6, e.getMessage());
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
            Marshaller marshaller = JAXBContext.newInstance(A2AEnquiryResponse.class).createMarshaller();
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
            log.log(Level.WARNING, e.getMessage());
        }
        return authentication;
    }
}
