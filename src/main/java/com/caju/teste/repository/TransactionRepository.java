package com.caju.teste.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.caju.teste.model.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, String> {
}
