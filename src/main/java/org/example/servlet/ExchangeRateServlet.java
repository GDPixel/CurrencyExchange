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

import java.io.BufferedReader;
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
        var currencyDAO = CurrencyDAO.getInstance();

        String servletPath = req.getServletPath();
        if (servletPath.equals("/exchangeRates")) {
            List<ExchangeRate> exchangeRates = exchangeRateDAO.findAll();
            List<ExchangeRateDTO> exchangeRateDTOS = new ArrayList<>();

            for (ExchangeRate exchangeRate : exchangeRates) {
                Optional<Currency> maybeBaseCurrency = currencyDAO.findById(exchangeRate.getBaseCurrencyId());
                Optional<Currency> maybeTargetCurrency = currencyDAO.findById(exchangeRate.getTargetCurrencyId());
                if (maybeBaseCurrency.isPresent() && maybeTargetCurrency.isPresent()) {
                    ExchangeRateDTO exchangeRateDTO = new ExchangeRateDTO(exchangeRate.getId(),
                            maybeBaseCurrency.get(),
                            maybeTargetCurrency.get(),
                            exchangeRate.getRate());
                    exchangeRateDTOS.add(exchangeRateDTO);
                }
            }
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            mapper.writeValue(resp.getWriter(), exchangeRateDTOS);
        } else if (servletPath.equals("/exchangeRate")) {
            // TODO USDRUB -> USD RUB проверить длину на 6 символов, 7 вместе с '/'
            String baseCurrencyCode = req.getPathInfo().substring(1,4);
            String targetCurrencyCode = req.getPathInfo().substring(4,7);
            var maybeBaseCurrency = currencyDAO.findByCode(baseCurrencyCode);
            var maybeTargetCurrency = currencyDAO.findByCode(targetCurrencyCode);
            if (maybeBaseCurrency.isPresent() && maybeTargetCurrency.isPresent()) {
                Currency baseCurrency = maybeBaseCurrency.get();
                Currency targetCurrency = maybeTargetCurrency.get();
                var maybeExchangeRate = exchangeRateDAO.findByBaseAndTargetIds(baseCurrency.getId(), targetCurrency.getId());
                if (maybeExchangeRate.isPresent()) {
                    ExchangeRate exchangeRate = maybeExchangeRate.get();
                    ExchangeRateDTO exchangeRateDTO = new ExchangeRateDTO(exchangeRate.getId(),
                            baseCurrency,
                            targetCurrency,
                            exchangeRate.getRate());
                    resp.setContentType("application/json");
                    resp.setCharacterEncoding("UTF-8");
                    mapper.writeValue(resp.getWriter(), exchangeRateDTO);
                } else {
                    // ошибка! нету такой пары
                }

            } else {
                // ошибка! одна из пар не существует либо обе..
            }

        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String baseCurrencyCode = req.getParameter("baseCurrencyCode");
        String targetCurrencyCode = req.getParameter("targetCurrencyCode");
        double rate = Double.parseDouble(req.getParameter("rate"));
        // TODO проверить введенные данные
        // 1. создать два объекта валют и если они не существуют в бд то исключение
        // 2. создать exchangeRate
        // 3. добавить его в бд
        // 4. сформировать ДТО
        var maybeBaseCurrency = CurrencyDAO.getInstance().findByCode(baseCurrencyCode);
        var maybeTargetCurrency = CurrencyDAO.getInstance().findByCode(targetCurrencyCode);
        if (maybeBaseCurrency.isPresent() && maybeTargetCurrency.isPresent()) {
            Currency baseCurrency = maybeBaseCurrency.get();
            Currency targetCurrency = maybeTargetCurrency.get();

            ExchangeRate exchangeRate = new ExchangeRate();
            exchangeRate.setBaseCurrencyId(baseCurrency.getId());
            exchangeRate.setTargetCurrencyId(targetCurrency.getId());
            exchangeRate.setRate(rate);

            exchangeRate = ExchangeRateDAO.getInstance().save(exchangeRate);
            ExchangeRateDTO exchangeRateDTO = new ExchangeRateDTO(exchangeRate.getId(),
                    baseCurrency,
                    targetCurrency,
                    exchangeRate.getRate());
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            mapper.writeValue(resp.getWriter(), exchangeRateDTO);
        }
    }

    @Override
    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        BufferedReader reader = req.getReader();
        String body = reader.readLine();
        // TODO проверить что это именно "rate=0.1" и не null
        String rateValue = body.split("=")[1];
        double rate = Double.parseDouble(rateValue);

        // TODO USDRUB -> USD RUB проверить длину на 6 символов, 7 вместе с '/'
        String baseCurrencyCode = req.getPathInfo().substring(1,4);
        String targetCurrencyCode = req.getPathInfo().substring(4,7);

        var exchangeRateDAO = ExchangeRateDAO.getInstance();
        var currencyDAO = CurrencyDAO.getInstance();

        var maybeBaseCurrency = currencyDAO.findByCode(baseCurrencyCode);
        var maybeTargetCurrency = currencyDAO.findByCode(targetCurrencyCode);
        if (maybeBaseCurrency.isPresent() && maybeTargetCurrency.isPresent()) {
            Currency baseCurrency = maybeBaseCurrency.get();
            Currency targetCurrency = maybeTargetCurrency.get();
            var maybeExchangeRate = exchangeRateDAO.findByBaseAndTargetIds(baseCurrency.getId(), targetCurrency.getId());
            if (maybeExchangeRate.isPresent()) {
                ExchangeRate exchangeRate = maybeExchangeRate.get();

                exchangeRate.setRate(rate);
                exchangeRateDAO.update(exchangeRate);
                ExchangeRateDTO exchangeRateDTO = new ExchangeRateDTO(exchangeRate.getId(),
                        baseCurrency,
                        targetCurrency,
                        exchangeRate.getRate());
                resp.setContentType("application/json");
                resp.setCharacterEncoding("UTF-8");
                mapper.writeValue(resp.getWriter(), exchangeRateDTO);
            }
        }

        //TODO ловим ошибки и исключения PATCH
    }
}
