package microservice.lstmpredictionmicroservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/predict")
public class LSTMPredictionController {

    @GetMapping("/{companyCode}")
    public ResponseEntity<String> predict(@PathVariable String companyCode) {
        try {
            String result = executePredictionScript(companyCode);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    private String executePredictionScript(String issuer) throws IOException, InterruptedException {
        String pythonScriptPath = "python";
        String scriptPath = "lstm_prediction.py";

        System.out.println("LSTM Prediction for Issuer: " + issuer);

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

