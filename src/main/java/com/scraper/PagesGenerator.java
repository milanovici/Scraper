package com.scraper;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Igor Milanovic on 4/3/17.
 */
public class PagesGenerator {

    public static void main(String[] args) {
        String url = "http://www.limundo.com/Auto-i-moto/aukcije/g1_strana_{0}";
        List<String> links = generatePages(url, 1, 10, 1);
        System.out.println("End..");

    }

    public static List<String> generatePages(String url, int from, int to, int step) {
        List<String> retVal = new ArrayList<>();
        for(int i=from;i<to;i+=step){
            retVal.add(String.format(url, i)); //MessageFormat.format(url, i)
        }

        return retVal;
    }

    public static List<String> generatePages(String url, List<String> values) {
        List<String> retVal = new ArrayList<>();
        for(String val : values){
            retVal.add(MessageFormat.format(url, val));
        }

        return retVal;
    }
}