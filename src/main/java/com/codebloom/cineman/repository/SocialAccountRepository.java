package com.codebloom.cineman.repository;

import com.codebloom.cineman.model.SocialAccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SocialAccountRepository extends JpaRepository<SocialAccountEntity,Integer> {
}
