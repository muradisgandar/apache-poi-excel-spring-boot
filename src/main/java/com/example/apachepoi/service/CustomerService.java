package com.example.apachepoi.service;

import com.example.apachepoi.repo.CustomerRepo;
import com.example.apachepoi.util.FileUtil;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {

    private final CustomerRepo customerRepo;
    private final FileUtil fileUtil;

    public CustomerService(CustomerRepo customerRepo, FileUtil fileUtil) {
        this.customerRepo = customerRepo;
        this.fileUtil = fileUtil;
    }

    public void getEncodedCustomerExcelFile() {
        String encodedFile = fileUtil.encodeExcelFileToBase64(customerRepo.findAll());
        fileUtil.decodeBase64ToExcelFile(encodedFile);
    }
}
