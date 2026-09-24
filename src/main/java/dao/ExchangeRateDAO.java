package dao;

import exception.DatabaseException;
import model.ExchangeRate;
import util.ConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExchangeRateDAO {
    private static final ExchangeRateDAO INSTANCE = new ExchangeRateDAO();

    private static final String SAVE_SQL = """
            INSERT INTO ExchangeRates (BaseCurrencyId, TargetCurrencyId, Rate)
            VALUES (?, ?, ?)
            """;

    private static final String UPDATE_SQL = """
            UPDATE ExchangeRates
            SET BaseCurrencyId = ?,
                TargetCurrencyId = ?,
                Rate = ?
            WHERE id = ?
            """;

    private static final String FIND_ALL_SQL = """
            SELECT * FROM ExchangeRates;
            """;

    private static final String FIND_BY_BASE_AND_TARGET_IDS = """
            SELECT * FROM ExchangeRates
            WHERE BaseCurrencyId = ? AND TargetCurrencyId = ?
            """;

    private ExchangeRateDAO() {
    }

    public static ExchangeRateDAO getInstance() {
        return INSTANCE;
    }

    public List<ExchangeRate> findAll() {
        List<ExchangeRate> exchangeRates = new ArrayList<>();
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

    public Optional<ExchangeRate> findByBaseAndTargetIds(Long baseCurrencyId, Long targetCurrencyId) {
        try (var connection = ConnectionManager.open()) {
            var preparedStatement = connection.prepareStatement(FIND_BY_BASE_AND_TARGET_IDS);
            preparedStatement.setLong(1, baseCurrencyId);
            preparedStatement.setLong(2, targetCurrencyId);
            var resultSet = preparedStatement.executeQuery();
            ExchangeRate exchangeRate = null;
            if (resultSet.next()) {
                exchangeRate = new ExchangeRate(
                        resultSet.getLong("id"),
                        resultSet.getLong("BaseCurrencyId"),
                        resultSet.getLong("TargetCurrencyId"),
                        resultSet.getDouble("Rate")
                );
            }
            return Optional.ofNullable(exchangeRate);
        } catch (SQLException e) {
            throw new DatabaseException();
        }
    }

    public ExchangeRate save(ExchangeRate exchangeRate) {
        try (var connection = ConnectionManager.open()) {
            var preparedStatement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setLong(1, exchangeRate.getBaseCurrencyId());
            preparedStatement.setLong(2, exchangeRate.getTargetCurrencyId());
            preparedStatement.setDouble(3, exchangeRate.getRate());
            preparedStatement.executeUpdate();

            var generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                exchangeRate.setId(generatedKeys.getLong(1));
            }

            return exchangeRate;

        } catch (SQLException e) {
            throw new DatabaseException();
        }
    }

    public void update(ExchangeRate exchangeRate) {
        try (var connection = ConnectionManager.open()) {
            var preparedStatement = connection.prepareStatement(UPDATE_SQL);
            preparedStatement.setLong(1, exchangeRate.getBaseCurrencyId());
            preparedStatement.setLong(2, exchangeRate.getTargetCurrencyId());
            preparedStatement.setDouble(3, exchangeRate.getRate());
            preparedStatement.setLong(4, exchangeRate.getId());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException();
        }
    }
}
