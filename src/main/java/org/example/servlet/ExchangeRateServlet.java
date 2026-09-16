package org.example.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import dao.CurrencyDAO;
import dao.ExchangeRateDAO;
import dto.ExchangeRateDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Currency;
import model.ExchangeRate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@WebServlet({"/exchangeRates", "/exchangeRate/*"})
public class ExchangeRateServlet extends HttpServlet {
    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        var exchangeRateDAO = ExchangeRateDAO.getInstance();
        List<ExchangeRate> exchangeRates = exchangeRateDAO.findAll();
        List<ExchangeRateDTO> exchangeRateDTOS = new ArrayList<>();
        CurrencyDAO currencyDAO = CurrencyDAO.getInstance();
        for (ExchangeRate exchangeRate : exchangeRates) {
            Optional<Currency> mayBeBaseCurrency = currencyDAO.findById(exchangeRate.getBaseCurrencyId());
            Optional<Currency> mayBeTargetCurrency = currencyDAO.findById(exchangeRate.getTargetCurrencyId());
            if (mayBeBaseCurrency.isPresent() && mayBeTargetCurrency.isPresent()) {
                ExchangeRateDTO exchangeRateDTO = new ExchangeRateDTO(exchangeRate.getId(),
                        mayBeBaseCurrency.get(),
                        mayBeTargetCurrency.get(),
                        exchangeRate.getRate());
                exchangeRateDTOS.add(exchangeRateDTO);
            }
        }
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        mapper.writeValue(resp.getWriter(), exchangeRateDTOS);
    }
}
