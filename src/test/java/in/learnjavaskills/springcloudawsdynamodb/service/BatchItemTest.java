package in.learnjavaskills.springcloudawsdynamodb.service;

import in.learnjavaskills.springcloudawsdynamodb.service.BatchItemExample;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class BatchItemTest
{
    @Autowired
    private BatchItemExample batchItemExample;

    @Test
    void writeBatchItems() {
        batchItemExample.batchWriteItem();
    }

    @Test
    void getBatchItems() {
        batchItemExample.batchGetItem();
    }

}
