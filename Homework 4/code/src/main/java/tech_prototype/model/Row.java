package tech_prototype.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Row {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String date;
    private double lastTradePrice;
    private double max;
    private double min;
    private double avgPrice;
    private double change;
    private int volume;
    private double turnoverInBest;
    private double totalTurnover;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "issuer_id")
    @JsonIgnore
    private Issuer issuer;

    public Row() {
    }

    public Row(String date, double lastTradePrice, double max, double min, double avgPrice, double change, int volume, double turnoverInBest, double totalTurnover) {
        this.date = date;
        this.lastTradePrice = lastTradePrice;
        this.max = max;
        this.min = min;
        this.avgPrice = avgPrice;
        this.change = change;
        this.volume = volume;
        this.turnoverInBest = turnoverInBest;
        this.totalTurnover = totalTurnover;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setLastTradePrice(double lastTradePrice) {
        this.lastTradePrice = lastTradePrice;
    }

    public void setMax(double max) {
        this.max = max;
    }

    public void setMin(double min) {
        this.min = min;
    }

    public void setAvgPrice(double avgPrice) {
        this.avgPrice = avgPrice;
    }

    public void setChange(double change) {
        this.change = change;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public void setTurnoverInBest(double turnoverInBest) {
        this.turnoverInBest = turnoverInBest;
    }

    public void setTotalTurnover(double totalTurnover) {
        this.totalTurnover = totalTurnover;
    }

    public void setIssuer(Issuer issuer) {
        this.issuer = issuer;
    }

    public Long getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public double getLastTradePrice() {
        return lastTradePrice;
    }

    public double getMax() {
        return max;
    }

    public double getMin() {
        return min;
    }

    public double getAvgPrice() {
        return avgPrice;
    }

    public double getChange() {
        return change;
    }

    public int getVolume() {
        return volume;
    }

    public double getTurnoverInBest() {
        return turnoverInBest;
    }

    public double getTotalTurnover() {
        return totalTurnover;
    }

    public Issuer getIssuer() {
        return issuer;
    }
}
