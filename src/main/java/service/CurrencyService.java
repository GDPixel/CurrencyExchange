package service;

import dao.CurrencyDAO;
import dto.CurrencyDTO;
import exception.CurrencyNotFoundException;
import model.Currency;

import java.util.ArrayList;
import java.util.List;

public class CurrencyService {
    private static final CurrencyService INSTANCE = new CurrencyService();
    private static final CurrencyDAO currencyDAO = CurrencyDAO.getInstance();

    private CurrencyService() {
    }

    public List<CurrencyDTO> getAll() {
        var currencies = currencyDAO.findAll();

        List<CurrencyDTO> currencyDTOs = new ArrayList<>();
        for (var currency : currencies) {
            CurrencyDTO currencyDTO = new CurrencyDTO(
                    currency.getId(),
                    currency.getCode(),
                    currency.getFullName(),
                    currency.getSign());
            currencyDTOs.add(currencyDTO);
        }

        return currencyDTOs;
    }

    public CurrencyDTO getByCode(String code) {
        var currency = currencyDAO.findByCode(code)
                .orElseThrow(() -> new CurrencyNotFoundException(code));

        return new CurrencyDTO(
                currency.getId(),
                currency.getCode(),
                currency.getFullName(),
                currency.getSign());
    }


    public static CurrencyService getInstance() {
        return INSTANCE;
    }
}
