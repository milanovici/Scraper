package com.scraper.component;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.concurrent.Callable;

/**
 * Created by Igor Milanovic on 4/11/17.
 */
public class Downloader extends Component implements Input<String>, Output<String>, Callable {

    private String url;
    private int responseStatus;
    private String content;

    @Override
    public void in(String url){
        this.url = url;
    }

    @Override
    public String out(){
        return this.content;
    }

    @Override
    public Void call(){
        //LOGGER.info("Getting nonInterpretedSource from url : {}", url);
        try {
            Connection.Response res = Jsoup.connect(this.url) // link + start
                    .timeout(10000)
                    .method(Connection.Method.GET)
                    .execute();
            //LOGGER.info("Getting response : {}", res.statusCode());
            responseStatus = res.statusCode();
            this.content = res.parse().html();
        }catch(IOException e){
            responseStatus = 500;
        }
        return null;
    }
}
