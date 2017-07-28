package com.scraper.component;

import com.scraper.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by Igor Milanovic on 4/12/17.
 */
public class ExecutorPool {

    private ExecutorService executor;
    private List<Downloader> tasks;
    public static final int NUM_OF_THREADS = 10;

    public ExecutorPool() {
        this.executor =  Executors.newFixedThreadPool(NUM_OF_THREADS);
        this.tasks = new ArrayList<>();
    }

    public void addTask(Downloader d){
        this.tasks.add(d);
    }

    public List<Downloader> getTasks(){
        return this.tasks;
    }

    public void clearTasks(){
        this.tasks = new ArrayList<>();
    }

    public void execute() {
        //List<List<String>> listOfUrls = Utils.chopped(urls, NUM_OF_THREADS);
        //ConcurrentHashMap<String, ConcurrentLinkedQueue<String>> retVal = new ConcurrentHashMap<>();
        List<Future> futures = new ArrayList();
        for(Downloader task : this.tasks){
            futures.add(executor.submit(task));
        }

        try {
            for (Future f : futures) {
                f.get();
            }
        }catch(InterruptedException | ExecutionException e){

        }

       // executor.

//        for (List<String> chunk : listOfUrls) {
//            for (String u : chunk) {
//                Scraper s = new Scraper(u, titleWithSelector);
//                Future<Map<String, ConcurrentLinkedQueue<String>>> data = executor.submit(s);
//                try {
//                    for (String k : data.get().keySet()) {
//                        if (retVal.get(k) == null || retVal.get(k).isEmpty()) {
//                            retVal.put(k, data.get().get(k));
//                        } else {
//                            retVal.get(k).addAll(data.get().get(k));
//                        }
//                    }
//
//                } catch (InterruptedException | ExecutionException ee) {
//                    LOGGER.info("Error : " + ee);
//                }
//            }
//        }
    }





}
