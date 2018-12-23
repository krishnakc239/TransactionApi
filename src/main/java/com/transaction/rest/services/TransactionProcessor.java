package com.transaction.rest.services;

import com.transaction.rest.model.Transaction;
import com.transaction.rest.model.Violation;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * Created by Ahmad Y. Saleh on 7/24/17.
 */
public interface TransactionProcessor {

    void importTransactions(InputStream is);

    List<Transaction> getImportedTransactions();

    List<Violation> validate();

    Map<String, Boolean> isBalanced();
}
