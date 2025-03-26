package com.alshubaily.fintech.tiger_ledger_service.service;

import com.alshubaily.fintech.tiger_ledger_service.model.transaction.response.GetTransactionResponse;
import com.alshubaily.fintech.tiger_ledger_service.util.AccountUtil;
import com.alshubaily.fintech.tiger_ledger_service.util.CurrencyUtil;
import com.tigerbeetle.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

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
            resp.add(new GetTransactionResponse(
                    AccountUtil.toLong(tb.getDebitAccountId()),
                    AccountUtil.toLong(tb.getCreditAccountId()),
                    CurrencyUtil.halalaToSar(tb.getAmount()),
                    Instant.ofEpochSecond(0, tb.getTimestamp())
            ));
        }
        return resp;
    }
}
