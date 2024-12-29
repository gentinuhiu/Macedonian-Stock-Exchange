package tech_prototype.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tech_prototype.model.Issuer;
import tech_prototype.model.Row;
import tech_prototype.model.RowTemplate;
import tech_prototype.service.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
public class DataController {
    private final RowService rowService;
    private final IssuerService issuerService;
    private final TechnicalAnalysisService technicalAnalysisService;
    private final FundamentalAnalysisService fundamentalAnalysisService;
    private final LSTMPredictionService lstmPredictionService;

    private Issuer issuerObj;
    private String presentIssuer;
    private List<Row> allRows;
    private List<Row> issuerRows;

    private List<Double> prices;
    private List<String> dates;
    private List<Double> buy;
    private List<Double> sell;

    public DataController(RowService rowService, IssuerService issuerService,
                          TechnicalAnalysisService technicalAnalysisService,
                          FundamentalAnalysisService fundamentalAnalysisService,
                          LSTMPredictionService lstmPredictionService) {
        this.rowService = rowService;
        this.issuerService = issuerService;
        this.technicalAnalysisService = technicalAnalysisService;
        this.fundamentalAnalysisService = fundamentalAnalysisService;
        this.lstmPredictionService = lstmPredictionService;

        this.issuerObj = issuerService.findByName("ADIN");

        if(issuerObj == null)
            return;

        this.presentIssuer = "ADIN";
        this.allRows = rowService.sortRowsByDateStream(issuerObj.getRows());
        this.issuerRows = allRows.subList(0, 10);
    }
    @GetMapping("/api/stock-table-data")
    public Map<String, Object> getStockTable(
            @RequestParam String issuer,
            @RequestParam int page) {

        if(!issuer.equals(presentIssuer)) {
            initializeData(issuer, page);
        }
        updateRows(page);

        List<RowTemplate> issuerRowTemplates = new ArrayList<>();
        issuerRows.forEach(ir ->
            issuerRowTemplates.add(new RowTemplate(ir))
        );

        return Map.of("rows", issuerRowTemplates);
    }

    @GetMapping("/api/stock-chart-data")
    public Map<String, Object> getStockData(
            @RequestParam String issuer,
            @RequestParam int page) {

        if(!issuer.equals(presentIssuer)) {
            initializeData(issuer, page);
        }
        updateRows(page);

        List<String> dates = new ArrayList<>();
        List<Double> avgPrices = new ArrayList<>();

        for(Row row : issuerRows){
            dates.add(row.getDate());
            avgPrices.add(row.getAvgPrice());
        }
        dates = formatDates(dates);
        Collections.reverse(dates);
        Collections.reverse(avgPrices);

        return Map.of(
                "dates", dates,
                "avgPrices", avgPrices
        );
    }

    @GetMapping("/api/stock-chart-total-data")
    public Map<String, Object> getStockTotalData(
            @RequestParam String issuer,
            @RequestParam String timePeriod,
            @RequestParam int page) {

        if(!issuer.equals(presentIssuer)) {
            initializeData(issuer, page);
        }
        updateRows(page);

        List<String> periodDates = new ArrayList<>();
        List<Double> periodAvgPrices = new ArrayList<>();

        int differenceCoefficient = calculateDifferenceCoefficient(timePeriod);

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

        return Map.of(
                "periodDates", periodDates,
                "periodAvgPrices", periodAvgPrices
        );
    }

    @GetMapping("/api/rows")
    public Map<String, Object> getPaginatedData(
            @RequestParam String issuer,
            @RequestParam int page){

        if(!issuer.equals(presentIssuer)) {
            initializeData(issuer, page);
        }
        updateRows(page);

        return Map.of(
                "rowsSize", allRows.size()
        );
    }

    @GetMapping("/api/technical-analysis")
    public Map<String, Object> getTechnicalAnalysis(
            @RequestParam String issuer,
            @RequestParam String period){

        if(prices == null || !issuer.equals(presentIssuer)){
            initializeTechnicalAnalysisData(issuer);
        }

        int differenceCoefficient = calculateDifferenceCoefficient(period); // kalkulira koeficient za day, week, month
        int coefficient = getDateCoefficient(dates.get(dates.size() - 1)); // go zema posledniot date
        int counter = dates.size(); // kalkulira od posledniot date do koeficientot i ja cuva goleminata za nizite
        for(int i = dates.size() - 1; i >= 0; i--){
            int rowCoefficient = getDateCoefficient(dates.get(i));
            if(rowCoefficient < coefficient - differenceCoefficient){
                break;
            }
            counter--;
        }

        return Map.of(
                "prices", prices.subList(counter, prices.size()).toArray(),
                "dates", dates.subList(counter, dates.size()).toArray(),
                "buy", buy.subList(counter, buy.size()).toArray(),
                "sell", sell.subList(counter, sell.size()).toArray()
        );
    }
    @GetMapping("/api/predict/{companyCode}")
    public Map<String, Object> predictStockPrice(@PathVariable String companyCode) throws IOException {
        String result = lstmPredictionService.predict(companyCode);

        if (result == null || result.trim().isEmpty()) {
            throw new RuntimeException("No output from Python script");
        }

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> response = mapper.readValue(result, Map.class);

        if (response.containsKey("error")) {
            throw new RuntimeException("Error from Python script: " + response.get("error"));
        }

        return response;
    }
    @GetMapping("/api/fundamentalAnalysis/{companyCode}")
    public Map<String, Object> fundamentalAnalysis(@PathVariable String companyCode) throws IOException {
        String result = fundamentalAnalysisService.analyse(companyCode);

        if (result == null || result.trim().isEmpty()) {
            throw new RuntimeException("Fundamental Analysis - No output from Python script");
        }

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> response = mapper.readValue(result, Map.class);

        if (response.containsKey("error")) {
            throw new RuntimeException("Fundamental Analysis - Error from Python script: " + response.get("error"));
        }

        return response;
    }
    private void initializeTechnicalAnalysisData(String issuer){
        String result = technicalAnalysisService.analyse(issuer);
        String[] parts = result.split("\\$");
        List<String> partsList = new ArrayList<>();

        for(int i = 0; i < parts.length; i++){
            partsList.add(parts[i]);
        }

        while(partsList.size() != 4)
            partsList.add("10");

        String[] pricesArr = partsList.get(0).split("#");
        String[] datesArr = partsList.get(1).split("#");
        String[] buyArr = partsList.get(2).split("#");
        String[] sellArr = partsList.get(3).split("#");

        prices = new ArrayList<>();
        dates = new ArrayList<>();
        buy = new ArrayList<>();
        sell = new ArrayList<>();

        int j = 0;
        int k = 0;
        for(int i = 0; i < pricesArr.length - 1; i++){
            if(pricesArr[i] == null)
                continue;
            prices.add(Double.parseDouble(pricesArr[i]));

            if(j < buyArr.length && !buyArr[j].isEmpty() && Double.parseDouble(buyArr[j]) == Double.parseDouble(pricesArr[i])){
                buy.add(Double.parseDouble(pricesArr[i]));
                j++;
            }
            else {
                buy.add(null);
            }

            if(k < sellArr.length && !sellArr[k].isEmpty() && Double.parseDouble(sellArr[k]) == Double.parseDouble(pricesArr[i])){
                sell.add(Double.parseDouble(pricesArr[i]));
                k++;
            }
            else {
                sell.add(null);
            }
        }
        for(int i = 0; i < datesArr.length - 1; i++){
            if(datesArr[i] == null)
                continue;

            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

            LocalDate date = LocalDate.parse(datesArr[i].split(" ")[0], inputFormatter);
            String formattedDate = date.format(outputFormatter);
            dates.add(formattedDate);
        }
    }
    private void initializeData(String issuer, int page){
        this.issuerObj = issuerService.findByName(issuer); // GET OR CREATE
        this.presentIssuer = issuer;
        this.allRows = rowService.sortRowsByDateStream(issuerObj.getRows());
    }
    public void updateRows(int page){
        this.issuerRows = allRows.subList((page - 1) * 10, page * 10);
    }
    private int calculateDifferenceCoefficient(String timePeriod) {
        if(timePeriod.equals("1_day"))
            return 1;
        if(timePeriod.equals("1_week"))
            return 7;
        if(timePeriod.equals("1_month"))
            return 30;
        if(timePeriod.equals("6_months"))
            return 30 * 6;
        if(timePeriod.equals("1_year"))
            return 365;
        if(timePeriod.equals("5_years"))
            return 365 * 5;
        return 3650;
    }
    private List<String> formatDates(List<String> dates) {
        for(int i = 0; i < dates.size(); i++){
            String s = dates.get(i);
            s = s.substring(0, s.length() - 5);
            dates.set(i, s);
        }
        return dates;
    }
    private int getDateCoefficient(String date){
        String parts[] = date.split("/");
        return Integer.parseInt(parts[0]) * 30 + Integer.parseInt(parts[1]) + Integer.parseInt(parts[2]) * 365;
    }
}
