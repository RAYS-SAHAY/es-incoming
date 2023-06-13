package com.example.demo.service;

import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class ServiceLogger {
    private static String LOGS_PATH = "";

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void log(int logLevel, String details) {
        LOGS_PATH = System.getProperty("user.dir") + File.separator + "Logs";
        String typeOfLog = "";
        switch (logLevel) {

            case 1: {
                typeOfLog = "REQUEST_FROM_ESWITCH";
                break;
            }
            case 2: {
                typeOfLog = "REQUEST_TO_SAHAY";
                break;
            }
            case 3: {
                typeOfLog = "REQUEST_TO_CBS";
                break;
            }
            case 4: {
                typeOfLog = "RESPONSE_FROM_SAHAY";
                break;
            }
            case 5: {
                typeOfLog = "RESPONSE_FROM_CBS";
                break;
            }
            case 6: {
                typeOfLog = "APPLICATION_ERROR";
                break;
            }
            case 7: {
                typeOfLog = "RESPONSE_TO_ESWITCH";
                break;
            }
            case 8: {
                typeOfLog = "REQUEST_TO_SP";
                break;
            }
            case 9: {
                typeOfLog = "RESPONSE_FROM_SP";
                break;
            }

            default: {
                typeOfLog = "Others";
            }
        }
        Date today = Calendar.getInstance().getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String LogDate = formatter.format(today);
        SimpleDateFormat LogTimeformatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String LogTime = LogTimeformatter.format(today);
        File dir = new File(LOGS_PATH + "/" + LogDate + "/" + typeOfLog);
        BufferedWriter writer = null;
        if (dir.exists()) {
            dir.setWritable(true);
        } else {
            dir.mkdirs();
            dir.setWritable(true);
        }
        try {
            SimpleDateFormat formatterLog = new SimpleDateFormat("HHmm");
            Date todayDate = new Date();
            Calendar calendars = Calendar.getInstance();
            calendars.setTime(todayDate);
            int unRoundedMinutes = calendars.get(12);
            int mod = unRoundedMinutes % 5;
            calendars.add(12, mod < 8 ? -mod : 5 - mod);
            Date roundOfTime = calendars.getTime();
            String fileName = "/" + LogDate + "-" + formatterLog.format(roundOfTime) + ".log";
            writer = new BufferedWriter(new FileWriter(dir + fileName, true));
            writer.write(LogTime + " ~ " + details);
            writer.newLine();
        } catch (IOException e) {
            Logger.getLogger(ServiceLogger.class.getName()).log(Level.SEVERE, "ERROR: Failed to load properties file.\nCause: \n", e);
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                Logger.getLogger(ServiceLogger.class.getName()).log(Level.SEVERE, "ERROR: Failed to load properties file.\nCause: \n", e);
            }
        }
    }
}