package com.scraper.component;

import com.google.common.collect.Sets;
import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by Igor Milanovic on 5/19/17.
 */
public class CimeriRs {

    public static void main(String[] args) {

        PageGenerator pg = new PageGenerator("http://www.cimeri.rs/trazim-stan_NoviSad&offset=%02d", 0, 120, 10);

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
        s.addDSL("Link",
                "div.pictureContainer > a",
                Sets.newHashSet("href"));

        s.in(documents);


        Map<String, ConcurrentLinkedQueue<String>> data = s.out();

        // SECOND
        for(String ss : data.get("Link:href")){
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
        s.addDSL("Cena",
                "#details > div.rightBlock > div.listBlock1 > div.listPrice",
                new HashSet<>());

        s.addDSL("Ime",
                "#details > div.rightBlock > div.listBlock1 > div.location > h2",
                new HashSet<>());

        s.addDSL("Datum",
                "#details > div.rightBlock > div.listBlock1 > div.listDate",
                new HashSet<>());

        s.addDSL("Sifra",
                "#details > div.rightBlock > div:nth-child(2) > div.parameters > div:nth-child(1) > strong",
                new HashSet<>());

        s.addDSL("Trazim",
                "#details > div.rightBlock > div:nth-child(2) > h2",
                new HashSet<>());

        s.addDSL("Datum useljenja",
                "#details > div.rightBlock > div:nth-child(2) > div.parameters > div:nth-child(2) > b",
                new HashSet<>());

        s.addDSL("Maks broj cimera",
                "#details > div.rightBlock > div:nth-child(2) > div.parameters > div:nth-child(4) > b",
                new HashSet<>());

        s.addDSL("Godina i pol",
                "#details > div.rightBlock > div:nth-child(2) > div.parameters > div:nth-child(9) > label > b",
                new HashSet<>());

        s.addDSL("Pusac",
                "#details > div.rightBlock > div:nth-child(2) > div.parameters > div:nth-child(10)",
                new HashSet<>());

        s.addDSL("Zanimanje",
                "#details > div.rightBlock > div:nth-child(2) > div.parameters > div:nth-child(10) > b",
                new HashSet<>());

        s.addDSL("Ljubimac",
                "#details > div.rightBlock > div:nth-child(2) > div.parameters > div:nth-child(13) > b",
                new HashSet<>());

        s.addDSL("Nesto o meni",
                "#details > div.leftBlock",
                new HashSet<>());


        s.in(documents);

        Writer wr = new Writer("cimerirs.xls");
        wr.in(s.out());
        System.out.println("...");
    }
}
