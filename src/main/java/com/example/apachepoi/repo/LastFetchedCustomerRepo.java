package com.example.apachepoi.repo;

import com.example.apachepoi.entity.LastFetchedCustomerEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface LastFetchedCustomerRepo extends JpaRepository<LastFetchedCustomerEntity, Long> {

    @Query(value = "SELECT * FROM last_fetched_customer ORDER BY customer_id DESC LIMIT 1", nativeQuery = true)
    LastFetchedCustomerEntity getLastFetchedCustomer();
}
