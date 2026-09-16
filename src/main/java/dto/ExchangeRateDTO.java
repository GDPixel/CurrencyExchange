package dto;

import model.Currency;

public class ExchangeRateDTO {
    private Long id;
    private Currency baseCurrency;
    private Currency targetCurrency;
    private double rate;

    public ExchangeRateDTO(Long id, Currency baseCurrency, Currency targetCurrency, double rate) {
        this.id = id;
        this.baseCurrency = baseCurrency;
        this.targetCurrency = targetCurrency;
        this.rate = rate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Currency getBaseCurrency() {
        return baseCurrency;
    }

    public void setBaseCurrency(Currency baseCurrency) {
        this.baseCurrency = baseCurrency;
    }

    public Currency getTargetCurrency() {
        return targetCurrency;
    }

    public void setTargetCurrency(Currency targetCurrency) {
        this.targetCurrency = targetCurrency;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    @Override
    public String toString() {
        return "ExchangeRateDTO{" +
               "id=" + id +
               ", baseCurrency=" + baseCurrency +
               ", targetCurrency=" + targetCurrency +
               ", rate=" + rate +
               '}';
    }
}
