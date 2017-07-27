/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author Igor Milanovic <igor.milanovic204@gmail.com>
 */
public class Yelp {

    public static Map<String, YelpStudio> studios = new ConcurrentHashMap();
    public static int TIMEOUT = 10 * 1000; // 10 sec
    public static String link = "http://www.limundo.com/Auto-i-moto/aukcije/g1";
    public static int NUM_OF_THREADS = 1;

    public static void main(String[] args) throws Exception {
        int start = 0;
        int end = 990;

        ExecutorService executor = Executors.newFixedThreadPool(NUM_OF_THREADS);

        for (int i = 0; i < NUM_OF_THREADS; ++i) { // All threads
            for (int s = start; s <= end; s += 10) {
                MyThread mt = new MyThread(s);
                executor.execute(mt);
            }
        }

        executor.shutdown();
        while (!executor.isTerminated()) {
        }

        write();
    }

    public static class MyThread extends Thread {

        private int start;

        public MyThread(int start) {
            this.start = start;
        }

        @Override
        public void run() {
            Yelp.run(start);
        }
    }

    public static void run(int start) {
        try {
            Connection.Response res = Jsoup.connect(link) // link + start
                    .timeout(TIMEOUT)
                    .method(Connection.Method.GET)
                    .execute();

            Document doc = res.parse();
            Elements clubs = doc.select(".regular-search-result");
            for (Element club : clubs) {
                String name = club.select(".biz-name").text();
                String link = "";
                if (club.select(".biz-name").first() != null) {
                    link = "https://www.yelp.com" + club.select(".biz-name").first().attr("href");
                }
                String price = "";
                if (club.select(".business-attribute").first() != null) {
                    price = club.select(".business-attribute").first().html();
                }
                String location = club.select("address").text();
                String number = club.select(".biz-phone").text();
                String neigh = club.select(".neighborhood-str-list").text();
                String category = club.select(".category-str-list").text();
                String reviews = club.select(".review-count").text();
                String stars = "";
                if (club.select(".star-img").first() != null) {
                    stars = club.select(".star-img").first().attr("title");
                }

                YelpStudio ys = new YelpStudio();
                ys.setName(name);
                ys.setLink(link);
                ys.setLocation(location);
                ys.setPrice(price);
                ys.setNeighboorhoodList(neigh);
                ys.setNumber(number);
                ys.setCategory(category);
                ys.setReviews(reviews);
                ys.setStars(stars);

                studios.put(link, ys);
                //System.out.println(s);
            }
        } catch (Exception ex) {
            Logger.getLogger(Yelp.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void write() throws IOException {

        Workbook workbook = new HSSFWorkbook();
        Sheet sheet = workbook.createSheet();

        int rowCount = 0;

        for (YelpStudio s : studios.values()) {
            Row row = sheet.createRow(++rowCount);
            writeStudio(s, row);
        }

        try (FileOutputStream outputStream = new FileOutputStream("yelp_java.xls")) {
            workbook.write(outputStream);
        }

    }

    public static void writeStudio(YelpStudio s, Row row) {
        Cell cell = row.createCell(1);
        cell.setCellValue(s.getName());

        cell = row.createCell(2);
        cell.setCellValue(s.getNumber());

        cell = row.createCell(3);
        cell.setCellValue(s.getCategory());

        cell = row.createCell(4);
        cell.setCellValue(s.getLink());

        cell = row.createCell(5);
        cell.setCellValue(s.getLocation());

        cell = row.createCell(6);
        cell.setCellValue(s.getNeighboorhoodList());

        cell = row.createCell(7);
        cell.setCellValue(s.getPrice());

        cell = row.createCell(8);
        cell.setCellValue(s.getReviews());

    }

}
