package exception;

public class DatabaseException extends RuntimeException {
    public DatabaseException() {
        super("Ошибка базы данных");
    }
}
