package com.example.apachepoi.entity;


import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Entity
@Table(name = "last_fetched_customer")
@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class LastFetchedCustomerEntity {

    @Id
    @Column(name = "customer_id")
    private Long customerId;

    @Column(name = "last_processed")
    private LocalDateTime lastProcessed;
}
