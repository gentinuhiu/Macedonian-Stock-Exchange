package tech_prototype.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tech_prototype.model.Row;

public interface RowRepository extends JpaRepository<Row, Long> {
}
