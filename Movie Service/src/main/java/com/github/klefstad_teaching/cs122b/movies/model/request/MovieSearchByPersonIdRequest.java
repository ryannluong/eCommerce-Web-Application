package com.github.klefstad_teaching.cs122b.movies.model.request;

public class MovieSearchByPersonIdRequest {
    private String authorization, transactionID, orderBy, direction;
    private Long personId;
    private Integer limit, page;

    public String getAuthorization() {
        return authorization;
    }

    public MovieSearchByPersonIdRequest setAuthorization(String authorization) {
        this.authorization = authorization;
        return this;
    }

    public String getTransactionID() {
        return transactionID;
    }

    public MovieSearchByPersonIdRequest setTransactionID(String transactionID) {
        this.transactionID = transactionID;
        return this;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public MovieSearchByPersonIdRequest setOrderBy(String orderBy) {
        this.orderBy = orderBy;
        return this;
    }

    public String getDirection() {
        return direction;
    }

    public MovieSearchByPersonIdRequest setDirection(String direction) {
        this.direction = direction;
        return this;
    }

    public long getPersonId() {
        return personId;
    }

    public MovieSearchByPersonIdRequest setPersonId(long personId) {
        this.personId = personId;
        return this;
    }

    public Integer getLimit() {
        return limit;
    }

    public MovieSearchByPersonIdRequest setLimit(Integer limit) {
        this.limit = limit;
        return this;
    }

    public Integer getPage() {
        return page;
    }

    public MovieSearchByPersonIdRequest setPage(Integer page) {
        this.page = page;
        return this;
    }
}
