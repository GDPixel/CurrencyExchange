package dao;

import exception.DatabaseException;
import model.ExchangeRate;
import util.ConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ExchangeRateDAO {
    private static final ExchangeRateDAO INSTANCE = new ExchangeRateDAO();

    private static final String FIND_ALL_SQL = """
            SELECT * FROM ExchangeRates;
            """;

    private ExchangeRateDAO() {
    }

    public static ExchangeRateDAO getInstance() {
        return INSTANCE;
    }

    public List<ExchangeRate> findAll() {
        List<ExchangeRate> exchangeRates = new ArrayList<ExchangeRate>();
        try (var connection = ConnectionManager.open()) {
            var preparedStatement = connection.prepareStatement(FIND_ALL_SQL);
            var resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                ExchangeRate exchangeRate = new ExchangeRate(resultSet.getLong("id"),
                        resultSet.getLong("BaseCurrencyId"),
                        resultSet.getLong("TargetCurrencyId"),
                        resultSet.getDouble("Rate"));
                exchangeRates.add(exchangeRate);
            }

        } catch (SQLException e) {
            throw new DatabaseException();
        }
        return exchangeRates;
    }
}
