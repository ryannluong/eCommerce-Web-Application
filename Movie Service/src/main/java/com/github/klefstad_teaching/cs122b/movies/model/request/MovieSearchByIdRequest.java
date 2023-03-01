package com.github.klefstad_teaching.cs122b.movies.model.request;

public class MovieSearchByIdRequest {
    private String authorization, transactionId;
    private Long movieId;

    public String getAuthorization() {
        return authorization;
    }

    public MovieSearchByIdRequest setAuthorization(String authorization) {
        this.authorization = authorization;
        return this;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public MovieSearchByIdRequest setTransactionId(String transactionId) {
        this.transactionId = transactionId;
        return this;
    }

    public Long getMovieId() {
        return movieId;
    }

    public MovieSearchByIdRequest setMovieId(Long movieId) {
        this.movieId = movieId;
        return this;
    }
}
