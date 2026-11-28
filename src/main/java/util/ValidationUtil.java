package util;

import exception.WrongInputException;

public final class ValidationUtil {
    private ValidationUtil() {

    }

    public static void validateCurrencyCode(String code) {
        if ((code == null) || !code.matches("^[A-Z]{3}$")) {
            throw new WrongInputException("Не верный код валюты ");
        }
    }
}
