package com.codebloom.cineman.repository;

import com.codebloom.cineman.common.enums.InvoiceStatus;
import com.codebloom.cineman.common.enums.ShowTimeStatus;
import com.codebloom.cineman.model.InvoiceEntity;
import com.codebloom.cineman.model.PromotionEntity;
import com.codebloom.cineman.model.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<InvoiceEntity, Long> {

    Page<InvoiceEntity> findAllByStatusNot(InvoiceStatus status, Pageable pageable);

    Optional<InvoiceEntity> findByIdAndStatus(Long id, InvoiceStatus status);

    @Query("""
            SELECT i FROM InvoiceEntity i INNER JOIN i.tickets t ON t.invoice.id = i.id
                        WHERE t.showTime.id = :showTimeId AND i.status = :status AND t.showTime.status = :showTimeStatus
            """)
    Optional<InvoiceEntity> findByShowTimeIdAndStatus(Long showTimeId, InvoiceStatus status, ShowTimeStatus showTimeStatus);

    List<InvoiceEntity> findByCustomerAndStaffAndStatus(UserEntity customer, UserEntity staff, InvoiceStatus status);

    Optional<InvoiceEntity> findByIdAndStatusNot(Long id, InvoiceStatus status);

    boolean existsByIdAndStatusIsNot(Long id, InvoiceStatus status);

    Optional<InvoiceEntity> findByVnTxnRefAndStatus(String vnTxnRef, InvoiceStatus invoiceStatus);

    Optional<InvoiceEntity> findByVnTxnRef(String vnTxnRef);

    Optional<InvoiceEntity> findByQrCode(String qrCode);

    List<InvoiceEntity> findByCustomerOrStaff(UserEntity customer, UserEntity staff);

    @Query("""
            SELECT i FROM InvoiceEntity i
                        WHERE (i.customer = :user OR i.staff = :user)
                            AND i.status = :status
            """)
    List<InvoiceEntity> findByUserAndStatus(UserEntity user, InvoiceStatus status);

    List<InvoiceEntity> findByStaff(UserEntity staff);

    @Query("""
            SELECT i FROM InvoiceEntity i INNER JOIN TicketEntity t ON t.invoice.id = i.id
                        WHERE (i.customer.userId = :id OR i.staff.userId = :id)
                                    AND t.showTime.id = :showTimeId 
                                    AND i.status = :status
            """)
    Optional<InvoiceEntity> findByUserIdAndShowTimeIdAndStatus(Long id, Long showTimeId, InvoiceStatus status);

    @Query("""
            SELECT i FROM InvoiceEntity i
                        WHERE i.customer.userId = :id OR i.staff.userId = :id
            """)
    List<InvoiceEntity> findByCustomerOrStaff(Long id);

    Optional<InvoiceEntity> findByCustomerAndPromotion(UserEntity customer, PromotionEntity promotion);

    boolean existsByPromotionAndStatusIn(PromotionEntity promotion, Collection<InvoiceStatus> statuses);

    /**
     * Mỗi promotion chỉ áp dụng được cho 1 khách hàng trên 1 hóa đơn duy nhất
     * @param userId id User
     * @param promotionId id Promotion
     * @return Optional<InvoiceEntity>
     */
    @Query("""
            SELECT i
            FROM InvoiceEntity i
            WHERE i.customer.userId = :userId AND i.promotion.id = :promotionId
            """)
    Optional<InvoiceEntity> findByUserIdAndPromotionId(Long userId, Long promotionId);

    @Query(
            value = """
                    SELECT DISTINCT i
                    FROM InvoiceEntity i
                    JOIN i.tickets t
                    JOIN t.showTime st
                    WHERE st.showDate = :showDate
                    """,
            countQuery = """
                    SELECT COUNT(DISTINCT i.id)
                    FROM InvoiceEntity i
                    JOIN i.tickets t
                    JOIN t.showTime st
                    WHERE st.showDate = :showDate
                    """
    )
    Page<InvoiceEntity> findAllByShowDate(Date showDate, Pageable pageable);

    @Query(
            value = """
                    SELECT DISTINCT i
                    FROM InvoiceEntity i
                    JOIN i.tickets t
                    JOIN t.showTime st
                    JOIN st.cinemaTheater ct
                    JOIN ct.movieTheater mt
                    WHERE st.showDate = :showDate
                      AND mt.movieTheaterId = :movieTheaterId
                    """,
            countQuery = """
                    SELECT COUNT(DISTINCT i.id)
                    FROM InvoiceEntity i
                    JOIN i.tickets t
                    JOIN t.showTime st
                    JOIN st.cinemaTheater ct
                    JOIN ct.movieTheater mt
                    WHERE st.showDate = :showDate
                      AND mt.movieTheaterId = :movieTheaterId
                    """
    )
    Page<InvoiceEntity> findAllByShowDateAndMovieTheaterId(Date showDate, Integer movieTheaterId, Pageable pageable);

    @Query("""
            SELECT DISTINCT i
            FROM InvoiceEntity i
            JOIN i.tickets t
            JOIN t.showTime st
            WHERE i.customer.userId = :userId
              AND st.movie.movieId = :movieId
              AND i.status IN :statuses
            ORDER BY i.createdAt DESC, i.id DESC
            """)
    List<InvoiceEntity> findEligibleInvoicesForMovieReview(
            Long userId,
            Integer movieId,
            Collection<InvoiceStatus> statuses,
            Pageable pageable
    );
}
