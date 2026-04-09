package com.codebloom.cineman.service.impl;

import com.codebloom.cineman.common.enums.*;
import com.codebloom.cineman.controller.request.TicketRequest;
import com.codebloom.cineman.controller.response.DummyTicket;
import com.codebloom.cineman.controller.response.SeatResponse;
import com.codebloom.cineman.controller.response.TicketResponse;
import com.codebloom.cineman.exception.DataExistingException;
import com.codebloom.cineman.exception.DataNotFoundException;
import com.codebloom.cineman.model.*;
import com.codebloom.cineman.repository.*;
import com.codebloom.cineman.repository.projection.SeatSelectionProjection;
import com.codebloom.cineman.repository.projection.TicketTypeSelectionProjection;
import com.codebloom.cineman.service.TicketService;
import com.codebloom.cineman.service.dto.TicketSelectionResult;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
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

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public TicketResponse findById(Integer ticketId) {
        return null;
    }


    @Override
    public List<DummyTicket> findAllTicketsByShowTimeIdAndUserId(Long showTimeId, Long userId) {

        // TODO: Cần phải tối ưu lại để chạy nhanh hơn
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
                    .price(0.0)
                    .status(TicketStatus.EMPTY)
                    .build();
            tickets.add(dummyTicket);
        }
        ShowTimeEntity showTime = showTimeRepository.findByIdAndStatus(showTimeId, ShowTimeStatus.VALID)
                .orElseThrow(() -> new DataNotFoundException("Show time not found"));
        List<TicketEntity> listTicket = ticketRepository.findByShowTime(showTime);

        // Tạo ra các vé đã dat //
        for(TicketEntity ticket : listTicket) {
            Date now = new Date();

            UserEntity customer = ticket.getInvoice().getCustomer();
            UserEntity staff = ticket.getInvoice().getStaff();

            // Kiểm tra nếu vé của người dùng đang gọi request thì --> trạng thái vé là SELECTED //
            if(((customer != null && customer.getUserId().equals(userId) && ticket.getStatus() == TicketStatus.PENDING) )
                    || ((staff != null && staff.getUserId() != null && staff.getUserId().equals(userId)) && ticket.getStatus() == TicketStatus.PENDING )) {
                ticket.setCreateBooking(now);
                ticket = ticketRepository.save(ticket);
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
                        .price(ticket.getPrice())
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
                    .price(ticket.getPrice())
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

        log.info("Create new ticket");

        ShowTimeEntity showTimeEntity = showTimeRepository.findByIdAndStatus(request.getShowTimeId(), ShowTimeStatus.VALID)
                .orElseThrow(() -> new DataNotFoundException("Show time not found or invalid"));

        SeatEntity seatEntity = seatRepository.findByIdAndStatus(request.getSeatId(), SeatStatus.ACTIVE)
                .orElseThrow(() -> new DataNotFoundException("Seat not found or invalid"));

        TicketTypeEntity ticketTypeEntity = ticketTypeRepository.findByNameAndStatus(request.getTicketType(), true)
                .orElseThrow(() -> new DataNotFoundException("Ticket type not found or invalid"));

        InvoiceEntity invoiceEntity = getPendingInvoice(request.getInvoiceId());

        ticketRepository.findByShowTimeAndSeat(showTimeEntity, seatEntity)
                .ifPresent(ticket -> {
                    throw new DataExistingException("Ticket already exist");
                });

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
    @Transactional
    public TicketSelectionResult createSeatSelection(TicketRequest request) {

        log.info("Create new ticket for realtime seat selection");

        Double showTimePrice = showTimeRepository.findOriginPriceByIdAndStatus(request.getShowTimeId(), ShowTimeStatus.VALID)
                .orElseThrow(() -> new DataNotFoundException("Show time not found or invalid"));

        SeatSelectionProjection seatSelection = seatRepository.findSeatSelectionByIdAndStatus(request.getSeatId(), SeatStatus.ACTIVE)
                .orElseThrow(() -> new DataNotFoundException("Seat not found or invalid"));

        TicketTypeSelectionProjection ticketTypeSelection = ticketTypeRepository
                .findSelectionByNameAndStatus(request.getTicketType(), true)
                .orElseThrow(() -> new DataNotFoundException("Ticket type not found or invalid"));

        getPendingInvoice(request.getInvoiceId());

        if (ticketRepository.existsByShowTime_IdAndSeat_Id(request.getShowTimeId(), request.getSeatId())) {
            throw new DataExistingException("Ticket already exist");
        }

        Double price = showTimePrice + ticketTypeSelection.getPrice() + seatSelection.getSeatTypePrice();

        TicketEntity ticketEntity = TicketEntity.builder()
                .showTime(entityManager.getReference(ShowTimeEntity.class, request.getShowTimeId()))
                .ticketType(entityManager.getReference(TicketTypeEntity.class, ticketTypeSelection.getId()))
                .invoice(entityManager.getReference(InvoiceEntity.class, request.getInvoiceId()))
                .seat(entityManager.getReference(SeatEntity.class, request.getSeatId()))
                .price(price)
                .status(TicketStatus.PENDING)
                .limitTime(10)
                .build();

        TicketEntity savedTicket = ticketRepository.save(ticketEntity);

        return TicketSelectionResult.builder()
                .ticketId(savedTicket.getId())
                .invoiceId(request.getInvoiceId())
                .price(price)
                .seat(toSeatResponse(seatSelection))
                .build();
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
    public TicketEntity delete(Long ticketId) {
        TicketEntity ticketEntity = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new DataNotFoundException("Ticket not found"));

        if(ticketEntity.getInvoice().getStatus() != InvoiceStatus.PENDING) {
            throw new DataNotFoundException("Ticket must not be deleted");
        }
        ticketRepository.delete(ticketEntity);
        return ticketEntity;
    }

    /**
     * Dọn dẹp những vé quá thời gian chờ hợp lệ --> mặc định là 10 phút
     */
    @Override
    @Transactional
    public void clearTicketOutTimeLimit() {
        log.info("Clear ticket out time limit");
        ticketRepository.deleteAllTicketOutOfLimitTime(TicketStatus.PENDING);
        log.info("Clear ticket out time limit successfully");
    }

    /**
     * Lấy tất cả vé theo invoiceId có trạng thái là PENDING
     * @param invoiceId ID hóa đơn
     * @return  Danh sách vé
     */
    @Override
    public List<TicketEntity> findByInvoiceId(Long invoiceId) {
        InvoiceEntity invoiceEntity = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new DataNotFoundException("Invoice not found or invalid"));
        List<TicketEntity> tickets = ticketRepository.findByInvoice(invoiceEntity);
        return !tickets.isEmpty() ? tickets : null;
    }

    /**
     * Hàm tính tổng tiền vé theo invoiceId
     * @param invoiceId ID hóa đơn
     * @return Tính tổng tiền vé
     */
    @Override
    public Double getTotalMoneyOfTickets(Long invoiceId) {
        return ticketRepository.sumPriceByInvoiceId(invoiceId);
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

    private SeatResponse toSeatResponse(SeatSelectionProjection seatSelection) {
        return SeatResponse.builder()
                .id(seatSelection.getId())
                .rowIndex(seatSelection.getRowIndex())
                .columnIndex(seatSelection.getColumnIndex())
                .label(seatSelection.getLabel())
                .seatType(
                        SeatTypeEntity.builder()
                                .id(seatSelection.getSeatTypeId())
                                .name(seatSelection.getSeatTypeName())
                                .price(seatSelection.getSeatTypePrice())
                                .status(seatSelection.getSeatTypeStatus())
                                .build()
                )
                .status(seatSelection.getStatus())
                .build();
    }

    private InvoiceEntity getPendingInvoice(Long invoiceId) {
        InvoiceEntity invoiceEntity = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new DataNotFoundException("Invoice not found"));
        if (invoiceEntity.getStatus() != InvoiceStatus.PENDING) {
            throw new DataNotFoundException("Invoice is not available for seat selection");
        }
        return invoiceEntity;
    }
}
