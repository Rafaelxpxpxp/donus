package com.donus.balance;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface BalanceTransactionRepository extends R2dbcRepository<BalanceTransaction, UUID> {
}