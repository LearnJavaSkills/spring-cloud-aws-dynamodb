package in.learnjavaskills.springcloudawsdynamodb.document;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

import java.time.LocalDateTime;

@DynamoDbBean
public class Orders
{
    private String orderId;

    private String customerId;

    private String productId;

    private String quantity;

    private LocalDateTime orderDate;

    public Orders(String orderId, String customerId, String productId, String quantity, LocalDateTime orderDate)
    {
        this.orderId = orderId;
        this.customerId = customerId;
        this.productId = productId;
        this.quantity = quantity;
        this.orderDate = orderDate;
    }

    @DynamoDbPartitionKey
    @DynamoDbAttribute(value = "order_id")
    public String getOrderId()
    {
        return orderId;
    }

    public void setOrderId(String orderId)
    {
        this.orderId = orderId;
    }

    @DynamoDbSortKey
    @DynamoDbAttribute(value = "customer_id")
    public String getCustomerId()
    {
        return customerId;
    }

    public void setCustomerId(String customerId)
    {
        this.customerId = customerId;
    }

    public String getProductId()
    {
        return productId;
    }

    public void setProductId(String productId)
    {
        this.productId = productId;
    }

    public String getQuantity()
    {
        return quantity;
    }

    public void setQuantity(String quantity)
    {
        this.quantity = quantity;
    }

    public LocalDateTime getOrderDate()
    {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate)
    {
        this.orderDate = orderDate;
    }

    @Override
    public String toString()
    {
        return "Orders{" + "orderId='" + orderId + '\'' + ", customerId='" + customerId + '\'' + ", productId='" + productId + '\'' + ", quantity='" + quantity + '\'' + ", orderDate=" + orderDate + '}';
    }
}
