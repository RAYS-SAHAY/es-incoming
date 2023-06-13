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
public class QueryRaysCore {

    @Value(value = "${org.app.properties.rays.endpoint}")
    private String CORE_URL;
    private final ServiceLogger serviceLogger;
    private final HttpProcessor httpProcessor;

    @Autowired
    public QueryRaysCore(ServiceLogger serviceLogger, HttpProcessor httpProcessor) {
        this.serviceLogger = serviceLogger;
        this.httpProcessor = httpProcessor;
    }


    public JSONObject pickAndProcess(String account) {
        JSONObject response = new JSONObject();
        try {
            JSONObject jsonRequest = new JSONObject();
            jsonRequest.put("username", "channel");
            jsonRequest.put("password", "$_@C0NNEKT");
            jsonRequest.put("messageType", "1200");
            jsonRequest.put("serviceCode", "BC30003");
            jsonRequest.put("transactionType", "BNKQA");
            jsonRequest.put("bankCode", "231426");
            jsonRequest.put("accountNumber", account);

            serviceLogger.log(3, jsonRequest.toString());

            RequestBuilder payBuilder = new RequestBuilder("POST");
            payBuilder.addHeader("Content-Type", "application/json")
                    .setBody(jsonRequest.toString())
                    .setUrl(CORE_URL)
                    .build();
            JSONObject payResponse = httpProcessor.processProperRequest(payBuilder);

            serviceLogger.log(5, payResponse.toString());

            if (payResponse.has("connection")) {
                payResponse.put("response", "999")
                        .put("responseDescription", "Account not Valid");
            }
            log.log(Level.INFO, "A QUERY RAYS CORE RESPONSE : " + payResponse.toString());
            return payResponse;
        } catch (Exception ex) {
            response.put("response", "999")
                    .put("responseDescription", ex.getMessage());
            log.log(Level.WARNING, ex.getMessage());
            serviceLogger.log(6, ex.getMessage());
        }
        return response;
    }
}
