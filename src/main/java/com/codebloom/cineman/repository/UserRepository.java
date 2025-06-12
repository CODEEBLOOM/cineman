package com.codebloom.cineman.repository;


import com.codebloom.cineman.model.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
//    @Query(value = "select u from UserEntity u where u.status='ACTIVE' " +
//            "and (lower(u.fullName) like :keyword " +
//            "or lower(u.phoneNumber) like :keyword " +
//            "or lower(u.email) like :keyword)")
//    Page<UserEntity> searchByKeyword(String keyword, Pageable pageable);

    UserEntity findByPhoneNumber(String phoneNumber);

    UserEntity findByEmail(String email);
}
