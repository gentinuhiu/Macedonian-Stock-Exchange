package tech_prototype.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tech_prototype.model.Issuer;

public interface IssuerRepository extends JpaRepository<Issuer, Long> {
    Issuer findByName(String name);
}

