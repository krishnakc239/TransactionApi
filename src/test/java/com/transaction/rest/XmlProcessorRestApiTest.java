package com.transaction.rest;


import com.transaction.rest.model.Transaction;
import com.transaction.rest.services.TransactionProcessor;
import com.transaction.rest.services.XmlTransactionProcessor;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = RestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class XmlProcessorRestApiTest {

    @LocalServerPort
    private int port;

    TestRestTemplate restTemplate = new TestRestTemplate();

    HttpHeaders headers = new HttpHeaders();

    @Autowired
    private TransactionProcessor xmlTransactionProcessor;

    private HttpEntity<String> entity;
    private ResponseEntity<String> response;

    @Before
    public void setUp() {
        xmlTransactionProcessor = new XmlTransactionProcessor();
        entity = new HttpEntity<>(null, headers);
    }
    @Test
    public void givenValidXmlStream_WhenImport_ThenReturnTheExpectedTransactions() throws JSONException {

        InputStream is = asStream("<TransactionList>\n" +
                "    <Transaction type=\"C\" amount=\"1000\" narration=\"salary\" />\n" +
                "    <Transaction type=\"D\" amount=\"200\" narration=\"rent\" />\n" +
                "    <Transaction type=\"D\" amount=\"800\" narration=\"other\" />\n" +
                "</TransactionList>");
        xmlTransactionProcessor.importTransactions(is);
        response = restTemplate.exchange(
                createURLWithPort("/xmlList"),
                HttpMethod.GET, entity, String.class);
        String expected = "[{type:C,amount:1000,narration:salary},{type:D,amount:200,narration:rent},{type:D,amount:800,narration:other}]";

        JSONAssert.assertEquals(expected, response.getBody(), false);
    }

    @Test
    public void givenBalancedXmlStream_WhenImportAndCheckIfBalanced_ThenReturnTrue() throws JSONException {
        InputStream is = asStream("<TransactionList>\n" +
                "    <Transaction type=\"C\" amount=\"1000.50\" narration=\"salary\" />\n" +
                "    <Transaction type=\"D\" amount=\"200\" narration=\"rent\" />\n" +
                "    <Transaction type=\"D\" amount=\"800.50\" narration=\"other\" />\n" +
                "</TransactionList>");
        xmlTransactionProcessor.importTransactions(is);
        response= restTemplate.exchange(
                createURLWithPort("/isXmlBalanced"),
                HttpMethod.GET, entity, String.class);

        String expected = "{success:true}";

        JSONAssert.assertEquals(expected, response.getBody(), false);

    }

    @Test
    public void givenImbalancedXmlStream_WhenImportAndCheckIfBalanced_ThenReturnFalse() throws JSONException {
        InputStream is = asStream("<TransactionList>\n" +
                "    <Transaction type=\"C\" amount=\"1000\" narration=\"salary\" />\n" +
                "    <Transaction type=\"D\" amount=\"400\" narration=\"rent\" />\n" +
                "    <Transaction type=\"D\" amount=\"750\" narration=\"other\" />\n" +
                "</TransactionList>");
        xmlTransactionProcessor.importTransactions(is);

        response= restTemplate.exchange(
                createURLWithPort("/isXmlBalanced"),
                HttpMethod.GET, entity, String.class);
        String expected = "{success:false}";

        JSONAssert.assertEquals(expected, response.getBody(), false);
    }

    @Test
    public void givenXmlStreamWithAnInvalidTransaction_WhenCallingValidate_ThenReportTheProperViolations() throws Exception {
        InputStream is = asStream("<TransactionList>\n" +
                "    <Transaction type=\"C\" amount=\"1000\" narration=\"salary\" />\n" +
                "    <Transaction type=\"X\" amount=\"400\" narration=\"rent\" />\n" +
                "    <Transaction type=\"D\" amount=\"750\" narration=\"other\" />\n" +
                "</TransactionList>");
        xmlTransactionProcessor.importTransactions(is);

        response= restTemplate.exchange(
                createURLWithPort("/xmlViolationList"),
                HttpMethod.GET, entity, String.class);
        String expected = "[{order:2,property:type,description:null}]";
        JSONAssert.assertEquals(expected, response.getBody(), false);

    }

    @Test
    public void givenXmlStreamWithMultipleInvalidTransactions_WhenCallingValidate_ThenReportTheProperViolations() throws Exception {
        InputStream is = asStream("<TransactionList>\n" +
                "    <Transaction type=\"C\" amount=\"one thousand\" narration=\"salary\" />\n" +
                "    <Transaction type=\"X\" amount=\"400\" narration=\"rent\" />\n" +
                "    <Transaction type=\"D\" amount=\"750\" narration=\"other\" />\n" +
                "</TransactionList>");
        xmlTransactionProcessor.importTransactions(is);
        response= restTemplate.exchange(
                createURLWithPort("/xmlViolationList"),
                HttpMethod.GET, entity, String.class);
        String expected = "[{order:2,property:type,description:null},{order:1,property:amount,description:null}]";
        JSONAssert.assertEquals(expected, response.getBody(), false);

    }

    @Test
    public void givenXmlStreamWithMultipleErrorsInSameTransaction_WhenCallingValidate_ThenReportTheProperViolations() throws Exception {
        InputStream is = asStream("<TransactionList>\n" +
                "    <Transaction type=\"C\" amount=\"one thousand\" narration=\"salary\" />\n" +
                "    <Transaction type=\"X\" amount=\"0\" narration=\"rent\" />\n" +
                "    <Transaction type=\"D\" amount=\"750\" narration=\"other\" />\n" +
                "</TransactionList>");
        xmlTransactionProcessor.importTransactions(is);
        response= restTemplate.exchange(
                createURLWithPort("/xmlViolationList"),
                HttpMethod.GET, entity, String.class);
        String expected = "[{order:2,property:type,description:null},{order:2,property:amount,description:null},{order:1,property:amount,description:null}]";
        JSONAssert.assertEquals(expected, response.getBody(), false);
    }

    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }

    private Transaction newTransaction(String type, BigDecimal amount, String narration) {
        return new Transaction(type, amount, narration);
    }

    private InputStream asStream(String s) {
        return new ByteArrayInputStream(s.getBytes());
    }

}
