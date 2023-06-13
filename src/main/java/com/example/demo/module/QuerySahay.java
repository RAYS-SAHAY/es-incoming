package com.example.demo.module;

import com.example.demo.service.ProcessingService;
import com.example.demo.service.ServiceLogger;
import lombok.extern.java.Log;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.logging.Level;

@Log
@Service
@SuppressWarnings("Duplicates")
public class QuerySahay {
    private final ServiceLogger serviceLogger;
    private final ProcessingService processingService;

    @Autowired
    public QuerySahay(ServiceLogger serviceLogger, ProcessingService processingService) {
        this.serviceLogger = serviceLogger;
        this.processingService = processingService;
    }

    public JSONObject pickAndProcess(String account) {
        JSONObject response = new JSONObject();
        try {
            JSONObject load = new JSONObject();
            load.put("msisdn", account);
            load.put("transactionType", "CUD");

            serviceLogger.log(2, load.toString());
            JSONObject cudResponse = processingService.pickAndProcess(load);
            log.log(Level.INFO, "A QUERY SAHAY RESPONSE : " + cudResponse.toString());
            serviceLogger.log(4, cudResponse.toString());
            JSONObject detail = cudResponse.getJSONObject("detail");
            //check validation if the customer exists
            if (detail.has("LinkedAccounts")) {
                response.put("customerName", detail.getString("FirstName") + " " + detail.getString("LastName") + " " + detail.getString("GrandFatherName"));
                response.put("response", "000");
                response.put("responseDescription", "The Customer exists");
            } else {
                response.put("response", "999");
                response.put("responseDescription", "The Phone number is not registered with Sahay");
            }
            serviceLogger.log(4, cudResponse.toString());
        } catch (Exception ex) {
            serviceLogger.log(6, ex.getMessage());
            log.log(Level.WARNING, "CUSTOMER CLASS : " + ex.getMessage());
            response.put("response", "101");
            response.put("responseDescription", "Failed while processing the request");
        }
        return response;
    }

}
