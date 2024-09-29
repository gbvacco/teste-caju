package com.caju.teste.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.caju.teste.model.Account;

public interface AccountRepository extends JpaRepository<Account, String> {
    Account findByAccountId(String accountId);
}
