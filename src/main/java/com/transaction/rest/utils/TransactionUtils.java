package com.transaction.rest.utils;

import com.transaction.rest.model.Transaction;
import com.transaction.rest.model.Violation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class TransactionUtils {
    public static boolean checkBalanced(List<Transaction> transactions) {
        BigDecimal creditSum = BigDecimal.ZERO;
        BigDecimal debitSum = BigDecimal.ZERO;
        for (Transaction ts : transactions) {
            if (ts.getType().equalsIgnoreCase(TransactionConstants.TYPE_C)) {
                creditSum = creditSum.add(ts.getAmount());
            } else if (ts.getType().equalsIgnoreCase(TransactionConstants.TYPE_D)) {
                debitSum = debitSum.add(ts.getAmount());
            }
        }
        return debitSum.compareTo(creditSum) == 0;
    }

    public static List<Violation> getViolations(List<Transaction> transactionList) {
        List<Violation> violationList = new ArrayList<>();
        Violation violation;
        String property;
        int order = 1;
        for (Transaction t : transactionList) {
            String type = t.getType();
            if (!(type.equalsIgnoreCase(TransactionConstants.TYPE_C) || type.equalsIgnoreCase(TransactionConstants.TYPE_D))) {
                property = "type";
                violation = new Violation(order, property);
                violationList.add(violation);
            }
            if (t.getAmount().equals(TransactionConstants.BIGDECIMAL_DEFAULT_VALUE)) {
                property = "amount";
                violation = new Violation(order, property);
                violationList.add(violation);
            }
            order++;
        }
        return violationList;
    }

    public static List<Transaction> parseXmlTrasactionData(InputStream inputStream) throws IOException, SAXException {
        List<Transaction> transactionList = new ArrayList<>();
        Transaction transaction;

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        Document document = builder.parse(inputStream);
        document.getDocumentElement().normalize();
        NodeList nodeList = document.getElementsByTagName("Transaction");
        for (int temp = 0; temp < nodeList.getLength(); temp++) {
            Node node = nodeList.item(temp);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) node;
                transaction = new Transaction(
                        StringUtils.getRequiredValue(eElement.getAttribute("type")),
                        StringUtils.getRequiredAmount(eElement.getAttribute("amount")),
                        StringUtils.getRequiredValue(eElement.getAttribute("narration")));

                transactionList.add(transaction);
            }
        }
        return transactionList;

    }
}
