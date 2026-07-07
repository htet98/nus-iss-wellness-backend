package nus.iss.wellness.backend.exception;

//author: Junior

public class EmailAlreadyExistsException extends RuntimeException {

    public EmailAlreadyExistsException(String message) {
        super(message);
    }
}