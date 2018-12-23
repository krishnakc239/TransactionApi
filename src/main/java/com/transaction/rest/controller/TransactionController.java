package com.transaction.rest.controller;

import com.transaction.rest.model.Transaction;
import com.transaction.rest.model.Violation;
import com.transaction.rest.services.CsvTransactionProcessor;
import com.transaction.rest.services.TransectionServices;
import com.transaction.rest.services.XmlTransactionProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class TransactionController {


    @Autowired
    CsvTransactionProcessor csvTransactionProcessor;

    @Autowired
    XmlTransactionProcessor xmlTransactionProcessor;


    @RequestMapping("/csvList")
    public List<Transaction> getCSVTransectionList(){
        return csvTransactionProcessor.getImportedTransactions();
    }

    @RequestMapping("/csvViolationList")
    public List<Violation> getViolationList(){
        return csvTransactionProcessor.validate();
    }

    @RequestMapping("/isCsvBalanced")
    public Map<String, Boolean> balanced(){
        return csvTransactionProcessor.isBalanced();
    }
    @RequestMapping("/xmlList")
    public List<Transaction> getXMLTransectionList(){
        return xmlTransactionProcessor.getImportedTransactions();
    }

    @RequestMapping("/xmlViolationList")
    public List<Violation> getXmlViolationList(){
        return xmlTransactionProcessor.validate();
    }

    @RequestMapping("/isXmlBalanced")
    public Map<String, Boolean> isXmlbalanced(){
        return xmlTransactionProcessor.isBalanced();
    }

}
