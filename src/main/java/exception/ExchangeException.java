package exception;

public class ExchangeException extends RuntimeException {
    public ExchangeException() {
        super("Ошибка конвертации");
    }
}
