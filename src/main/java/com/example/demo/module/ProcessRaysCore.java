package com.example.demo.module;

import com.example.demo.service.HttpProcessor;
import com.example.demo.service.ServiceLogger;
import lombok.extern.java.Log;
import org.asynchttpclient.RequestBuilder;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.logging.Level;

@Log
@Service
@SuppressWarnings("Duplicates")
public class ProcessRaysCore {
    @Value(value = "${org.app.properties.rays.endpoint}")
    private String CORE_URL;

    private final ServiceLogger serviceLogger;
    private final HttpProcessor httpProcessor;

    @Autowired
    public ProcessRaysCore(ServiceLogger serviceLogger,
                           HttpProcessor httpProcessor) {
        this.serviceLogger = serviceLogger;
        this.httpProcessor = httpProcessor;
    }

    public JSONObject pickAndProcess(JSONObject jsnObj) {
        JSONObject response = new JSONObject();
        response.put("response", "999")
                .put("responseDescription", "Account is not valid");
        try {
            JSONObject jsonRequest = new JSONObject();
            jsonRequest.put("username", "channel");
            jsonRequest.put("password", "$_@C0NNEKT");
            jsonRequest.put("messageType", "1200");
            jsonRequest.put("TransactionReqType", "1200");
            jsonRequest.put("serviceCode", "BC40005");
            jsonRequest.put("transactionType", "DTB");
            jsonRequest.put("msisdn", "0");
            jsonRequest.put("bankCode", jsnObj.getString("InsId"));
            jsonRequest.put("TransactionId", jsnObj.getString("TransRef"));
            jsonRequest.put("fromAccount", jsnObj.getString("AccTransRef"));
            jsonRequest.put("creditAccount", jsnObj.getString("Account"));
            jsonRequest.put("amount", jsnObj.getString("Amount"));
            jsonRequest.put("timestamp", "20200101120000");
            jsonRequest.put("channel", "ESWITCH");
            log.log(Level.INFO, "TO CORE REQUEST : " + jsonRequest.toString());
            serviceLogger.log(3, jsonRequest.toString());
            RequestBuilder payBuilder = new RequestBuilder("POST");
            payBuilder.addHeader("Content-Type", "application/json")
                    .setBody(jsonRequest.toString())
                    .setUrl(CORE_URL)
                    .build();
            response = httpProcessor.processProperRequest(payBuilder);

            log.log(Level.INFO, "POST TO RAYS CORE RESPONSE : " + response.toString());
            serviceLogger.log(5, jsonRequest.toString());
        } catch (Exception ex) {
            response.put("response", "999")
                    .put("responseDescription", ex.getMessage());
            log.log(Level.WARNING, ex.getMessage());
            serviceLogger.log(6, ex.getMessage());
        }
        return response;
    }
}
