### Script of creating table

```
aws dynamodb create-table --table-name movie \
    --attribute-definitions AttributeName=movie_id,AttributeType=N AttributeName=movie_title,AttributeType=S \
    --key-schema AttributeName=movie_id,KeyType=HASH AttributeName=movie_title,KeyType=RANGE \
    --provisioned-throughput ReadCapacityUnits=1,WriteCapacityUnits=1
```
