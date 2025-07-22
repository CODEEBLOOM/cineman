package com.codebloom.cineman.component;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WebSocketSchedulerCleanup {


    private final ThreadPoolTaskScheduler myMessageBrokerScheduler;

    @PreDestroy
    public void shutdownScheduler() {
        myMessageBrokerScheduler.shutdown();
    }
}
