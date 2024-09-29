package com.caju.teste.dto;

import lombok.Data;

@Data
public class TransactionRequest {
    private String account;
    private Double totalAmount;
    private String mcc;
    private String merchant;
}
