package tech_prototype.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tech_prototype.model.Row;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RowRepository extends JpaRepository<Row, Long> {
}
