package com.example.apachepoi.service;

import static net.logstash.logback.argument.StructuredArguments.kv;

import com.example.apachepoi.entity.CustomerEntity;
import com.example.apachepoi.util.FileUtil;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class FileService {

    public void getEncodedCustomerExcelFile(List<CustomerEntity> customers) {
        FileUtil fileUtil = new FileUtil();
        log.info("getEncodedCustomerExcelFile started");
        String encodedFile = fileUtil.encodeExcelFileToBase64(customers);
        log.info("getEncodedCustomerExcelFile completed successfully, {}", kv("encodedFile", encodedFile));
        fileUtil.decodeBase64ToExcelFile(encodedFile);
    }
}
