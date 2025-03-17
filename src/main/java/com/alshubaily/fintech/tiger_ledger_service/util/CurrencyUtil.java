package com.alshubaily.fintech.tiger_ledger_service.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class CurrencyUtil {
    public static final int HALALA_PER_SAR = 100;

    public static long sarToHalala(double sarAmount) {
        return BigDecimal.valueOf(sarAmount)
                .multiply(BigDecimal.valueOf(HALALA_PER_SAR))
                .setScale(0, RoundingMode.HALF_UP)
                .longValue();
    }

    public static double halalaToSar(long halalaAmount) {
        return BigDecimal.valueOf(halalaAmount)
                .divide(BigDecimal.valueOf(HALALA_PER_SAR), 2, RoundingMode.HALF_UP)
                .doubleValue();
    }
}
