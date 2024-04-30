package in.learnjavaskills.springcloudawsdynamodb.document;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

import java.util.List;
import java.util.Map;
import java.util.Set;

@DynamoDbBean
public class Movie
{
    private Long movieId;
    private String movieTitle;
    private Map<String, List<String>> directors;

    private Set<String> characters;

    private Genres genre;

    private Boolean baseOnBook;

    private Double rating;

    public Movie()
    {
    }

    public Movie(Long movieId, String movieTitle, Map<String, List<String>> directors, Set<String> characters, Genres genre, Boolean baseOnBook, Double rating)
    {
        this.movieId = movieId;
        this.movieTitle = movieTitle;
        this.directors = directors;
        this.characters = characters;
        this.genre = genre;
        this.baseOnBook = baseOnBook;
        this.rating = rating;
    }

    @DynamoDbPartitionKey
    @DynamoDbAttribute(value = "movie_id")
    public Long getMovieId()
    {
        return movieId;
    }

    public void setMovieId(Long movieId)
    {
        this.movieId = movieId;
    }

    @DynamoDbSortKey
    @DynamoDbAttribute(value = "movie_title")
    public String getMovieTitle()
    {
        return movieTitle;
    }

    public void setMovieTitle(String movieTitle)
    {
        this.movieTitle = movieTitle;
    }

    @DynamoDbAttribute(value = "directors")
    public Map<String, List<String>> getDirectors()
    {
        return directors;
    }

    public void setDirectors(Map<String, List<String>> directors)
    {
        this.directors = directors;
    }

    public Set<String> getCharacters()
    {
        return characters;
    }

    public void setCharacters(Set<String> characters)
    {
        this.characters = characters;
    }

    public Genres getGenre()
    {
        return genre;
    }

    public void setGenre(Genres genre)
    {
        this.genre = genre;
    }

    public Boolean getBaseOnBook()
    {
        return baseOnBook;
    }

    public void setBaseOnBook(Boolean baseOnBook)
    {
        this.baseOnBook = baseOnBook;
    }

    public Double getRating()
    {
        return rating;
    }

    public void setRating(Double rating)
    {
        this.rating = rating;
    }

    @Override
    public String toString()
    {
        return "Movie{" + "movieId=" + movieId + ", movieTitle='" + movieTitle + '\'' + ", directors=" + directors + ", characters=" + characters + ", genre=" + genre + ", baseOnBook=" + baseOnBook + ", rating=" + rating + '}';
    }
}
