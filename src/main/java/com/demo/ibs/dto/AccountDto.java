package com.demo.ibs.dto;

import java.math.BigDecimal;
import java.math.BigInteger;

public record AccountDto(BigDecimal amount, BigInteger customerId){
}
