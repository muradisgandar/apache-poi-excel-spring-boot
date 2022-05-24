package com.example.apachepoi.repo;

import com.example.apachepoi.entity.CustomerEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepo extends JpaRepository<CustomerEntity, Integer> {

    @Query("SELECT C FROM CustomerEntity C WHERE C.id > ?1")
    List<CustomerEntity> getAllCustomers(Long id);
}
