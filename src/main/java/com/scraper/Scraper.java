package com.scraper;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

/**
 * Created by Igor Milanovic on 4/5/17.
 */
public class Scraper implements Callable<Map<String, ConcurrentLinkedQueue<String>>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(Scraper.class);

    private String link;
    private Map<String, AttributesWithSelectors> titleWithSelector;
    private final int TIMEOUT = 1000 * 5; // 5 secs
    private Map<String, ConcurrentLinkedQueue<String>> retVal = new HashMap<>();
    private WebDriver driver;


    public Scraper(String link, Map<String, AttributesWithSelectors> titleWithSelector, WebDriver driver) {
        this.link = link;
        this.titleWithSelector = titleWithSelector;
        this.driver = driver;
        //init();
    }

    @Override
    public Map<String, ConcurrentLinkedQueue<String>> call() throws Exception {
            LOGGER.info("Getting data from : {}",link);

            Document doc = this.parse(link);

            // Getting elements
            for(String key: titleWithSelector.keySet()){
                Elements els = doc.select(titleWithSelector.get(key).getSelectors().get(2));
                if(titleWithSelector.get(key).getAttributes() != null && !titleWithSelector.get(key).getAttributes().isEmpty()) {
                    String attributeSelector = titleWithSelector.get(key).getAttributes().get(0);

                    // Getting attributes
                    ConcurrentLinkedQueue<String> attributeValues = new ConcurrentLinkedQueue();
                    for (Element el : els) {
                        String attribute = el.attr(attributeSelector);
                        //LOGGER.info("Attr : {}", attribute);
                        attributeValues.add(attribute);
                    }
                    retVal.put(key + ":" + attributeSelector, attributeValues);
                }
                ConcurrentLinkedQueue<String> d = new ConcurrentLinkedQueue<>(els.stream().map(e -> e.text()).collect(Collectors.toList()));
                retVal.put(key, d);
            }


            return retVal;
    }

    private String getInterpretedSource(String url){
        LOGGER.info("Getting interpretedSource from url : {}", url);
        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setJavascriptEnabled(true); // enabled by default
        caps.setCapability(
                PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY,
                "/usr/local/bin/phantomjs"
        );

        if (!url.toLowerCase().matches("^\\w+://.*")) {
            url = "http://" + url;
        }

        driver.get(url);
        String source = driver.getPageSource();
        return source;
    }

    private String getNonInterpretedSource(String url){
        LOGGER.info("Getting nonInterpretedSource from url : {}", url);
        try {
            Connection.Response res = Jsoup.connect(url) // link + start
                    .timeout(TIMEOUT)
                    .method(Connection.Method.GET)
                    .execute();
            LOGGER.info("Getting response : {}", res.statusCode());
            return res.parse().html();
        }catch(IOException e){}
        return "";

    }

    public Document parse(String url) {
        String source = this.getInterpretedSource(url);
        return Jsoup.parse(source);
    }

    private void init(){
        for(String key : titleWithSelector.keySet()){
            this.retVal.put(key, new ConcurrentLinkedQueue());
        }
    }

}