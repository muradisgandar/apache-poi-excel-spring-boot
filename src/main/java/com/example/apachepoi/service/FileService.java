package com.example.apachepoi.service;

import com.example.apachepoi.entity.CustomerEntity;
import com.example.apachepoi.util.FileUtil;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class FileService {

    private final FileUtil fileUtil;

    public FileService(FileUtil fileUtil) {
        this.fileUtil = fileUtil;
    }

    public void getEncodedCustomerExcelFile(List<CustomerEntity> customers) {
        log.info("excel generating");
        String encodedFile = fileUtil.encodeExcelFileToBase64(customers);
        log.info("encodedUrl: " + encodedFile);
        fileUtil.decodeBase64ToExcelFile(encodedFile);
    }
}
