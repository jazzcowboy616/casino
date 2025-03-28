package org.omega.casino.exceptions;

public class UserExistingException extends RuntimeException {
    public UserExistingException(String username) {
        super("Username " + username + " has already been used");
    }
}
