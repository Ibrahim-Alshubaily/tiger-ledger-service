package com.alshubaily.fintech.tiger_ledger_service.service;

import com.alshubaily.fintech.tiger_ledger_service.eventbus.KafkaEventPublisher;
import com.alshubaily.fintech.tiger_ledger_service.model.transaction.request.DepositRequest;
import com.alshubaily.fintech.tiger_ledger_service.model.transaction.request.TransferRequest;
import com.alshubaily.fintech.tiger_ledger_service.model.transaction.request.WithdrawRequest;
import com.alshubaily.fintech.tiger_ledger_service.model.account.response.TransferResponse;
import com.alshubaily.fintech.tiger_ledger_service.util.CurrencyUtil;
import com.tigerbeetle.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.time.Instant;

import static com.alshubaily.fintech.tiger_ledger_service.service.AccountService.*;
import static com.alshubaily.fintech.tiger_ledger_service.util.TransactionUtil.publishToEventBus;

@Service
@AllArgsConstructor
public class TransactionService {

    private final Client client;
    private final KafkaEventPublisher eventPublisher;


    private TransferResponse transfer(BigInteger debitAccountId, BigInteger creditAccountId, double amountSar) {
        long amountHalala = CurrencyUtil.sarToHalala(amountSar);
        byte[] transactionIdBytes = UInt128.id();

        TransferBatch transfers = new TransferBatch(1);
        transfers.add();
        transfers.setId(transactionIdBytes);
        transfers.setDebitAccountId(UInt128.asBytes(debitAccountId));
        transfers.setCreditAccountId(UInt128.asBytes(creditAccountId));
        transfers.setAmount(amountHalala);
        transfers.setLedger(LEDGER);
        transfers.setCode(CODE);
        transfers.setFlags(TransferFlags.NONE);

        CreateTransferResultBatch result = client.createTransfers(transfers);

        if (result.getLength() > 0) {
            result.next();
            throw new IllegalStateException("Transfer rejected by TigerBeetle: " + result.getResult());
        }

        publishToEventBus(transfers, eventPublisher);

        return new TransferResponse(
                UInt128.asBigInteger(transactionIdBytes).toString(),
                Instant.now()
        );
    }

    public TransferResponse transfer(BigInteger accountId, TransferRequest request) {
        return transfer(accountId, request.creditAccountId(), request.amount());
    }

    public TransferResponse deposit(BigInteger accountId, DepositRequest request) {
        return transfer(CASH_ACCOUNT_ID, accountId, request.amount());
    }

    public TransferResponse withdraw(BigInteger accountId, WithdrawRequest request) {
        return transfer(accountId, CASH_ACCOUNT_ID, request.amount());
    }
}
