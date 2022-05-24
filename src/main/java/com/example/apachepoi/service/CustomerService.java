package com.example.apachepoi.service;

import com.example.apachepoi.entity.CustomerEntity;
import com.example.apachepoi.entity.LastFetchedCustomerEntity;
import com.example.apachepoi.repo.CustomerRepo;
import com.example.apachepoi.repo.LastFetchedCustomerRepo;
import com.example.apachepoi.util.FileUtil;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CustomerService {

    private final CustomerRepo customerRepo;
    private final LastFetchedCustomerRepo lastFetchedCustomerRepo;
    private final FileService fileService;

    public CustomerService(CustomerRepo customerRepo, LastFetchedCustomerRepo lastFetchedCustomerRepo, FileService fileService) {
        this.customerRepo = customerRepo;
        this.lastFetchedCustomerRepo = lastFetchedCustomerRepo;
        this.fileService = fileService;
    }

    @Scheduled(cron = "0 0/3 * * * ?")
    @SchedulerLock(name = "TaskScheduler_scheduledTask",
            lockAtMostFor = "15M", lockAtLeastFor = "5M")
    public void scheduledTask() {
        log.info("job started");
        List<CustomerEntity> allCustomers1 = customerRepo.getAllCustomers(1L);
        log.info("all customers: " + allCustomers1);
        List<LastFetchedCustomerEntity> lastFetchedCustomerList = lastFetchedCustomerRepo.findAll();
        if (lastFetchedCustomerList.isEmpty()) {
            log.info("lastFetchedCustomer  " + lastFetchedCustomerList);
            return;
        }

        LastFetchedCustomerEntity lastFetchedCustomer = lastFetchedCustomerList.get(0);
        List<CustomerEntity> allCustomers = customerRepo.getAllCustomers(lastFetchedCustomer.getCustomerId());
        fileService.getEncodedCustomerExcelFile(allCustomers);

        long lastId = allCustomers.stream()
                .mapToLong(CustomerEntity::getId)
                .max().orElseThrow(NoSuchElementException::new);
        log.info("job executed successfully: " + lastId);
        lastFetchedCustomer.setCustomerId(lastId);
        lastFetchedCustomerRepo.save(lastFetchedCustomer);

    }
}
