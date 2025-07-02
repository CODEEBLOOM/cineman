package com.codebloom.cineman.repository;

import com.codebloom.cineman.model.InvoiceEntity;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvoiceRepository extends JpaRepository<InvoiceEntity,Long> {
	List<InvoiceEntity> findByEmail(String email);
}
