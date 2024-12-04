package tech_prototype.model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

@Data
@Getter
@Setter
public class RowTemplate {
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
    private String lastTradePriceStr;
    private String maxStr;
    private String minStr;
    private String avgPriceStr;
    private String changeStr;
    private String turnoverInBestStr;
    private String totalTurnoverStr;

    public RowTemplate(Row row) {
        DecimalFormat df = new DecimalFormat("#");
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator('.');
        symbols.setDecimalSeparator(',');

        // Create a DecimalFormat with a pattern
        DecimalFormat formatter = new DecimalFormat("#,##0.00", symbols);
        id = row.getId();
        date = row.getDate();
        lastTradePrice = row.getLastTradePrice();
        max = row.getMax();
        min = row.getMin();
        avgPrice = row.getAvgPrice();
        change = row.getChange();
        volume = row.getVolume();
        turnoverInBest = row.getTurnoverInBest();
        totalTurnover = row.getTotalTurnover();
        lastTradePriceStr = formatter.format(lastTradePrice);
        maxStr = formatter.format(max);
        minStr = formatter.format(min);
        avgPriceStr = formatter.format(avgPrice);
        changeStr = formatter.format(change);
        turnoverInBestStr = df.format(turnoverInBest);
        totalTurnoverStr = df.format(totalTurnover);
        double tmp1 = Double.parseDouble(turnoverInBestStr);
        double tmp2 = Double.parseDouble(turnoverInBestStr);
        turnoverInBestStr = formatter.format(tmp1);
        totalTurnoverStr = formatter.format(tmp2);
    }
}