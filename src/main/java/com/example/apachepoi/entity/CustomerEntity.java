package com.example.apachepoi.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name = "customers")
@Entity
@AllArgsConstructor
@Data
@NoArgsConstructor
public class CustomerEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "phone")
    private String phone;

    @Column(name = "customer_code")
    private String customerCode;

    @Column(name = "pin")
    private String pin;
}
