package tech_prototype.service;

import org.springframework.stereotype.Service;
import tech_prototype.model.Row;
import tech_prototype.repository.RowRepository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RowService {
    private final RowRepository rowRepository;

    public RowService(RowRepository rowRepository) {
        this.rowRepository = rowRepository;
    }
    public List<Row> findAll() {
        return rowRepository.findAll();
    }
    public List<Row> sortRowsByDateStream(List<Row> rows) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        return rows.stream()
                .sorted((r1, r2) -> {
                    try {
                        Date date1 = dateFormat.parse(r1.getDate());
                        Date date2 = dateFormat.parse(r2.getDate());
                        return date2.compareTo(date1);
                    } catch (ParseException e) {
                        throw new RuntimeException("Invalid date format", e);
                    }
                })
                .collect(Collectors.toList());
    }

}
