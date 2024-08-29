package in.learnjavaskills.springcloudawsdynamodb.service;

import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;


@Service
public class TransactionsExample
{
    private DynamoDbClient dynamoDbClient;

    public TransactionsExample(DynamoDbClient dynamoDbClient) {
        this.dynamoDbClient = dynamoDbClient;
    }

    /**
     * transaction write item example - e-commerce clone where customer place a order.
     * We have four table, i.e., customer, products, orders and cart
     * 1. we'll validate customer is active or not by using the ConditionCheck action
     * 2. Once customer is active, then we will place a order using the Put action
     * 3. Update the product table using the Update action
     * 4. finally delete the cart entry using the Delete action
     */
    public void transactionWrite() {

        // Collect Transaction Write in a list
        List<TransactWriteItem> transactionWriteItem = List.of(TransactWriteItem.builder().conditionCheck(customerExistsConditionCheck()).build(),
                TransactWriteItem.builder().put(placeOrder()).build(),
                TransactWriteItem.builder().update(updateProductQuantity()).build(),
                TransactWriteItem.builder().delete(deleteCartEntry()).build());

        // TransactWriteItemRequest
        TransactWriteItemsRequest transactWriteItemsRequest = TransactWriteItemsRequest.builder()
                .transactItems(transactionWriteItem)
                .build();

        // Run the transaction write item
        try
        {
            TransactWriteItemsResponse transactWriteItemsResponse = dynamoDbClient.transactWriteItems(transactWriteItemsRequest);
            System.out.println("Successfully completed write operation");
        } catch (Exception exception) {
            exception.printStackTrace();
        }

    }

    /**
     * customer table
     * validate customer is active or not
     */
    private ConditionCheck customerExistsConditionCheck() {
        // customer table partition key and sort key
        HashMap<String, AttributeValue> customerKey = new HashMap<>();
        customerKey.put("customer_id", AttributeValue.fromS("c_123")); // partition key
        customerKey.put("email", AttributeValue.fromS("dummy@email.com")); // sort key

        // expression attribute values is fill the expected values in the query placeholder.
        Map<String, AttributeValue> customerExpressionAttributeValue = new HashMap<>();
        customerExpressionAttributeValue.put(":expected_active_status", AttributeValue.fromBool(true));

        // verify attribute 'active' exist in the items and active status must be true.
        String attribute = "active";
        return ConditionCheck.builder()
                .tableName("customer")
                .key(customerKey)
                .conditionExpression("attribute_exists(" + attribute + ")")
                .conditionExpression("active = :expected_active_status")
                .expressionAttributeValues(customerExpressionAttributeValue)
                .build();
    }

    /**
     * place order, insert items into orders table
     * @return
     */
    private Put placeOrder() {
        // create a map with key value pair to insert a record into order table
        Map<String, AttributeValue> orderItems = new HashMap<>();
        orderItems.put("order_id", AttributeValue.fromS("od_123")); // partition key
        orderItems.put("product_id", AttributeValue.fromS("p_123")); // sort key
        orderItems.put("customer_id", AttributeValue.fromS("c_123"));
        orderItems.put("total_amount", AttributeValue.fromS("101"));
        orderItems.put("status", AttributeValue.fromS("CONFIRM"));

        return Put.builder()
                .tableName("orders")
                .item(orderItems)
                // An optional parameter that returns the item attributes for an UpdateItem operation that failed a condition check.
                .returnValuesOnConditionCheckFailure(ReturnValuesOnConditionCheckFailure.ALL_OLD)
                .build();
    }

    /**
     * update the product table
     * @return
     */
    private Update updateProductQuantity() {

        // products table partition key and sort key
        HashMap<String, AttributeValue> productsKey = new HashMap<>();
        productsKey.put("product_id", AttributeValue.fromS("p_123")); // partition key
        productsKey.put("name", AttributeValue.fromS("laptop")); // sort key

        // expression attribute values is fill the expected values in the query placeholder.
        Map<String, AttributeValue> productsExpressionAttributeValue = new HashMap<>();
        productsExpressionAttributeValue.put(":available_quantity", AttributeValue.fromN("11"));
        productsExpressionAttributeValue.put(":expected_product_status", AttributeValue.fromBool(true));

        return Update.builder()
                .tableName("products")
                .key(productsKey)
                // query - set quantity to 11
                .updateExpression("SET quantity = :available_quantity")
                // query - just like a where clause
                .conditionExpression("product_status = :expected_product_status")
                // provide actual values of placeholder used in the query i.e., available_quantity and  expected_product_status
                .expressionAttributeValues(productsExpressionAttributeValue)
                // An optional parameter that returns the item attributes for an UpdateItem operation that failed a condition check.
                .returnValuesOnConditionCheckFailure(ReturnValuesOnConditionCheckFailure.ALL_OLD)
                .build();
    }

    /**
     * delete cart entry
     * @return
     */
    private Delete deleteCartEntry() {

        // cart table partition key and sort key
        HashMap<String, AttributeValue> cartKey = new HashMap<>();
        cartKey.put("cart_id", AttributeValue.fromS("ct_123")); // partition key
        cartKey.put("product_id", AttributeValue.fromS("p_123")); // sort key

        // expression attribute values is fill the expected values in the query placeholder.
        Map<String, AttributeValue> cartExpressionAttributeValue = new HashMap<>();
        cartExpressionAttributeValue.put(":expected_quantity", AttributeValue.fromN("1"));

        return Delete.builder()
                .tableName("cart")
                .key(cartKey)
                // query - just like where clause, here verifying the quantity is equal to 1
                .conditionExpression("quantity = :expected_quantity")
                // provide actual values of placeholder used in the query i.e expected_quantity which is 1
                .expressionAttributeValues(cartExpressionAttributeValue)
                // An optional parameter that returns the item attributes for an UpdateItem operation that failed a condition check.
                .returnValuesOnConditionCheckFailure(ReturnValuesOnConditionCheckFailure.ALL_OLD)
                .build();

    }

    /**
     *  Transaction get operation.
     *  get customer and orders details from tables in a single transaction.
     */
    public void transactionRead() {
        // customer's partition key and sort key
        HashMap<String, AttributeValue> customerKey = new HashMap<>();
        customerKey.put("customer_id", AttributeValue.fromS("c_123")); // partition Key
        customerKey.put("email", AttributeValue.fromS("dummy@email.com")); // sort key

        // order's partition key and sort key
        Map<String, AttributeValue> orderKey = new HashMap<>();
        orderKey.put("order_id", AttributeValue.fromS("od_123")); // partition Key
        orderKey.put("product_id", AttributeValue.fromS("p_123")); // sort key

        // Fetch all the customer whose partition key and sort key match
        Get customersGet = Get.builder()
                .tableName("customer")
                .key(customerKey)
                .build();

        // Fetch all the orders whose partition key and sort key match
        Get ordersGet = Get.builder()
                .tableName("orders")
                .key(orderKey)
                .build();

        // Collect all the read operation in a list of TransactGetItem
        List<TransactGetItem> transactGetItemList = List.of(TransactGetItem.builder().get(customersGet).build(),
                TransactGetItem.builder().get(ordersGet).build());

        // TransactGetItemRequest
        TransactGetItemsRequest transactGetItemsRequest = TransactGetItemsRequest.builder()
                .transactItems(transactGetItemList)
                .returnConsumedCapacity(ReturnConsumedCapacity.TOTAL)
                .build();

        // Run the transactGetItem operation
        try {
            TransactGetItemsResponse transactGetItemsResponse = dynamoDbClient.transactGetItems(transactGetItemsRequest);
            List<ItemResponse> responses = transactGetItemsResponse.responses();
            if (Objects.nonNull(responses) && !responses.isEmpty()) {
                responses.stream()
                        .flatMap(response -> response.item().entrySet().stream())
                        .forEach(entry -> {
                            System.out.println("attribute: " + entry.getKey() + " value: " + entry.getValue());
                        } );
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
