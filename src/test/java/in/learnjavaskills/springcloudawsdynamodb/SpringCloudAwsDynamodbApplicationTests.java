package in.learnjavaskills.springcloudawsdynamodb;

import in.learnjavaskills.springcloudawsdynamodb.document.Genres;
import in.learnjavaskills.springcloudawsdynamodb.document.Movie;
import in.learnjavaskills.springcloudawsdynamodb.service.MovieService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import software.amazon.awssdk.core.pagination.sync.SdkIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;

import java.util.*;

@SpringBootTest
class SpringCloudAwsDynamodbApplicationTests {

	public static final String HARRY_POTTER_AND_THE_DEATHLY_HALLOWS_PART_1 = "Harry Potter and the Deathly Hallows: Part 1";
	public static final String HARRY_POTTER_AND_THE_DEATHLY_HALLOWS_PART_2 = "Harry Potter and the Deathly Hallows: Part 2";
	public static final long MOVIE_ID = 101L;
	@Autowired
	private MovieService movieService;



	@Test
	void saveMovie() {
		Map<String, List<String>> directors = new HashMap<>();
		List<String> directorsName = new ArrayList<>();
		directorsName.add("David Yates");
		directors.put("Dir1", directorsName);

		Set<String> characters = new HashSet<>();
		characters.add("Harry Potter");
		characters.add("Hermione Granger");
		characters.add("Ron Weasley");
		characters.add("Draco Malfoy");

		Movie movie = new Movie(MOVIE_ID, HARRY_POTTER_AND_THE_DEATHLY_HALLOWS_PART_2,
				directors, characters, Genres.DRAMA, true, 3.9);

		Long movieId = movieService.addMovie(movie);
		Assertions.assertThat(movieId).isEqualTo(movie.getMovieId());
	}

	@Test
	public void findMovie() {
		Optional<Movie> movie = movieService.findMovie(MOVIE_ID, HARRY_POTTER_AND_THE_DEATHLY_HALLOWS_PART_1);
		Assertions.assertThat(movie).isPresent();

		System.out.println(movie.get());
	}

	@Test
	public void deleteMovie() {
		movieService.deleteMovie(MOVIE_ID, HARRY_POTTER_AND_THE_DEATHLY_HALLOWS_PART_1);
	}

	@Test
	public void updateMovie() {
		Map<String, List<String>> directors = new HashMap<>();
		List<String> directorsName = new ArrayList<>();
		directorsName.add("David Yates");
		directors.put("Dir1", directorsName);

		Set<String> characters = new HashSet<>();
		characters.add("Harry Potter");
		characters.add("Hermione Granger");
		characters.add("Ron Weasley");
		characters.add("Draco Malfoy");

		Movie movie = new Movie(MOVIE_ID, HARRY_POTTER_AND_THE_DEATHLY_HALLOWS_PART_1,
				directors, characters, Genres.DRAMA, true, 4.7);

		Long movieId = movieService.updateMovie(movie);
		Assertions.assertThat(movieId).isEqualTo(MOVIE_ID);
	}

	@Test
	public void findMovieByQueryUsingMovieId()
	{
		PageIterable<Movie> movieByQuery = movieService.findMovieByQuery(MOVIE_ID);
		if (Objects.nonNull(movieByQuery))
		{
			SdkIterable<Movie> items = movieByQuery.items();
			if (Objects.nonNull(items))
				items.forEach(System.out::println);
		}
	}

	@Test
	public void findMovieByMovieIdAndMovieTitle()
	{
		PageIterable<Movie> movieByQuery = movieService.findMovieByQuery(MOVIE_ID, HARRY_POTTER_AND_THE_DEATHLY_HALLOWS_PART_2);
		if (Objects.nonNull(movieByQuery))
		{
			SdkIterable<Movie> items = movieByQuery.items();
			if (Objects.nonNull(items))
				items.forEach(System.out :: println);
		}
	}


	@Test
	public void findMovieByQueryUsingMovieIdAndFilteringByRating()
	{
		PageIterable<Movie> movieByQuery = movieService.findMovieByQuery(MOVIE_ID, 4d);
		if (Objects.nonNull(movieByQuery))
		{
			SdkIterable<Movie> items = movieByQuery.items();
			if (Objects.nonNull(items))
				items.forEach(System.out :: println);
		}
	}

	@Test
	void findMovieTitle()
	{
		PageIterable<Movie> movieByQuery = movieService.findMovieTitle("DRA", 101L);
		if (Objects.nonNull(movieByQuery))
		{
			SdkIterable<Movie> items = movieByQuery.items();
			if (Objects.nonNull(items))
				items.forEach(System.out :: println);
		}
	}

	@Test
	void scanTable()
	{
		PageIterable<Movie> moviePageIterable = movieService.scanMovieTable();
		if (Objects.nonNull(moviePageIterable))
		{
			SdkIterable<Movie> items = moviePageIterable.items();
			if (Objects.nonNull(items))
				items.forEach(System.out :: println);
		}
	}

	@Test
	public void scanTableWithFilterExpression()
	{
		PageIterable<Movie> moviePageIterable = movieService.scanMovieTableWithFilterExpression("Har");
		if (Objects.nonNull(moviePageIterable))
		{
			SdkIterable<Movie> items = moviePageIterable.items();
			if (Objects.nonNull(items))
				items.forEach(System.out :: println);
		}
	}

	@Test
	void scanTableAndRetrieveOnlyCharactersAndDirectorAttribute() {
		PageIterable<Movie> moviePageIterable = movieService.scanMovieTableWithProjectionExpression();
		if (Objects.nonNull(moviePageIterable))
		{
			SdkIterable<Movie> items = moviePageIterable.items();
			if (Objects.nonNull(items))
				items.forEach(System.out :: println);
		}
	}


}
