package dto;

import model.Currency;

public record ExchangeDTO(Currency targetCurrency, Currency baseCurrency, double rate, double amount,
                          double convertedAmount) {

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
