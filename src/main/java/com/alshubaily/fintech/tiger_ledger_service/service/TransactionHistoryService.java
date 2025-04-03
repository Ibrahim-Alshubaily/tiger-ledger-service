package com.alshubaily.fintech.tiger_ledger_service.service;

import com.alshubaily.fintech.tiger_ledger_service.model.transaction.Transaction;
import com.alshubaily.fintech.tiger_ledger_service.model.transaction.response.GetTransactionResponse;
import com.alshubaily.fintech.tiger_ledger_service.util.CurrencyUtil;
import com.tigerbeetle.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static com.alshubaily.fintech.tiger_ledger_service.service.AccountService.CASH_ACCOUNT_ID;

@Service
@AllArgsConstructor
public class TransactionHistoryService {

    private final Client client;
    public List<GetTransactionResponse> getTransactionHistory(BigInteger accountId) {
        AccountFilter filter = new AccountFilter();
        filter.setAccountId(UInt128.asBytes(accountId));
        filter.setLimit(10); // TODO: Pagination
        filter.setDebits(true);
        filter.setCredits(true);

        TransferBatch tb = client.getAccountTransfers(filter);
        List<GetTransactionResponse> resp = new ArrayList<>();
        while (tb.next()) {
            resp.add(getTransactionResponse(tb));
        }
        return resp;
    }
    private GetTransactionResponse getTransactionResponse(TransferBatch curr) {


        BigInteger debitAccountId = UInt128.asBigInteger(curr.getDebitAccountId());
        BigInteger creditAccountId = UInt128.asBigInteger(curr.getCreditAccountId());
        Transaction transactionType = getTransactionType(debitAccountId, creditAccountId);

        if (transactionType == Transaction.DEPOSIT) {
            debitAccountId = null;
        } else if (transactionType == Transaction.WITHDRAW) {
            creditAccountId = null;
        }

        GetTransactionResponse transactionResponse =  new GetTransactionResponse(
                transactionType,
                debitAccountId,
                creditAccountId,
                CurrencyUtil.halalaToSar(curr.getAmount()),
                Instant.ofEpochSecond(0, curr.getTimestamp())
        );


        return transactionResponse;
    }

    private Transaction getTransactionType(BigInteger debitAccountId, BigInteger creditAccountId) {
        if (CASH_ACCOUNT_ID.equals(debitAccountId)) {
            return Transaction.DEPOSIT;
        }

        if (CASH_ACCOUNT_ID.equals(creditAccountId)) {
            return Transaction.WITHDRAW;
        }

        return Transaction.TRANSFER;
    }
}
