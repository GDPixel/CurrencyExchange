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
import java.util.Map;

@WebServlet({"/currencies", "/currency/*"})
public class CurrencyServlet extends HttpServlet {

    private static final CurrencyDAO currencyDAO = CurrencyDAO.getInstance();
    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    @Override
    public void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        super.service(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String servletPath = req.getServletPath();
        try {
            if (servletPath.equals("/currencies")) {
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
                sendJson(resp, HttpServletResponse.SC_OK, currencyDTOS);

            } else if (servletPath.equals("/currency")) {
                String currencyCode = req.getPathInfo().substring(1);
                if (currencyCode.isEmpty()) {
                    sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Код валюты отсутствует в адресе");
                    return;
                }

                var currencyOpt = currencyDAO.findByCode(currencyCode);
                if (currencyOpt.isPresent()) {
                    Currency currency = currencyOpt.get();
                    CurrencyDTO currencyDTO = new CurrencyDTO(
                            currency.getId(),
                            currency.getCode(),
                            currency.getFullName(),
                            currency.getSign());

                    sendJson(resp, HttpServletResponse.SC_OK, currencyDTO);
                } else {
                    sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Валюта не найдена");
                }
            }

        } catch (DatabaseException e) {
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String name = req.getParameter("name");
        String code = req.getParameter("code");
        String sign = req.getParameter("sign");

        if (name == null || code == null || sign == null
            || name.trim().isEmpty() || code.trim().isEmpty() || sign.trim().isEmpty()) {

            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Отсутствует нужное поле формы");
            return;
        }

        Currency currency = new Currency();
        currency.setFullName(name);
        currency.setCode(code);
        currency.setSign(sign);


        try {
            currency = currencyDAO.save(currency);
            CurrencyDTO currencyDTO = new CurrencyDTO(
                    currency.getId(),
                    currency.getCode(),
                    currency.getFullName(),
                    currency.getSign());
            sendJson(resp, HttpServletResponse.SC_CREATED, currencyDTO);

        } catch (CodeAlreadyExistException e) {
            sendError(resp, HttpServletResponse.SC_CONFLICT, e.getMessage());
        } catch (DatabaseException e) {
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    public void destroy() {
        super.destroy();
    }

    private void sendError(HttpServletResponse resp, int statusCode, String message) throws IOException {
        resp.setStatus(statusCode);
        mapper.writeValue(resp.getWriter(), Map.of("message", message));
    }

    private void sendJson(HttpServletResponse resp, int statusCode, Object data) throws IOException {
        resp.setStatus(statusCode);
        mapper.writeValue(resp.getWriter(), data);
    }
}
