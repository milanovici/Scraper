package com.scraper;

import java.util.List;

/**
 * Created by Igor Milanovic on 4/10/17.
 */
public class AttributesWithSelectors {

    private List<String> attributes;
    private List<String> selectors;


    public AttributesWithSelectors() {
    }

    public List<String> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<String> attributes) {
        this.attributes = attributes;
    }

    public List<String> getSelectors() {
        return selectors;
    }

    public void setSelectors(List<String> selectors) {
        this.selectors = selectors;
    }
}
