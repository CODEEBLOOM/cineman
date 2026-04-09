package com.codebloom.cineman.service.impl;

import com.codebloom.cineman.common.enums.InvoiceStatus;
import com.codebloom.cineman.common.enums.UserStatus;
import com.codebloom.cineman.controller.request.MovieReviewRequest;
import com.codebloom.cineman.controller.response.MetaResponse;
import com.codebloom.cineman.controller.response.MovieReviewContextResponse;
import com.codebloom.cineman.controller.response.MovieReviewPageResponse;
import com.codebloom.cineman.controller.response.MovieReviewResponse;
import com.codebloom.cineman.exception.ConflictException;
import com.codebloom.cineman.exception.DataNotFoundException;
import com.codebloom.cineman.exception.ForBiddenException;
import com.codebloom.cineman.model.InvoiceEntity;
import com.codebloom.cineman.model.MovieEntity;
import com.codebloom.cineman.model.MovieReviewEntity;
import com.codebloom.cineman.model.UserEntity;
import com.codebloom.cineman.model.UserPrincipal;
import com.codebloom.cineman.repository.InvoiceRepository;
import com.codebloom.cineman.repository.MovieRepository;
import com.codebloom.cineman.repository.MovieReviewRepository;
import com.codebloom.cineman.repository.UserRepository;
import com.codebloom.cineman.service.MovieReviewService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.EnumSet;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "MOVIE_REVIEW_SERVICE")
public class MovieReviewServiceImpl implements MovieReviewService {

    private static final EnumSet<InvoiceStatus> REVIEWABLE_INVOICE_STATUSES =
            EnumSet.of(InvoiceStatus.PAID, InvoiceStatus.USED);

    private final MovieReviewRepository movieReviewRepository;
    private final MovieRepository movieRepository;
    private final InvoiceRepository invoiceRepository;
    private final UserRepository userRepository;

    @Override
    public MovieReviewPageResponse findAllByMovieId(Integer movieId, int page, int size) {
        findMovie(movieId);

        Page<MovieReviewEntity> reviewPage = movieReviewRepository.findAllByMovieMovieId(
                movieId,
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "updatedAt", "reviewId"))
        );

        List<MovieReviewResponse> reviews = reviewPage.getContent().stream()
                .map(this::toResponse)
                .toList();

        return MovieReviewPageResponse.builder()
                .meta(MetaResponse.builder()
                        .currentPage(reviewPage.getNumber())
                        .pageSize(reviewPage.getSize())
                        .totalPages(reviewPage.getTotalPages())
                        .totalElements((int) reviewPage.getTotalElements())
                        .build())
                .averageRating(getAverageRating(movieId))
                .reviewCount(getReviewCount(movieId))
                .reviews(reviews)
                .build();
    }

    @Override
    public MovieReviewContextResponse getReviewContext(Integer movieId) {
        findMovie(movieId);

        UserEntity currentUser = getCurrentAuthenticatedUser(false);
        MovieReviewResponse myReview = null;
        boolean hasReviewed = false;
        boolean canReview = false;

        if (currentUser != null) {
            myReview = movieReviewRepository.findByMovieMovieIdAndUserUserId(movieId, currentUser.getUserId())
                    .map(this::toResponse)
                    .orElse(null);
            hasReviewed = myReview != null;
            canReview = hasEligiblePurchase(currentUser.getUserId(), movieId);
        }

        return MovieReviewContextResponse.builder()
                .averageRating(getAverageRating(movieId))
                .reviewCount(getReviewCount(movieId))
                .authenticated(currentUser != null)
                .canReview(canReview)
                .hasReviewed(hasReviewed)
                .myReview(myReview)
                .build();
    }

    @Override
    @Transactional
    public MovieReviewResponse create(Integer movieId, MovieReviewRequest request) {
        MovieEntity movie = findMovie(movieId);
        UserEntity currentUser = getCurrentAuthenticatedUser(true);

        if (movieReviewRepository.existsByMovieMovieIdAndUserUserId(movieId, currentUser.getUserId())) {
            throw new ConflictException("Ban da danh gia phim nay roi");
        }

        InvoiceEntity eligibleInvoice = findLatestEligibleInvoice(currentUser.getUserId(), movieId);

        MovieReviewEntity movieReview = MovieReviewEntity.builder()
                .movie(movie)
                .user(currentUser)
                .invoice(eligibleInvoice)
                .ratingScore(request.getRatingScore())
                .comment(normalizeComment(request.getComment()))
                .build();

        return toResponse(movieReviewRepository.save(movieReview));
    }

    @Override
    @Transactional
    public MovieReviewResponse update(Integer movieId, MovieReviewRequest request) {
        UserEntity currentUser = getCurrentAuthenticatedUser(true);
        MovieReviewEntity review = movieReviewRepository.findByMovieMovieIdAndUserUserId(movieId, currentUser.getUserId())
                .orElseThrow(() -> new DataNotFoundException("Khong tim thay danh gia cua ban cho phim nay"));

        review.setRatingScore(request.getRatingScore());
        review.setComment(normalizeComment(request.getComment()));
        return toResponse(movieReviewRepository.save(review));
    }

    @Override
    @Transactional
    public void delete(Integer movieId) {
        UserEntity currentUser = getCurrentAuthenticatedUser(true);
        MovieReviewEntity review = movieReviewRepository.findByMovieMovieIdAndUserUserId(movieId, currentUser.getUserId())
                .orElseThrow(() -> new DataNotFoundException("Khong tim thay danh gia cua ban cho phim nay"));
        movieReviewRepository.delete(review);
    }

    private MovieEntity findMovie(Integer movieId) {
        return movieRepository.findById(movieId)
                .orElseThrow(() -> new DataNotFoundException("Movie not found with id: " + movieId));
    }

    private Double getAverageRating(Integer movieId) {
        Double averageRating = movieReviewRepository.findAverageRatingByMovieId(movieId);
        return averageRating == null ? 0D : Math.round(averageRating * 10.0) / 10.0;
    }

    private Long getReviewCount(Integer movieId) {
        return movieReviewRepository.countByMovieMovieId(movieId);
    }

    private boolean hasEligiblePurchase(Long userId, Integer movieId) {
        return !invoiceRepository.findEligibleInvoicesForMovieReview(
                userId,
                movieId,
                REVIEWABLE_INVOICE_STATUSES,
                PageRequest.of(0, 1)
        ).isEmpty();
    }

    private InvoiceEntity findLatestEligibleInvoice(Long userId, Integer movieId) {
        return invoiceRepository.findEligibleInvoicesForMovieReview(
                        userId,
                        movieId,
                        REVIEWABLE_INVOICE_STATUSES,
                        PageRequest.of(0, 1)
                ).stream()
                .findFirst()
                .orElseThrow(() -> new ForBiddenException("Chi nguoi dung da mua ve va thanh toan thanh cong moi duoc danh gia phim nay"));
    }

    private MovieReviewResponse toResponse(MovieReviewEntity review) {
        return MovieReviewResponse.builder()
                .reviewId(review.getReviewId())
                .movieId(review.getMovie().getMovieId())
                .ratingScore(review.getRatingScore())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .userId(review.getUser().getUserId())
                .userFullName(review.getUser().getFullName())
                .userAvatar(review.getUser().getAvatar())
                .build();
    }

    private String normalizeComment(String comment) {
        if (!StringUtils.hasText(comment)) {
            return null;
        }
        return comment.trim();
    }

    private UserEntity getCurrentAuthenticatedUser(boolean required) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            if (required) {
                throw new BadCredentialsException("Nguoi dung chua dang nhap");
            }
            return null;
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof UserPrincipal userPrincipal) {
            return findActiveUser(userPrincipal.getUserId());
        }
        if (Objects.equals(principal, "anonymousUser")) {
            if (required) {
                throw new BadCredentialsException("Nguoi dung chua dang nhap");
            }
            return null;
        }
        if (principal instanceof String username && !Objects.equals(username, "anonymousUser")) {
            return userRepository.findByEmailAndStatus(username, UserStatus.ACTIVE)
                    .orElseThrow(() -> new DataNotFoundException("Khong tim thay nguoi dung dang dang nhap"));
        }

        if (required) {
            throw new BadCredentialsException("Khong xac dinh duoc nguoi dung dang nhap");
        }
        return null;
    }

    private UserEntity findActiveUser(Long userId) {
        return userRepository.findByUserIdAndStatus(userId, UserStatus.ACTIVE)
                .orElseThrow(() -> new DataNotFoundException("Khong tim thay nguoi dung co id: " + userId));
    }
}
