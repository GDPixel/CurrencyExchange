package org.example.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import dao.CurrencyDAO;
import dto.CurrencyDTO;
import exception.CodeAlreadyExistException;
import exception.DatabaseException;
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
public class CurrencyServlet extends HttpServlet {

    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String servletPath = req.getServletPath();
        try {
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
                if (currencyCode.isEmpty()) {
                    resp.setContentType("application/json");
                    resp.setCharacterEncoding("UTF-8");
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    resp.getWriter().print("{\"message\": \"Код валюты отсутствует в адресе\"}");
                    return;
                }

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
                    resp.setContentType("application/json");
                    resp.setCharacterEncoding("UTF-8");
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    resp.getWriter().print("{\"message\": \"Валюта не найдена\"}");
                }
            }

        } catch (DatabaseException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.setContentType("application/json");
            resp.getWriter().print("{\"message\": \"%s\"}".formatted(e.getMessage()));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String name = req.getParameter("name");
        String code = req.getParameter("code");
        String sign = req.getParameter("sign");

        if (name == null || code == null || sign == null
            || name.trim().isEmpty() || code.trim().isEmpty() || sign.trim().isEmpty()) {

            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.getWriter().print("{\"message\": \"Отсутствует нужное поле формы\"}");
            return;
        }

        Currency currency = new Currency();
        currency.setFullName(name);
        currency.setCode(code);
        currency.setSign(sign);


        try {
            var currencyDAO = CurrencyDAO.getInstance();
            currency = currencyDAO.save(currency);
            CurrencyDTO currencyDTO = new CurrencyDTO(
                    currency.getId(),
                    currency.getCode(),
                    currency.getFullName(),
                    currency.getSign());

            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            mapper.writeValue(resp.getWriter(), currencyDTO);
        } catch (CodeAlreadyExistException e) {
            resp.setStatus(HttpServletResponse.SC_CONFLICT);
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.getWriter().print("{\"message\": \"%s\"}".formatted(e.getMessage()));
        } catch (DatabaseException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.setContentType("application/json");
            resp.getWriter().print("{\"message\": \"%s\"}".formatted(e.getMessage()));
        }
    }

    @Override
    public void destroy() {
        super.destroy();
    }
}
