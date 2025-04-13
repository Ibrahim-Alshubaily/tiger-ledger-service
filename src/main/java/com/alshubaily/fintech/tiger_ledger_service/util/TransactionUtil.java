package com.alshubaily.fintech.tiger_ledger_service.util;

import static com.alshubaily.fintech.tiger_ledger_service.service.AccountService.CASH_ACCOUNT_ID;

import com.alshubaily.fintech.tiger_ledger_service.eventbus.KafkaEventPublisher;
import com.alshubaily.fintech.tiger_ledger_service.eventbus.TransactionEvent;
import com.alshubaily.fintech.tiger_ledger_service.model.transaction.TransactionType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.tigerbeetle.TransferBatch;
import com.tigerbeetle.UInt128;
import java.math.BigInteger;
import java.sql.Timestamp;
import org.springframework.scheduling.annotation.Async;

public class TransactionUtil {

    private static final ObjectMapper OBJECT_MAPPER =
            new ObjectMapper().configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

    public static TransactionType getTransactionType(
            BigInteger debitAccountId, BigInteger creditAccountId) {
        if (CASH_ACCOUNT_ID.equals(debitAccountId)) {
            return TransactionType.DEPOSIT;
        }

        if (CASH_ACCOUNT_ID.equals(creditAccountId)) {
            return TransactionType.WITHDRAW;
        }

        return TransactionType.TRANSFER;
    }

    @Async
    public static void publishToEventBus(
            TransferBatch transfers, KafkaEventPublisher eventPublisher) {
        transfers.beforeFirst();
        transfers.next();

        BigInteger transferId = UInt128.asBigInteger(transfers.getId());
        BigInteger debitAccountId = UInt128.asBigInteger(transfers.getDebitAccountId());
        BigInteger creditAccountId = UInt128.asBigInteger(transfers.getCreditAccountId());

        TransactionEvent event =
                new TransactionEvent(
                        transferId,
                        debitAccountId,
                        creditAccountId,
                        transfers.getAmount(),
                        UInt128.asBigInteger(transfers.getPendingId()),
                        UInt128.asBigInteger(transfers.getUserData128()),
                        transfers.getUserData64(),
                        transfers.getUserData32(),
                        transfers.getTimeout(),
                        transfers.getLedger(),
                        transfers.getCode(),
                        transfers.getFlags(),
                        new Timestamp(System.currentTimeMillis()),
                        getTransactionType(debitAccountId, creditAccountId));

        try {
            String json = OBJECT_MAPPER.writeValueAsString(event);
            eventPublisher.publish("transactions", json);
        } catch (Exception e) {
            System.err.println("Failed to publish transaction event: " + e.getMessage());
        }
    }
}
