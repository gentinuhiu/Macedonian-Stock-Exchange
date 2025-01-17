package tech_prototype.service;

import org.springframework.stereotype.Service;
import tech_prototype.model.Issuer;
import tech_prototype.model.IssuerTemplate;
import tech_prototype.repository.IssuerRepository;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class IssuerService {
    private final IssuerRepository issuerRepository;

    public IssuerService(IssuerRepository issuerRepository) {
        this.issuerRepository = issuerRepository;
    }

    public List<Issuer> findAll() {
        return issuerRepository.findAll();
    }

    public Issuer save(Issuer issuer) {
        return issuerRepository.save(issuer);
    }
    public Issuer findByName(String name) {
        return issuerRepository.findByName(name);
    }
    public Issuer getOrCreate(String code){
        List<Issuer> issuers = issuerRepository.findAll();
        Issuer issuer = issuers.stream().filter(i -> i.getName().equals(code)).findFirst().orElse(null);

        if(issuer == null){
            issuer = new Issuer(code);
            issuerRepository.save(issuer);
        }
        return issuer;
    }
    public boolean upToDate(Issuer issuer, String lastUpdate){
        return issuer.getRows().stream()
                .filter(r -> r.getDate().contains(lastUpdate))
                .findFirst().orElse(null) != null;
    }
    public Optional<Issuer> findById(Long id) {
        return issuerRepository.findById(id);
    }

    public List<IssuerTemplate> readAll() {
        List<IssuerTemplate> list = new ArrayList<>();

        File directory = new File("src/main/java/tech_prototype/exe/csv-updates");

        // Ensure the directory exists and is indeed a directory
        if (directory.exists() && directory.isDirectory()) {
            // List all files in the directory and filter by .txt extension
            File[] files = directory.listFiles((dir, name) -> name.toLowerCase().endsWith(".txt"));

            if (files != null && files.length > 0) {
                // Print out all the .txt file names
                for (File file : files) {
                    list.add(new IssuerTemplate(file.getName()));
                }
            } else {
                System.out.println("No .txt files found.");
            }
        } else {
            System.out.println("The specified path is not a directory or doesn't exist.");
        }
        return list;
    }
}


