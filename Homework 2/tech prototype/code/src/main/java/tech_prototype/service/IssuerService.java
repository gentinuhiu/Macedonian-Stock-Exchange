package tech_prototype.service;


import org.springframework.stereotype.Service;
import tech_prototype.model.Issuer;
import tech_prototype.repository.IssuerRepository;

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
}


