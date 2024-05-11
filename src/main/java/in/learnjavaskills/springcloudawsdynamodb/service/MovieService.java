package in.learnjavaskills.springcloudawsdynamodb.service;

import in.learnjavaskills.springcloudawsdynamodb.document.Movie;
import in.learnjavaskills.springcloudawsdynamodb.exception.DynamoDbDataException;
import io.awspring.cloud.dynamodb.DynamoDbTemplate;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.NestedAttributeName;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.ScanEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class MovieService
{
    private final DynamoDbTemplate dynamoDbTemplate;

    public MovieService(DynamoDbTemplate dynamoDbTemplate) {
        this.dynamoDbTemplate = dynamoDbTemplate;
    }

    /**
     * save movie
     * @param movie
     * @return
     */
    public Long addMovie(Movie movie) {
        if (Objects.isNull(movie))
            throw new DynamoDbDataException("Movie must be non null");

        Movie savedMovie = dynamoDbTemplate.save(movie);
        if (Objects.nonNull(savedMovie) && Objects.nonNull(savedMovie.getMovieId()))
            return savedMovie.getMovieId();
        throw new DynamoDbDataException("Unsaved movie, Please try again.");
    }

    /**
     * find movie composite key i.e partition key and sort key
     * @param movieId
     * @return
     */
    public Optional<Movie> findMovie(Long movieId, String movieTitle) {
        Key key = Key.builder().partitionValue(movieId).sortValue(movieTitle).build();
        Movie movie = dynamoDbTemplate.load(key, Movie.class);
        return Optional.ofNullable(movie);
    }

    /**
     * Delete items by partition key and sort key
     * @param movieId
     * @param movieTitle
     */
    public void deleteMovie(Long movieId, String movieTitle) {
        Key key = Key.builder().partitionValue(movieId).sortValue(movieTitle).build();
        dynamoDbTemplate.delete(key, Movie.class);
    }

    /**
     * Delete item by only partition key
     * NOTE: Since we are using composite primary key, this method will
     * throw DynamoDBException Key element does not match the schema,
     * because this method is only accepting partition key not sort key.
     *
     * @param movieId
     */
    @Deprecated
    public void deleteMovie(Long movieId) {
        Key key = Key.builder().partitionValue(movieId).build();
        dynamoDbTemplate.delete(key, Movie.class);
    }

    /**
     * Delete item by movie object
     * @param movie
     */
    public void deleteMovie(Movie movie) {
        dynamoDbTemplate.delete(movie);
    }

    /**
     * update movie
     * @param movie
     * @return
     */
    public Long updateMovie(Movie movie) {
        Movie updatedMovie = dynamoDbTemplate.update(movie);
        if (Objects.nonNull(updatedMovie))
            return updatedMovie.getMovieId();
        return 0L;
    }

    /**
     * Query the dynamoDB using the partition key i.e movieId in our case.
     * @param movieId
     * @return
     */
    public PageIterable<Movie> findMovieByQuery(Long movieId) {
        Key key = Key.builder().partitionValue(movieId).build();
        QueryConditional queryConditional = QueryConditional.keyEqualTo(key);

        QueryEnhancedRequest queryEnhancedRequest = QueryEnhancedRequest.builder()
                .queryConditional(queryConditional)
                .build();

        PageIterable<Movie> moviePageIterable = dynamoDbTemplate.query(queryEnhancedRequest, Movie.class);
        return  moviePageIterable;
    }

    /**
     * Query the dynamoDB using the partition key i.e movieId in our case. and sort key i.e movie_title
     * @param movieId
     * @param movieTitle
     * @return
     */
    public PageIterable<Movie> findMovieByQuery(Long movieId, String movieTitle) {
        Key key = Key.builder().partitionValue(movieId).sortValue(movieTitle).build();
        QueryConditional queryConditional = QueryConditional.keyEqualTo(key);

        QueryEnhancedRequest queryEnhancedRequest = QueryEnhancedRequest.builder()
                .queryConditional(queryConditional).build();

        PageIterable<Movie> moviePageIterable = dynamoDbTemplate.query(queryEnhancedRequest, Movie.class);
        return  moviePageIterable;
    }

    /**
     * Finding a movie with movie Id whose rating is greater(>) or equal(=) to given rating
     * @param movieId mandatory movieId
     * @param rating  rating is greater(>) or equal(=) to given rating
     * @return  PageIterable<Movie>
     *
     * NOTE: Filter expression only for non-primary key(partition key and sort key)
     * else DynamoDB throw DynamoDBException with message Filter Expression can only contain non-primary key attributes: Primary key attribute:
     */
    public PageIterable<Movie> findMovieByQuery(Long movieId, Double rating) {

        Key key = Key.builder().partitionValue(movieId).build();
        QueryConditional queryConditional = QueryConditional.keyEqualTo(key);

        // if you have multiple expression then use below
//        Map<String, AttributeValue> expressionValue = new HashMap<>();
//        expressionValue.put(":rating_value", AttributeValue.fromN(rating.toString()));

        Expression expression = Expression.builder()
                .expression("rating >= :rating_value")
//                .expressionValues(expressionValue) // If you are using a map of AttributeValue
                .putExpressionValue(":rating_value", AttributeValue.fromN(rating.toString()))
                .build();

        QueryEnhancedRequest queryEnhancedRequest = QueryEnhancedRequest.builder()
                .queryConditional(queryConditional) // query-condition with partition-key
                .filterExpression(expression) // filter-expression
                .build();

        PageIterable<Movie> moviePageIterable = dynamoDbTemplate.query(queryEnhancedRequest, Movie.class);
        return moviePageIterable;
    }

    /**
     *
     * @param genreContain
     * @return
     */
    public PageIterable<Movie> findMovieTitle(String genreContain, Long movieId) {

        // it's mandatory to use the key with the filter expression
        Key key = Key.builder().partitionValue(movieId).build();
        QueryConditional queryConditional = QueryConditional.keyEqualTo(key);

        // filter expression, finding only those movie which genre's contain genreContain
        Expression expression = Expression.builder()
                .expression("contains(genre, :genre_contain_value)")
                .putExpressionValue(":genre_contain_value", AttributeValue.fromS(genreContain))
                .build();

        // Selecting only movie_id and movie title attributes only.
        List<NestedAttributeName> nestedAttributeNameList = new ArrayList<>();

        nestedAttributeNameList.add(NestedAttributeName.builder()
                .elements("movie_id")
                .build());

        nestedAttributeNameList.add(NestedAttributeName.builder()
                .elements("movie_title")
                .build());

        QueryEnhancedRequest queryEnhancedRequest = QueryEnhancedRequest.builder()
                .queryConditional(queryConditional)
                .filterExpression(expression) // filter-expression
                .addNestedAttributesToProject(nestedAttributeNameList) // projection-expression
                .build();

        PageIterable<Movie> moviePageIterable = dynamoDbTemplate.query(queryEnhancedRequest, Movie.class);
        return moviePageIterable;
    }

    /**
     * Scan whole table and return all the data
     * @return
     */
    public PageIterable<Movie> scanMovieTable() {
        PageIterable<Movie> moviePageIterable = dynamoDbTemplate.scanAll(Movie.class);
        return moviePageIterable;
    }

    /**
     * Scan entire table with filter condition on  movie title begin with
     * @param movieTitleBeginWith
     */
    public PageIterable<Movie> scanMovieTableWithFilterExpression(String movieTitleBeginWith) {

        // Filter expression is nothing but, It's just like a where clause of SQL. Here we are only selecting whose
        // movie_title begin with the given-movie-title(movieTitleBeginWith) like a LIKE operation of SQL.
        Expression expression = Expression.builder()
                .expression("begins_with(movie_title, :movie_title)")
                .putExpressionValue(":movie_title", AttributeValue.fromS(movieTitleBeginWith))
                .build();

        ScanEnhancedRequest scanEnhancedRequest = ScanEnhancedRequest.builder()
                .filterExpression(expression) // filter-expression
                .build();
        PageIterable<Movie> scan = dynamoDbTemplate.scan(scanEnhancedRequest, Movie.class);
        return scan;
    }


    /**
     * Scan movie table to retrieve only selected attributes from dynamoDB
     * @return PageIterable<Movie>
     */
    public PageIterable<Movie> scanMovieTableWithProjectionExpression() {

        // ProjectionExpression, If we want to select only selected attributes from the dynamoDB then we can utilize
        // the projection expression, Since we are using the Spring cloud AWS, we can utilize the NestedAttributeName
        // to set the desire attributes name just like below.
        List<NestedAttributeName> nestedAttributeNameList = new ArrayList<>();

        // retrieve characters attribute in a result
        nestedAttributeNameList.add(NestedAttributeName.builder()
                .elements("characters")
                .build());

        // data type of attribute directors is a map, here we are only selecting key = dir2 from the directors attribute.
        nestedAttributeNameList.add(NestedAttributeName.builder()
                .elements("directors", "Dir2")
                .build());

        ScanEnhancedRequest scanEnhancedRequest = ScanEnhancedRequest.builder()
                .addNestedAttributesToProject(nestedAttributeNameList) // projection-expression
                .build();

        PageIterable<Movie> scan = dynamoDbTemplate.scan(scanEnhancedRequest, Movie.class);
        return scan;
    }
}
