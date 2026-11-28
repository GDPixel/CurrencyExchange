package exception;

public class WrongInputException extends RuntimeException{
    public WrongInputException(String message) {
        super("Ошибка ввода: " + message);
    }
}
