package com.scraper;

import org.jsoup.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;

import javax.annotation.PostConstruct;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Igor Milanovic on 3/31/17.
 */
@Service
public class ScraperService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScraperService.class);

    public static int NUM_OF_THREADS = 5;
    public static int TIMEOUT = 10 * 1000; // 10 sec
    public static Map<String, ConcurrentLinkedQueue<String>> data = new ConcurrentHashMap();
    //public static Map<String, String> titleWithSelector = new ConcurrentHashMap<>();

    private WebDriver driver;
    private ExecutorService executor;

//    public static void init(){
//        for(String key : titleWithSelector.keySet()){
//            data.put(key, new ConcurrentLinkedQueue<String>());
//        }
//    }

    @PostConstruct
    public void init(){
        LOGGER.info("Init ScrapeService...");
        this.driver = new PhantomJSDriver();
        this.executor =  Executors.newFixedThreadPool(NUM_OF_THREADS);
    }

    public ConcurrentHashMap<String, ConcurrentLinkedQueue<String>> scrape(List<String> urls, Map<String, AttributesWithSelectors> titleWithSelector){
        List<List<String>> listOfUrls = Utils.chopped(urls, NUM_OF_THREADS);
        ConcurrentHashMap<String, ConcurrentLinkedQueue<String>> retVal = new ConcurrentHashMap<>();

        for(List<String> chunk : listOfUrls){
            for(String u : chunk){
                Scraper s = new Scraper(u, titleWithSelector, driver);
                Future<Map<String, ConcurrentLinkedQueue<String>>> data = executor.submit(s);
                try {
                    for(String k : data.get().keySet()){
                        if(retVal.get(k) == null || retVal.get(k).isEmpty()){
                            retVal.put(k, data.get().get(k));
                        }else{
                            retVal.get(k).addAll(data.get().get(k));
                        }
                    }

                }catch(InterruptedException | ExecutionException ee){
                    LOGGER.info("Error : " + ee);
                }
            }
        }

        //executor.shutdown();
//        while (!executor.isTerminated()) {
//        }


        return retVal;

    }


//    public static void main(String[] args) throws IOException{
//        //String link = "http://www.limundo.com/Auto-i-moto/aukcije/g1_strana_{0}";
//        String link = "http://www.ebay.com/";
//        generateTitleWithSelector();
//        init();
//
//        ExecutorService executor = Executors.newFixedThreadPool(NUM_OF_THREADS);
//
//        List<String> urls = PagesGenerator.generatePages(link, 1, 2, 1);
//        List<List<String>> listOfUrls = chopped(urls, NUM_OF_THREADS);
//
//        for(List<String> chunk : listOfUrls){
//            for(String u : chunk){
//                Scraper s = new Scraper(u, titleWithSelector);
//                executor.execute(s);
//            }
//        }
//
//        executor.shutdown();
//        while (!executor.isTerminated()) {
//        }
//
//        write(data);
//
//        System.out.println("End...");
//    }

//    public static void generateTitleWithSelector(){
//        String selector1 = "#featuredCollectionsFragment > div > div.big-heros > div:nth-child(1) > div.framing-description-under > div > h3 > a";
//        String selector2 = "#featuredCollectionsFragment > div > div.big-heros > div:nth-child(4) > div.framing-description-under > div > h3 > a";
//
//        String commonSelector = SimilarSelectors.findSimillarSelector(selector1, selector2);
//        titleWithSelector.put("Title", commonSelector);
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
//        selector2 = "#main > ul > li:nth-child(6) > div.col-xs-12.col-md-2.list_item_info > div.lista_bids > p";
//
//        commonSelector = SimilarSelectors.findSimillarSelector(selector1, selector2);
//        titleWithSelector.put("Ponuda", commonSelector);
//
//        selector1 = "#main > ul > li:nth-child(7) > div.col-xs-12.col-md-2.list_item_info > div.lista_viewers > p";
//        selector2 = "#main > ul > li:nth-child(9) > div.col-xs-12.col-md-2.list_item_info > div.lista_viewers > p";
//
//        commonSelector = SimilarSelectors.findSimillarSelector(selector1, selector2);
//        titleWithSelector.put("Posmatra", commonSelector);

 //   }



    public Document parse(String url) {
        String source = this.getInterpretedSource(url);
        return Jsoup.parse(source);
    }

    public String getInterpretedSource(String url){
        LOGGER.info("Getting interpretedSource from url : {}", url);
//        DesiredCapabilities caps = new DesiredCapabilities();
//        caps.setJavascriptEnabled(true); // enabled by default
//        caps.setCapability(
//                PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY,
//                "/usr/local/bin/phantomjs"
//        );

        if (!url.toLowerCase().matches("^\\w+://.*")) {
            url = "http://" + url;
        }

        driver.get(url);
        String source = driver.getPageSource();
        return source;
    }

    public String getNonInterpretedSource(String url){
        LOGGER.info("Getting nonInterpretedSource from url : {}", url);
        try {
            Connection.Response res = Jsoup.connect(url) // link + start
                    .timeout(10000)
                    .method(Connection.Method.GET)
                    .execute();
            LOGGER.info("Getting response : {}", res.statusCode());
            return res.parse().html();
        }catch(IOException e){}
        return "";

    }
}
