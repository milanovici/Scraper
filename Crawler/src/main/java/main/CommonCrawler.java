package main;

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
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * Created by Igor Milanovic on 3/31/17.
 */
public class CommonCrawler {

    public static int NUM_OF_THREADS = 5;
    public static int TIMEOUT = 10 * 1000; // 10 sec
    public static Map<String, ConcurrentLinkedQueue<String>> data = new ConcurrentHashMap();
    public static Map<String, String> titleWithSelector = new ConcurrentHashMap<>();

    public static void init(){
        for(String key : titleWithSelector.keySet()){
            data.put(key, new ConcurrentLinkedQueue<String>());
        }
    }

    public static void main(String[] args) throws IOException{
        //String link = "http://www.limundo.com/Auto-i-moto/aukcije/g1_strana_{0}";
        String link = "http://www.ebay.com/";
        generateTitleWithSelector();
        init();

        ExecutorService executor = Executors.newFixedThreadPool(NUM_OF_THREADS);

        List<String> urls = PagesGenerator.generatePages(link, 1, 2, 1);
        List<List<String>> listOfUrls = chopped(urls, NUM_OF_THREADS);

        for(List<String> chunk : listOfUrls){
            for(String u : chunk){
                Scraper s = new Scraper(u, titleWithSelector);
                executor.execute(s);
            }
        }

        executor.shutdown();
        while (!executor.isTerminated()) {
        }

        write(data);

        System.out.println("End...");
    }

    public static void generateTitleWithSelector(){
        String selector1 = "#featuredCollectionsFragment > div > div.big-heros > div:nth-child(1) > div.framing-description-under > div > h3 > a";
        String selector2 = "#featuredCollectionsFragment > div > div.big-heros > div:nth-child(4) > div.framing-description-under > div > h3 > a";

        String commonSelector = SimilarSelectors.findSimillarSelector(selector1, selector2);
        titleWithSelector.put("Title", commonSelector);
//        String selector1 = "#main > ul > li:nth-child(2) > div.col-xs-12.col-md-5.col-lg-6.list_item_body > div > p:nth-child(1) > span";
//        String selector2 = "#main > ul > li:nth-child(6) > div.col-xs-12.col-md-5.col-lg-6.list_item_body > div > p:nth-child(1) > span";
//
//        String commonSelector = SimilarSelectors.findSimillarSelector(selector1, selector2);
//        titleWithSelector.put("Cena", commonSelector);
//
//        selector1 = "#main > ul > li:nth-child(2) > div.col-xs-12.col-md-5.col-lg-6.list_item_body > h2 > a";
//        selector2 = "#main > ul > li:nth-child(5) > div.col-xs-12.col-md-5.col-lg-6.list_item_body > h2 > a";
//
//        commonSelector = SimilarSelectors.findSimillarSelector(selector1, selector2);
//        titleWithSelector.put("Naslov", commonSelector);
//
//        selector1 = "#main > ul > li:nth-child(3) > div.col-xs-12.col-md-5.col-lg-6.list_item_body > p.list_item_opis.hidden-xs.hidden-sm";
//        selector2 = "#main > ul > li:nth-child(2) > div.col-xs-12.col-md-5.col-lg-6.list_item_body > p.list_item_opis.hidden-xs.hidden-sm";
//
//        commonSelector = SimilarSelectors.findSimillarSelector(selector1, selector2);
//        titleWithSelector.put("Opis", commonSelector);
//
//        selector1 = "#main > ul > li:nth-child(3) > div.col-xs-12.col-md-2.list_item_info > div.lista_time.hidden-xs.hidden-sm > p";
//        selector2 = "#main > ul > li:nth-child(5) > div.col-xs-12.col-md-2.list_item_info > div.lista_time.hidden-xs.hidden-sm > p";
//
//        commonSelector = SimilarSelectors.findSimillarSelector(selector1, selector2);
//        titleWithSelector.put("Preostalo", commonSelector);
//
//        selector1 = "#main > ul > li:nth-child(6) > div.col-xs-12.col-md-2.list_item_info > div.lista_bids > p";
//        selector2 = "#main > ul > li:nth-child(7) > div.col-xs-12.col-md-2.list_item_info > div.lista_bids > p";
//
//        commonSelector = SimilarSelectors.findSimillarSelector(selector1, selector2);
//        titleWithSelector.put("Ponuda", commonSelector);
//
//        selector1 = "#main > ul > li:nth-child(7) > div.col-xs-12.col-md-2.list_item_info > div.lista_viewers > p";
//        selector2 = "#main > ul > li:nth-child(9) > div.col-xs-12.col-md-2.list_item_info > div.lista_viewers > p";
//
//        commonSelector = SimilarSelectors.findSimillarSelector(selector1, selector2);
//        titleWithSelector.put("Posmatra", commonSelector);

    }

    public static class Scraper extends Thread {

        private String link;
        private Map<String, String> titleWithSelector;

        public Scraper(String link, Map<String, String> titleWithSelector){
            this.link = link;
            this.titleWithSelector = titleWithSelector;
        }

        @Override
        public void run() {
            try {
                System.out.println("Getting data from : " + link);

                Document doc = myParse(link);
                //System.out.println(doc.getAllElements());
                Connection.Response res = Jsoup.connect(link) // link + start
                        .timeout(TIMEOUT)
                        .method(Connection.Method.GET)
                        .execute();

                System.out.println("Getting response : " + res.statusCode());
               // Document doc = res.parse();
                Map<String, Long> topSelectors = new HashMap<>();
                for(Element el : doc.getAllElements()){
                    System.out.println(el.cssSelector());
                    if(topSelectors.containsKey(el.cssSelector())) {
                        Long cnt = topSelectors.get(el.cssSelector());
                        topSelectors.put(el.cssSelector(), ++cnt);
                    }else{
                        topSelectors.put(el.cssSelector(), 0L);
                    }
                    System.out.println("Counting...");
                }
                for(String key: titleWithSelector.keySet()){
                    Elements els = doc.select(titleWithSelector.get(key));
                    data.get(key).addAll( els.stream().map(e -> e.text()).collect(Collectors.toList()));
                }
            }catch(IOException e){
                System.out.println(e);
            }
        }
    }

    public static Document myParse(String url) {
        WebDriver ghostDriver = new PhantomJSDriver();
        try {
            ghostDriver.get(url);
            String source = ghostDriver.getPageSource();
            // save it to file
            try(PrintStream ps = new PrintStream("_blank.html")) { ps.println(source); }catch (Exception e){}
            return Jsoup.parse(source);
        }
        finally {
            ghostDriver.quit();
        }
    }


    public static void write(Map<String, ConcurrentLinkedQueue<String>> data) throws IOException{

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

        try (FileOutputStream outputStream = new FileOutputStream("out.xls")) {
            workbook.write(outputStream);
        }
//        FileWriter fw = new FileWriter("out.csv");
//        PrintWriter pw = new PrintWriter(fw);
//        pw.printl
//        pw.println(title);
//        for(String s : data){
//            pw.println(s);
//        }
//
//        pw.flush();
    }

    public static void writeColumn(ConcurrentLinkedQueue<String> d, Sheet sheet, int column){
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


    static <T> List<List<T>> chopped(List<T> list, final int L) {
        List<List<T>> parts = new ArrayList<>();
        final int N = list.size();
        for (int i = 0; i < N; i += L) {
            parts.add(new ArrayList<T>(
                    list.subList(i, Math.min(N, i + L)))
            );
        }
        return parts;
    }


}
