package com.codebloom.cineman.service.impl;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.codebloom.cineman.common.enums.InvoiceStatus;
import com.codebloom.cineman.common.enums.TicketStatus;
import com.codebloom.cineman.common.enums.UserStatus;
import com.codebloom.cineman.controller.request.RatingRequest;
import com.codebloom.cineman.controller.response.RatingResponse;
import com.codebloom.cineman.exception.DataNotFoundException;
import com.codebloom.cineman.model.InvoiceEntity;
import com.codebloom.cineman.model.MovieEntity;
import com.codebloom.cineman.model.TicketEntity;
import com.codebloom.cineman.model.UserEntity;
import com.codebloom.cineman.repository.InvoiceRepository;
import com.codebloom.cineman.repository.MovieRepository;
import com.codebloom.cineman.repository.TicketRepository;
import com.codebloom.cineman.repository.UserRepository;
import com.codebloom.cineman.service.RatingService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RatingServiceImpl implements RatingService {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final InvoiceRepository invoiceRepository; 
    private final MovieRepository movieRepository;

    
    
    /**
     * Người dùng chỉ được đánh giá một lần cho mỗi vé.
     *
     * @param userId ID người dùng (xác thực quyền sở hữu vé)
     * @param request Dữ liệu gồm ticketId và điểm đánh giá
     * @return RatingResponse sau khi đã lưu
     * @throws DataNotFoundException nếu không tìm thấy vé
     * @throws RuntimeException nếu người dùng không sở hữu vé, vé không hợp lệ hoặc đã đánh giá
     */
    @Override
    @Transactional
    public RatingResponse saveRating(Long userId, RatingRequest request) {
        TicketEntity ticket = ticketRepository.findById(request.getTicketId())
                .orElseThrow(() -> new DataNotFoundException("Không tìm thấy vé"));

        InvoiceEntity invoice = ticket.getInvoice();
        
        if (!ticket.getInvoice().getCustomer().getUserId().equals(userId)) {
            throw new RuntimeException("Bạn không có quyền đánh giá vé này");
        }
        
        if (invoice.getStatus() != InvoiceStatus.PAID) {
            throw new RuntimeException("Trạng thái hóa đơn không hợp lệ");
        }
      
        if (ticket.getStatus() == TicketStatus.REVERSED || ticket.getStatus() == TicketStatus.EXPIRED) {
            throw new RuntimeException("Không thể đánh giá vé đã hủy hoặc hết hạn");
        }

        if (ticket.getShowTime().getEndTime().after(new Date())) {
            throw new RuntimeException("Chỉ được đánh giá sau khi buổi chiếu đã kết thúc");
        }

        if (ticket.getRating() != null) {
            throw new RuntimeException("Vé này đã được đánh giá. Không thể đánh giá lại.");
        }

        boolean invoiceAlreadyRated = invoice.getTickets().stream()
                .anyMatch(t -> t.getRating() != null);

        if (invoiceAlreadyRated) {
            throw new RuntimeException("Hóa đơn này đã được đánh giá. Không thể đánh giá thêm vé khác.");
        }
        
        ticket.setRating(request.getRating());
        ticketRepository.save(ticket);

        MovieEntity movie = ticket.getShowTime().getMovie();
        Double avgRating = ticketRepository.findAverageRatingByMovieId(movie.getMovieId());
        movie.setRating(avgRating != null ? Math.round(avgRating * 100.0) / 100.0 : 0.0);
        movieRepository.save(movie);

        return mapToRatingResponse(ticket);
    }


    /**
     * Lấy danh sách đánh giá theo email của người dùng — chỉ nếu user đang ACTIVE.
     *
     * @param userEmail Email của người dùng
     * @return Danh sách đánh giá (rating) ứng với các vé trong các invoice có email khớp
     * @throws DataNotFoundException nếu không tìm thấy user ACTIVE
     */
    @Override
    public List<RatingResponse> getRatingsByEmail(String userEmail) {
        userRepository.findByEmail(userEmail)
            .filter(u -> u.getStatus() == UserStatus.ACTIVE)
            .orElseThrow(() -> new DataNotFoundException("Không tìm thấy người dùng đang hoạt động với email: " + userEmail));

        List<InvoiceEntity> invoices = invoiceRepository.findByEmail(userEmail);

        return invoices.stream()
                .flatMap(invoice -> invoice.getTickets().stream())
                .filter(ticket -> ticket.getRating() != null)
                .map(ticket -> {
                    RatingResponse response = mapToRatingResponse(ticket);
                    response.setUserEmail(userEmail);
                    return response;
                })
                .collect(Collectors.toList());
    }
 
    
    
    /**
     * Lấy danh sách tất cả các đánh giá mà người dùng đã thực hiện.
     *
     * @param userId ID của người dùng
     * @return Danh sách đánh giá từ các vé thuộc invoice của user
     */
    @Override
    public List<RatingResponse> getRatingsByUser(Long userId) {
        return ticketRepository.findAllByUserId(userId)
                .stream()
                .map(this::mapToRatingResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Lấy đánh giá một vé cụ thể nếu user là chủ sở hữu.
     *
     * @param userId ID người dùng
     * @param ticketId ID vé
     * @return Đánh giá của vé đó
     * @throws DataNotFoundException nếu vé không tồn tại
     * @throws RuntimeException nếu user không sở hữu vé
     */
    @Override
    public RatingResponse getRatingByTicket(Long userId, Long ticketId) {
        TicketEntity ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new DataNotFoundException("Không tìm thấy vé"));

        if (!ticket.getInvoice().getCustomer().getUserId().equals(userId)) {
            throw new RuntimeException("Bạn không có quyền xem đánh giá vé này");
        }

        return mapToRatingResponse(ticket);
    }

    
    /**
     * Lấy tất cả đánh giá cho một bộ phim từ tất cả người dùng.
     *
     * @param movieId ID phim
     * @return Danh sách đánh giá theo movieId
     */
    @Override
    public List<RatingResponse> getRatingsByMovie(Integer movieId) {
        return ticketRepository.findAllByMovieId(movieId)
                .stream()
                .filter(ticket -> ticket.getRating() != null) 
                .map(this::mapToRatingResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Lấy tất cả đánh giá của người dùng đối với một phim cụ thể.
     *
     * @param userId ID người dùng
     * @param movieId ID phim
     * @return Danh sách đánh giá của user đối với phim
     * @throws DataNotFoundException nếu không tìm thấy user
     */
    @Override
    public List<RatingResponse> getRatingsByUserAndMovie(Long userId, Integer movieId) {
        UserEntity user = userRepository.findById(userId)
            .orElseThrow(() -> new DataNotFoundException("Không tìm thấy người dùng"));

        return user.getCustomerInvoices().stream()
            .flatMap(invoice -> invoice.getTickets().stream())
            .filter(ticket -> ticket.getRating() != null &&
                              ticket.getShowTime().getMovie().getMovieId().equals(movieId))
            .map(this::mapToRatingResponse)
            .toList();
    }
    
    
    /**
     * Chuyển đổi thực thể TicketEntity thành RatingResponse.
     *
     * @param ticket Vé đã được đánh giá
     * @return RatingResponse gồm id vé, phim, điểm đánh giá cá nhân, điểm trung bình và email
     */
    private RatingResponse mapToRatingResponse(TicketEntity ticket) {
        MovieEntity movie = ticket.getShowTime().getMovie();

        return RatingResponse.builder()
                .ticketId(ticket.getId())
                .movieId(movie.getMovieId())
                .movieTitle(movie.getTitle())
                .rating(ticket.getRating())
                .averageRating(movie.getRating() != null ? movie.getRating() : 0.0)
                .userEmail(ticket.getInvoice().getCustomer().getEmail())
                .build();
    }


}
