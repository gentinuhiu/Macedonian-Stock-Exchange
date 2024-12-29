package tech_prototype.service;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Service
public class FundamentalAnalysisService {

    public String analyse(String issuer) throws IOException {
        String pythonScriptPath = "python";
        String scriptPath = "fundamental_analysis.py";

        System.out.println("Fundamental Analysis for Issuer: " + issuer);

        ProcessBuilder processBuilder = new ProcessBuilder(pythonScriptPath, scriptPath, issuer);
        processBuilder.directory(new File("src/main/java/tech_prototype/exe"));
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));
        StringBuilder output = new StringBuilder();
        String line;
        boolean ignore = true;

        while ((line = reader.readLine()) != null) {

            if(line.equals("EXCHANGE")){
                ignore = false;
                continue;
            }
            if(ignore){
                continue;
            }

            output.append(line);
            //System.out.println(line);
        }

        // Capture error output
        BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream(), StandardCharsets.UTF_8));
        StringBuilder errorOutput = new StringBuilder();
        while ((line = errorReader.readLine()) != null) {
            errorOutput.append(line);
        }

        try {
            process.waitFor();
        } catch (InterruptedException e) {
            throw new RuntimeException("Fundamental Analysis - Python process interrupted", e);
        }

        if (process.exitValue() != 0) {
            throw new RuntimeException("Fundamental Analysis - Python script error: " + errorOutput.toString());
        }

        return output.toString();

    }
}
