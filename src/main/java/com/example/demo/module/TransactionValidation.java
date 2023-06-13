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
public class TransactionValidation {
    @Value(value = "${org.app.properties.sp.endpoint}")
    private String SP_URL;

    private final ServiceLogger serviceLogger;
    private final HttpProcessor httpProcessor;

    @Autowired
    public TransactionValidation(ServiceLogger serviceLogger,
                                 HttpProcessor httpProcessor) {
        this.serviceLogger = serviceLogger;
        this.httpProcessor = httpProcessor;
    }

    public JSONObject pickAndProcess(JSONObject jsnObj) {
        JSONObject response = new JSONObject();
        try {
            JSONObject jsonRequest = new JSONObject();
            jsonRequest.put("TransactionType", jsnObj.getString("TransactionType"));
            jsonRequest.put("FromAccount", jsnObj.getString("Account"));
            jsonRequest.put("ToAccount", jsnObj.getString("Account"));
            jsonRequest.put("TransactionAmount", jsnObj.getString("Amount"));

            serviceLogger.log(8, jsonRequest.toString());

            RequestBuilder payBuilder = new RequestBuilder("POST");
            payBuilder.addHeader("Content-Type", "application/json")
                    .setBody(jsonRequest.toString())
                    .setUrl(SP_URL)
                    .build();
            JSONObject payResponse = httpProcessor.processProperRequest(payBuilder);
            log.log(Level.INFO, "POST TO SAHAY RESPONSE : " + payResponse.toString());
            serviceLogger.log(9, payResponse.toString());
            return payResponse;
        } catch (Exception ex) {
            response.put("Status", "101")
                    .put("Message", ex.getMessage());
            log.log(Level.WARNING, ex.getMessage());
            serviceLogger.log(6, ex.getMessage());
        }
        return response;
    }
}
