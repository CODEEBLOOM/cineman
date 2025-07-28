package com.codebloom.cineman.service.impl;

import com.codebloom.cineman.common.enums.PaymentMethod;
import com.codebloom.cineman.common.enums.TicketStatus;
import com.codebloom.cineman.common.utils.XStr;
import com.codebloom.cineman.model.*;
import com.codebloom.cineman.repository.PromotionRepository;
import com.codebloom.cineman.repository.TicketRepository;
import com.codebloom.cineman.service.mail.MailService;
import com.google.zxing.WriterException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

import com.codebloom.cineman.common.enums.InvoiceStatus;
import com.codebloom.cineman.controller.request.InvoiceCreateRequest;
import com.codebloom.cineman.controller.request.InvoiceUpdateRequest;
import com.codebloom.cineman.exception.DataNotFoundException;
import com.codebloom.cineman.repository.InvoiceRepository;
import com.codebloom.cineman.repository.UserRepository;
import com.codebloom.cineman.service.InvoiceService;
import com.codebloom.cineman.controller.response.InvoiceResponse;
import com.codebloom.cineman.service.TicketService;

@Slf4j(topic = "INVOICE_SERVICE")
@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final UserRepository userRepository;
    private final TicketRepository ticketRepository;
    private final TicketService ticketService;
    private final XStr xStr;
    private final PromotionRepository promotionRepository;
    private final MailService mailService;


    /**
     * Tìm kiểm hóa đơn theo userId, showTimeId, trang thái
     * @param id userId
     * @param showTimeId showTimeId
     * @return InvoiceResponse
     */
    @Override
    public InvoiceResponse findByUserIdAndShowTimeId(Long id, Long showTimeId) {
        List<InvoiceEntity> invoiceEntity = invoiceRepository.findByCustomerOrStaff(id);
        for(InvoiceEntity invoice : invoiceEntity) {
            if(invoice.getStatus() != InvoiceStatus.PAID && invoice.getStatus() != InvoiceStatus.CANCELLED) {
                if(invoice.getTickets().size() > 0) {
                    if(invoice.getTickets().get(0).getShowTime().getId() != showTimeId) {
                        return null;
                    }else{
                        return toInvoiceResponse(invoice);
                    }
                }

                // trường hợp chưa có show time cho hóa đơn //
                return toInvoiceResponse(invoice);
            }
        }
        return null;
    }

    /**
     * Tạo mới một hóa đơn
     *
     * @param invoice thông tin hóa đơn
     * @return InvoiceEntity
     */
    @Transactional
    @Override
    public InvoiceResponse create(InvoiceCreateRequest invoice, Long showTimeId) {
        UserEntity customer = null;
        UserEntity staff = null;

        if (invoice.getCustomerId() == null && invoice.getStaffId() == null) {
            throw new DataNotFoundException("Customer or Staff not found");
        }

        if (invoice.getCustomerId() != null) {
            customer = userRepository.findById(invoice.getCustomerId())
                    .orElseThrow(() -> new DataNotFoundException("Customer not found"));
        }

        if (invoice.getStaffId() != null) {
            staff = userRepository.findById(invoice.getStaffId())
                    .orElseThrow(() -> new DataNotFoundException("Staff not found"));
        }

        // Kiểm tra xem hóa đơn đã có sẵn hay chưa nếu có thì cập nhật không tạo mới một hóa đơn //
        InvoiceEntity invoiceEntity = this.getInvoice(customer, staff);
        if (invoiceEntity != null) {
            Date now = new Date();

            // Cập nhật hóa đơn //
            invoiceEntity.setCustomer(customer);
            invoiceEntity.setStaff(staff);
            invoiceEntity.setEmail(invoice.getEmail());
            invoiceEntity.setPhoneNumber(invoice.getPhoneNumber());
            invoiceEntity.setPaymentMethod(invoice.getPaymentMethod() != null ? invoice.getPaymentMethod() : null);
            invoiceEntity.setTotalTicket(invoice.getTotalTicket() != null ? invoice.getTotalTicket() : 0);
            invoiceEntity.setStatus(invoice.getInvoiceStatus() != null ? invoice.getInvoiceStatus() : InvoiceStatus.PENDING);
            invoiceEntity.setCreatedAt(now);
            invoiceEntity.setUpdatedAt(now);
        } else {

            // Tạo mới một hóa đơn //
            invoiceEntity = InvoiceEntity.builder()
                    .email(invoice.getEmail())
                    .phoneNumber(invoice.getPhoneNumber())
                    .paymentMethod(invoice.getPaymentMethod() != null ? invoice.getPaymentMethod() : PaymentMethod.CASH)
                    .totalTicket(invoice.getTotalTicket() != null ? invoice.getTotalTicket() : 0)
                    .customer(customer)
                    .staff(staff)
                    .status(InvoiceStatus.PENDING)
                    .build();
        }
        return toInvoiceResponse(invoiceRepository.save(invoiceEntity));
    }

    @Override
    public InvoiceResponse createInvoice(InvoiceCreateRequest invoice) {

        UserEntity customer = null;
        UserEntity staff = null;
        if(invoice.getCustomerId() != null) {
            customer = userRepository.findById(invoice.getCustomerId())
                    .orElseThrow(() -> new DataNotFoundException("Customer not found"));
        }
        if(invoice.getStaffId() != null) {
            staff = userRepository.findById(invoice.getStaffId())
                    .orElseThrow(() -> new DataNotFoundException("Staff not found"));
        }

        InvoiceEntity invoiceEntity = InvoiceEntity.builder()
                .email(invoice.getEmail())
                .phoneNumber(invoice.getPhoneNumber())
                .paymentMethod(invoice.getPaymentMethod() != null ? invoice.getPaymentMethod() : PaymentMethod.CASH)
                .totalTicket(invoice.getTotalTicket() != null ? invoice.getTotalTicket() : 0)
                .customer(customer)
                .staff(staff)
                .status(InvoiceStatus.PENDING)
                .build();
        return toInvoiceResponse(invoiceRepository.save(invoiceEntity));
    }

    /**
     * Cập nhật hóa đơn
     *
     * @param id      id hóa đơn
     * @param invoice thống tin hóa đơn
     * @return InvoiceEntity
     */
    @Override
    @Transactional
    public InvoiceResponse update(Long id, InvoiceUpdateRequest invoice) {
        UserEntity customer = null;
        UserEntity staff = null;
        PromotionEntity promotion = null;

        if (invoice.getCustomerId() == null && invoice.getStaffId() == null) {
            throw new DataNotFoundException("Customer or Staff not found");
        }


        if (invoice.getCustomerId() != null) {
            customer = userRepository.findById(invoice.getCustomerId())
                    .orElseThrow(() -> new DataNotFoundException("Customer not found"));
        }

        if (invoice.getStaffId() != null) {
            staff = userRepository.findById(invoice.getStaffId())
                    .orElseThrow(() -> new DataNotFoundException("Staff not found"));
        }

        if (invoice.getPromotionId() != null) {
            promotion = promotionRepository.findById(invoice.getPromotionId())
                    .orElseThrow(() -> new DataNotFoundException("Promotion not found"));
        }

        InvoiceEntity invoiceEntity = invoiceRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Invoice not found"));
        if (invoiceEntity.getStatus() == InvoiceStatus.PAID || invoiceEntity.getStatus() == InvoiceStatus.CANCELLED) {
            throw new DataNotFoundException("Invoice not found");
        }
        invoiceEntity.setEmail(invoice.getEmail());
        invoiceEntity.setPhoneNumber(invoice.getPhoneNumber());
        invoiceEntity.setPaymentMethod(invoice.getPaymentMethod());
        invoiceEntity.setTotalTicket(invoice.getTotalTicket());
        invoiceEntity.setCustomer(customer);
        invoiceEntity.setStaff(staff);
        invoiceEntity.setPromotion(promotion);
        invoiceEntity.setStatus(invoice.getInvoiceStatus());
        invoiceEntity.setUpdatedAt(new Date());

        return toInvoiceResponse(invoiceRepository.save(invoiceEntity));
    }

    /**
     * Cập nhật id của VNPay trả về khi thanh toán cho hóa đơn
     *
     * @param id     id của hóa đơn
     * @param tnxRef id của VNPay
     * @return InvoiceResponse
     */
    @Override
    @Transactional
    public InvoiceResponse updateTnx(Long id, String tnxRef) {
        InvoiceEntity invoiceEntity = invoiceRepository.findByIdAndStatusNot(id, InvoiceStatus.CANCELLED)
                .orElseThrow(() -> new DataNotFoundException("Invoice not found"));
        invoiceEntity.setVnTxnRef(tnxRef);
        return toInvoiceResponse(invoiceRepository.save(invoiceEntity));
    }


    /**
     * Cập nhật tràng thái hóa đơn thành công
     *
     * @param txnRef id hóa đơn
     * @return InvoiceResponse
     */
    @Override
    @Transactional
    public InvoiceResponse updateStatusPaymentSuccess(String txnRef) {
        InvoiceEntity invoiceEntity = invoiceRepository.findByVnTxnRefAndStatus(txnRef, InvoiceStatus.PROCESSING)
                .orElseThrow(() -> new DataNotFoundException("Invoice not found"));
        invoiceEntity.setStatus(InvoiceStatus.PAID);

        // Thêm QR code cho hóa đơn //
        String qrCode = xStr.getKey();
        invoiceEntity.setQrCode(qrCode);
        InvoiceResponse invoiceResponse = toInvoiceResponse(invoiceRepository.save(invoiceEntity));

        // Câp nhật tràng thái cho ticket //
        invoiceEntity.getTickets().forEach(ticket -> {
            ticket.setStatus(TicketStatus.SOLD);
            ticketRepository.save(ticket);
        });

        // Gửi mail invoice cho khách hàng //
        // Thông tin cần có hóa đơn //
        String titleMovie = invoiceEntity.getTickets().get(0).getShowTime().getMovie().getTitle();
        String titleMovieTheater = invoiceEntity.getTickets().get(0).getShowTime().getCinemaTheater().getMovieTheater().getName();
        String titleCinema = invoiceEntity.getTickets().get(0).getShowTime().getCinemaTheater().getName();
        String dateShow = invoiceEntity.getTickets().get(0).getShowTime().getShowDate().toString() + " - " + invoiceEntity.getTickets().get(0).getShowTime().getStartTime().toString();
        String seats = "";
        int i = 0;
        for (TicketEntity ticket : invoiceEntity.getTickets()) {
            SeatEntity seat = ticket.getSeat();
            if (i > 0) {
                seats += ", " + ticket.getSeat().getLabel();
                i++;
                continue;
            }
            i++;
            seats += seat.getLabel();
        }

        // Thông tin combo //
        List<DetailBookingSnackEntity> detailBookingSnacks = invoiceEntity.getDetailBookingSnacks();
        String snacks = """
                <table width="100%" cellspacing="0" cellpadding="5">
                """;
        for (DetailBookingSnackEntity detailBookingSnack : detailBookingSnacks) {
            String content = String.format("""
                    <tr>
                        <td>%s</td>
                        <td align="right">%d x %.2f</td>
                    </tr>
                    """, detailBookingSnack.getSnack().getSnackName(), detailBookingSnack.getTotalSnack(), detailBookingSnack.getSnack().getUnitPrice());
            snacks += content;
        }
        snacks += """
                </table>
                """;
        // TODO: Thông tin khuyến mãi //
        String body = String.format("""
                <html>
                  <body style="font-family: sans-serif; background: #f7f7f7; padding: 20px">
                    <div
                      style="
                        max-width: 600px;
                        margin: auto;
                        background: #fff;
                        padding: 20px;
                        border: 1px solid #ddd;
                      "
                    >
                      <div style="text-align: center">
                        <img
                          src="cid:qrImage"
                          alt="QR Code"
                          width="150"
                          height="150"
                        />
                        <p style="color: #f4499f; font-style: italic">
                          Mã QR Code này dùng để quét và nhận vé của bạn tại rạp chiếu Cineman
                          Cinemas
                        </p>
                      </div>
                
                      <hr />
                      <h3 style="color: #2e4ca6">Thông tin vé</h3>
                      <table width="100%%" cellspacing="0" cellpadding="5">
                        <tr>
                          <td>Mã vé:</td>
                          <td align="right">%s</td>
                        </tr>
                        <tr>
                          <td>Tên phim:</td>
                          <td align="right">%s</td>
                        </tr>
                        <tr>
                          <td>Rạp chiếu:</td>
                          <td align="right">%s</td>
                        </tr>
                        <tr>
                          <td>Phòng chiếu:</td>
                          <td align="right">%s</td>
                        </tr>
                        <tr>
                          <td>Xuất chiếu:</td>
                          <td align="right">%s</td>
                        </tr>
                        <tr>
                          <td>Ghế:</td>
                          <td align="right">%s</td>
                        </tr>
                      </table>
                
                      <hr />
                      <h3 style="color: #2e4ca6">Thông tin combo</h3>
                      %s
                
                      <hr />
                      <h3 style="color: #2e4ca6">Thông tin khuyến mãi</h3>
                      <table width="100%%" cellspacing="0" cellpadding="5">
                        <tr>
                          <td>Giảm giá:</td>
                          <td align="right">50.000đ</td>
                        </tr>
                        <tr>
                          <td>Điểm cineman:</td>
                          <td align="right">10.000</td>
                        </tr>
                      </table>
                      <hr />
                      <h3 style="text-align: right">Tổng cộng: 100.000đ</h3>
                    </div>
                  </body>
                </html>
                """, qrCode, titleMovie, titleMovieTheater, titleCinema, dateShow, seats, snacks);

        try {
            mailService.sendInvoiceWithQRCode(
                    invoiceEntity.getEmail(),
                    "Thông tin vé và hóa đơn xem phim Cineman Cinemas",
                    body,
                    qrCode);
            log.info("Send mail success");
        } catch (WriterException | IOException e) {
            log.error("Error message: {}", e.getMessage());
        }

        return invoiceResponse;
    }


    /**
     * Tính tổng tiền của hóa đơn
     *
     * @param invoiceId id hóa đơn
     * @return Double
     */
    @Override
    public Double getTotalMoney(Long invoiceId) {
        InvoiceEntity invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new DataNotFoundException("Invoice not found"));
        return Optional.ofNullable(invoice.getTickets())
                .orElse(Collections.emptyList())
                .stream()
                .map(TicketEntity::getPrice)
                .filter(Objects::nonNull)
                .reduce(0.0, Double::sum);
    }


    private InvoiceEntity getInvoice(UserEntity customer, UserEntity staff) {
        List<InvoiceEntity> invoices = invoiceRepository.findByCustomerAndStaffAndStatus(customer, staff, InvoiceStatus.PENDING);
        InvoiceEntity existingInvoice = null;
        for (InvoiceEntity invoice : invoices) {
            // Chỉ lấy invoice đang ở trang thái PENDING đầu tiên //
            if (invoice.getTickets().isEmpty()) {
                if (existingInvoice == null) {
                    existingInvoice = invoice;
                } else {
                    // Xóa những invoice ở khác ở trang thái PENDING //
                    invoiceRepository.delete(invoice);
                }
            }
        }
        return existingInvoice;
    }

    /**
     * Chuyển đổi từ InvoiceEntity sang InvoiceResponse
     *
     * @param invoice InvoiceEntity
     * @return InvoiceResponse
     */
    private InvoiceResponse toInvoiceResponse(InvoiceEntity invoice) {

        Double totalMoneyOfTickets = ticketService.getTotalMoneyOfTickets(invoice.getId());

        return InvoiceResponse.builder()
                .id(invoice.getId())
                .email(invoice.getEmail())
                .phoneNumber(invoice.getPhoneNumber())
                .paymentMethod(invoice.getPaymentMethod())
                .totalTicket(invoice.getTotalTicket())
                .customerId(invoice.getCustomer() != null ? invoice.getCustomer().getUserId() : null)
                .staffId(invoice.getStaff() != null ? invoice.getStaff().getUserId() : null)
                .status(invoice.getStatus())
                .totalMoney(
                        Optional.ofNullable(invoice.getTickets())
                                .orElse(Collections.emptyList())
                                .stream()
                                .map(TicketEntity::getPrice)
                                .filter(Objects::nonNull)
                                .reduce(0.0, Double::sum)
                )
                .totalMoneyTicket(totalMoneyOfTickets)
                .createdAt(invoice.getCreatedAt())
                .updatedAt(invoice.getUpdatedAt())
                .tickets(Optional.ofNullable(invoice.getTickets())
                        .orElse(Collections.emptyList()))
                .build();
    }

}
