package com.scraper.component;

import java.util.Map;
import java.util.Set;

/**
 * Created by Igor Milanovic on 4/11/17.
 */
public class DSLSelectorWithAttributes {
        private String selector;
        private Set<String> attributes;

        public DSLSelectorWithAttributes(String selector) {
                this.selector = selector;
        }

        public String getSelector() {
                return selector;
        }

        public void setSelector(String selector) {
                this.selector = selector;
        }

        public Set<String> getAttributes() {
                return attributes;
        }

        public void setAttributes(Set<String> attributes) {
                this.attributes = attributes;
        }
}
