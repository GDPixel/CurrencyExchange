package org.example.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import dao.CurrencyDAO;
import dto.CurrencyDTO;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Currency;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet({"/currencies", "/currency/*"})
public class CurrencyExchangeServlet extends HttpServlet {

    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String servletPath = req.getServletPath();

        if (servletPath.equals("/currencies")) {


            CurrencyDAO currencyDAO = CurrencyDAO.getInstance();
            var currencies = currencyDAO.findAll();

            List<CurrencyDTO> currencyDTOS = new ArrayList<>();
            for (var currency : currencies) {
                CurrencyDTO currencyDTO = new CurrencyDTO(
                        currency.getId(),
                        currency.getCode(),
                        currency.getFullName(),
                        currency.getSign());
                currencyDTOS.add(currencyDTO);
            }

            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            mapper.writeValue(resp.getWriter(), currencyDTOS);

        } else if (servletPath.equals("/currency")) {
            String currencyCode = req.getPathInfo().substring(1);
            // TODO проверить код полученной валюты
            CurrencyDAO currencyDAO = CurrencyDAO.getInstance();
            var mayBeCurrency = currencyDAO.findByCode(currencyCode);
            if (mayBeCurrency.isPresent()) {
                Currency currency = mayBeCurrency.get();
                CurrencyDTO currencyDTO = new CurrencyDTO(
                        currency.getId(),
                        currency.getCode(),
                        currency.getFullName(),
                        currency.getSign());

                resp.setContentType("application/json");
                resp.setCharacterEncoding("UTF-8");
                mapper.writeValue(resp.getOutputStream(), currencyDTO);
            } else {
                //writer.write("Currency not found");
            }

        } else {
            // TODO какая ошибка тут может быть?
            // writer.write(404);
            ;
        }

        super.doGet(req, resp);
    }

    @Override
    public void destroy() {
        super.destroy();
    }
}
