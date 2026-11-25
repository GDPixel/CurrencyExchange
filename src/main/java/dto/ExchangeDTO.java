package dto;

import model.Currency;

public class ExchangeDTO {
    private final Currency baseCurrency;
    private final Currency targetCurrency;
    private final double rate;
    private final double amount;
    private final double convertedAmount;


    public ExchangeDTO(Currency targetCurrency, Currency baseCurrency, double rate, double amount, double convertedAmount) {
        this.convertedAmount = convertedAmount;
        this.amount = amount;
        this.rate = rate;
        this.targetCurrency = targetCurrency;
        this.baseCurrency = baseCurrency;
    }

    public Currency getBaseCurrency() {
        return baseCurrency;
    }

    public Currency getTargetCurrency() {
        return targetCurrency;
    }

    public double getRate() {
        return rate;
    }

    public double getAmount() {
        return amount;
    }

    public double getConvertedAmount() {
        return convertedAmount;
    }

    @Override
    public String toString() {
        return "ExchangeDTO{" +
               "baseCurrency=" + baseCurrency +
               ", targetCurrency=" + targetCurrency +
               ", rate=" + rate +
               ", amount=" + amount +
               ", convertedAmount=" + convertedAmount +
               '}';
    }
}
