package com.github.klefstad_teaching.cs122b.movies.model.request;

public class MovieSearchRequest {
    private String authorization, transactionId;
    private String title, director, genre, orderBy, direction;
    private Integer year, limit, page;

    public String getAuthorization() {
        return authorization;
    }

    public MovieSearchRequest setAuthorization(String authorization) {
        this.authorization = authorization;
        return this;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public MovieSearchRequest setTransactionId(String transactionId) {
        this.transactionId = transactionId;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public MovieSearchRequest setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getDirector() {
        return director;
    }

    public MovieSearchRequest setDirector(String director) {
        this.director = director;
        return this;
    }

    public String getGenre() {
        return genre;
    }

    public MovieSearchRequest setGenre(String genre) {
        this.genre = genre;
        return this;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public MovieSearchRequest setOrderBy(String orderBy) {
        this.orderBy = orderBy;
        return this;
    }

    public String getDirection() {
        return direction;
    }

    public MovieSearchRequest setDirection(String direction) {
        this.direction = direction;
        return this;
    }

    public Integer getYear() {
        return year;
    }

    public MovieSearchRequest setYear(Integer year) {
        this.year = year;
        return this;
    }

    public Integer getLimit() {
        return limit;
    }

    public MovieSearchRequest setLimit(Integer limit) {
        this.limit = limit;
        return this;
    }

    public Integer getPage() {
        return page;
    }

    public MovieSearchRequest setPage(Integer page) {
        this.page = page;
        return this;
    }
}
