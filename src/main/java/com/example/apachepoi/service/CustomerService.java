package com.example.apachepoi.service;

import static net.logstash.logback.argument.StructuredArguments.kv;

import com.example.apachepoi.entity.CustomerEntity;
import com.example.apachepoi.entity.LastFetchedCustomerEntity;
import com.example.apachepoi.repo.CustomerRepo;
import com.example.apachepoi.repo.LastFetchedCustomerRepo;
import com.example.apachepoi.util.FileUtil;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
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
    @SchedulerLock(name = "TaskScheduler_processNewCustomer",
            lockAtMostFor = "15M", lockAtLeastFor = "5M")
    public void processNewCustomer() {
        log.info("processNewCustomer job started");
        LastFetchedCustomerEntity lastFetchedCustomer = lastFetchedCustomerRepo.getLastFetchedCustomer();
        if (Objects.isNull(lastFetchedCustomer)) {
            log.info("There is no lastFetchedCustomer for now");
            return;
        }

        List<CustomerEntity> allCustomers = customerRepo.getAllCustomers(lastFetchedCustomer.getCustomerId());
        if(allCustomers.isEmpty()) {
            log.info("There are not new customers for processing");
            return;
        }
        fileService.getEncodedCustomerExcelFile(allCustomers);

        long lastId = allCustomers.stream()
                .mapToLong(CustomerEntity::getId)
                .max().orElseThrow(NoSuchElementException::new);

        LastFetchedCustomerEntity newLastCustomer = new LastFetchedCustomerEntity();
        newLastCustomer.setCustomerId(lastId);
        newLastCustomer.setLastProcessed(LocalDateTime.now());
        lastFetchedCustomerRepo.save(newLastCustomer);

        log.info("processNewCustomer job executed successfully, {} ", kv("lastCustomerId", lastId));
    }
}
