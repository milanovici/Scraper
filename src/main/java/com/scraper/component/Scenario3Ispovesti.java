package com.scraper.component;

import com.google.common.collect.Sets;
import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by Igor Milanovic on 4/28/17.
 */
public class Scenario3Ispovesti
{

    public static void main(String[] args) {

        PageGenerator pg = new PageGenerator("http://ispovesti.com/sort/sud/%01d", 1, 101, 1);

        ExecutorPool ep = new ExecutorPool();
        List<Downloader> tasks;

        for (String s : pg.out()) {
            Downloader dl = new Downloader();
            dl.in(s);
            ep.addTask(dl);
        }
        ep.execute();


        tasks = ep.getTasks();
        ep.clearTasks();

        List<Document> documents = new ArrayList();

        for (Downloader task : tasks) {
            Parser pr = new Parser();
            pr.in(task.out());
            documents.add(pr.out());
        }

        Selector s = new Selector();
        s.addDSL("Content",
                "#content > div > div.confession-text",
                Sets.newHashSet());

        s.in(documents);

        Writer wr = new Writer("ispovesti.xls");
        wr.in(s.out());

        System.out.println("END...");
    }
}
