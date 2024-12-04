package tech_prototype.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech_prototype.model.Row;
import tech_prototype.service.IssuerService;
import tech_prototype.service.PythonService;
import tech_prototype.service.RowService;

import java.io.File;
import java.util.List;

@RestController
public class ChartController {

    @GetMapping("/chart")
    public FileSystemResource getChart() {
        return new FileSystemResource(new File("chart.png"));
    }
}
