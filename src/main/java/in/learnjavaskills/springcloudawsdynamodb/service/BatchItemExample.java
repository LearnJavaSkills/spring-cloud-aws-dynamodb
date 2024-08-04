package in.learnjavaskills.springcloudawsdynamodb.service;

import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Service
public class BatchItemExample
{
    private final DynamoDbClient dynamoDbClient;

    public BatchItemExample(DynamoDbClient dynamoDbClient) {
        this.dynamoDbClient = dynamoDbClient;
    }

    public void batchWriteItem() {
        Map<String, AttributeValue> productsMap = new HashMap<>();
        productsMap.put("product_id", AttributeValue.fromS("pd_12345"));
        productsMap.put("product_name", AttributeValue.fromS("Laptop"));
        productsMap.put("description", AttributeValue.fromS("14 inch screen laptop"));
        productsMap.put("price", AttributeValue.fromN("49.87"));
        productsMap.put("stock", AttributeValue.fromN("101"));

        PutRequest productsPutRequest = PutRequest.builder().item(productsMap).build();
        WriteRequest productsWriteRequest = WriteRequest.builder().putRequest(productsPutRequest).build();

        Map<String, AttributeValue> ordersMap = new HashMap<>();
        ordersMap.put("order_id", AttributeValue.fromS("od_12345"));
        ordersMap.put("customer_id", AttributeValue.fromS("c_12345"));
        ordersMap.put("product_id", AttributeValue.fromS("pd_12345"));
        ordersMap.put("order_date", AttributeValue.fromS(LocalDate.now().toString()));

        PutRequest orderPutRequest = PutRequest.builder().item(ordersMap).build();
        WriteRequest orderWriteRequest = WriteRequest.builder().putRequest(orderPutRequest).build();

        Map<String, List<WriteRequest>> writeRequestMap = new HashMap<>();
        writeRequestMap.put("products", Arrays.asList(productsWriteRequest));
        writeRequestMap.put("Orders", Arrays.asList(orderWriteRequest));

        BatchWriteItemRequest batchWriteItemRequest = BatchWriteItemRequest.builder().requestItems(writeRequestMap).build();

        BatchWriteItemResponse batchWriteItemResponse = dynamoDbClient.batchWriteItem(batchWriteItemRequest);

        while (!batchWriteItemResponse.unprocessedItems().isEmpty()) {
            Map<String, List<WriteRequest>> unprocessedItems = batchWriteItemResponse.unprocessedItems();
            batchWriteItemRequest
                    = BatchWriteItemRequest.builder().requestItems(unprocessedItems).build();
            batchWriteItemResponse = dynamoDbClient.batchWriteItem(batchWriteItemRequest);
        }
    }

    public void batchGetItem() {

        Map<String, AttributeValue> productsKeys = new HashMap<>();
        productsKeys.put("product_id", AttributeValue.fromS("pd_12345"));
        productsKeys.put("product_name", AttributeValue.fromS("Laptop"));

        Map<String, AttributeValue> ordersKeys = new HashMap<>();
        ordersKeys.put("order_id", AttributeValue.fromS("od_12345"));
        ordersKeys.put("customer_id", AttributeValue.fromS("c_12345"));

        Map<String, KeysAndAttributes> requestItems = new HashMap<>();
        KeysAndAttributes productsKeysAndAttributes = KeysAndAttributes.builder().consistentRead(false)
                .keys(productsKeys).build();
        requestItems.put("products", productsKeysAndAttributes);

        KeysAndAttributes ordersKeysAndAttributes  = KeysAndAttributes.builder()
                .keys(ordersKeys)
                .build();
        requestItems.put("Orders", ordersKeysAndAttributes);


        BatchGetItemRequest batchGetItemRequest = BatchGetItemRequest.builder().requestItems(requestItems).build();
        BatchGetItemResponse batchGetItemResponse = dynamoDbClient.batchGetItem(batchGetItemRequest);
        Map<String, List<Map<String, AttributeValue>>> responses = batchGetItemResponse.responses();
        responses.forEach( (table, itemsList) -> {
            System.out.println("table name: " + table);
            itemsList.forEach( attributesAndValues -> {
                attributesAndValues.forEach( (key, value) -> {
                    System.out.println("attributes name : " + key + ", attributes value: " + value.s());
                });
            });
        } );

        // Implement a retry mechanism for batchGetItem operations that encounter errors.
        while(!batchGetItemResponse.unprocessedKeys().isEmpty()) {
            Map<String, KeysAndAttributes> unprocessedKeys = batchGetItemResponse.unprocessedKeys();
            batchGetItemRequest = BatchGetItemRequest.builder().requestItems(unprocessedKeys).build();
            batchGetItemResponse = dynamoDbClient.batchGetItem(batchGetItemRequest);
            responses = batchGetItemResponse.responses();
            responses.forEach( (table, itemsList) -> {
                System.out.println("table name: " + table);
                itemsList.forEach( attributesAndValues -> {
                    attributesAndValues.forEach( (key, value) -> {
                        System.out.println("attributes name : " + key + ", attributes value: " + value.s());
                    });
                });
            } );
        }
    }
}
