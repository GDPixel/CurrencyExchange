package exception;

public class CurrencyNotFoundException extends RuntimeException {
    public CurrencyNotFoundException(String code) {
        super("Валюта " + code + " не найдена");
    }
}

