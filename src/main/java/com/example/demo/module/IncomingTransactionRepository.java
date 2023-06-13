package com.example.demo.module;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IncomingTransactionRepository extends JpaRepository<IncomingTransaction, Integer> {
    Optional<IncomingTransaction> findIncomingTransactionByRefNum(String ref);
}
