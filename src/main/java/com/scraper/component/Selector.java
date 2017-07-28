package com.scraper.component;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

/**
 * Created by Igor Milanovic on 4/11/17.
 */
public class Selector extends Component implements Input<Collection<Document>>, Output<Map<String, ConcurrentLinkedQueue<String>>> {

    private Collection<Document> documents;
    private Map<String, DSLSelectorWithAttributes> selectors;
    private Map<String, ConcurrentLinkedQueue<String>> retVal;

    public Selector(){
        this.retVal = new ConcurrentHashMap<>();
    }

    public void addDSL(String selectorTitle, String cssSelector, Set<String> attributes){
        if(this.selectors == null)
            this.selectors = new ConcurrentHashMap();
        DSLSelectorWithAttributes d = new DSLSelectorWithAttributes(cssSelector);
        if(attributes != null)
            d.setAttributes(attributes);
        else
            d.setAttributes(new HashSet<>());
        this.selectors.put(selectorTitle, d);
    }

    @Override
    public void in(Collection<Document> documents){
        this.documents = documents;
    }

    @Override
    public Map<String, ConcurrentLinkedQueue<String>> out(){
        this.selectElements();
        return retVal;
    }

    private void selectElements(){
        for(Document document : this.documents) {
            for (Map.Entry<String, DSLSelectorWithAttributes> entry : selectors.entrySet()) {
                DSLSelectorWithAttributes selectorWithAttributes = entry.getValue();
                document.select(".hide").remove();
                document.select("span[style='display:none']").remove();
                Elements els = document.select(selectorWithAttributes.getSelector());


                this.selectAttributes(els, entry);

                ConcurrentLinkedQueue<String> d = new ConcurrentLinkedQueue<>(els.stream().map(e -> e.text()).collect(Collectors.toList()));
                //this.retVal.put(entry.getKey(), d);
                this.union(entry.getKey(), d);
            }
        }
    }

    private void selectAttributes(Elements els, Map.Entry<String, DSLSelectorWithAttributes> entry){
        for(String attribute : entry.getValue().getAttributes()){ // href, data-id
            ConcurrentLinkedQueue<String> attributeValues = new ConcurrentLinkedQueue();
            for (Element el : els) {
                String attributeValue = el.attr(attribute);
                //LOGGER.info("Attr : {}", attribute);
                attributeValues.add(attributeValue);
            }
            this.union(entry.getKey() + ":" + attribute, attributeValues);
            //retVal.put(entry.getKey() + ":" + attribute, attributeValues);
        }
    }

    private void union(String key, ConcurrentLinkedQueue<String> values){
        if(this.retVal.containsKey(key)){
            this.retVal.get(key).addAll(values);
        }else{
            this.retVal.put(key, values);
        }
    }

}
