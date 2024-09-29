package com.caju.teste.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.caju.teste.dto.TransactionRequest;
import com.caju.teste.dto.TransactionResponse;
import com.caju.teste.service.TransactionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/transactions")
@Tag(name = "Transações", description = "Endpoints para processamento de transações")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @PostMapping
    @Operation(summary = "Processar uma nova transação", description = "Processa uma transação com base nos dados fornecidos")
    public TransactionResponse processTransaction(@RequestBody TransactionRequest request) {
        return transactionService.processTransaction(request);
    }
}
