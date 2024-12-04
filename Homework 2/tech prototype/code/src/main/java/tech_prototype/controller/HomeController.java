package tech_prototype.controller;

import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.springframework.stereotype.*;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import tech_prototype.model.Issuer;
import tech_prototype.model.Row;
import tech_prototype.model.RowTemplate;
import tech_prototype.service.IssuerService;
import tech_prototype.service.PythonService;
import tech_prototype.service.RowService;

import javax.swing.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("")
public class HomeController {
    private final PythonService pythonService;
    private final IssuerService issuerService;
    private final RowService rowService;
    public HomeController(PythonService pythonService, IssuerService issuerService, RowService rowService) {
        this.pythonService = pythonService;
        this.issuerService = issuerService;
        this.rowService = rowService;
    }
    @GetMapping
    public String index(Model model){

        try {
            pythonService.scrape();
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }

        Issuer issuer = issuerService.findByName("ADIN");
        List<Row> allRows = rowService.sortRowsByDateStream(issuer.getRows());
        List<Row> issuerRows = allRows.subList(0, 10);
        List<RowTemplate> issuerRowTemplates = new ArrayList<>();

        issuerRows.forEach(ir ->
                issuerRowTemplates.add(new RowTemplate(ir))
        );
        model.addAttribute("issuer", "ADIN");
        model.addAttribute("issuerRows", issuerRowTemplates);
        model.addAttribute("issuers", issuerService.findAll());
        model.addAttribute("startPage", 1);
        model.addAttribute("endPage", 10);
        model.addAttribute("actualPage", 1);
        model.addAttribute("lowerBound", false);
        model.addAttribute("upperBound", true);

        StringBuilder sb = new StringBuilder();
        Issuer tmpIssuer = issuerService.findByName(issuerService.findAll().get(3).getName());
        sb.append(tmpIssuer.getName() + " " + tmpIssuer.getRows().get(0).getAvgPrice());
        sb.append("     |     ");

        tmpIssuer = issuerService.findByName(issuerService.findAll().get(30).getName());
        sb.append(tmpIssuer.getName() + " " + tmpIssuer.getRows().get(0).getAvgPrice());
        sb.append("     |     ");

        tmpIssuer = issuerService.findByName(issuerService.findAll().get(40).getName());
        sb.append(tmpIssuer.getName() + " " + tmpIssuer.getRows().get(0).getAvgPrice());
        sb.append("     |     ");

        tmpIssuer = issuerService.findByName(issuerService.findAll().get(55).getName());
        sb.append(tmpIssuer.getName() + " " + tmpIssuer.getRows().get(0).getAvgPrice());
        sb.append("     |     ");

        tmpIssuer = issuerService.findByName(issuerService.findAll().get(70).getName());
        sb.append(tmpIssuer.getName() + " " + tmpIssuer.getRows().get(0).getAvgPrice());
        sb.append("     |     ");

        tmpIssuer = issuerService.findByName(issuerService.findAll().get(64).getName());
        sb.append(tmpIssuer.getName() + " " + tmpIssuer.getRows().get(0).getAvgPrice());
        sb.append("     |     ");

        tmpIssuer = issuerService.findByName(issuerService.findAll().get(101).getName());
        sb.append(tmpIssuer.getName() + " " + tmpIssuer.getRows().get(0).getAvgPrice());
        sb.append("     |     ");

        tmpIssuer = issuerService.findByName(issuerService.findAll().get(75).getName());
        sb.append(tmpIssuer.getName() + " " + tmpIssuer.getRows().get(0).getAvgPrice());
        sb.append("     |     ");

        tmpIssuer = issuerService.findByName(issuerService.findAll().get(100).getName());
        sb.append(tmpIssuer.getName() + " " + tmpIssuer.getRows().get(0).getAvgPrice());
        sb.append("     |     ");

        tmpIssuer = issuerService.findByName(issuerService.findAll().get(3).getName());
        sb.append(tmpIssuer.getName() + " " + tmpIssuer.getRows().get(0).getAvgPrice());
        sb.append("     |     ");

        tmpIssuer = issuerService.findByName(issuerService.findAll().get(30).getName());
        sb.append(tmpIssuer.getName() + " " + tmpIssuer.getRows().get(0).getAvgPrice());
        sb.append("     |     ");

        tmpIssuer = issuerService.findByName(issuerService.findAll().get(40).getName());
        sb.append(tmpIssuer.getName() + " " + tmpIssuer.getRows().get(0).getAvgPrice());
        sb.append("     |     ");

        tmpIssuer = issuerService.findByName(issuerService.findAll().get(55).getName());
        sb.append(tmpIssuer.getName() + " " + tmpIssuer.getRows().get(0).getAvgPrice());
        sb.append("     |     ");

        tmpIssuer = issuerService.findByName(issuerService.findAll().get(70).getName());
        sb.append(tmpIssuer.getName() + " " + tmpIssuer.getRows().get(0).getAvgPrice());
        sb.append("     |     ");

        tmpIssuer = issuerService.findByName(issuerService.findAll().get(64).getName());
        sb.append(tmpIssuer.getName() + " " + tmpIssuer.getRows().get(0).getAvgPrice());
        sb.append("     |     ");

        tmpIssuer = issuerService.findByName(issuerService.findAll().get(101).getName());
        sb.append(tmpIssuer.getName() + " " + tmpIssuer.getRows().get(0).getAvgPrice());
        sb.append("     |     ");

        tmpIssuer = issuerService.findByName(issuerService.findAll().get(75).getName());
        sb.append(tmpIssuer.getName() + " " + tmpIssuer.getRows().get(0).getAvgPrice());
        sb.append("     |     ");

        tmpIssuer = issuerService.findByName(issuerService.findAll().get(100).getName());
        sb.append(tmpIssuer.getName() + " " + tmpIssuer.getRows().get(0).getAvgPrice());
        sb.append("     |     ");
        model.addAttribute("movingText", sb.toString());

        List<String> dates = new ArrayList<>();
        List<Double> avgPrices = new ArrayList<>();

        for(Row row : issuerRows){
            dates.add(row.getDate());
            avgPrices.add(row.getAvgPrice());
        }
        dates = formatDates(dates);
        Collections.reverse(dates);
        Collections.reverse(avgPrices);
        model.addAttribute("datesChart", dates.toArray());
        model.addAttribute("avgPricesChart", avgPrices.toArray());

        List<String> periodDates = new ArrayList<>();
        List<Double> periodAvgPrices = new ArrayList<>();

        int coefficient = getDateCoefficient(allRows.get(0).getDate());
        for(Row row: allRows){
            int rowCoefficient = getDateCoefficient(row.getDate());
            if(rowCoefficient < coefficient - 30){
                break;
            }

            periodDates.add(row.getDate());
            periodAvgPrices.add(row.getAvgPrice());
        }
        Collections.reverse(periodDates);
        Collections.reverse(periodAvgPrices);
        //model.addAttribute("graph", "1_month");
        model.addAttribute("periodDates", periodDates.toArray());
        model.addAttribute("periodAvgPrices", periodAvgPrices.toArray());
        model.addAttribute("checkedRadio", "1_month");

        model.addAttribute("bodyContent", "issuer");
        return "master-template";
    }
    @GetMapping("/paginate")
    public String paginate(@RequestParam int page,
                           @RequestParam String timePeriod,
                           @RequestParam String issuer,
                           @RequestParam int startPage,
                           @RequestParam int endPage,
                           Model model) {

        Issuer issuerObj = issuerService.findByName(issuer);
        List<Row> rows = rowService.sortRowsByDateStream(issuerObj.getRows());
        List<Row> allRows = new ArrayList<>(rows);

        boolean lowerBound = !(startPage == 1);
        boolean upperBound = true;
        if(!(endPage * 10 > rows.size())){
            int diff = endPage - startPage;
            endPage += (10 - diff) - 1;

        }

        if(endPage * 10 > rows.size()){
            endPage = startPage + (rows.size() / 10) % 10 - 1;
            upperBound = false;
        }
        else{
            int diff = endPage - startPage;
            endPage += (10 - diff) - 1;
        }
//        boolean upperBound = endPage < rows.size() / 10;
//


        List<Row> issuerRows = rows.subList((page - 1) * 10, page * 10);
        List<RowTemplate> issuerRowTemplates = new ArrayList<>();

        issuerRows.forEach(ir ->
                issuerRowTemplates.add(new RowTemplate(ir))
        );

        model.addAttribute("issuer", issuer);
        model.addAttribute("issuerRows", issuerRowTemplates);
        model.addAttribute("issuers", issuerService.findAll());
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("actualPage", page);
        model.addAttribute("lowerBound", lowerBound);
        model.addAttribute("upperBound", upperBound);
        model.addAttribute("checkedRadio", timePeriod);
        StringBuilder sb = new StringBuilder();
        Issuer tmpIssuer = issuerService.findByName(issuerService.findAll().get(3).getName());
        sb.append(tmpIssuer.getName() + " " + tmpIssuer.getRows().get(0).getAvgPrice());
        sb.append("     |     ");

        tmpIssuer = issuerService.findByName(issuerService.findAll().get(30).getName());
        sb.append(tmpIssuer.getName() + " " + tmpIssuer.getRows().get(0).getAvgPrice());
        sb.append("     |     ");

        tmpIssuer = issuerService.findByName(issuerService.findAll().get(40).getName());
        sb.append(tmpIssuer.getName() + " " + tmpIssuer.getRows().get(0).getAvgPrice());
        sb.append("     |     ");

        tmpIssuer = issuerService.findByName(issuerService.findAll().get(55).getName());
        sb.append(tmpIssuer.getName() + " " + tmpIssuer.getRows().get(0).getAvgPrice());
        sb.append("     |     ");

        tmpIssuer = issuerService.findByName(issuerService.findAll().get(70).getName());
        sb.append(tmpIssuer.getName() + " " + tmpIssuer.getRows().get(0).getAvgPrice());
        sb.append("     |     ");

        tmpIssuer = issuerService.findByName(issuerService.findAll().get(64).getName());
        sb.append(tmpIssuer.getName() + " " + tmpIssuer.getRows().get(0).getAvgPrice());
        sb.append("     |     ");

        tmpIssuer = issuerService.findByName(issuerService.findAll().get(101).getName());
        sb.append(tmpIssuer.getName() + " " + tmpIssuer.getRows().get(0).getAvgPrice());
        sb.append("     |     ");

        tmpIssuer = issuerService.findByName(issuerService.findAll().get(75).getName());
        sb.append(tmpIssuer.getName() + " " + tmpIssuer.getRows().get(0).getAvgPrice());
        sb.append("     |     ");

        tmpIssuer = issuerService.findByName(issuerService.findAll().get(100).getName());
        sb.append(tmpIssuer.getName() + " " + tmpIssuer.getRows().get(0).getAvgPrice());
        sb.append("     |     ");
        tmpIssuer = issuerService.findByName(issuerService.findAll().get(3).getName());
        sb.append(tmpIssuer.getName() + " " + tmpIssuer.getRows().get(0).getAvgPrice());
        sb.append("     |     ");

        tmpIssuer = issuerService.findByName(issuerService.findAll().get(30).getName());
        sb.append(tmpIssuer.getName() + " " + tmpIssuer.getRows().get(0).getAvgPrice());
        sb.append("     |     ");

        tmpIssuer = issuerService.findByName(issuerService.findAll().get(40).getName());
        sb.append(tmpIssuer.getName() + " " + tmpIssuer.getRows().get(0).getAvgPrice());
        sb.append("     |     ");

        tmpIssuer = issuerService.findByName(issuerService.findAll().get(55).getName());
        sb.append(tmpIssuer.getName() + " " + tmpIssuer.getRows().get(0).getAvgPrice());
        sb.append("     |     ");

        tmpIssuer = issuerService.findByName(issuerService.findAll().get(70).getName());
        sb.append(tmpIssuer.getName() + " " + tmpIssuer.getRows().get(0).getAvgPrice());
        sb.append("     |     ");

        tmpIssuer = issuerService.findByName(issuerService.findAll().get(64).getName());
        sb.append(tmpIssuer.getName() + " " + tmpIssuer.getRows().get(0).getAvgPrice());
        sb.append("     |     ");

        tmpIssuer = issuerService.findByName(issuerService.findAll().get(101).getName());
        sb.append(tmpIssuer.getName() + " " + tmpIssuer.getRows().get(0).getAvgPrice());
        sb.append("     |     ");

        tmpIssuer = issuerService.findByName(issuerService.findAll().get(75).getName());
        sb.append(tmpIssuer.getName() + " " + tmpIssuer.getRows().get(0).getAvgPrice());
        sb.append("     |     ");

        tmpIssuer = issuerService.findByName(issuerService.findAll().get(100).getName());
        sb.append(tmpIssuer.getName() + " " + tmpIssuer.getRows().get(0).getAvgPrice());
        sb.append("     |     ");
        model.addAttribute("movingText", sb.toString());

        model.addAttribute("bodyContent", "issuer");

        List<String> dates = new ArrayList<>();
        List<Double> avgPrices = new ArrayList<>();

        for(Row row : issuerRows){
            dates.add(row.getDate());
            avgPrices.add(row.getAvgPrice());
        }
        dates = formatDates(dates);
        Collections.reverse(dates);
        Collections.reverse(avgPrices);

        // Add reversed lists to the model
        model.addAttribute("datesChart", dates.toArray());
        model.addAttribute("avgPricesChart", avgPrices.toArray());

        List<String> periodDates = new ArrayList<>();
        List<Double> periodAvgPrices = new ArrayList<>();

        int differenceCoefficient;
        if(timePeriod.equals("1_month")){
            differenceCoefficient = 30;
        }
        else if(timePeriod.equals("6_months")){
            differenceCoefficient = 30 * 6;
        }
        else if(timePeriod.equals("1_year")){
            differenceCoefficient = 365;
        }
        else if(timePeriod.equals("5_years")){
            differenceCoefficient = 365 * 5;
        }
        else{
            differenceCoefficient = 3650;
        }


        int coefficient = getDateCoefficient(allRows.get(0).getDate());
        for(Row row: allRows){
            int rowCoefficient = getDateCoefficient(row.getDate());
            if(rowCoefficient < coefficient - differenceCoefficient){
                break;
            }

            periodDates.add(row.getDate());
            periodAvgPrices.add(row.getAvgPrice());
        }
        Collections.reverse(periodDates);
        Collections.reverse(periodAvgPrices);
        model.addAttribute("graph", "1_month");
        model.addAttribute("periodDates", periodDates.toArray());
        model.addAttribute("periodAvgPrices", periodAvgPrices.toArray());

        return "master-template";
    }
    @PostMapping("/search")
    public String search(@RequestParam Long issuer, Model model) {
        Optional<Issuer> issOpt = issuerService.findById(issuer);
        Issuer iss = Optional.of(issOpt.get()).orElse(new Issuer());
        List<Row> allRows = rowService.sortRowsByDateStream(iss.getRows());
        List<Row> issuerRows = allRows.subList(0, 10);

        List<RowTemplate> issuerRowTemplates = new ArrayList<>();

        issuerRows.forEach(ir ->
                issuerRowTemplates.add(new RowTemplate(ir))
        );
        model.addAttribute("issuer", iss.getName());
        model.addAttribute("issuerRows", issuerRowTemplates);
        model.addAttribute("issuers", issuerService.findAll());
        model.addAttribute("startPage", 1);
        model.addAttribute("endPage", 10);
        model.addAttribute("actualPage", 1);
        model.addAttribute("lowerBound", false);
        model.addAttribute("upperBound", true);
        model.addAttribute("checkedRadio", "1_month");

        StringBuilder sb = new StringBuilder();
        Issuer tmpIssuer = issuerService.findByName(issuerService.findAll().get(3).getName());
        sb.append(tmpIssuer.getName() + " " + tmpIssuer.getRows().get(0).getAvgPrice());
        sb.append("     |     ");

        tmpIssuer = issuerService.findByName(issuerService.findAll().get(30).getName());
        sb.append(tmpIssuer.getName() + " " + tmpIssuer.getRows().get(0).getAvgPrice());
        sb.append("     |     ");

        tmpIssuer = issuerService.findByName(issuerService.findAll().get(40).getName());
        sb.append(tmpIssuer.getName() + " " + tmpIssuer.getRows().get(0).getAvgPrice());
        sb.append("     |     ");

        tmpIssuer = issuerService.findByName(issuerService.findAll().get(55).getName());
        sb.append(tmpIssuer.getName() + " " + tmpIssuer.getRows().get(0).getAvgPrice());
        sb.append("     |     ");

        tmpIssuer = issuerService.findByName(issuerService.findAll().get(70).getName());
        sb.append(tmpIssuer.getName() + " " + tmpIssuer.getRows().get(0).getAvgPrice());
        sb.append("     |     ");

        tmpIssuer = issuerService.findByName(issuerService.findAll().get(64).getName());
        sb.append(tmpIssuer.getName() + " " + tmpIssuer.getRows().get(0).getAvgPrice());
        sb.append("     |     ");

        tmpIssuer = issuerService.findByName(issuerService.findAll().get(101).getName());
        sb.append(tmpIssuer.getName() + " " + tmpIssuer.getRows().get(0).getAvgPrice());
        sb.append("     |     ");

        tmpIssuer = issuerService.findByName(issuerService.findAll().get(75).getName());
        sb.append(tmpIssuer.getName() + " " + tmpIssuer.getRows().get(0).getAvgPrice());
        sb.append("     |     ");

        tmpIssuer = issuerService.findByName(issuerService.findAll().get(100).getName());
        sb.append(tmpIssuer.getName() + " " + tmpIssuer.getRows().get(0).getAvgPrice());
        sb.append("     |     ");
        tmpIssuer = issuerService.findByName(issuerService.findAll().get(3).getName());
        sb.append(tmpIssuer.getName() + " " + tmpIssuer.getRows().get(0).getAvgPrice());
        sb.append("     |     ");

        tmpIssuer = issuerService.findByName(issuerService.findAll().get(30).getName());
        sb.append(tmpIssuer.getName() + " " + tmpIssuer.getRows().get(0).getAvgPrice());
        sb.append("     |     ");

        tmpIssuer = issuerService.findByName(issuerService.findAll().get(40).getName());
        sb.append(tmpIssuer.getName() + " " + tmpIssuer.getRows().get(0).getAvgPrice());
        sb.append("     |     ");

        tmpIssuer = issuerService.findByName(issuerService.findAll().get(55).getName());
        sb.append(tmpIssuer.getName() + " " + tmpIssuer.getRows().get(0).getAvgPrice());
        sb.append("     |     ");

        tmpIssuer = issuerService.findByName(issuerService.findAll().get(70).getName());
        sb.append(tmpIssuer.getName() + " " + tmpIssuer.getRows().get(0).getAvgPrice());
        sb.append("     |     ");

        tmpIssuer = issuerService.findByName(issuerService.findAll().get(64).getName());
        sb.append(tmpIssuer.getName() + " " + tmpIssuer.getRows().get(0).getAvgPrice());
        sb.append("     |     ");

        tmpIssuer = issuerService.findByName(issuerService.findAll().get(101).getName());
        sb.append(tmpIssuer.getName() + " " + tmpIssuer.getRows().get(0).getAvgPrice());
        sb.append("     |     ");

        tmpIssuer = issuerService.findByName(issuerService.findAll().get(75).getName());
        sb.append(tmpIssuer.getName() + " " + tmpIssuer.getRows().get(0).getAvgPrice());
        sb.append("     |     ");

        tmpIssuer = issuerService.findByName(issuerService.findAll().get(100).getName());
        sb.append(tmpIssuer.getName() + " " + tmpIssuer.getRows().get(0).getAvgPrice());
        sb.append("     |     ");
        model.addAttribute("movingText", sb.toString());

        model.addAttribute("bodyContent", "issuer");

        List<String> dates = new ArrayList<>();
        List<Double> avgPrices = new ArrayList<>();

        for(Row row : issuerRows){
            dates.add(row.getDate());
            avgPrices.add(row.getAvgPrice());
        }
        dates = formatDates(dates);
        Collections.reverse(dates);
        Collections.reverse(avgPrices);

        model.addAttribute("datesChart", dates);
        model.addAttribute("avgPricesChart", avgPrices);

        List<String> periodDates = new ArrayList<>();
        List<Double> periodAvgPrices = new ArrayList<>();

        int coefficient = getDateCoefficient(allRows.get(0).getDate());
        for(Row row: allRows){
            int rowCoefficient = getDateCoefficient(row.getDate());
            if(rowCoefficient < coefficient - 30){
                break;
            }

            periodDates.add(row.getDate());
            periodAvgPrices.add(row.getAvgPrice());
        }
        Collections.reverse(periodDates);
        Collections.reverse(periodAvgPrices);
        //model.addAttribute("graph", "1_month");
        model.addAttribute("periodDates", periodDates.toArray());
        model.addAttribute("periodAvgPrices", periodAvgPrices.toArray());

        return "master-template";
    }
    private List<String> formatDates(List<String> dates) {
        for(int i = 0; i < dates.size(); i++){
            String s = dates.get(i);
            s = s.substring(0, s.length() - 5);
            dates.set(i, s);
        }
        return dates;
    }
    @GetMapping("/graph")
    public String graph(@RequestParam String timePeriod,
                           @RequestParam int page,
                           @RequestParam String issuer,
                           @RequestParam int startPage,
                           @RequestParam int endPage,
                           Model model) {
        // Handle the logic for filtering and pagination here

        Issuer issuerObj = issuerService.findByName(issuer);
        List<Row> rows = rowService.sortRowsByDateStream(issuerObj.getRows());
        List<Row> allRows = new ArrayList<>(rows);

        boolean lowerBound = !(startPage == 1);
        boolean upperBound = true;
        if(!(endPage * 10 > rows.size())){
            int diff = endPage - startPage;
            endPage += (10 - diff) - 1;

        }

        if(endPage * 10 > rows.size()){
            endPage = startPage + (rows.size() / 10) % 10 - 1;
            upperBound = false;
        }
        else{
            int diff = endPage - startPage;
            endPage += (10 - diff) - 1;
        }
//        boolean upperBound = endPage < rows.size() / 10;
//
        List<Row> issuerRows = rows.subList((page - 1) * 10, page * 10);
        List<RowTemplate> issuerRowTemplates = new ArrayList<>();

        issuerRows.forEach(ir ->
                issuerRowTemplates.add(new RowTemplate(ir))
        );
        model.addAttribute("issuer", issuer);
        model.addAttribute("issuerRows", issuerRowTemplates);
        model.addAttribute("issuers", issuerService.findAll());
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("actualPage", page);
        model.addAttribute("lowerBound", lowerBound);
        model.addAttribute("upperBound", upperBound);

        StringBuilder sb = new StringBuilder();
        Issuer tmpIssuer = issuerService.findByName(issuerService.findAll().get(3).getName());
        sb.append(tmpIssuer.getName() + " " + tmpIssuer.getRows().get(0).getAvgPrice());
        sb.append("     |     ");

        tmpIssuer = issuerService.findByName(issuerService.findAll().get(30).getName());
        sb.append(tmpIssuer.getName() + " " + tmpIssuer.getRows().get(0).getAvgPrice());
        sb.append("     |     ");

        tmpIssuer = issuerService.findByName(issuerService.findAll().get(40).getName());
        sb.append(tmpIssuer.getName() + " " + tmpIssuer.getRows().get(0).getAvgPrice());
        sb.append("     |     ");

        tmpIssuer = issuerService.findByName(issuerService.findAll().get(55).getName());
        sb.append(tmpIssuer.getName() + " " + tmpIssuer.getRows().get(0).getAvgPrice());
        sb.append("     |     ");

        tmpIssuer = issuerService.findByName(issuerService.findAll().get(70).getName());
        sb.append(tmpIssuer.getName() + " " + tmpIssuer.getRows().get(0).getAvgPrice());
        sb.append("     |     ");

        tmpIssuer = issuerService.findByName(issuerService.findAll().get(64).getName());
        sb.append(tmpIssuer.getName() + " " + tmpIssuer.getRows().get(0).getAvgPrice());
        sb.append("     |     ");

        tmpIssuer = issuerService.findByName(issuerService.findAll().get(101).getName());
        sb.append(tmpIssuer.getName() + " " + tmpIssuer.getRows().get(0).getAvgPrice());
        sb.append("     |     ");

        tmpIssuer = issuerService.findByName(issuerService.findAll().get(75).getName());
        sb.append(tmpIssuer.getName() + " " + tmpIssuer.getRows().get(0).getAvgPrice());
        sb.append("     |     ");

        tmpIssuer = issuerService.findByName(issuerService.findAll().get(100).getName());
        sb.append(tmpIssuer.getName() + " " + tmpIssuer.getRows().get(0).getAvgPrice());
        sb.append("     |     ");
        tmpIssuer = issuerService.findByName(issuerService.findAll().get(3).getName());
        sb.append(tmpIssuer.getName() + " " + tmpIssuer.getRows().get(0).getAvgPrice());
        sb.append("     |     ");

        tmpIssuer = issuerService.findByName(issuerService.findAll().get(30).getName());
        sb.append(tmpIssuer.getName() + " " + tmpIssuer.getRows().get(0).getAvgPrice());
        sb.append("     |     ");

        tmpIssuer = issuerService.findByName(issuerService.findAll().get(40).getName());
        sb.append(tmpIssuer.getName() + " " + tmpIssuer.getRows().get(0).getAvgPrice());
        sb.append("     |     ");

        tmpIssuer = issuerService.findByName(issuerService.findAll().get(55).getName());
        sb.append(tmpIssuer.getName() + " " + tmpIssuer.getRows().get(0).getAvgPrice());
        sb.append("     |     ");

        tmpIssuer = issuerService.findByName(issuerService.findAll().get(70).getName());
        sb.append(tmpIssuer.getName() + " " + tmpIssuer.getRows().get(0).getAvgPrice());
        sb.append("     |     ");

        tmpIssuer = issuerService.findByName(issuerService.findAll().get(64).getName());
        sb.append(tmpIssuer.getName() + " " + tmpIssuer.getRows().get(0).getAvgPrice());
        sb.append("     |     ");

        tmpIssuer = issuerService.findByName(issuerService.findAll().get(101).getName());
        sb.append(tmpIssuer.getName() + " " + tmpIssuer.getRows().get(0).getAvgPrice());
        sb.append("     |     ");

        tmpIssuer = issuerService.findByName(issuerService.findAll().get(75).getName());
        sb.append(tmpIssuer.getName() + " " + tmpIssuer.getRows().get(0).getAvgPrice());
        sb.append("     |     ");

        tmpIssuer = issuerService.findByName(issuerService.findAll().get(100).getName());
        sb.append(tmpIssuer.getName() + " " + tmpIssuer.getRows().get(0).getAvgPrice());
        sb.append("     |     ");
        model.addAttribute("movingText", sb.toString());
        model.addAttribute("bodyContent", "issuer");

        List<String> dates = new ArrayList<>();
        List<Double> avgPrices = new ArrayList<>();

        for(Row row : issuerRows){
            dates.add(row.getDate());
            avgPrices.add(row.getAvgPrice());
        }
        dates = formatDates(dates);
        Collections.reverse(dates);
        Collections.reverse(avgPrices);

        // Add reversed lists to the model
        model.addAttribute("datesChart", dates.toArray());
        model.addAttribute("avgPricesChart", avgPrices.toArray());

        List<String> periodDates = new ArrayList<>();
        List<Double> periodAvgPrices = new ArrayList<>();


        int differenceCoefficient;
        if(timePeriod.equals("1_month")){
            differenceCoefficient = 30;
        }
        else if(timePeriod.equals("6_months")){
            differenceCoefficient = 30 * 6;
        }
        else if(timePeriod.equals("1_year")){
            differenceCoefficient = 365;
        }
        else if(timePeriod.equals("5_years")){
            differenceCoefficient = 365 * 5;
        }
        else{
            differenceCoefficient = 3650;
        }

        int coefficient = getDateCoefficient(allRows.get(0).getDate());
        for(Row row: allRows){
            int rowCoefficient = getDateCoefficient(row.getDate());
            if(rowCoefficient < coefficient - differenceCoefficient){
                break;
            }

            periodDates.add(row.getDate());
            periodAvgPrices.add(row.getAvgPrice());
        }
        Collections.reverse(periodDates);
        Collections.reverse(periodAvgPrices);
        //model.addAttribute("graph", "1_month");
        model.addAttribute("periodDates", periodDates.toArray());
        model.addAttribute("periodAvgPrices", periodAvgPrices.toArray());
        model.addAttribute("checkedRadio", timePeriod);

        return "master-template";
    }
    private int getDateCoefficient(String date){
        String parts[] = date.split("/");
        return Integer.parseInt(parts[0]) * 30 + Integer.parseInt(parts[1]) + Integer.parseInt(parts[2]) * 365;
    }
}
