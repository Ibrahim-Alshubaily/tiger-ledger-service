package com.alshubaily.fintech.tiger_ledger_service.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class AccountUtil {
    public static long toLong(byte[] accountId) {
        return ByteBuffer.wrap(accountId)
                .order(ByteOrder.LITTLE_ENDIAN)
                .getLong(0);
    }
}
