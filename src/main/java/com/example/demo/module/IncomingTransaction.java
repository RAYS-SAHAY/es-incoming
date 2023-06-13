package com.example.demo.module;

import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@Entity
@Table(name = "IncomingTransaction")
public class IncomingTransaction {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Basic
    @Column(name = "sahayRef")
    private String sahayRef;
    @Basic
    @Column(name = "reqType")
    private String reqType;
    @Basic
    @Column(name = "refNum")
    private String refNum;
    @Basic
    @Column(name = "instId")
    private Integer instId;
    @Basic
    @Column(name = "accountType")
    private String accountType;
    @Basic
    @Column(name = "accountNumber")
    private String accountNumber;
    @Basic
    @Column(name = "accCurrency")
    private String accCurrency;
    @Basic
    @Column(name = "amount")
    private BigDecimal amount;
    @Basic
    @Column(name = "currency")
    private Integer currency;
    @Basic
    @Column(name = "sourceBin")
    private Integer sourceBin;
    @Basic
    @Column(name = "terminalId")
    private String terminalId;
    @Basic
    @Column(name = "terminalName")
    private String terminalName;
    @Basic
    @Column(name = "merchantId")
    private String merchantId;
    @Basic
    @Column(name = "mcc")
    private Integer mcc;
    @Basic
    @Column(name = "acqInstId")
    private Integer acqInstId;

    @Basic
    @Column(name = "origRefNum")
    private String origRefNum;
    @Basic
    @Column(name = "localTransactionDateTime")
    private String localTransactionDateTime;
    @Basic
    @Column(name = "sttlDate")
    private String sttlDate;
    @Basic
    @Column(name = "request_payload")
    private String requestPayload;
    @Basic
    @Column(name = "request_date")
    private Timestamp requestDate;
    @Basic
    @Column(name = "response_payload")
    private String responsePayload;
    @Basic
    @Column(name = "response_status")
    private String responseStatus;
    @Basic
    @Column(name = "response_message")
    private String responseMessage;
    @Basic
    @Column(name = "response_date")
    private Timestamp responseDate;

}
