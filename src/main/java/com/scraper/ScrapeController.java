package com.scraper;

import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by Igor Milanovic on 4/4/17.
 */
@RestController
@RequestMapping(value = "/scrape")
public class ScrapeController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScrapeController.class);

    @Autowired
    private ScraperService service;

    @Autowired
    private WriterService writerService;

    @RequestMapping(method = RequestMethod.GET)
    @CrossOrigin(origins = "*")
    public String getHtmlPageFromUrl (@RequestParam(value = "url") @NotNull String url, HttpServletResponse response){
        LOGGER.info("GET /scrape/{}", url);
        String fileName = "_blank.html";
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
        String content = service.getNonInterpretedSource(url);
        return content;
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity dummy(@RequestBody TitlesWithSelectorsRequestDTO dto,  HttpServletResponse response){
        LOGGER.info("POST /scraper {}", dto);
        //String link = "http://www.limundo.com/Auto-i-moto/aukcije/g1_strana_{0}";
        List<String> urls = PagesGenerator.generatePages(dto.getUrl().getUrls().get(0), dto.getUrl().getStart(), dto.getUrl().getStop(), dto.getUrl().getStep());
        ConcurrentHashMap<String, ConcurrentLinkedQueue<String>> data ;
        try {
            data = service.scrape(urls, this.convertDTOtoTitlesWithSelectors(dto));
        }catch (BadSelectorRequest bse){
            return ResponseEntity.badRequest().body(bse.getMessage());
        }

        Workbook wb = writerService.generateWorkbook(data);
        response.setHeader("Content-disposition", "attachment; filename=out.xls");
        try {
            wb.write(response.getOutputStream());
            writerService.write(writerService.generateWorkbook(data));
        }catch(IOException io){
            LOGGER.error("Writing to a response {}", io.getMessage());
        }

        return ResponseEntity.ok().build();
    }

    private Map<String, AttributesWithSelectors> convertDTOtoTitlesWithSelectors(TitlesWithSelectorsRequestDTO request) throws BadSelectorRequest{
        Map<String, AttributesWithSelectors> retVal = new HashMap<>();
        for(String label : request.getData().keySet()){
            //assert(request.getData().get(label).size() == 2);
            AttributesWithSelectors aws = request.getData().get(label);
            assert(aws.getSelectors().size() == 2);
            String selector1 = aws.getSelectors().get(0);
            String selector2 = aws.getSelectors().get(1);
            aws.getSelectors().add(SimilarSelectors.findSimillarSelector(selector1, selector2));
            retVal.put(label, aws);
        }
        return retVal;
    }

}