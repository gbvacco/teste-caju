package com.caju.teste.service;

import com.caju.teste.dto.TransactionRequest;
import com.caju.teste.dto.TransactionResponse;
import com.caju.teste.model.Account;
import com.caju.teste.model.Transaction;
import com.caju.teste.repository.AccountRepository;
import com.caju.teste.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionService transactionService;

    @BeforeEach
    public void setup() {
        // Inicialização se necessária
    }

    @Test
    public void testProcessTransaction_FoodCategory_SufficientBalance() {
        // Configuração
        TransactionRequest request = new TransactionRequest();
        request.setAccount("123");
        request.setTotalAmount(100.0);
        request.setMerchant("Some Merchant");
        request.setMcc("5411"); // FOOD category

        Account account = new Account("123", 200.0, 0.0, 0.0);

        when(accountRepository.findByAccountId("123")).thenReturn(account);

        // Execução
        TransactionResponse response = transactionService.processTransaction(request);

        // Verificação
        assertEquals("00", response.getCode());
        assertEquals(100.0, account.getFoodBalance());
        verify(transactionRepository, times(1)).save(any(Transaction.class));
        verify(accountRepository, times(1)).save(account);
    }

    @Test
    public void testProcessTransaction_FoodCategory_InsufficientFoodBalance_SufficientCashBalance() {
        // Configuração
        TransactionRequest request = new TransactionRequest();
        request.setAccount("123");
        request.setTotalAmount(100.0);
        request.setMerchant("Some Merchant");
        request.setMcc("5411"); // FOOD category

        Account account = new Account("123", 50.0, 0.0, 150.0);

        when(accountRepository.findByAccountId("123")).thenReturn(account);

        // Execução
        TransactionResponse response = transactionService.processTransaction(request);

        // Verificação
        assertEquals("00", response.getCode());
        assertEquals(50.0, account.getFoodBalance());
        assertEquals(50.0, account.getCashBalance());
        verify(transactionRepository, times(1)).save(any(Transaction.class));
        verify(accountRepository, times(1)).save(account);
    }

    @Test
    public void testProcessTransaction_FoodCategory_InsufficientBalance() {
        // Configuração
        TransactionRequest request = new TransactionRequest();
        request.setAccount("123");
        request.setTotalAmount(100.0);
        request.setMerchant("Some Merchant");
        request.setMcc("5411"); // FOOD category

        Account account = new Account("123", 50.0, 0.0, 30.0);

        when(accountRepository.findByAccountId("123")).thenReturn(account);

        // Execução
        TransactionResponse response = transactionService.processTransaction(request);

        // Verificação
        assertEquals("51", response.getCode());
        verify(transactionRepository, never()).save(any(Transaction.class));
        verify(accountRepository, never()).save(account);
    }

    @Test
    public void testProcessTransaction_MealCategory_SufficientBalance() {
        // Configuração
        TransactionRequest request = new TransactionRequest();
        request.setAccount("456");
        request.setTotalAmount(80.0);
        request.setMerchant("Some Merchant");
        request.setMcc("5811"); // MEAL category

        Account account = new Account("456", 0.0, 100.0, 0.0);

        when(accountRepository.findByAccountId("456")).thenReturn(account);

        // Execução
        TransactionResponse response = transactionService.processTransaction(request);

        // Verificação
        assertEquals("00", response.getCode());
        assertEquals(20.0, account.getMealBalance());
        verify(transactionRepository, times(1)).save(any(Transaction.class));
        verify(accountRepository, times(1)).save(account);
    }

    @Test
    public void testProcessTransaction_MealCategory_InsufficientMealBalance_SufficientCashBalance() {
        // Configuração
        TransactionRequest request = new TransactionRequest();
        request.setAccount("456");
        request.setTotalAmount(80.0);
        request.setMerchant("Some Merchant");
        request.setMcc("5811"); // MEAL category

        Account account = new Account("456", 0.0, 50.0, 100.0);

        when(accountRepository.findByAccountId("456")).thenReturn(account);

        // Execução
        TransactionResponse response = transactionService.processTransaction(request);

        // Verificação
        assertEquals("00", response.getCode());
        assertEquals(50.0, account.getMealBalance());
        assertEquals(20.0, account.getCashBalance());
        verify(transactionRepository, times(1)).save(any(Transaction.class));
        verify(accountRepository, times(1)).save(account);
    }

    @Test
    public void testProcessTransaction_CashCategory_SufficientBalance() {
        // Configuração
        TransactionRequest request = new TransactionRequest();
        request.setAccount("789");
        request.setTotalAmount(60.0);
        request.setMerchant("Some Merchant");
        request.setMcc("0000"); // Not mapped, defaults to CASH

        Account account = new Account("789", 0.0, 0.0, 100.0);

        when(accountRepository.findByAccountId("789")).thenReturn(account);

        // Execução
        TransactionResponse response = transactionService.processTransaction(request);

        // Verificação
        assertEquals("00", response.getCode());
        assertEquals(40.0, account.getCashBalance());
        verify(transactionRepository, times(1)).save(any(Transaction.class));
        verify(accountRepository, times(1)).save(account);
    }

    @Test
    public void testProcessTransaction_CashCategory_InsufficientBalance() {
        // Configuração
        TransactionRequest request = new TransactionRequest();
        request.setAccount("789");
        request.setTotalAmount(120.0);
        request.setMerchant("Some Merchant");
        request.setMcc("0000"); // Not mapped, defaults to CASH

        Account account = new Account("789", 0.0, 0.0, 100.0);

        when(accountRepository.findByAccountId("789")).thenReturn(account);

        // Execução
        TransactionResponse response = transactionService.processTransaction(request);

        // Verificação
        assertEquals("51", response.getCode());
        verify(transactionRepository, never()).save(any(Transaction.class));
        verify(accountRepository, never()).save(account);
    }

    @Test
    public void testProcessTransaction_NewAccountCreated() {
        // Configuração
        TransactionRequest request = new TransactionRequest();
        request.setAccount("999");
        request.setTotalAmount(50.0);
        request.setMerchant("Some Merchant");
        request.setMcc("5411"); // FOOD category

        // Simular que a conta não existe
        when(accountRepository.findByAccountId("999")).thenReturn(null);
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Execução
        TransactionResponse response = transactionService.processTransaction(request);

        // Verificação
        assertEquals("00", response.getCode());
        verify(accountRepository, times(2)).save(any(Account.class)); // Uma vez na criação, outra na atualização
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }


    @Test
    public void testProcessTransaction_GenericException() {
        // Configuração
        TransactionRequest request = new TransactionRequest();
        request.setAccount("123");
        request.setTotalAmount(100.0);
        request.setMerchant("Some Merchant");
        request.setMcc("5411"); // FOOD category

        when(accountRepository.findByAccountId("123")).thenThrow(new RuntimeException("Database error"));

        // Execução
        TransactionResponse response = transactionService.processTransaction(request);

        // Verificação
        assertEquals("07", response.getCode());
        verify(transactionRepository, never()).save(any(Transaction.class));
    }
}
