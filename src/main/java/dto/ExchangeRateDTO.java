package dto;

import model.Currency;

public record ExchangeRateDTO(Long id, Currency baseCurrency, Currency targetCurrency, double rate) {

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
