package dao;

import model.Currency;
import util.ConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CurrencyDAO {
    private static final CurrencyDAO INSTANCE = new CurrencyDAO();

    private static final String FIND_BY_CODE_SQL = """
            SELECT id,
                Code,
                FullName,
                Sign
            FROM Currencies
            WHERE Code = ?
            """;

    private static final String FIND_ALL_SQL = """
            SELECT id,
                 Code,
                 FullName,
                 Sign
            FROM Currencies
            """;

    private CurrencyDAO() {
    }

    public static CurrencyDAO getInstance() {
        return INSTANCE;
    }

    public Optional<Currency> findByCode(String code) {

        try (var connection = ConnectionManager.open()) {
            var preparedStatement = connection.prepareStatement(FIND_BY_CODE_SQL);
            preparedStatement.setString(1, code);
            var resultSet = preparedStatement.executeQuery();
            Currency currency = null;
            if (resultSet.next()) {
                currency = new Currency(
                        resultSet.getLong("id"),
                        resultSet.getString("Code"),
                        resultSet.getString("fullName"),
                        resultSet.getString("Sign")
                );
            }
            return Optional.ofNullable(currency);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Currency> findAll() {
        List<Currency> currencies = new ArrayList<>();
        try (var connection = ConnectionManager.open()) {
            var preparedStatement = connection.prepareStatement(FIND_ALL_SQL);
            var resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Currency currency = new Currency(
                        resultSet.getLong("id"),
                        resultSet.getString("Code"),
                        resultSet.getString("fullName"),
                        resultSet.getString("Sign"));
                currencies.add(currency);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return currencies;
    }
}
