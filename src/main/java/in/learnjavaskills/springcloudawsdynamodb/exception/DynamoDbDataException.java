package in.learnjavaskills.springcloudawsdynamodb.exception;

public class DynamoDbDataException extends RuntimeException {

    public DynamoDbDataException(String message) {
        super(message);
    }
}
