package com.transaction.rest.utils;

import java.math.BigDecimal;
import java.util.Optional;

public class StringUtils {

    public static String getRequiredValue(String value) {
        return !isNull(value) ? value.trim() : "";
    }

    public static BigDecimal getRequiredAmount(String value) {
        Optional<String> amountValue = Optional.ofNullable(value);
        return (amountValue.isPresent() &&
                isValidAmount(value) &&
                (new BigDecimal(amountValue.get()).compareTo(BigDecimal.ZERO)) > 0) ?
                new BigDecimal(value) :
                TransactionConstants.BIGDECIMAL_DEFAULT_VALUE;

    }

    public static boolean isNull(String field) {
        if (field == null)
            return true;
        else
            field = field.trim();

        return (field.equalsIgnoreCase("NULL") || field.equalsIgnoreCase("") || field.isEmpty());
    }

    //check amount field signed/decimal/integer
    public static boolean isValidAmount(String str) {
        str = str.trim();
        return StringUtils.isNotNull(str) &&
                (str.matches("[-\\+]?\\d+(\\.\\d+)?") ||
                        str.matches("[-\\+]?+(\\.\\d+)?") ||
                        str.matches("[-\\+]?\\d+(\\.)?"));

    }

    public static boolean isNotNull(String field) {
        return !isNull(field);
    }

    public static String[] split(String str, String delim) {
        return str.split("\\Q" + delim + "\\E", -1);
    }
}
