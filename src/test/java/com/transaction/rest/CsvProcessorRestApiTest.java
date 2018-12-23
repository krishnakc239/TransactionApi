package com.transaction.rest;


import com.transaction.rest.services.CsvTransactionProcessor;
import com.transaction.rest.services.TransactionProcessor;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = RestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CsvProcessorRestApiTest {

    @LocalServerPort
    private int port;

    TestRestTemplate restTemplate = new TestRestTemplate();
    HttpHeaders headers = new HttpHeaders();

    @Autowired
    private TransactionProcessor csvTransactionProcessor;

    private HttpEntity<String> entity;
    private ResponseEntity<String> response;

    @Before
    public void setUp() {
        csvTransactionProcessor = new CsvTransactionProcessor();
        entity = new HttpEntity<>(null, headers);

    }
    @Test
    public void givenValidCsvStream_WhenImport_ThenReturnTheExpectedTransactions() throws JSONException {
        InputStream is = asStream("C,1000,salary\nD,200,rent\nD,800,other");
        csvTransactionProcessor.importTransactions(is);

        entity = new HttpEntity<>(null, headers);
        response = restTemplate.exchange(
                createURLWithPort("/csvList"),
                HttpMethod.GET, entity, String.class);
        System.out.println(response.getBody());
        String expected = "[{type:C,amount:1000,narration:salary},{type:D,amount:200,narration:rent},{type:D,amount:800,narration:other}]";

        JSONAssert.assertEquals(expected, response.getBody(), false);
    }

    @Test
    public void givenBalancedCsvStream_WhenImportAndCheckIfBalanced_ThenReturnTrue() throws JSONException {
        InputStream is = asStream("C,1000.50,salary\nD,200,rent\nD,800.50,other");
        csvTransactionProcessor.importTransactions(is);

        entity = new HttpEntity<>(null, headers);
        response = restTemplate.exchange(
                createURLWithPort("/isCsvBalanced"),
                HttpMethod.GET, entity, String.class);

        System.out.println(response.getBody());
        String expected = "{success:true}";
        JSONAssert.assertEquals(expected, response.getBody(), false);
    }


    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }

    private InputStream asStream(String s) {
        return new ByteArrayInputStream(s.getBytes());
    }


}
