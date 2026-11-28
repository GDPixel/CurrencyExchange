package dao;

import exception.CodeAlreadyExistException;
import exception.DatabaseException;
import model.Currency;
import util.ConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CurrencyDAO {
    private static final CurrencyDAO INSTANCE = new CurrencyDAO();

    private static final String SAVE_SQL = """
            INSERT INTO currencies (code, full_name, sign)
            VALUES (?, ?, ?)
            """;
    private static final String FIND_BY_ID_SQL = """
            SELECT id,
                code,
                full_name,
                sign
            FROM currencies
            WHERE id = ?
            """;

    private static final String FIND_BY_CODE_SQL = """
            SELECT id,
                code,
                full_name,
                sign
            FROM currencies
            WHERE code = ?
            """;

    private static final String FIND_ALL_SQL = """
            SELECT id,
                 code,
                 full_name,
                 sign
            FROM currencies
            """;
    private static final int SQLITE_CONSTRAINT_UNIQUE = 2067;
    private static final String UNIQUE_CONSTRAINT_MESSAGE = "UNIQUE";

    private CurrencyDAO() {
    }

    public static CurrencyDAO getInstance() {
        return INSTANCE;
    }

    public Currency save(Currency currency) {
        try (var connection = ConnectionManager.open()) {
            var preparedStatement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, currency.getCode());
            preparedStatement.setString(2, currency.getFullName());
            preparedStatement.setString(3, currency.getSign());
            preparedStatement.executeUpdate();

            var generatedKeys = preparedStatement.getGeneratedKeys();

            if (generatedKeys.next()) {
                currency.setId(generatedKeys.getLong(1));
            }

            return currency;
        } catch (SQLException e) {
            if (e.getErrorCode() == SQLITE_CONSTRAINT_UNIQUE || e.getMessage().contains(UNIQUE_CONSTRAINT_MESSAGE)) {
                throw new CodeAlreadyExistException(currency.getCode());
            } else {
                throw new DatabaseException();
            }
        }
    }

    public Optional<Currency> findByCode(String code) {
        try (var connection = ConnectionManager.open()) {
            var preparedStatement = connection.prepareStatement(FIND_BY_CODE_SQL);
            preparedStatement.setString(1, code);
            var resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return Optional.of(builtCurrency(resultSet));
            } else {
                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new DatabaseException();
        }
    }

    public List<Currency> findAll() {
        List<Currency> currencies = new ArrayList<>();
        try (var connection = ConnectionManager.open()) {
            var preparedStatement = connection.prepareStatement(FIND_ALL_SQL);
            var resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                currencies.add(builtCurrency(resultSet));
            }

        } catch (SQLException e) {
            throw new DatabaseException();
        }
        return currencies;
    }

    public Optional<Currency> findById(Long id) {
        try (var connection = ConnectionManager.open()) {
            var preparedStatement = connection.prepareStatement(FIND_BY_ID_SQL);
            preparedStatement.setLong(1, id);
            var resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(builtCurrency(resultSet));
            } else {
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new DatabaseException();
        }
    }

    private Currency builtCurrency(ResultSet resultSet) throws SQLException {
        return new Currency(
                resultSet.getLong("id"),
                resultSet.getString("code"),
                resultSet.getString("full_name"),
                resultSet.getString("sign")
        );
    }
}
