package com.codebloom.cineman.scheduler;

import com.codebloom.cineman.service.TicketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j(topic = "CLEANUP_TICKET")
@RequiredArgsConstructor
public class TicketCleanupScheduler {

    private final TicketService ticketService;

    /**
     * Lên lịch sau 20 giây kiểm tra xóa ticket quá thời gian chờ hợp lệ
     */
    @Scheduled(fixedDelay = 1000 * 60, initialDelay = 10000)
    public void cleanupTickets() {
        log.info("Cleanup tickets");
        ticketService.clearTicketOutTimeLimit();
    }

}
