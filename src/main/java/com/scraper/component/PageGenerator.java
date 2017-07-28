package com.scraper.component;

import com.scraper.PagesGenerator;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Igor Milanovic on 4/11/17.
 */
public class PageGenerator extends Component implements Output<Set<String>>{

    private String urlWithParameters;
    private Set<String> urls;
    private int start;
    private int stop;
    private int step;

    public PageGenerator(){
        this.urls = new HashSet<>();
    }

    public PageGenerator(Set<String> urls) {
        this.urls = urls;
    }

    public PageGenerator(String urlWithParameters, int start, int stop, int step) {
        this.urlWithParameters = urlWithParameters;
        this.start = start;
        this.stop = stop;
        this.step = step;
    }

    public void addPage(String url){
        this.urls.add(url);
    }

    @Override
    public Set<String> out(){
        Set<String> d = new HashSet(PagesGenerator.generatePages(urlWithParameters, start, stop, step));
        //d.addAll(this.urls);
        return d;
    }

    //pg.setParameterPage("https://www.theguardian.com/world/2017/feb/%02d/all", start:1, stop:31, step:1);
    //pg.setParameterPage("https://www.theguardian.com/world/2017/%s/05/all", ["jan", "feb", "mar"]);
    //pg.setParameterPage("https://www.theguardian.com/world/2017/%s/%02d/all", ["jan", "feb", "mar"], {start: 1, stop: 32, step: 1});
    //pg.addPage("https://www.theguardian.com/world/2014/feb/feb/all");
    //pg.addPage("https://www.theguardian.com/world/2013/feb/feb/all");
}
