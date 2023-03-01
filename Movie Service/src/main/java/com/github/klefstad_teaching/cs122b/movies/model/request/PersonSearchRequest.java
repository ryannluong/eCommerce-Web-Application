package com.github.klefstad_teaching.cs122b.movies.model.request;

public class PersonSearchRequest {
    private String authorization, transactionId;
    private String name, birthday, movieTitle, orderBy, direction;
    private Integer limit, page;
    private Long personId;

    public String getAuthorization() {
        return authorization;
    }

    public PersonSearchRequest setAuthorization(String authorization) {
        this.authorization = authorization;
        return this;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public PersonSearchRequest setTransactionId(String transactionId) {
        this.transactionId = transactionId;
        return this;
    }

    public String getName() {
        return name;
    }

    public PersonSearchRequest setName(String name) {
        this.name = name;
        return this;
    }

    public String getBirthday() {
        return birthday;
    }

    public PersonSearchRequest setBirthday(String birthday) {
        this.birthday = birthday;
        return this;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public PersonSearchRequest setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
        return this;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public PersonSearchRequest setOrderBy(String orderBy) {
        this.orderBy = orderBy;
        return this;
    }

    public String getDirection() {
        return direction;
    }

    public PersonSearchRequest setDirection(String direction) {
        this.direction = direction;
        return this;
    }

    public Integer getLimit() {
        return limit;
    }

    public PersonSearchRequest setLimit(Integer limit) {
        this.limit = limit;
        return this;
    }

    public Integer getPage() {
        return page;
    }

    public PersonSearchRequest setPage(Integer page) {
        this.page = page;
        return this;
    }

    public Long getPersonId() {
        return personId;
    }

    public PersonSearchRequest setPersonId(Long personId) {
        this.personId = personId;
        return this;
    }
}
