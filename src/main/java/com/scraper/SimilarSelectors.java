package com.scraper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by Igor Milanovic on 3/31/17.
 */
public class SimilarSelectors {

    public static void main(String[] args) throws IOException {
//        Connection.Response res = Jsoup.connect("http://www.limundo.com/Tehnika/aukcije/g14") // link + start
//                .timeout(10000)
//                .method(Connection.Method.GET)
//                .execute();
//
//        Document doc = res.parse();
//        Elements clubs = doc.select("list-items > ul > li:nth-child(19) > div");
        String s1 = "#category-left > div:nth-child(1) > div:nth-child(1) > div > div.item > div > div.mask-loupe2.wrapper-parent";
        String s2 = "#category-left > div:nth-child(1) > div:nth-child(2) > div > div.item > div > div.mask-loupe2.wrapper-parent";
//        String retVal = findSimillarSelector(s1, s2);
//        System.out.print("RetVal : " + retVal);
    }

    // Limundo
    //#main > ul > li:nth-child(2) > div.col-xs-12.col-md-5.col-lg-6.list_item_body > div > p:nth-child(1) > span
    //#main > ul > li:nth-child(6) > div.col-xs-12.col-md-5.col-lg-6.list_item_body > div > p:nth-child(1) > span

    // Redit
    //#thing_t3_62iocn > div.entry.unvoted > p.title > a
    //#thing_t3_62iutr > div.entry.unvoted > p.title > a
    //#thing_t3_62icnd > div.entry.unvoted > p.title > a
    //#thing_t3_62hxvs > div.entry.unvoted > p.title > a

    // Assport.rs
    //#category-left > div:nth-child(1) > div:nth-child(1) > div > div.item > div > div.mask-loupe2.wrapper-parent
    //#category-left > div:nth-child(1) > div:nth-child(2) > div > div.item > div > div.mask-loupe2.wrapper-parent

    //Ifit
    //#em-grid-mode > ol > li:nth-child(1) > div
    //#em-grid-mode > ol > li:nth-child(3) > div
    //#em-grid-mode > ol > li:nth-child(10) > div

    //Zara
    //#product-4479551 > div > div.product-info-item.product-info-item-name > a
    //#product-4081999 > div > div.product-info-item.product-info-item-name > a
    //#product-4484503 > div > div.product-info-item.product-info-item-name > a

    //Deichmann
    //#\30 0013501411749 > div.product-overlay > a
    //#\30 0013501377837 > div.product-overlay > a
    //#\30 0013501409177 > div.product-overlay > a


    public static String findSimillarSelector(String selector1, String selector2) throws BadSelectorRequest{
        List<String> s1data = Arrays.asList(selector1.split(">"));
        List<String> s2data = Arrays.asList(selector2.split(">"));

        if(s1data.size() != s2data.size()){
            throw new BadSelectorRequest("Sizes of arrays are different!");
        }

        List<String> diffList = new ArrayList();

        for(int i=0;i<s1data.size();++i) {

            // Find diffList part in selectors
            if (!s1data.get(i).equals(s2data.get(i))) {
                System.out.println("Adding diffList : " + s1data.get(i));
                diffList.add(s1data.get(i));
            }
        }

        if(diffList.size() != 1){
            throw new BadSelectorRequest("Diff size is : " + diffList.size() + ". Diff elements : " + String.join(",", diffList));
        }

        String diff = diffList.get(0);

        if(Pattern.compile(":nth-child(.[0-9]+.)").matcher(diff).find()) {
            String retVal = selector1.replace(diff, diff.replaceAll(":nth-child(.[0-9]+.)", "")); //dif.rep
            return retVal;
        }else{
            String retVal = selector1.replace(diff, "");
            return trimStringByString(retVal, ">").trim();
        }
    }

    public static String trimStringByString(String text, String trimBy) {
        int beginIndex = 0;
        int endIndex = text.length();

        while (text.substring(beginIndex, endIndex).startsWith(trimBy)) {
            beginIndex += trimBy.length();
        }

        while (text.substring(beginIndex, endIndex).endsWith(trimBy)) {
            endIndex -= trimBy.length();
        }

        return text.substring(beginIndex, endIndex);
    }



}
