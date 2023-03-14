package main.service.exception;

public class KVTaskClientRegisterException extends RuntimeException {
    public KVTaskClientRegisterException(String message) {
        super(message);
    }
}