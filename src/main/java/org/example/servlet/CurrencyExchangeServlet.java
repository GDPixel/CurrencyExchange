package org.example.servlet;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/")
public class CurrencyExchangeServlet extends HttpServlet {

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uri = req.getRequestURI();
        resp.setContentType("text/html");
        try (var writer = resp.getWriter()) {
            writer.write("<h1>Hello Currency Exchange project</h1>");
            writer.write(uri);
        }
        super.doGet(req, resp);
    }

    @Override
    public void destroy() {
        super.destroy();
    }
}
