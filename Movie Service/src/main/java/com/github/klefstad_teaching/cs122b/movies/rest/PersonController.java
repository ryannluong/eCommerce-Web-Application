package com.github.klefstad_teaching.cs122b.movies.rest;

import com.github.klefstad_teaching.cs122b.core.result.MoviesResults;
import com.github.klefstad_teaching.cs122b.movies.model.request.PersonSearchRequest;
import com.github.klefstad_teaching.cs122b.movies.model.response.PersonSearchResponse;
import com.github.klefstad_teaching.cs122b.movies.repo.MovieRepo;
import com.github.klefstad_teaching.cs122b.movies.repo.entity.type.Person;
import com.github.klefstad_teaching.cs122b.movies.repo.entity.type.PersonDetail;
import com.github.klefstad_teaching.cs122b.movies.util.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class PersonController
{
    private final MovieRepo repo;

    @Autowired
    public PersonController(MovieRepo repo)
    {
        this.repo = repo;
    }

    @GetMapping("/person/search")
    public ResponseEntity<PersonSearchResponse> personSearch(PersonSearchRequest request)
    {
        Validate.limit(request.getLimit());
        Validate.page(request.getPage());
        Validate.direction(request.getDirection());

        List<PersonDetail> persons = repo.personSearch(request);

        if (persons.size() > 0) {
            return new PersonSearchResponse()
                    .setResult(MoviesResults.PERSONS_FOUND_WITHIN_SEARCH)
                    .setPersons(persons)
                    .toResponse();
        } else
            return new PersonSearchResponse()
                    .setResult(MoviesResults.NO_PERSONS_FOUND_WITHIN_SEARCH)
                    .toResponse();
    }

    @GetMapping("/person/{personId}")
    public ResponseEntity<PersonSearchResponse> personSearchById(PersonSearchRequest request) {

        PersonDetail person = repo.personSearchById(request.getPersonId());

        if (person != null)
            return new PersonSearchResponse()
                    .setResult(MoviesResults.PERSON_WITH_ID_FOUND)
                    .setPerson(person)
                    .toResponse();
        else
            return new PersonSearchResponse()
                    .setResult(MoviesResults.NO_PERSON_WITH_ID_FOUND)
                    .toResponse();
    }

}
