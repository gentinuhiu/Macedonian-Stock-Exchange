package tech_prototype.service;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import tech_prototype.model.Issuer;
import tech_prototype.model.Row;
import tech_prototype.repository.IssuerRepository;
import tech_prototype.repository.RowRepository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class PythonService {
    private final IssuerService issuerService;
    private final RowRepository rowRepository;
    private final RowService rowService;

    public PythonService(IssuerService issuerService, RowRepository rowRepository, RowService rowService) {
        this.issuerService = issuerService;
        this.rowRepository = rowRepository;
        this.rowService = rowService;
    }
    @Async
    public void scrape() throws IOException, InterruptedException {
        String scriptPath = "scraping.py";

        ProcessBuilder processBuilder = new ProcessBuilder("python", scriptPath);
        processBuilder.directory(new File("src/main/java/tech_prototype/exe"));
        processBuilder.redirectErrorStream(true);
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        Process process = processBuilder.start();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                String[] parts = line.split(" ");
                if(parts.length == 2){
                    String code = parts[0];

                    executorService.submit(() -> {
                            readCsv("src/main/java/tech_prototype/exe/" + code + ".csv", code);
                    });
                }
                else{
                    System.out.println("Error splitting at " + line);
                }
            }
        }

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("Python script execution failed with exit code: " + exitCode);
        }
        executorService.shutdown();
    }
    public void readCsv(String filePath, String code) {

        try (CSVReader csvReader = new CSVReader(new FileReader(filePath))) {
            List<String[]> rows = csvReader.readAll();
            System.out.println("Reading: " + filePath);

            Issuer issuer = issuerService.getOrCreate(code);
            String lastUpdate = (rows.get(rows.size() - 1)[0]);

            if(issuerService.upToDate(issuer, lastUpdate))
                return;
            System.out.println("Updating " + code);

            List<Row> rowObjList = new ArrayList<>();
            for (int i = 1; i < rows.size(); i++) {
                String[] row = rows.get(i);

                System.out.println(row[0]);

                Row rowObj = new Row();
                rowRepository.save(rowObj);

                if(checkInput(row[0]))
                    rowObj.setDate(row[0]);

                if(checkInput(row[1])) {
                    rowObj.setLastTradePrice(formatDoubleInput(row[1]));
                }

                if(checkInput(row[2])) {
                    rowObj.setMax(formatDoubleInput(row[2]));
                }

                if(checkInput(row[3])) {
                    rowObj.setMin(formatDoubleInput(row[3]));
                }

                if(checkInput(row[4])) {
                    rowObj.setAvgPrice(formatDoubleInput(row[4]));
                }

                if(checkInput(row[5])) {
                    rowObj.setChange(formatDoubleInput(row[5]));
                }

                if(checkInput(row[6])) {
                    rowObj.setVolume(formatIntegerInput(row[6]));
                }

                if(checkInput(row[7])) {
                    rowObj.setTurnoverInBest(formatDoubleInput(row[7]));
                }

                if(checkInput(row[8])) {
                    rowObj.setTotalTurnover(formatDoubleInput(row[8]));
                }

                rowObj.setIssuer(issuer);
                rowRepository.save(rowObj);
                rowObjList.add(rowObj);
            }

            issuer.setRows(rowObjList);
            issuerService.save(issuer);

        } catch (IOException | CsvException e) {
            System.err.println("Error reading the CSV file: " + e.getMessage());
        }
    }
    private Double formatDoubleInput(String text){
        return Double.parseDouble(text.replaceAll(",", ""));
    }
    private Integer formatIntegerInput(String text){
        return Integer.parseInt(text.replaceAll(",", ""));
    }
    private boolean checkInput(String text){
        return text != null && !text.isEmpty();
    }
}
