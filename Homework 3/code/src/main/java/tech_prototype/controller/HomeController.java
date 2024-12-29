package tech_prototype.controller;

import org.springframework.stereotype.*;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import tech_prototype.model.IssuerTemplate;
import tech_prototype.service.IssuerService;
import tech_prototype.service.ScrapingService;
import tech_prototype.service.RowService;

import java.io.IOException;
import java.util.*;

@Controller
@RequestMapping("")
public class HomeController {
    private final ScrapingService scrapingService;
    private final IssuerService issuerService;
    private final RowService rowService;
    private List<IssuerTemplate> allIssuers;

    public HomeController(ScrapingService scrapingService, IssuerService issuerService, RowService rowService) {
        this.scrapingService = scrapingService;
        this.issuerService = issuerService;
        this.rowService = rowService;
        this.allIssuers = issuerService.readAll();
    }
    @GetMapping
    public String index(Model model) throws IOException, InterruptedException {

        if(allIssuers.isEmpty()) {
            scrapingService.scrape();
            return "error";
        }

        model.addAttribute("issuer", "ADIN");
        model.addAttribute("issuers", allIssuers);
        model.addAttribute("checkedRadio", "1_month");
        model.addAttribute("bodyContent", "issuer");
        return "master-template";
    }
    @GetMapping("/search")
    public String getSearch(){
        return "redirect:/";
    }
    @PostMapping("/search")
    public String search(@RequestParam(required = false) String issuer, Model model) {

        if(issuer == null || issuer.isEmpty())
            issuer = "ADIN";

        model.addAttribute("issuer", issuer);
        model.addAttribute("issuers", allIssuers);
        model.addAttribute("checkedRadio", "1_month");
        model.addAttribute("bodyContent", "issuer");
        return "master-template";
    }
}
