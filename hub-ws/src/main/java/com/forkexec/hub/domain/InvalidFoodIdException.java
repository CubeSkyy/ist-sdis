package com.forkexec.hub.domain;

/** Exception used to signal a problem with the product quantity. */
public class InvalidFoodIdException extends Exception {

    private static final long serialVersionUID = 1L;

    public InvalidFoodIdException() {
    }

    public InvalidFoodIdException(String message) {
        super(message);
    }

}