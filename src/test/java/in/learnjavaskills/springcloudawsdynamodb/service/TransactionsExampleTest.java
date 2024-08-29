package in.learnjavaskills.springcloudawsdynamodb.service;

import in.learnjavaskills.springcloudawsdynamodb.service.TransactionsExample;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TransactionsExampleTest
{

    @Autowired
    private TransactionsExample transactionsExample;

    @Test
    void transactionWrite()
    {
        transactionsExample.transactionWrite();
    }

    @Test
    void transactionRead() {
        transactionsExample.transactionRead();
    }
}