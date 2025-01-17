package tech_prototype.service;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

@Service
public class TechnicalAnalysisService {
    public String analyse(String issuer) {
        StringBuilder output = new StringBuilder();

        try {
            String pythonScriptPath = "python";
            String scriptPath = "technical_analysis.py";
            System.out.println("Technical Analysis for Issuer: " + issuer);
            ProcessBuilder processBuilder = new ProcessBuilder(pythonScriptPath, scriptPath, issuer);
            processBuilder.directory(new File("src/main/java/tech_prototype/exe"));
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            boolean ignore = true;

            while ((line = reader.readLine()) != null) {

                if(line.equals("EXCHANGE")){
                    ignore = false;
                    continue;
                }
                if(ignore)
                    continue;
                output.append(line);
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                System.err.println("Technical Analysis - Python script execution failed!");
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return output.toString();
    }
}
