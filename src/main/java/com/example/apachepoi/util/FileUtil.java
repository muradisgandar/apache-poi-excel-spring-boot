package com.example.apachepoi.util;

import static net.logstash.logback.argument.StructuredArguments.kv;

import com.example.apachepoi.entity.CustomerEntity;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFName;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

@Slf4j
public class FileUtil {

    private final XSSFWorkbook workbook;
    private XSSFSheet sheet;

    public FileUtil() {
        workbook = new XSSFWorkbook();
    }


    private void writeHeaderLine() {
        sheet = workbook.createSheet("Customers");

        Row row = sheet.createRow(0);

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(16);
        style.setFont(font);

        createCell(row, 0, "Phone", style);
        createCell(row, 1, "Customer Code", style);
        createCell(row, 2, "Pin", style);

    }

    private void createCell(Row row, int columnCount, Object value, CellStyle style) {
        sheet.autoSizeColumn(columnCount);
        Cell cell = row.createCell(columnCount);
        if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else if (value instanceof LocalDateTime) {
            cell.setCellValue((LocalDateTime) value);
        }
        else {
            cell.setCellValue((String) value);
        }
        cell.setCellStyle(style);
    }

    private void writeDataLines(List<CustomerEntity> customerEntities) {
        AtomicInteger rowCount = new AtomicInteger(1);

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(14);
        style.setFont(font);

        customerEntities.forEach(customer -> {
            Row row = sheet.createRow(rowCount.getAndIncrement());
            int columnCount = 0;

            createCell(row, columnCount++, customer.getPhone(), style);
            createCell(row, columnCount++, customer.getCustomerCode(), style);
            createCell(row, columnCount++, customer.getPin(), style);
        });

    }

    public String encodeExcelFileToBase64(List<CustomerEntity> customerEntities) throws IOException {
        writeHeaderLine();
        writeDataLines(customerEntities);
//        try (FileOutputStream outputStream = new FileOutputStream("Orders.xlsx")) {
//            workbook.write(outputStream);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        workbook.write(bos);
        return Base64.getEncoder().encodeToString(bos.toByteArray());
    }

    public void decodeBase64ToExcelFile(String base64EncodedFile) {
        log.info("decodeBase64ToExcelFile started, {}", kv("base64EncodedFile", base64EncodedFile));
        InputStream is = new ByteArrayInputStream(Base64.getDecoder().decode(base64EncodedFile.getBytes(StandardCharsets.UTF_8)));
        File file = new File("src/main/resources/Customers.xlsx");
        try(OutputStream outputStream = new FileOutputStream(file)){
            IOUtils.copy(is, outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
