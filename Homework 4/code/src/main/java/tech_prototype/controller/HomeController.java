package tech_prototype.controller;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.*;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import tech_prototype.model.IssuerTemplate;
import tech_prototype.service.IssuerService;
import tech_prototype.service.ScrapingService;
import tech_prototype.service.RowService;

import java.util.*;

@Controller
@RequestMapping("")
public class HomeController {

    private static HomeController instance;

    private final ScrapingService scrapingService;
    private final IssuerService issuerService;
    private List<IssuerTemplate> allIssuers;

    private HomeController(ScrapingService scrapingService, IssuerService issuerService) {
        this.scrapingService = scrapingService;
        this.issuerService = issuerService;
    }
    public static synchronized HomeController getInstance(ScrapingService scrapingService, IssuerService issuerService) {
        if (instance == null) {
            instance = new HomeController(scrapingService, issuerService);
            instance.init();
        }
        return instance;
    }
    @PostConstruct
    private void init() {
        this.allIssuers = issuerService.readAll();
    }
    @GetMapping
    public String index(Model model) {
        setupModelAttributes(model, "ADIN", "1_month");
        return "master-template";
    }

////        scrapingService.scrape();

    @GetMapping("/search")
    public String getSearch() {
        return "redirect:/";
    }

    @PostMapping("/search")
    public String search(@RequestParam(required = false) String issuer, Model model) {
        if (issuer == null || issuer.isEmpty()) {
            issuer = "ADIN";
        }
        setupModelAttributes(model, issuer, "1_month");
        return "master-template";
    }
    private void setupModelAttributes(Model model, String issuer, String checkedRadio) {
        model.addAttribute("issuer", issuer);
        model.addAttribute("issuers", allIssuers);
        model.addAttribute("checkedRadio", checkedRadio);
        model.addAttribute("bodyContent", "issuer");
    }
}
