package nus.iss.wellness.backend.exception;

//author: Junior

public class UsernameAlreadyExistsException extends RuntimeException {

    public UsernameAlreadyExistsException(String message) {
        super(message);
    }
}