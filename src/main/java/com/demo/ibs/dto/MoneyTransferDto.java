package com.demo.ibs.dto;

import java.math.BigDecimal;
import java.math.BigInteger;

public record MoneyTransferDto(BigInteger senderAccountId, BigInteger receiverAccountId, BigDecimal amount) {
}
