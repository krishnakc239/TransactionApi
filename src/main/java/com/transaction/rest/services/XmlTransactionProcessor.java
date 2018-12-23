package com.transaction.rest.services;

import com.transaction.rest.model.Transaction;
import com.transaction.rest.model.Violation;
import com.transaction.rest.utils.TransactionUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.xml.sax.SAXException;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class XmlTransactionProcessor implements TransactionProcessor {
    private static List<Transaction> xmlTransactionList = new ArrayList<>();


    @Override
    public void importTransactions(InputStream is) {
        try {
            xmlTransactionList = TransactionUtils.parseXmlTrasactionData(is);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
    }


    @Override
    public List<Transaction> getImportedTransactions() {
        return xmlTransactionList;
    }

    @Override
    public List<Violation> validate() {
        return TransactionUtils.getViolations(xmlTransactionList);
    }

    @Override
    public Map<String, Boolean> isBalanced() {
        return Collections.singletonMap("success",TransactionUtils.checkBalanced(xmlTransactionList));
    }
}
