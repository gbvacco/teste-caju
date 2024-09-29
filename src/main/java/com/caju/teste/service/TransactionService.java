package com.caju.teste.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.caju.teste.dto.TransactionRequest;
import com.caju.teste.dto.TransactionResponse;
import com.caju.teste.model.Account;
import com.caju.teste.model.Transaction;
import com.caju.teste.repository.AccountRepository;
import com.caju.teste.repository.TransactionRepository;

import javax.persistence.OptimisticLockException;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;

@Service
public class TransactionService {
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    private static final Map<String, String> MCC_CATEGORY_MAP = new HashMap<>();
    static {
        MCC_CATEGORY_MAP.put("5411", "FOOD");
        MCC_CATEGORY_MAP.put("5412", "FOOD");
        MCC_CATEGORY_MAP.put("5811", "MEAL");
        MCC_CATEGORY_MAP.put("5812", "MEAL");
    }

    private static final Map<String, String> MERCHANT_CATEGORY_MAP = new HashMap<>();
    static {
        MERCHANT_CATEGORY_MAP.put("UBER TRIP                   SAO PAULO BR", "TRANSPORT");
        MERCHANT_CATEGORY_MAP.put("UBER EATS                   SAO PAULO BR", "MEAL");
        MERCHANT_CATEGORY_MAP.put("PAG*JoseDaSilva          RIO DE JANEI BR", "CASH");
        MERCHANT_CATEGORY_MAP.put("PICPAY*BILHETEUNICO           GOIANIA BR", "TRANSPORT");
    }

    @Transactional
    public TransactionResponse processTransaction(TransactionRequest request) {
        try {
            Account account = accountRepository.findByAccountId(request.getAccount());
            if (account == null) {
                account = new Account(request.getAccount(), 500.0, 500.0, 500.0);
                accountRepository.save(account);
            }

            String category = Optional.ofNullable(MERCHANT_CATEGORY_MAP.get(request.getMerchant()))
                    .orElse(MCC_CATEGORY_MAP.getOrDefault(request.getMcc(), "CASH"));

            Double amount = request.getTotalAmount();

            List<BalanceOption> balanceOptions = getBalanceOptions(account, category);

            Account finalAccount = account;
            Optional<BalanceOption> successfulOption = balanceOptions.stream()
                    .filter(option -> option.getBalance.apply(finalAccount) >= amount)
                    .findFirst();

            if (successfulOption.isPresent()) {
                BalanceOption option = successfulOption.get();
                Double currentBalance = option.getBalance.apply(account);
                option.setBalance.accept(account, currentBalance - amount);

                category = option.category;

                Transaction transaction = new Transaction(
                        request.getAccount(),
                        amount,
                        request.getMerchant(),
                        request.getMcc(),
                        category
                );

                transactionRepository.save(transaction);
                accountRepository.save(account);
                return new TransactionResponse("00");
            } else {
                return new TransactionResponse("51");
            }

        } catch (OptimisticLockException e) {
            return new TransactionResponse("07");
        } catch (Exception e) {
            return new TransactionResponse("07");
        }
    }

    private List<BalanceOption> getBalanceOptions(Account account, String category) {
        List<BalanceOption> balanceOptions = new ArrayList<>();

        switch (category) {
            case "FOOD":
                balanceOptions.add(new BalanceOption("FOOD", Account::getFoodBalance, Account::setFoodBalance));
                balanceOptions.add(new BalanceOption("CASH", Account::getCashBalance, Account::setCashBalance));
                break;
            case "MEAL":
                balanceOptions.add(new BalanceOption("MEAL", Account::getMealBalance, Account::setMealBalance));
                balanceOptions.add(new BalanceOption("CASH", Account::getCashBalance, Account::setCashBalance));
                break;
            default:
                balanceOptions.add(new BalanceOption("CASH", Account::getCashBalance, Account::setCashBalance));
                break;
        }

        return balanceOptions;
    }

    private static class BalanceOption {
        String category;
        Function<Account, Double> getBalance;
        BiConsumer<Account, Double> setBalance;

        BalanceOption(String category, Function<Account, Double> getBalance, BiConsumer<Account, Double> setBalance) {
            this.category = category;
            this.getBalance = getBalance;
            this.setBalance = setBalance;
        }
    }
}
