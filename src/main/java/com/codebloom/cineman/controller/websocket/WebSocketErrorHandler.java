package com.codebloom.cineman.controller.websocket;

import com.codebloom.cineman.exception.DataExistingException;
import com.codebloom.cineman.exception.DataNotFoundException;
import com.codebloom.cineman.message.ErrorMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
@Slf4j(topic = "WebSocketErrorHandler")
public class WebSocketErrorHandler {

    @MessageExceptionHandler(DataNotFoundException.class)
    @SendToUser("/queue/errors")
    public ErrorMessage handleDataNotFoundException(DataNotFoundException ex) {
        return ErrorMessage.builder()
                .type("DATA_NOT_FOUND")
                .message(ex.getMessage())
                .build();
    }

    @MessageExceptionHandler(DataExistingException.class)
    @SendToUser("/queue/errors")
    public ErrorMessage handleDataExistingException(DataExistingException ex) {
        return ErrorMessage.builder()
                .type("DATA_EXISTING")
                .message(ex.getMessage())
                .build();
    }

}
