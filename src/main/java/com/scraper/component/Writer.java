package com.scraper.component;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by Igor Milanovic on 4/11/17.
 */
public class Writer extends  Component implements Input<Map<String, ConcurrentLinkedQueue<String>>>{

    private String fileName;

    public Writer(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public void in(Map<String, ConcurrentLinkedQueue<String>> map){
        this.write(this.generateWorkbook(map));
    }

    public Workbook generateWorkbook(Map<String, ConcurrentLinkedQueue<String>> data) {

        Workbook workbook = new HSSFWorkbook();
        Sheet sheet = workbook.createSheet();

        int tmp = 0;

        Row header = sheet.createRow(0);
        for(String key : data.keySet()){
            header.createCell(tmp++).setCellValue(key);
        }

        int column = 0;

        for(String v : data.keySet()) { // a, b, c, d, 100, 200, 300, 400
            writeColumn(data.get(v), sheet, column++);
        }

        return workbook;
    }

    public void write(Workbook wb){
        try (FileOutputStream outputStream = new FileOutputStream(this.fileName)) {
            wb.write(outputStream);
        }catch(IOException io){
            //LOGGER.error("Got exception while writing to a file {}.", io.getMessage());
        }
    }

    private void writeColumn(ConcurrentLinkedQueue<String> d, Sheet sheet, int column){
        int rowCount = 1;
        for(String s : d) {
            Row row = sheet.getRow(rowCount);
            if(row == null)
                row = sheet.createRow(rowCount);
            ++rowCount;
            Cell c = row.createCell(column);
            c.setCellValue(s);
        }
    }
}
