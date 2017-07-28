package com.scraper.component;

import com.google.common.collect.Sets;
import org.jsoup.nodes.Document;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;

/**
 * Created by Igor Milanovic on 4/11/17.
 */
public class Scenario1 {

//    Scraper s = new Scraper("Guardian world scraper");
//
//    PageGenerator pg = new PageGenerator();
//    pg.setParameterPage("https://www.theguardian.com/world/2017/feb/%02d/all", start:1, stop:31, step:1);
//    //pg.setParameterPage("https://www.theguardian.com/world/2017/%s/05/all", ["jan", "feb", "mar"]);
//    //pg.setParameterPage("https://www.theguardian.com/world/2017/%s/%02d/all", ["jan", "feb", "mar"], {start: 1, stop: 32, step: 1});
//    //pg.addPage("https://www.theguardian.com/world/2014/feb/feb/all");
//    //pg.addPage("https://www.theguardian.com/world/2013/feb/feb/all");
//
//    s.addComponent(pg);
//
//    Downloader dl = new Downloader();
//    dl.setInput(pg);
//    //dl.setInput(new Selector(...));
//
//    Parser pr = new JsParser(dl);
//    // pr = new HtmlParser();
//    pr.setDownloader(dl);
//
//    Map titleSelector = {
//            "Title" : {
//                "selector" : "#top > div.l-side-margins > div > section > div > div.fc-container__body.fc-container--rolled-up-hide > div > ul > li > div > div > a",
//                "elements" : ["href", "data-id"]
//            },
//            "Number of comments" : {
//                "selector" : "#top > div.l-side-margins > div > section > div > div.fc-container__body.fc-container--rolled-up-hide > div > ul > li > ul > li > div > div > div > aside > a",
//                "elements" : []
//            }
//    }
//
//    Selector se = new Selector(titleSelector);
//    se.setInput(pr);
//    se.setOutput(wr);
//    //se.setOutput(dl);
//
//    Writer wr = new XLSWriter("out.xls");
//
//    s.scrape();

    public static void main(String[] args) {

        PageGenerator pg = new PageGenerator("https://www.theguardian.com/world/2017/feb/%02d/all", 1, 10, 1);

        ExecutorPool ep = new ExecutorPool();
        List<Downloader> tasks ;

        for(String s : pg.out()){
            Downloader dl = new Downloader();
            dl.in(s);
            ep.addTask(dl);
        }
        ep.execute();


        tasks = ep.getTasks();
        ep.clearTasks();

        List<Document> documents = new ArrayList();

        for(Downloader task : tasks) {
            Parser pr = new Parser();
            pr.in(task.out());
            documents.add(pr.out());
        }


        Selector s = new Selector();
        s.addDSL("Title",
                "#top > div.l-side-margins > div > section > div > div.fc-container__body.fc-container--rolled-up-hide > div:nth-child(1) > ul > li > div > div > a",
                Sets.newHashSet("href"));

        s.in(documents);
        Map<String, ConcurrentLinkedQueue<String>> data = s.out();

        // SECOND
       for(String ss : data.get("Title:href")){
            Downloader dl = new Downloader();
            dl.in(ss);
            ep.addTask(dl);
        }

        ep.execute();
        documents = new ArrayList();

        for(Downloader task : ep.getTasks()) {
            Parser pr = new Parser();
            pr.in(task.out());
            documents.add(pr.out());
        }

        s = new Selector();
        s.addDSL("Title",
                "#article > div.hide-on-mobile > header > div.content__header.tonal__header > div > div > h1",
                new HashSet<>());

        s.addDSL("Content",
                "#article > div.content__main.tonal__main.tonal__main--tone-news > div > div.content__main-column.content__main-column--article.js-content-main-column > div.content__article-body.from-content-api.js-article__body > p",
                new HashSet<>());


        s.in(documents);

        Writer wr = new Writer("guardian.xls");
        wr.in(s.out());


        System.out.println("END...");

    }

}
