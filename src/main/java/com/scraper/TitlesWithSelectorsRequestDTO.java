package com.scraper;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Map;

/**
 * Created by Igor Milanovic on 4/10/17.
 */
public class TitlesWithSelectorsRequestDTO {

    private Map<String, AttributesWithSelectors> data;

    private UrlsRequestDTO url;


    public TitlesWithSelectorsRequestDTO() {}

    public Map<String, AttributesWithSelectors> getData() {
        return data;
    }

    public void setData(Map<String, AttributesWithSelectors> data) {
        this.data = data;
    }

    public UrlsRequestDTO getUrl() {
        return url;
    }

    public void setUrl(UrlsRequestDTO url) {
        this.url = url;
    }

    @Override
    public String toString() {
        // TODO : Improve this
       // String retVal = "Urls=["+String.join(",",this.urls)+"]\n" ;
//        for(String k : data.keySet()){
//            retVal += "\n" + k + "=[" + String.join(",", data.get(k)) + "]";
//        }
        return "";
    }
}
