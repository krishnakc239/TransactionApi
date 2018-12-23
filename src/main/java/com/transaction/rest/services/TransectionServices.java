package com.transaction.rest.services;

import com.transaction.rest.model.Transaction;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class TransectionServices {
    TransactionProcessor csvTransactionProcessor;
    List<Transaction> transactionList = new ArrayList<>(Arrays.asList(
            new Transaction("D", new BigDecimal(200), "rent"),
            new Transaction("C", new BigDecimal(1000), "salary"),
            new Transaction("D", new BigDecimal(800), "other")
    ));

    public List<Transaction> getAllCsvTransactions(){
        return csvTransactionProcessor.getImportedTransactions();
    }

    public List<Transaction> getAllXmlTransactions(){
        return transactionList;
    }
}
