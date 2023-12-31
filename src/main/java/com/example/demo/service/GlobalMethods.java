package com.example.demo.service;

import lombok.extern.java.Log;
import org.apache.commons.lang.StringUtils;
import org.asynchttpclient.RequestBuilder;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.regex.Pattern;

@Log
@Component
public class GlobalMethods {
    @Value(value = "${org.app.properties.sp.endpoint}")
    private String SP_URL;
    @Value(value = "${org.app.properties.sms.endpoint}")
    private String SMS_URL;
    @Value(value = "${org.app.properties.awash.check.endpoint}")
    private String AWASH_CHECK_URL;

    private final HttpProcessor httpProcessor;
    private final ProcessingService processingService;

    @Autowired
    public GlobalMethods(HttpProcessor httpProcessor,
                         ProcessingService processingService) {
        this.httpProcessor = httpProcessor;
        this.processingService = processingService;
    }


    public static String saveFile(String uploadDir, String fileName, String picType,
                                  MultipartFile multipartFile) throws IOException {
        String finalFileName = "";
        Path uploadPath = Paths.get(uploadDir);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        try (InputStream inputStream = multipartFile.getInputStream()) {
            finalFileName = picType + "_" + String.valueOf(System.currentTimeMillis()) + "_" + fileName;
            Path filePath = uploadPath.resolve(finalFileName);
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ioe) {
            throw new IOException("Could not save image file: " + fileName, ioe);
        }
        return finalFileName;
    }

    public String generateTrans() {
        String ref = Timestamp.from(Instant.now()).toString();
        ref = IDGenerator.getInstance("ES").getRRN();
        return ref;
    }

    public String getTimeStamp() {
        String pattern = "yyyyMMddHHmmss";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String date = simpleDateFormat.format(new Date());
        return date;
    }

    public String getBookingStatus(Integer status) {

        Map<Integer, String> map = new HashMap<Integer, String>();
        map.put(0, "bookPending");
        map.put(1, "bookedPaymentInProgress");
        map.put(2, "bookDeclinedPayment");
        map.put(3, "bookSuccessInProgress");
        map.put(4, "bookReturned");
        map.put(5, "bookCancelled");
        map.put(6, "bookRefunded");

        return map.get(status);
    }

    public boolean isValid(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
    }

    public Map<String, String> validatePhoneNumber(String number) {
        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("Status", "99");
        try {
            String mobileNumber = number.trim();
            if (!StringUtils.isNumeric(mobileNumber)) {
                responseBody.put("Status", "99");
            }

            if (mobileNumber.length() >= 9 && mobileNumber.length() <= 12) {
                Integer prefix = 2;
                switch (mobileNumber.length()) {
                    case 9:
                        prefix = Integer.valueOf(mobileNumber.substring(0, 1));
                        if (prefix == 7) {

                        }
                        responseBody.put("Status", "99");
                        break;
                    case 10:
                        String prefixNu = mobileNumber.substring(0, 2);
                        if ("07".equals(prefixNu)) {
                        }
                        responseBody.put("Status", "99");
                        break;
                    case 11://No mobile number is 11 digits long
                        responseBody.put("Status", "99");
                        break;
                    case 12:
                        prefix = Integer.valueOf(mobileNumber.substring(0, 3));
                        responseBody.put("Prefix", prefix.toString());
                        responseBody.put("Status", "00");
                        break;
                    default:
                        responseBody.put("Status", "99");
                        break;
                }
            } else {
                responseBody.put("Status", "99");
            }
            return responseBody;
        } catch (Exception ex) {

        }
        return responseBody;
    }


    public String getRemicode() {
        Random rnd = new Random();
        Integer n = Integer.valueOf(100000 + rnd.nextInt(900000));
        return n.toString();
    }

    public String checkIfExists(JSONObject requestObject) {
        String response = "FAILED";
        try {
            JSONObject load = new JSONObject();
            if (requestObject.getString("customerType").equals("CUST")) {
                load.put("msisdn", requestObject.getString("account"));
                load.put("transactionType", "CUD");
                JSONObject cudResponse = processingService.pickAndProcess(load);
                JSONObject detail = cudResponse.getJSONObject("detail");
                //check validation if the customer exists
                if (detail.has("LinkedAccounts")) {
                    response = "SUCCESS";
                }
            } else {
                load.put("transactionType", "NAM");
                if (requestObject.getString("customerType").equals("AGT")) {
                    load.put("detType", "2");
                } else {
                    load.put("detType", "3");
                }
                load.put("accountNumber", requestObject.getString("account"));
                JSONObject cudResponse = processingService.pickAndProcess(load);
                if (cudResponse.has("name") && !cudResponse.isNull("name")) {
                    response = "SUCCESS";
                }
            }
        } catch (Exception ex) {
            log.log(Level.WARNING, "CUSTOMER CLASS : " + ex.getMessage());
        }
        return response;
    }

    public String getMerchantAgentNumber(String account, String accType) {
        JSONObject load = new JSONObject();
        load.put("TransactionReqType", "CHECK-ACCOUNT");
        if (accType.equals("AGT")) {
            load.put("AccountType", "200");
        } else {
            load.put("AccountType", "300");
        }
        load.put("Account", account);
        RequestBuilder builder = new RequestBuilder("POST");
        builder.addHeader("Content-Type", "application/json")
                .setBody(load.toString())
                .setUrl(SP_URL)
                .build();
        JSONObject jsonResponse = httpProcessor.processProperRequest(builder);
        return jsonResponse.getString("CustomerMsisdn");
    }

    public void sendSMS(String message, String phone, String ref) {
        try {
            log.log(Level.INFO, "SENT MESSAGE : " + message);
            JSONObject payload = new JSONObject();
            payload.put("TransactionReqType", "SAVE-SMS");
            payload.put("TRANS_REF", ref);
            payload.put("PHONENUMBER", phone);
            payload.put("MESSAGE", message);
            RequestBuilder builder = new RequestBuilder("POST");
            builder.addHeader("Content-Type", "application/json")
                    .setBody(payload.toString())
                    .setUrl(SMS_URL)
                    .build();
            httpProcessor.processProperRequest(builder);
        } catch (Exception ex) {

        }
    }


    public String getSahayName(String phone) {
        JSONObject load = new JSONObject();
        load.put("msisdn", phone);
        load.put("transactionType", "CUD");
        JSONObject cudResponse = processingService.pickAndProcess(load);
        JSONObject detail = cudResponse.getJSONObject("detail");
        String customerName = "Customer";
        //check validation if the customer exists
        if (detail.has("LinkedAccounts")) {
            customerName = detail.getString("FirstName") + " " + detail.getString("LastName") + " " + detail.getString("GrandFatherName");
        }
        return customerName;
    }

    public boolean checkAwashService() {
        boolean available = false;
        try {
            RequestBuilder builder = new RequestBuilder("AWASH_CHECK_URL");
            builder.setUrl(AWASH_CHECK_URL)
                    .build();
            JSONObject response = httpProcessor.jsonRequestProcessor(builder);
            if (!response.getString("StatusCode").equals("999")) {
                available = true;
            }
        } catch (Exception ex) {

        }
        return available;
    }
}
