package com.example.apachepoi.repo;

import com.example.apachepoi.entity.LastFetchedCustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LastFetchedCustomerRepo extends JpaRepository<LastFetchedCustomerEntity, Long> {

}
