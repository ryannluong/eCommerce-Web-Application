package com.github.klefstad_teaching.cs122b.movies.rest;

import com.github.klefstad_teaching.cs122b.core.result.MoviesResults;
import com.github.klefstad_teaching.cs122b.movies.model.request.MovieSearchByIdRequest;
import com.github.klefstad_teaching.cs122b.movies.model.request.MovieSearchByPersonIdRequest;
import com.github.klefstad_teaching.cs122b.movies.model.request.MovieSearchRequest;
import com.github.klefstad_teaching.cs122b.movies.model.response.MovieSearchByIdResponse;
import com.github.klefstad_teaching.cs122b.movies.model.response.MovieSearchByPersonIdResponse;
import com.github.klefstad_teaching.cs122b.movies.model.response.MovieSearchResponse;
import com.github.klefstad_teaching.cs122b.movies.repo.MovieRepo;
import com.github.klefstad_teaching.cs122b.movies.repo.entity.type.Movie;
import com.github.klefstad_teaching.cs122b.movies.util.Validate;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MovieController
{
    private final MovieRepo repo;
    private final Validate validate;

    @Autowired
    public MovieController(MovieRepo repo, Validate validate)
    {
        this.repo = repo;
        this.validate = validate;
    }

    @GetMapping("/movie/search")
    public ResponseEntity<MovieSearchResponse> search(MovieSearchRequest request, @AuthenticationPrincipal SignedJWT user)
    {
        validate.limit(request.getLimit());
        validate.direction(request.getDirection());
        validate.page(request.getPage());

        List<Movie> movies = repo.searchMovie(request, user);

        if (movies.size() > 0)
            return new MovieSearchResponse()
                    .setMovies(movies)
                    .setResult(MoviesResults.MOVIES_FOUND_WITHIN_SEARCH)
                    .toResponse();
        else
            return new MovieSearchResponse()
                    .setResult(MoviesResults.NO_MOVIES_FOUND_WITHIN_SEARCH)
                    .toResponse();
    }

    @GetMapping("/movie/search/person/{personId}")
    public ResponseEntity<MovieSearchByPersonIdResponse> searchByPersonId(MovieSearchByPersonIdRequest request, @AuthenticationPrincipal SignedJWT user)
    {
        validate.direction(request.getDirection());
        validate.page(request.getPage());
        validate.limit(request.getLimit());

        List<Movie> movies = repo.searchMovieByPersonId(request, user);

        if (movies.size() > 0)
            return new MovieSearchByPersonIdResponse()
                    .setMovies(movies)
                    .setResult(MoviesResults.MOVIES_WITH_PERSON_ID_FOUND)
                    .toResponse();
        else
            return new MovieSearchByPersonIdResponse()
                    .setResult(MoviesResults.NO_MOVIES_WITH_PERSON_ID_FOUND)
                    .toResponse();
    }

    @GetMapping("/movie/{movieId}")
    public ResponseEntity<MovieSearchByIdResponse> searchByMovieId(MovieSearchByIdRequest request, @AuthenticationPrincipal SignedJWT user)
    {
        MovieSearchByIdResponse response = repo.searchMovieByMovieId(request, user);

        if (response != null)
            return response
                    .setResult(MoviesResults.MOVIE_WITH_ID_FOUND)
                    .toResponse();
        else
            return new MovieSearchByIdResponse()
                    .setResult(MoviesResults.NO_MOVIE_WITH_ID_FOUND)
                    .toResponse();
    }
}
