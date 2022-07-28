package ru.practicum.konkov.exceptions;


public class APIException extends RuntimeException {

    public APIException(String message) {
        super(message);
    }
}

