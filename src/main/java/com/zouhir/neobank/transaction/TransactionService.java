package com.zouhir.neobank.transaction;

import com.zouhir.neobank.account.Account;
import com.zouhir.neobank.account.AccountRepository;
import com.zouhir.neobank.common.exceptions.AccountNotFoundException;
import com.zouhir.neobank.transaction.dto.TransactionDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionService {

}
