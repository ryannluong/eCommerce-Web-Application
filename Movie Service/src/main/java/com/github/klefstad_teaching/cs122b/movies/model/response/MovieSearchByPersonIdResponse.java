package com.github.klefstad_teaching.cs122b.movies.model.response;

import com.github.klefstad_teaching.cs122b.core.base.ResponseModel;
import com.github.klefstad_teaching.cs122b.movies.repo.entity.type.Movie;

import java.util.List;

public class MovieSearchByPersonIdResponse extends ResponseModel<MovieSearchByPersonIdResponse> {
    private List<Movie> movies;

    public List<Movie> getMovies() {
        return movies;
    }

    public MovieSearchByPersonIdResponse setMovies(List<Movie> movies) {
        this.movies = movies;
        return this;
    }
}
