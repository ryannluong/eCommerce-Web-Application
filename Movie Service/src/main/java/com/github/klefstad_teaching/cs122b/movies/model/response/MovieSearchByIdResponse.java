package com.github.klefstad_teaching.cs122b.movies.model.response;

import com.github.klefstad_teaching.cs122b.core.base.ResponseModel;
import com.github.klefstad_teaching.cs122b.movies.repo.entity.type.Genre;
import com.github.klefstad_teaching.cs122b.movies.repo.entity.type.MovieDetail;
import com.github.klefstad_teaching.cs122b.movies.repo.entity.type.Person;

import java.util.List;

public class MovieSearchByIdResponse extends ResponseModel<MovieSearchByIdResponse> {
    private MovieDetail movie;
    private List<Genre> genres;
    private List<Person> persons;

    public MovieDetail getMovie() {
        return movie;
    }

    public MovieSearchByIdResponse setMovie(MovieDetail movie) {
        this.movie = movie;
        return this;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public MovieSearchByIdResponse setGenres(List<Genre> genres) {
        this.genres = genres;
        return this;
    }

    public List<Person> getPersons() {
        return persons;
    }

    public MovieSearchByIdResponse setPersons(List<Person> persons) {
        this.persons = persons;
        return this;
    }
}
