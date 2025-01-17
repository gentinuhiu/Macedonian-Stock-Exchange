package microservice.fundamentalpredictionmicroservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/fundamentalAnalysis")
public class FundamentalPredictionController {

    @GetMapping("/{issuer}")
    public ResponseEntity<String> analyse(@PathVariable String issuer) {
        try {
            String result = executeAnalysisScript(issuer);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    private String executeAnalysisScript(String issuer) throws IOException, InterruptedException {
        String pythonScriptPath = "python";
        String scriptPath = "fundamental_analysis.py";

        System.out.println("Fundamental Analysis for Issuer: " + issuer);

        ProcessBuilder processBuilder = new ProcessBuilder(pythonScriptPath, scriptPath, issuer);
        processBuilder.directory(new File("C:/Users/genti/Desktop/SDA/tech_prototype/src/main/java/tech_prototype/exe"));
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));
        StringBuilder output = new StringBuilder();
        String line;
        boolean ignore = true;

        while ((line = reader.readLine()) != null) {
            if (line.equals("EXCHANGE")) {
                ignore = false;
                continue;
            }
            if (ignore) {
                continue;
            }
            output.append(line);
        }

        // Capture error output
        BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream(), StandardCharsets.UTF_8));
        StringBuilder errorOutput = new StringBuilder();
        while ((line = errorReader.readLine()) != null) {
            errorOutput.append(line);
        }

        if (process.waitFor() != 0) {
            throw new RuntimeException("Python script error: " + errorOutput.toString());
        }

        return output.toString();
    }
}
