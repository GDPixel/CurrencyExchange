package exception;

public class CodeAlreadyExistException extends RuntimeException {
    public CodeAlreadyExistException(String code) {
        super("Код валюты " + code + " уже существует");
    }
}
