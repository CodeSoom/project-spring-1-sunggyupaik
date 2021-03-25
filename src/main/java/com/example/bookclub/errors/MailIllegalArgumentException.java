package com.example.bookclub.errors;

public class MailIllegalArgumentException extends RuntimeException {
    public MailIllegalArgumentException() {
        super("Argument is illegal");
    }
}
