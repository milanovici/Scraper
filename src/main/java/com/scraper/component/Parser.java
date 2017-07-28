package com.scraper.component;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * Created by Igor Milanovic on 4/11/17.
 */
public class Parser extends Component implements Output<Document>, Input<String>{

    private String content;

    @Override
    public void in(String content){
        this.content = content;
    }

    @Override
    public Document out(){
        return this.parse();
    }


    private Document parse() {
        return Jsoup.parse(this.content);
    }
}
