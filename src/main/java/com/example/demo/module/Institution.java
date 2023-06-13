package com.example.demo.module;

import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;

@Data
@Entity
@Table(name = "Institution")
public class Institution {
    @Id@Column(name = "id")
    private Integer id;
    @Basic@Column(name = "InstId")
    private Integer instId;
    @Basic@Column(name = "InstName")
    private String instName;
    @Basic@Column(name = "Status")
    private Integer status;
    @Basic@Column(name = "CreatedDate")
    private Timestamp createdDate;

}
