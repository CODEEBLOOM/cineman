package com.codebloom.cineman.service.impl;

import com.codebloom.cineman.common.enums.InvoiceStatus;
import com.codebloom.cineman.common.utils.XStr;
import com.codebloom.cineman.controller.response.InvoiceDetailPageResponse;
import com.codebloom.cineman.controller.response.InvoiceResponse;
import com.codebloom.cineman.model.CinemaTheaterEntity;
import com.codebloom.cineman.model.InvoiceEntity;
import com.codebloom.cineman.model.MovieEntity;
import com.codebloom.cineman.model.MovieTheaterEntity;
import com.codebloom.cineman.model.ShowTimeEntity;
import com.codebloom.cineman.model.TicketEntity;
import com.codebloom.cineman.model.UserEntity;
import com.codebloom.cineman.repository.InvoiceRepository;
import com.codebloom.cineman.repository.PromotionRepository;
import com.codebloom.cineman.repository.TicketRepository;
import com.codebloom.cineman.repository.UserRepository;
import com.codebloom.cineman.service.TicketService;
import com.codebloom.cineman.service.UserPointHistoryService;
import com.codebloom.cineman.service.mail.MailService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InvoiceServiceImplTest {

    @Mock
    private InvoiceRepository invoiceRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private TicketService ticketService;

    @Mock
    private XStr xStr;

    @Mock
    private PromotionRepository promotionRepository;

    @Mock
    private MailService mailService;

    @Mock
    private UserPointHistoryService userPointHistoryService;

    @Mock
    private ModelMapper mapper;

    @InjectMocks
    private InvoiceServiceImpl invoiceService;

    @Test
    void findAllInvoicesByShowDateAndMovieTheater_shouldUseShowDateQueryWithoutMovieTheaterFilter() {
        Date showDate = Date.from(LocalDate.of(2026, 3, 28)
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant());
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("createdAt").descending());
        InvoiceEntity invoice = paidInvoice(1L, 7);
        Page<InvoiceEntity> page = new PageImpl<>(List.of(invoice), pageRequest, 1);

        when(invoiceRepository.findAllByShowDate(eq(showDate), any(PageRequest.class))).thenReturn(page);

        InvoiceDetailPageResponse response = invoiceService.findAllInvoicesByShowDateAndMovieTheater(showDate, 0, 10);

        assertEquals(1, response.getInvoiceDetailResponses().size());
        assertEquals(1, response.getMeta().getTotalElements());
        verify(invoiceRepository).findAllByShowDate(eq(showDate), any(PageRequest.class));
        verify(invoiceRepository, never()).findAllByShowDateAndMovieTheaterId(any(Date.class), any(Integer.class), any(PageRequest.class));
    }

    @Test
    void findAllInvoicesByShowDateAndMovieTheater_shouldUseMovieTheaterQueryWhenFilterProvided() {
        Date showDate = Date.from(LocalDate.of(2026, 3, 28)
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant());
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("createdAt").descending());
        InvoiceEntity paidInvoice = paidInvoice(1L, 7);
        InvoiceEntity pendingInvoice = pendingInvoice(2L, 7);
        Page<InvoiceEntity> page = new PageImpl<>(List.of(paidInvoice, pendingInvoice), pageRequest, 2);

        when(invoiceRepository.findAllByShowDateAndMovieTheaterId(eq(showDate), eq(7), any(PageRequest.class))).thenReturn(page);

        InvoiceDetailPageResponse response = invoiceService.findAllInvoicesByShowDateAndMovieTheater(showDate, 0, 10, 7);

        assertEquals(1, response.getInvoiceDetailResponses().size());
        assertEquals(2, response.getMeta().getTotalElements());
        verify(invoiceRepository).findAllByShowDateAndMovieTheaterId(eq(showDate), eq(7), any(PageRequest.class));
        verify(invoiceRepository, never()).findAllByShowDate(any(Date.class), any(PageRequest.class));
    }

    @Test
    void findByUserIdAndShowTimeIdShouldIgnoreUsedInvoiceForSameShowTime() {
        UserEntity customer = UserEntity.builder()
                .userId(10L)
                .build();
        InvoiceEntity usedInvoice = paidInvoice(3L, 7);
        usedInvoice.setCustomer(customer);
        usedInvoice.setStatus(InvoiceStatus.USED);

        when(invoiceRepository.findByCustomerOrStaff(10L)).thenReturn(List.of(usedInvoice));

        InvoiceResponse response = invoiceService.findByUserIdAndShowTimeId(10L, 1L);

        assertNull(response);
    }

    private InvoiceEntity paidInvoice(Long invoiceId, Integer movieTheaterId) {
        MovieTheaterEntity movieTheater = MovieTheaterEntity.builder()
                .movieTheaterId(movieTheaterId)
                .name("Cinema")
                .address("Address")
                .hotline("0123")
                .status(true)
                .iframeCode("iframe")
                .build();
        CinemaTheaterEntity cinemaTheater = CinemaTheaterEntity.builder()
                .cinemaTheaterId(1)
                .name("Room 1")
                .movieTheater(movieTheater)
                .build();
        ShowTimeEntity showTime = ShowTimeEntity.builder()
                .id(1L)
                .showDate(new Date())
                .movie(MovieEntity.builder().movieId(1).title("Movie").build())
                .cinemaTheater(cinemaTheater)
                .build();
        TicketEntity ticket = TicketEntity.builder()
                .id(1L)
                .price(100_000.0)
                .showTime(showTime)
                .build();

        InvoiceEntity invoice = InvoiceEntity.builder()
                .id(invoiceId)
                .email("invoice" + invoiceId + "@example.com")
                .phoneNumber("090000000" + invoiceId)
                .status(InvoiceStatus.PAID)
                .totalTicket(1)
                .totalAmount(100_000.0)
                .createdAt(new Date())
                .updatedAt(new Date())
                .detailBookingSnacks(Collections.emptyList())
                .userPointHistories(Collections.emptyList())
                .build();
        ticket.setInvoice(invoice);
        invoice.setTickets(List.of(ticket));
        return invoice;
    }

    private InvoiceEntity pendingInvoice(Long invoiceId, Integer movieTheaterId) {
        InvoiceEntity invoice = paidInvoice(invoiceId, movieTheaterId);
        invoice.setStatus(InvoiceStatus.PENDING);
        return invoice;
    }
}
