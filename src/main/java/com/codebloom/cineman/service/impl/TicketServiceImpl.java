package com.codebloom.cineman.service.impl;

import com.codebloom.cineman.common.enums.*;
import com.codebloom.cineman.controller.request.TicketRequest;
import com.codebloom.cineman.controller.response.DummyTicket;
import com.codebloom.cineman.controller.response.SeatResponse;
import com.codebloom.cineman.controller.response.TicketResponse;
import com.codebloom.cineman.exception.DataNotFoundException;
import com.codebloom.cineman.model.*;
import com.codebloom.cineman.repository.*;
import com.codebloom.cineman.service.TicketService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "TICKET-SERVICE")
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final ShowTimeRepository showTimeRepository;
    private final SeatRepository seatRepository;
    private final TicketTypeRepository ticketTypeRepository;
    private final InvoiceRepository invoiceRepository;
    private final UserRepository userRepository;

    @Override
    public TicketResponse findById(Integer ticketId) {
        return null;
    }


    @Override
    public List<DummyTicket> findAllTicketsByShowTimeIdAndUserId(Long showTimeId, Long userId) {
        List <SeatEntity> emptySeats = showTimeRepository.findAllSeatByShowTimeId(showTimeId, ShowTimeStatus.VALID);
        List <DummyTicket> tickets = new ArrayList<>();
        DummyTicket dummyTicket;

        // Tạo ra các vé trống //
        for(SeatEntity seat : emptySeats) {
            dummyTicket = DummyTicket.builder()
                    .id(null)
                    .seat(
                            SeatResponse.builder()
                                    .id(seat.getId())
                                    .rowIndex(seat.getRowIndex())
                                    .columnIndex(seat.getColumnIndex())
                                    .label(seat.getLabel())
                                    .seatType(seat.getSeatType())
                                    .status(seat.getStatus())
                                    .build()
                    )
                    .status(TicketStatus.EMPTY)
                    .build();
            tickets.add(dummyTicket);
        }
        ShowTimeEntity showTime = showTimeRepository.findByIdAndStatus(showTimeId, ShowTimeStatus.VALID)
                .orElseThrow(() -> new DataNotFoundException("Show time not found"));
        List<TicketEntity> listTicket = ticketRepository.findByShowTime(showTime);

        // Tạo ra các vé đã dat //
        for(TicketEntity ticket : listTicket) {

            UserEntity customer = ticket.getInvoice().getCustomer();
            UserEntity staff = ticket.getInvoice().getStaff();

            // Kiểm tra nếu vé của người dùng đang gọi request thì --> trạng thái vé là SELECTED //
            if((customer != null && customer.getUserId().equals(userId) )
                    || (staff != null && staff.getUserId() != null && staff.getUserId().equals(userId))) {
                dummyTicket = DummyTicket.builder()
                        .id(ticket.getId())
                        .seat(
                                SeatResponse.builder()
                                        .id(ticket.getSeat().getId())
                                        .rowIndex(ticket.getSeat().getRowIndex())
                                        .columnIndex(ticket.getSeat().getColumnIndex())
                                        .label(ticket.getSeat().getLabel())
                                        .seatType(ticket.getSeat().getSeatType())
                                        .status(ticket.getSeat().getStatus())
                                        .build()
                        )
                        .status(TicketStatus.SELECTED)
                        .build();
                tickets.add(dummyTicket);
                continue;
            }
            dummyTicket = DummyTicket.builder()
                    .id(ticket.getId())
                    .seat(
                            SeatResponse.builder()
                                    .id(ticket.getSeat().getId())
                                    .rowIndex(ticket.getSeat().getRowIndex())
                                    .columnIndex(ticket.getSeat().getColumnIndex())
                                    .label(ticket.getSeat().getLabel())
                                    .seatType(ticket.getSeat().getSeatType())
                                    .status(ticket.getSeat().getStatus())
                                    .build()
                    )
                    .status(ticket.getStatus().equals(TicketStatus.PENDING) ? TicketStatus.HOLDED : TicketStatus.SOLD)
                    .build();
            tickets.add(dummyTicket);
        }

        return tickets;
    }

    /**
     * Tạo vé mới với trạng thái PENDING
     * @param request thông tin vé
     * @return vé mới
     */
    @Override
    @Transactional
    public TicketEntity create(TicketRequest request) {
        ShowTimeEntity showTimeEntity = showTimeRepository.findByIdAndStatus(request.getShowTimeId(), ShowTimeStatus.VALID)
                .orElseThrow(() -> new DataNotFoundException("Show time not found or invalid"));

        SeatEntity seatEntity = seatRepository.findByIdAndStatus(request.getSeatId(), SeatStatus.ACTIVE)
                .orElseThrow(() -> new DataNotFoundException("Seat not found or invalid"));

        TicketTypeEntity ticketTypeEntity = ticketTypeRepository.findByNameAndStatus(request.getTicketType(), true)
                .orElseThrow(() -> new DataNotFoundException("Ticket type not found or invalid"));

        InvoiceEntity invoiceEntity = invoiceRepository.findByIdAndStatus(request.getInvoiceId(), InvoiceStatus.PENDING)
                .orElseThrow(() -> new DataNotFoundException("Invoice not found"));

        // Tính tiền giá vé //
        // giá cơ bản (showtime) + giá loại vé  (ticket type) + tính giá loại ghế (seat type)
        Double price = showTimeEntity.getOriginPrice() + ticketTypeEntity.getPrice() + seatEntity.getSeatType().getPrice();

        // Tạo ticket //
        TicketEntity ticketEntity = TicketEntity.builder()
                .showTime(showTimeEntity)
                .ticketType(ticketTypeEntity)
                .invoice(invoiceEntity)
                .seat(seatEntity)
                .price(price)
                .status(TicketStatus.PENDING)
                .limitTime(10)
                .build();

        return ticketRepository.save(ticketEntity);
    }



    @Override
    public TicketResponse update(Long ticketId, TicketRequest request) {
        return null;
    }


    /**
     * Xóa vé theo ID
     * @param ticketId ID của vé cần xóa
     */
    @Override
    public void delete(Long ticketId) {
        TicketEntity ticketEntity = ticketRepository.findByIdAndStatus(ticketId, TicketStatus.SELECTED)
                .orElseThrow(() -> new DataNotFoundException("Ticket not found"));

        if(ticketEntity.getInvoice().getStatus() != InvoiceStatus.PENDING) {
            throw new DataNotFoundException("Ticket must not be deleted");
        }
        ticketRepository.delete(ticketEntity);
    }


    /**
     * Chuyển đổi từ TicketEntity sang TicketResponse
     * @param savedTicket TicketEntity sau khi được lưu
     * @return TicketResponse
     */
    private TicketResponse convertToTicketResponse(TicketEntity savedTicket) {
        return TicketResponse.builder()
                .id(savedTicket.getId())
                .showTime(savedTicket.getShowTime())
                .ticketType(savedTicket.getTicketType())
                .price(savedTicket.getPrice())
                .status(savedTicket.getStatus())
                .limitTime(savedTicket.getLimitTime())
                .createBooking(savedTicket.getCreateBooking())
                .build();
    }
}
