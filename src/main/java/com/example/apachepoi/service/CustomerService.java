package com.example.apachepoi.service;

import static net.logstash.logback.argument.StructuredArguments.kv;

import com.example.apachepoi.entity.CustomerEntity;
import com.example.apachepoi.entity.LastFetchedCustomerEntity;
import com.example.apachepoi.repo.CustomerRepo;
import com.example.apachepoi.repo.LastFetchedCustomerRepo;
import com.example.apachepoi.util.FileUtil;
import java.io.IOException;
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

    /**
     * For making cron jobs timing
     * @link https://www.freeformatter.com/cron-expression-generator-quartz.html
     */
    @Scheduled(cron = "0 0/2 * * * ?") // At second :00, every 10 minutes starting at minute :00, of every hour
    @SchedulerLock(name = "TaskScheduler_processNewCustomers",
            lockAtMostFor = "15M", lockAtLeastFor = "2M")// locks at least 5 minutes(normally), at most 15 minutes(for disaster cases)
    public void processNewCustomers() throws IOException {
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

        log.info("processNewCustomers job executed successfully, {} ", kv("lastCustomerId", lastId));
    }
}
