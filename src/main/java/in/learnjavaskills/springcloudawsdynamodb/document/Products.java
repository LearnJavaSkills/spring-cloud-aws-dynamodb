package in.learnjavaskills.springcloudawsdynamodb.document;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

import java.math.BigDecimal;

@DynamoDbBean
public class Products
{
    private String productId;

    private String productName;

    private String description;

    private BigDecimal price;

    private Long stock;

    public Products(String productId, String productName, String description, BigDecimal price, Long stock)
    {
        this.productId = productId;
        this.productName = productName;
        this.description = description;
        this.price = price;
        this.stock = stock;
    }

    @DynamoDbPartitionKey
    @DynamoDbAttribute(value = "product_id")
    public String getProductId()
    {
        return productId;
    }


    public void setProductId(String productId)
    {
        this.productId = productId;
    }

    @DynamoDbSortKey
    @DynamoDbAttribute(value = "product_name")
    public String getProductName()
    {
        return productName;
    }

    public void setProductName(String productName)
    {
        this.productName = productName;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public BigDecimal getPrice()
    {
        return price;
    }

    public void setPrice(BigDecimal price)
    {
        this.price = price;
    }

    public Long getStock()
    {
        return stock;
    }

    public void setStock(Long stock)
    {
        this.stock = stock;
    }

    @Override
    public String toString()
    {
        return "Products{" + "productId='" + productId + '\'' + ", productName='" + productName + '\'' + ", description='" + description + '\'' + ", price=" + price + ", stock=" + stock + '}';
    }
}
