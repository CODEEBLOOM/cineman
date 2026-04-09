package com.codebloom.cineman.service.impl;

import com.codebloom.cineman.common.enums.InvoiceStatus;
import com.codebloom.cineman.common.enums.SeatStatus;
import com.codebloom.cineman.common.enums.ShowTimeStatus;
import com.codebloom.cineman.common.enums.TicketType;
import com.codebloom.cineman.controller.request.TicketRequest;
import com.codebloom.cineman.exception.DataNotFoundException;
import com.codebloom.cineman.model.InvoiceEntity;
import com.codebloom.cineman.model.SeatEntity;
import com.codebloom.cineman.model.SeatTypeEntity;
import com.codebloom.cineman.model.ShowTimeEntity;
import com.codebloom.cineman.model.TicketTypeEntity;
import com.codebloom.cineman.repository.InvoiceRepository;
import com.codebloom.cineman.repository.SeatRepository;
import com.codebloom.cineman.repository.ShowTimeRepository;
import com.codebloom.cineman.repository.TicketRepository;
import com.codebloom.cineman.repository.TicketTypeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TicketServiceImplTest {

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private ShowTimeRepository showTimeRepository;

    @Mock
    private SeatRepository seatRepository;

    @Mock
    private TicketTypeRepository ticketTypeRepository;

    @Mock
    private InvoiceRepository invoiceRepository;

    @InjectMocks
    private TicketServiceImpl ticketService;

    @Test
    void createShouldRejectUsedInvoice() {
        TicketRequest request = TicketRequest.builder()
                .invoiceId(9L)
                .showTimeId(5L)
                .seatId(11L)
                .ticketType(TicketType.ADULT)
                .build();
        ShowTimeEntity showTime = ShowTimeEntity.builder().id(5L).originPrice(100_000.0).build();
        SeatEntity seat = SeatEntity.builder()
                .id(11L)
                .status(SeatStatus.ACTIVE)
                .seatType(SeatTypeEntity.builder().price(20_000.0).build())
                .build();
        TicketTypeEntity ticketType = TicketTypeEntity.builder().id(3).name(TicketType.ADULT).price(30_000.0).build();
        InvoiceEntity usedInvoice = InvoiceEntity.builder().id(9L).status(InvoiceStatus.USED).build();

        when(showTimeRepository.findByIdAndStatus(5L, ShowTimeStatus.VALID)).thenReturn(Optional.of(showTime));
        when(seatRepository.findByIdAndStatus(11L, SeatStatus.ACTIVE)).thenReturn(Optional.of(seat));
        when(ticketTypeRepository.findByNameAndStatus(TicketType.ADULT, true)).thenReturn(Optional.of(ticketType));
        when(invoiceRepository.findById(9L)).thenReturn(Optional.of(usedInvoice));

        assertThrows(DataNotFoundException.class, () -> ticketService.create(request));
        verify(ticketRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }
}
