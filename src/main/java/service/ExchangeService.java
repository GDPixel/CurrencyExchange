package service;

import dao.CurrencyDAO;
import dao.ExchangeRateDAO;
import dto.ExchangeDTO;
import exception.ExchangeException;
import model.Currency;

import java.util.Optional;

public class ExchangeService {
    private static final ExchangeService INSTANCE = new ExchangeService();

    private ExchangeService() {
    }

    public ExchangeDTO exchange(Currency from, Currency to, double amount) {
        Double rate = null;
        Double convertedAmount = null;
        // if A -> B exist
        ExchangeRateDAO exchangeRateDAO = ExchangeRateDAO.getInstance();
        var mayBeExchangeRate  = exchangeRateDAO.findByBaseAndTargetIds(from.getId(), to.getId());
        if (mayBeExchangeRate.isPresent()) {
            rate = mayBeExchangeRate.get().getRate();
            convertedAmount = amount * rate;
            return new ExchangeDTO(from, to, rate, amount, convertedAmount);
        }

        // if B -> A exist
        mayBeExchangeRate = exchangeRateDAO.findByBaseAndTargetIds(to.getId(), from.getId());
        if (mayBeExchangeRate.isPresent()) {
            rate = 1.0 / mayBeExchangeRate.get().getRate();
            convertedAmount = amount * rate;
            return new ExchangeDTO(from, to, rate, amount, convertedAmount);
        }

        // if USD->A and USD->B => A -> B
        var mayBeUSDCurrency = CurrencyDAO.getInstance().findByCode("USD");
        if (mayBeUSDCurrency.isPresent()) {
            Long USDId = mayBeUSDCurrency.get().getId();
            var mayBeExchangeRateUSDtoTo = exchangeRateDAO.findByBaseAndTargetIds(USDId, to.getId());
            var mayBeExchangeRateUSDtoFrom = exchangeRateDAO.findByBaseAndTargetIds(USDId, from.getId());
            if (mayBeExchangeRateUSDtoFrom.isPresent() && mayBeExchangeRateUSDtoTo.isPresent()) {
                rate = mayBeExchangeRateUSDtoTo.get().getRate() / mayBeExchangeRateUSDtoFrom.get().getRate();
                convertedAmount = amount * rate;
                return new ExchangeDTO(from, to, rate, amount, convertedAmount);
            }
        }

        throw new ExchangeException();
    }

    public static ExchangeService getInstance() {
        return INSTANCE;
    }
}
