package com.transaction.rest.services;

import com.transaction.rest.model.Transaction;
import com.transaction.rest.model.Violation;
import com.transaction.rest.utils.StringUtils;
import com.transaction.rest.utils.TransactionUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class CsvTransactionProcessor implements TransactionProcessor {
    private final static String delimiter = ",";
    private static List<Transaction> csvTransactionList = new ArrayList<>();

    @Override
    public void importTransactions(InputStream is) {
        try {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                csvTransactionList = reader.lines().map(getTransaction).collect(Collectors.toList());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
//        csvTransactionList = reader.lines().map(getTransaction).collect(Collectors.toList());


    }
    private Function<String, Transaction> getTransaction = line -> {
        String[] fields = StringUtils.split(line, delimiter);
        return new Transaction(StringUtils.getRequiredValue(fields[0]), StringUtils.getRequiredAmount(fields[1]), StringUtils.getRequiredValue(fields[2]));
    };

    @Override
    public List<Transaction> getImportedTransactions() {
        return csvTransactionList;
    }

    @Override
    public List<Violation> validate() {
        return TransactionUtils.getViolations(csvTransactionList);
    }

    @Override
    public Map<String,Boolean> isBalanced() {
        return Collections.singletonMap("success",TransactionUtils.checkBalanced(csvTransactionList));
    }

}
