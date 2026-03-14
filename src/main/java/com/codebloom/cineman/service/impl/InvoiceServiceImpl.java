package com.codebloom.cineman.service.impl;

import com.codebloom.cineman.common.enums.PaymentMethod;
import com.codebloom.cineman.common.enums.TicketStatus;
import com.codebloom.cineman.common.enums.UserStatus;
import com.codebloom.cineman.common.utils.XStr;
import com.codebloom.cineman.controller.response.*;
import com.codebloom.cineman.controller.util.NumberFormatter;
import com.codebloom.cineman.exception.ConflictException;
import com.codebloom.cineman.exception.DataExistingException;
import com.codebloom.cineman.model.*;
import com.codebloom.cineman.repository.PromotionRepository;
import com.codebloom.cineman.repository.TicketRepository;
import com.codebloom.cineman.service.UserPointHistoryService;
import com.codebloom.cineman.service.mail.MailService;
import com.google.zxing.WriterException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import com.codebloom.cineman.common.enums.InvoiceStatus;
import com.codebloom.cineman.controller.request.InvoiceCreateRequest;
import com.codebloom.cineman.controller.request.InvoiceUpdateRequest;
import com.codebloom.cineman.exception.DataNotFoundException;
import com.codebloom.cineman.repository.InvoiceRepository;
import com.codebloom.cineman.repository.UserRepository;
import com.codebloom.cineman.service.InvoiceService;
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
    private final UserPointHistoryService userPointHistoryService;
    private final ModelMapper mapper;

    /**
     * Tìm kiểm hóa đơn theo userId, showTimeId, trang thái
     *
     * @param id         userId
     * @param showTimeId showTimeId
     * @return InvoiceResponse
     */
    @Override
    public InvoiceResponse findByUserIdAndShowTimeId(Long id, Long showTimeId) {

        List<InvoiceEntity> invoiceEntity = invoiceRepository.findByCustomerOrStaff(id);

        // Lưu thông tin hóa đơn tìm thấy //
        InvoiceEntity existInvoice = null;

        // Duyệt qua tất cả các hóa đơn tìm thấy //
        for (InvoiceEntity invoice : invoiceEntity) {

            // Nếu chưa thanh toán và không phải là Hủy //
            if (invoice.getStatus() != InvoiceStatus.PAID && invoice.getStatus() != InvoiceStatus.CANCELLED) {

                // Nếu tìm thấy hóa đơn rồi thì xóa các hóa đơn còn lại chưa thanh toán đi //
                if (existInvoice != null && invoice.getTickets().isEmpty()) {
                    invoiceRepository.delete(invoice);
                    continue;
                }

                // Nếu hóa đơn đã có vé //
                if (!invoice.getTickets().isEmpty()) {

                    // Kiểm tra showTime có trùng hay không //
                    if (!invoice.getTickets().get(0).getShowTime().getId().equals(showTimeId)) {
                        continue;
                    } else {
                        existInvoice = invoice;
                        continue;
                    }
                }
                // trường hợp chưa có show time cho hóa đơn //
                existInvoice = invoice;
            }
        }
        if (existInvoice == null) {
            return null;
        } else {
            return toInvoiceResponse(existInvoice);
        }
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
        if (invoice.getCustomerId() != null) {
            customer = userRepository.findById(invoice.getCustomerId())
                    .orElseThrow(() -> new DataNotFoundException("Customer not found"));
        }
        if (invoice.getStaffId() != null) {
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

        if (invoice.getPromotionId() != null && invoice.getCustomerId() != null) {

            Optional<InvoiceEntity> existingInvoice = invoiceRepository.findByUserIdAndPromotionId(invoice.getCustomerId(), invoice.getPromotionId());
            if (existingInvoice.isPresent() && existingInvoice.get().getId() != id) {
                throw new DataExistingException("Giảm giá đã được sử dụng cho hóa đơn khác !");
            }

            promotion = promotionRepository.findById(invoice.getPromotionId())
                    .orElseThrow(() -> new DataNotFoundException("Không tìm thấy khuyến mãi !"));
        }

        InvoiceEntity invoiceEntity = invoiceRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Invoice not found"));
        if (invoiceEntity.getStatus() == InvoiceStatus.PAID || invoiceEntity.getStatus() == InvoiceStatus.CANCELLED) {
            throw new DataNotFoundException("Invoice not found");
        }

        if (promotion != null) {
            int quantity = promotion.getQuantity();
            if (quantity == 0) {
                throw new DataNotFoundException("Khuyến mái hóa đã đươc sử dụng hết !");
            } else {
                promotion.setQuantity(promotion.getQuantity() - 1);
            }
            promotionRepository.save(promotion);
        }

        invoiceEntity.setEmail(invoice.getEmail());
        invoiceEntity.setPhoneNumber(invoice.getPhoneNumber());
        invoiceEntity.setPaymentMethod(invoice.getPaymentMethod());
        invoiceEntity.setTotalTicket(invoice.getTotalTicket());
        invoiceEntity.setTotalAmount(invoice.getTotalAmount());
        invoiceEntity.setCustomer(customer);
        invoiceEntity.setStaff(staff);
        invoiceEntity.setPromotion(promotion);
        invoiceEntity.setStatus(invoice.getInvoiceStatus());
        invoiceEntity.setUpdatedAt(new Date());
        invoiceRepository.save(invoiceEntity);

        if (invoice.getInvoiceStatus() == InvoiceStatus.PAID) {
            //  cập nhật trạng thái của vé đã thanh toán tại quầy
            invoiceEntity.getTickets().forEach(ticket -> {
                ticket.setStatus(TicketStatus.SOLD);
                ticketRepository.save(ticket);
            });

            // Tích điểm cho khách hàng nếu có customerId //
            if (invoice.getCustomerId() != null) {
                userPointHistoryService.earnPoints(invoiceEntity);
            }

            // Thêm QR code cho hóa đơn
            String qrCode = xStr.getKey();
            invoiceEntity.setQrCode(qrCode);
            invoiceEntity = invoiceRepository.save(invoiceEntity);

            // Gửi mail invoice cho khách hàng
            sendMailInvoice(invoiceEntity, invoiceEntity.getQrCode());

        }

        return toInvoiceResponse(invoiceEntity);
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
    public InvoiceResponse updateTnx(Long id, String tnxRef, Object... args) {

        log.info("updateTnx id: {}, tnxRef: {}, promotionId: {} totalAmount: {}", id, tnxRef, args.length > 0 ? args[0] : null, args.length > 1 ? args[1] : null);
        InvoiceEntity invoiceEntity = invoiceRepository.findByIdAndStatusNot(id, InvoiceStatus.CANCELLED)
                .orElseThrow(() -> new DataNotFoundException("Invoice not found"));
        invoiceEntity.setVnTxnRef(tnxRef);
        invoiceEntity.setTotalAmount(args[1] != null ? (Double) args[1] : invoiceEntity.getTotalAmount());


        // Nếu có áp dụng khuyến mái cho hóa đơn này //
        if (args[0] != null) {
            PromotionEntity promotion = invoiceEntity.getPromotion();
            if (promotion != null) {
                throw new DataExistingException("Hóa đơn đã được áp dụng khuyến mãi khác !");
            }

            // Khuyến mãi đã được áp dụng cho hóa đơn khác của khách hàng này //
            if (invoiceRepository.findByUserIdAndPromotionId(invoiceEntity.getCustomer().getUserId(), (Long) args[0]).isPresent()) {
                throw new DataExistingException("Giảm giá đã được sử dụng cho hóa đơn khác !");
            }

            promotion = promotionRepository.findById((Long) args[0])
                    .orElseThrow(() -> new DataNotFoundException("Promotion not found"));
            if (promotion.getQuantity() == 0) {
                throw new DataNotFoundException("Promotion not found");
            }
            if (invoiceRepository.findByCustomerAndPromotion(invoiceEntity.getCustomer(), promotion).isPresent()) {
                throw new DataExistingException("Giảm giá đã được bạn sử dụng cho hóa đơn khác !");
            }
            promotion.setQuantity(promotion.getQuantity() - 1);
            promotion = promotionRepository.save(promotion);
            invoiceEntity.setPromotion(promotion);
        }
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
        if(invoiceEntity.getStaff() != null) {
            invoiceEntity.setPaymentMethod(PaymentMethod.CASH);
        }else{
            invoiceEntity.setPaymentMethod(PaymentMethod.BANK_TRANSFER);
        }
        InvoiceResponse invoiceResponse = toInvoiceResponse(invoiceRepository.save(invoiceEntity));

        // Câp nhật tràng thái cho ticket //
        invoiceEntity.getTickets().forEach(ticket -> {
            ticket.setStatus(TicketStatus.SOLD);
            ticketRepository.save(ticket);
        });

        // Tích điểm hóa đơn cho khách hàng //
        userPointHistoryService.earnPoints(invoiceEntity);

        // Gửi mail invoice cho khách hàng //
        sendMailInvoice(invoiceEntity, qrCode);


        return invoiceResponse;
    }

    private void sendMailInvoice(InvoiceEntity invoiceEntity, String qrCode) {
        // Thông tin cần có hóa đơn //
        String titleMovie = invoiceEntity.getTickets().get(0).getShowTime().getMovie().getTitle();
        String titleMovieTheater = invoiceEntity.getTickets().get(0).getShowTime().getCinemaTheater().getMovieTheater().getName();
        String titleCinema = invoiceEntity.getTickets().get(0).getShowTime().getCinemaTheater().getName();
        String dateShow = invoiceEntity.getTickets().get(0).getShowTime().getShowDate().toString() + " - " + invoiceEntity.getTickets().get(0).getShowTime().getStartTime().toString();
        StringBuilder seats = new StringBuilder();
        Double totalMoneyTicket = 0.0;
        int i = 0;
        for (TicketEntity ticket : invoiceEntity.getTickets()) {
            totalMoneyTicket += ticket.getPrice();
            SeatEntity seat = ticket.getSeat();
            if (i > 0) {
                seats.append(", ").append(ticket.getSeat().getLabel());
                i++;
                continue;
            }
            i++;
            seats.append(seat.getLabel());
        }

        String totalPromotion = invoiceEntity.getPromotion() != null ? String.valueOf((invoiceEntity.getPromotion().getDiscount() * totalMoneyTicket)) : "0";

        Double totalMoney = invoiceEntity.getTotalAmount();

        double moneyChangeFromPoint = invoiceEntity.getUserPointHistories()
                .stream()
                .filter(userPointHistory -> userPointHistory.getChangePoint() < 0)
                .mapToDouble(UserPointHistoryEntity::getChangePoint)
                .sum();
        moneyChangeFromPoint = Math.abs(moneyChangeFromPoint);

        int savePointChange = invoiceEntity.getUserPointHistories()
                .stream()
                .filter(userPointHistory -> userPointHistory.getChangePoint() > 0)
                .mapToInt(UserPointHistoryEntity::getChangePoint)
                .sum();

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
                          Mã QR Code này dùng để quét và nhận vé của bạn tại rạp chiếu Poly Cinemas
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
                      %s
                      <h3 style="color: #2e4ca6">Thông tin khuyến mãi</h3>
                      <table width="100%%" cellspacing="0" cellpadding="5">
                        <tr>
                          <td>Giảm giá voucher:</td>
                          <td align="right">%s đ</td>
                        </tr>
                         <tr>
                          <td>Đổi điểm tích lũy:</td>
                          <td align="right">%s đ</td>
                        </tr>
                        <tr>
                          <td>Điểm tích lũy cho khách hàng:</td>
                          <td align="right">%s đ</td>
                        </tr>
                      </table>
                      <hr />
                      <h3 style="text-align: right">Tổng tiền phải trả: %s</h3>
                    </div>
                  </body>
                </html>
                """, qrCode, titleMovie, titleMovieTheater, titleCinema, dateShow, seats, generateInfoSnacks(invoiceEntity)[0], totalPromotion, NumberFormatter.format(moneyChangeFromPoint), savePointChange, NumberFormatter.format(totalMoney));

        try {
            mailService.sendInvoiceWithQRCode(
                    invoiceEntity.getEmail(),
                    "Thông tin vé và hóa đơn xem phim Poly Cinemas",
                    body,
                    qrCode);
            log.info("Send mail success");
        } catch (WriterException | IOException e) {
            log.error("Error message: {}", e.getMessage());
        }
    }

    private Object[] generateInfoSnacks(InvoiceEntity invoiceEntity) {
        // Thông tin combo //
        List<DetailBookingSnackEntity> detailBookingSnacks = invoiceEntity.getDetailBookingSnacks();
        double totalMoneySnack = 0.0;

        // Nếu không có mua thêm combo thi return "" //
        if (detailBookingSnacks.isEmpty()) {
            return new Object[]{"", 0.0};
        }

        // Trường hợp có mua thêm combo //
        StringBuilder snacks = new StringBuilder().append("""
                <h3 style="color: #2e4ca6">Thông tin combo</h3>
                <table width="100%" cellspacing="0" cellpadding="5">
                """);
        for (DetailBookingSnackEntity detailBookingSnack : detailBookingSnacks) {
            totalMoneySnack += detailBookingSnack.getSnack().getUnitPrice() * detailBookingSnack.getTotalSnack();
            String content = String.format("""
                    <tr>
                        <td>%s</td>
                        <td align="right">%d x %.2f</td>
                    </tr>
                    """, detailBookingSnack.getSnack().getSnackName(), detailBookingSnack.getTotalSnack(), detailBookingSnack.getSnack().getUnitPrice());
            snacks.append(content);
        }
        snacks.append("""
                </table>
                <hr /
                """);
        return new Object[]{snacks.toString(), totalMoneySnack};
    }

    /**
     * Áp dụng khuyến mãi cho hóa đơn
     *
     * @param id          id hóa đơn
     * @param promotionId id khuyến mãi
     * @return InvoiceResponse
     */
    @Override
    public InvoiceResponse applyPromotionToInvoice(Long id, Long promotionId) {
        InvoiceEntity invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Invoice not found"));
        PromotionEntity promotion = promotionRepository.findById(promotionId)
                .orElseThrow(() -> new DataNotFoundException("Promotion not found"));

        if (invoice.getStatus() == InvoiceStatus.PAID || invoice.getStatus() == InvoiceStatus.CANCELLED) {
            throw new ConflictException("Invoice has been paid or cancelled");
        }
        if (invoice.getPromotion() != null) {
            throw new DataExistingException("Invoice has been applied promotion");
        }

        invoice.setPromotion(promotion);
        return toInvoiceResponse(invoiceRepository.save(invoice));
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

    @Override
    public InvoiceDetailResponse findByQrCode(String qrCode) {
        InvoiceEntity invoice = invoiceRepository.findByQrCode(qrCode)
                .orElseThrow(() -> new DataNotFoundException("Không tìm thấy hóa đơn"));
        if (invoice.getStatus() != InvoiceStatus.CANCELLED) {
            return toInvoiceDetailResponse(invoice);
        }
        return null;
    }

    /**
     * Lấy tất cả hóa đơn của người dùng
     * @param userId id người dùng
     * @return List<InvoiceResponse>
     */
    @Override
    public List<InvoiceDetailResponse> findByUserId(Long userId) {
        UserEntity user = userRepository.findByUserIdAndStatus(userId, UserStatus.ACTIVE)
                .orElseThrow(() -> new DataNotFoundException("User not found"));
        List<InvoiceEntity> invoices = invoiceRepository.findByCustomerOrStaff(user, user);
        List<InvoiceDetailResponse> invoiceDetailResponses = new ArrayList<>();
        for (InvoiceEntity invoice : invoices) {
            if (invoice.getStatus() == InvoiceStatus.PENDING || invoice.getStatus() == InvoiceStatus.CANCELLED) {
                continue;
            }

            if (invoice.getTickets().isEmpty()) {
                continue;
            }

            double totalMoneyTicket = !invoice.getTickets().isEmpty() ? invoice.getTickets().stream()
                    .mapToDouble(TicketEntity::getPrice)
                    .sum(): 0.0;
            double totalMoneySnack = !invoice.getDetailBookingSnacks().isEmpty() ? invoice.getDetailBookingSnacks().stream()
                    .mapToDouble(DetailBookingSnackEntity::getTotalMoney)
                    .sum(): 0.0;
            double totalMoneyDiscount = !invoice.getUserPointHistories().isEmpty()  ? invoice.getUserPointHistories().stream()
                            .filter(userPointHistory -> userPointHistory.getChangePoint() < 0)
                            .mapToDouble(UserPointHistoryEntity::getChangePoint)
                            .sum() : 0.0;
            double totalMoneyPromotion = invoice.getPromotion() != null ? invoice.getPromotion().getDiscount() * totalMoneyTicket : 0.0;
            invoiceDetailResponses.add(toInvoiceDetailResponse(invoice));
        }
        return invoiceDetailResponses;
    }

    /**
     * Lấy tất cả hóa đơn theo ngày tạo và theo rạp chiếu
     * @param createdAt ngày tạo
     * @param movieTheaterId id rạp chiếu
     * @return List<InvoiceDetailPageResponse>
     */
    @Override
    public InvoiceDetailPageResponse findAllInvoicesByCreatedAtAndMovieTheater(Date createdAt, Integer pageNo, Integer pageSize, Integer... movieTheaterId) {
        Sort sort = Sort.by("created_at").descending();
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize, sort);
        Page<InvoiceEntity> page = null;
        InvoiceDetailPageResponse invoiceDetailPageResponse = new InvoiceDetailPageResponse();
        if (movieTheaterId.length > 0) {
            page = invoiceRepository.findAllByCreatedDate(createdAt, pageRequest);
            List<InvoiceDetailResponse> invoiceDetailResponses = new ArrayList<>();
            for (InvoiceEntity invoice : page.getContent()) {
                if (invoice.getStatus() == InvoiceStatus.PENDING || invoice.getStatus() == InvoiceStatus.CANCELLED) {
                    continue;
                }
                if (invoice.getTickets().isEmpty()) {
                    continue;
                }
                Integer theaterId = invoice.getTickets().get(0).getShowTime().getCinemaTheater().getMovieTheater().getMovieTheaterId();
                if (theaterId.equals(movieTheaterId[0])) {
                    invoiceDetailResponses.add(toInvoiceDetailResponse(invoice));
                }
            }
            invoiceDetailPageResponse.setInvoiceDetailResponses(invoiceDetailResponses);
        }else{
            page = invoiceRepository.findAllByCreatedAt(createdAt, PageRequest.of(pageNo, pageSize, Sort.by("createdAt").descending()));
            List<InvoiceDetailResponse> invoiceDetailResponses = new ArrayList<>();
            for (InvoiceEntity invoice : page.getContent()) {
                if (invoice.getStatus() == InvoiceStatus.PENDING || invoice.getStatus() == InvoiceStatus.CANCELLED) {
                    continue;
                }
                if (invoice.getTickets().isEmpty()) {
                    continue;
                }
                invoiceDetailResponses.add(toInvoiceDetailResponse(invoice));
            }
            invoiceDetailPageResponse.setInvoiceDetailResponses(invoiceDetailResponses);
        }

        // Xử lý phân trang //
        invoiceDetailPageResponse.setMeta(toMetaResponse(page));
        return invoiceDetailPageResponse;
    }

    @Override
    public InvoiceTicketResponse findInvoiceByQRCode(String qrCode) {
        InvoiceEntity invoice = invoiceRepository.findByQrCode(qrCode)
                .orElseThrow(() -> new DataNotFoundException("Không tìm thấy hóa đơn !"));
        if (invoice.getStatus() != InvoiceStatus.CANCELLED) {
            throw new DataNotFoundException("Không tìm thấy hóa đơn");
        }

        List<DetailBookingSnackResponse> detailBookingSnackResponses = null;
        if (!invoice.getDetailBookingSnacks().isEmpty()) {
            detailBookingSnackResponses = new ArrayList<>();
            List<DetailBookingSnackEntity> detailBookingSnackEntities = invoice.getDetailBookingSnacks();
            for (DetailBookingSnackEntity detailBookingSnackEntity : detailBookingSnackEntities) {
                detailBookingSnackResponses.add(this.convert(detailBookingSnackEntity));
            }
        }

        int changePoint = invoice.getUserPointHistories().stream().filter(
                userPointHistoryEntity -> userPointHistoryEntity.getChangePoint() < 0
        ).mapToInt(UserPointHistoryEntity::getChangePoint).sum();

        double changeMoney = changePoint;

        int savePoint = invoice.getUserPointHistories().stream().filter(
                userPointHistoryEntity -> userPointHistoryEntity.getChangePoint() > 0
        ).mapToInt(UserPointHistoryEntity::getChangePoint).sum();

        return InvoiceTicketResponse.builder()
                .invoiceEntity(invoice)
                .showTimeEntity(invoice.getTickets().get(0).getShowTime())
                .movieEntity(invoice.getTickets().get(0).getShowTime().getMovie())
                .detailBookingSnackResponse(detailBookingSnackResponses)
                .changePoint(changePoint)
                .changeMoney(changeMoney)
                .savePoint(savePoint)
                .promotionEntity(invoice.getPromotion())
                .customer(convertToUserResponse(invoice.getCustomer()))
                .build();
    }

    @Override
    public void updateStatusUsed(String qrCode) {
        InvoiceEntity invoice = invoiceRepository.findByQrCode(qrCode)
                .orElseThrow(() -> new DataNotFoundException("Không tìm thấy hóa đơn !"));
        invoice.setStatus(InvoiceStatus.USED);
        invoiceRepository.save(invoice);
    }


    private MetaResponse toMetaResponse(Page<InvoiceEntity> page) {
        return MetaResponse.builder()
                .currentPage(page.getNumber())
                .totalElements((int) page.getTotalElements())
                .totalPages(page.getTotalPages())
                .pageSize(page.getSize())
                .build();
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
                .totalMoney(totalMoneyOfTickets == 0 ? 0 : invoice.getTotalAmount() == null ? 0 : invoice.getTotalAmount())
                .totalMoneyTicket(totalMoneyOfTickets)
                .promotionId(invoice.getPromotion() != null ? invoice.getPromotion().getId() : null)
                .createdAt(invoice.getCreatedAt())
                .updatedAt(invoice.getUpdatedAt())
                .tickets(Optional.ofNullable(invoice.getTickets())
                        .orElse(Collections.emptyList()))
                .build();
    }

    private InvoiceDetailResponse toInvoiceDetailResponse(InvoiceEntity invoice) {
        List<TicketResponse> ticketResponses = invoice.getTickets().stream()
                .map(this::convertToTicketResponse)
                .toList();
        List<DetailBookingSnackResponse> detailBookingSnackResponses = invoice.getDetailBookingSnacks().stream()
                .map(this::convert)
                .toList();
        double totalMoneyTicket = !invoice.getTickets().isEmpty() ? invoice.getTickets().stream()
                .mapToDouble(TicketEntity::getPrice)
                .sum(): 0.0;
        double totalMoneySnack = !invoice.getDetailBookingSnacks().isEmpty() ? invoice.getDetailBookingSnacks().stream()
                .mapToDouble(DetailBookingSnackEntity::getTotalMoney)
                .sum(): 0.0;
        double totalMoneyDiscount = !invoice.getUserPointHistories().isEmpty()  ? invoice.getUserPointHistories().stream()
                .filter(userPointHistory -> userPointHistory.getChangePoint() < 0)
                .mapToDouble(UserPointHistoryEntity::getChangePoint)
                .sum() : 0.0;
        double totalMoneyPromotion = invoice.getPromotion() != null ? invoice.getPromotion().getDiscount() * totalMoneyTicket : 0.0;
        return InvoiceDetailResponse.builder()
                .id(invoice.getId())
                .code(invoice.getQrCode())
                .email(invoice.getEmail())
                .phoneNumber(invoice.getPhoneNumber())
                .paymentMethod(invoice.getPaymentMethod())
                .totalMoneyTicket(totalMoneyTicket)
                .totalMoneySnack(totalMoneySnack)
                .totalMoneyDiscount(totalMoneyDiscount)
                .totalTicket(invoice.getTotalTicket())
                .totalMoney(invoice.getTotalAmount())
                .totalMoneyPromotion(totalMoneyPromotion)
                .status(invoice.getStatus())
                .customer(invoice.getCustomer())
                .staff(invoice.getStaff())
                .promotion(invoice.getPromotion())
                .createdAt(invoice.getCreatedAt())
                .updatedAt(invoice.getUpdatedAt())
                .tickets(ticketResponses)
                .cinemaTheater(invoice.getTickets() == null ? null : invoice.getTickets().get(0).getShowTime().getCinemaTheater())
                .detailBookingSnacks(detailBookingSnackResponses)
                .showTime(invoice.getTickets() == null ? null : invoice.getTickets().get(0).getShowTime())
                .movie(invoice.getTickets() == null ? null : invoice.getTickets().get(0).getShowTime().getMovie())
                .movieTheater(invoice.getTickets() == null ? null : invoice.getTickets().get(0).getShowTime().getCinemaTheater().getMovieTheater())
                .build();
    }


    /**
     * Convert DetailBookingSnackEntity to DetailBookingSnackResponse
     * @param entity DetailBookingSnackEntity
     * @return DetailBookingSnackResponse
     */
    private DetailBookingSnackResponse convert(DetailBookingSnackEntity entity) {
        DetailBookingSnackResponse response = mapper.map(entity, DetailBookingSnackResponse.class);
        Double totalMoney = entity.getTotalSnack() * entity.getSnack().getUnitPrice();
        response.setTotalMoney(totalMoney);
        response.setSnack(entity.getSnack());
        return response;
    }

    /**
     * Hàm nội bộ để thực hiện convert
     * @param user UserEntity
     * @return UserResponse
     */
    private UserResponse convertToUserResponse(UserEntity user) {
        MovieTheaterEntity movieTheater = user.getMovieTheater();
        UserResponse userResponse = UserResponse.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .phoneNumber(user.getPhoneNumber())
                .address(user.getAddress())
                .dateOfBirth(user.getDateOfBirth())
                .gender(user.getGender().name())
                .savePoint(user.getSavePoint())
                .facebookId(user.getFacebookId())
                .googleId(user.getGoogleId())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .membershipRank(user.getMembershipRank())
                .movieTheater(movieTheater)
                .avatar(user.getAvatar())
                .build();
        List<RoleEntity> roles = new ArrayList<>();
        user.getUserRoles().forEach(userRoleEntity -> roles.add(userRoleEntity.getRole()));
        userResponse.setRoles(roles);
        userResponse.setStatus(user.getStatus().toString());
        return userResponse;
    }


    private TicketResponse convertToTicketResponse(TicketEntity savedTicket) {
        return TicketResponse.builder()
                .id(savedTicket.getId())
                .showTime(savedTicket.getShowTime())
                .ticketType(savedTicket.getTicketType())
                .price(savedTicket.getPrice())
                .status(savedTicket.getStatus())
                .limitTime(savedTicket.getLimitTime())
                .createBooking(savedTicket.getCreateBooking())
                .seat(savedTicket.getSeat())
                .build();
    }

}
