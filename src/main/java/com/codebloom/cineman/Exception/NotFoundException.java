package com.codebloom.cineman.Exception;

import org.apache.logging.log4j.message.Message;

public class NotFoundException extends RuntimeException{
    public NotFoundException(String message) {
        super(message);
    }
}
