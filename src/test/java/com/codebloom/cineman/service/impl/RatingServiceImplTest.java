package com.codebloom.cineman.service.impl;

import com.codebloom.cineman.common.enums.InvoiceStatus;
import com.codebloom.cineman.common.enums.TicketStatus;
import com.codebloom.cineman.common.enums.UserStatus;
import com.codebloom.cineman.controller.request.RatingRequest;
import com.codebloom.cineman.controller.response.RatingResponse;
import com.codebloom.cineman.exception.DataNotFoundException;
import com.codebloom.cineman.model.*;
import com.codebloom.cineman.repository.InvoiceRepository;
import com.codebloom.cineman.repository.MovieRepository;
import com.codebloom.cineman.repository.TicketRepository;
import com.codebloom.cineman.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class RatingServiceImplTest {

    @InjectMocks
    private RatingServiceImpl ratingService;

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private InvoiceRepository invoiceRepository;

    @Mock
    private MovieRepository movieRepository;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    private TicketEntity buildTicket(Long ticketId, Long userId, Integer movieId, Integer rating, Date endTime, TicketStatus ticketStatus) {
        UserEntity user = UserEntity.builder()
                .userId(userId)
                .email("user@example.com")
                .status(UserStatus.ACTIVE)
                .build();

        MovieEntity movie = MovieEntity.builder()
                .movieId(movieId)
                .title("Inception")
                .rating(null)
                .build();

        ShowTimeEntity showTime = ShowTimeEntity.builder()
                .id(1L)
                .movie(movie)
                .endTime(endTime)
                .build();

        TicketEntity ticket = TicketEntity.builder()
                .id(ticketId)
                .status(ticketStatus)
                .rating(rating)
                .showTime(showTime)
                .build();

        InvoiceEntity invoice = InvoiceEntity.builder()
                .invoiceId(101L)
                .status(InvoiceStatus.PAID)
                .customer(user)
                .tickets(List.of(ticket))
                .build();

        ticket.setInvoice(invoice);
        return ticket;
    }

    @Test
    void saveRating_ShouldSaveAndReturnResponse() {
        Long userId = 1L;
        Long ticketId = 10L;
        Integer movieId = 100;

        RatingRequest request = new RatingRequest(ticketId, 5);
        TicketEntity ticket = buildTicket(ticketId, userId, movieId, null, new Date(System.currentTimeMillis() - 3600 * 1000), TicketStatus.PURCHASED);

        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));
        when(ticketRepository.findAverageRatingByMovieId(movieId)).thenReturn(5.0);
        when(ticketRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        RatingResponse response = ratingService.saveRating(userId, request);

        assertThat(response.getRating()).isEqualTo(5);
        assertThat(response.getTicketId()).isEqualTo(ticketId);
        assertThat(response.getMovieId()).isEqualTo(movieId);
        assertThat(response.getUserEmail()).isEqualTo("user@example.com");

        verify(ticketRepository).save(ticket);
        verify(movieRepository).save(ticket.getShowTime().getMovie());
    }

    @Test
    void saveRating_ShouldThrow_WhenTicketNotFound() {
        when(ticketRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> ratingService.saveRating(1L, new RatingRequest(1L, 4)))
                .isInstanceOf(DataNotFoundException.class)
                .hasMessage("Không tìm thấy vé");
    }

    @Test
    void saveRating_ShouldThrow_WhenTicketNotBelongToUser() {
        TicketEntity ticket = buildTicket(1L, 2L, 100, null, new Date(), TicketStatus.PURCHASED);
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));

        assertThatThrownBy(() -> ratingService.saveRating(1L, new RatingRequest(1L, 3)))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Bạn không có quyền đánh giá vé này");
    }

    @Test
    void saveRating_ShouldThrow_WhenMovieNotEnded() {
        TicketEntity ticket = buildTicket(1L, 1L, 100, null, new Date(System.currentTimeMillis() + 3600 * 1000), TicketStatus.PURCHASED);
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));

        assertThatThrownBy(() -> ratingService.saveRating(1L, new RatingRequest(1L, 3)))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Chỉ được đánh giá sau khi buổi chiếu đã kết thúc");
    }

    @Test
    void saveRating_ShouldThrow_WhenAlreadyRated() {
        TicketEntity ticket = buildTicket(1L, 1L, 100, 4, new Date(System.currentTimeMillis() - 3600 * 1000), TicketStatus.PURCHASED);
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));

        assertThatThrownBy(() -> ratingService.saveRating(1L, new RatingRequest(1L, 3)))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Vé này đã được đánh giá. Không thể đánh giá lại.");
    }

    @Test
    void getRatingsByEmail_ShouldReturnListOfRatings() {
        String email = "user@example.com";
        Long userId = 1L;
        Integer movieId = 100;

        UserEntity user = UserEntity.builder()
                .userId(userId)
                .email(email)
                .status(UserStatus.ACTIVE)
                .build();

        TicketEntity ticket = buildTicket(10L, userId, movieId, 4, new Date(), TicketStatus.PURCHASED);
        InvoiceEntity invoice = ticket.getInvoice();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(invoiceRepository.findByEmail(email)).thenReturn(List.of(invoice));

        List<RatingResponse> ratings = ratingService.getRatingsByEmail(email);

        assertThat(ratings).hasSize(1);
        assertThat(ratings.get(0).getUserEmail()).isEqualTo(email);
    }

    @Test
    void getRatingsByUser_ShouldReturnRatingsList() {
        Long userId = 1L;
        Integer movieId = 100;

        TicketEntity ticket = buildTicket(11L, userId, movieId, 3, new Date(), TicketStatus.PURCHASED);
        when(ticketRepository.findAllByUserId(userId)).thenReturn(List.of(ticket));

        List<RatingResponse> result = ratingService.getRatingsByUser(userId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getRating()).isEqualTo(3);
    }

    @Test
    void getRatingByTicket_ShouldReturnRatingResponse() {
        Long userId = 1L;
        TicketEntity ticket = buildTicket(12L, userId, 200, 4, new Date(), TicketStatus.PURCHASED);

        when(ticketRepository.findById(12L)).thenReturn(Optional.of(ticket));

        RatingResponse response = ratingService.getRatingByTicket(userId, 12L);

        assertThat(response.getRating()).isEqualTo(4);
        assertThat(response.getMovieTitle()).isEqualTo("Inception");
    }

    @Test
    void getRatingByTicket_ShouldThrow_WhenNotFound() {
        when(ticketRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> ratingService.getRatingByTicket(1L, 999L))
                .isInstanceOf(DataNotFoundException.class)
                .hasMessage("Không tìm thấy vé");
    }

    @Test
    void getRatingsByMovie_ShouldReturnAllRatingsForMovie() {
        Integer movieId = 100;
        TicketEntity ticket = buildTicket(13L, 1L, movieId, 3, new Date(), TicketStatus.PURCHASED);

        when(ticketRepository.findAllByMovieId(movieId)).thenReturn(List.of(ticket));

        List<RatingResponse> responses = ratingService.getRatingsByMovie(movieId);

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getRating()).isEqualTo(3);
    }

    @Test
    void getRatingsByUserAndMovie_ShouldReturnFilteredRatings() {
        Long userId = 1L;
        Integer movieId = 100;
        TicketEntity ticket = buildTicket(14L, userId, movieId, 1, new Date(), TicketStatus.PURCHASED);

        InvoiceEntity invoice = ticket.getInvoice();
        UserEntity user = invoice.getCustomer();
        user.setCustomerInvoices(List.of(invoice));

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        List<RatingResponse> result = ratingService.getRatingsByUserAndMovie(userId, movieId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getMovieId()).isEqualTo(movieId);
        assertThat(result.get(0).getRating()).isEqualTo(1);
    }

    @Test
    void getRatingsByUserAndMovie_ShouldThrow_WhenUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> ratingService.getRatingsByUserAndMovie(1L, 100))
                .isInstanceOf(DataNotFoundException.class)
                .hasMessage("Không tìm thấy người dùng");
    }
}
