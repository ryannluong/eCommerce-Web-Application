package com.github.klefstad_teaching.cs122b.movies.model.response;

import com.github.klefstad_teaching.cs122b.core.base.ResponseModel;
import com.github.klefstad_teaching.cs122b.movies.repo.entity.type.PersonDetail;

import java.util.List;

public class PersonSearchResponse extends ResponseModel<PersonSearchResponse> {
    List<PersonDetail> persons;
    PersonDetail person;

    public List<PersonDetail> getPersons() {
        return persons;
    }

    public PersonSearchResponse setPersons(List<PersonDetail> persons) {
        this.persons = persons;
        return this;
    }

    public PersonDetail getPerson() {
        return person;
    }

    public PersonSearchResponse setPerson(PersonDetail person) {
        this.person = person;
        return this;
    }
}
