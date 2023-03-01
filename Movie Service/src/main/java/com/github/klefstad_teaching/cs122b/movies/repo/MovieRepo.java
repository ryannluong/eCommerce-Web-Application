package com.github.klefstad_teaching.cs122b.movies.repo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.klefstad_teaching.cs122b.core.error.ResultError;
import com.github.klefstad_teaching.cs122b.core.result.MoviesResults;
import com.github.klefstad_teaching.cs122b.core.security.JWTManager;
import com.github.klefstad_teaching.cs122b.movies.model.data.MovieOrderBy;
import com.github.klefstad_teaching.cs122b.movies.model.data.PersonOrderBy;
import com.github.klefstad_teaching.cs122b.movies.model.request.MovieSearchByIdRequest;
import com.github.klefstad_teaching.cs122b.movies.model.request.MovieSearchByPersonIdRequest;
import com.github.klefstad_teaching.cs122b.movies.model.request.MovieSearchRequest;
import com.github.klefstad_teaching.cs122b.movies.model.request.PersonSearchRequest;
import com.github.klefstad_teaching.cs122b.movies.model.response.MovieSearchByIdResponse;
import com.github.klefstad_teaching.cs122b.movies.repo.entity.type.*;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class MovieRepo
{
    private final NamedParameterJdbcTemplate template;
    private ObjectMapper mapper;

    //language=SQL
    private final static String GET_MOVIE =
            "SELECT DISTINCT m.hidden, m.year, p.name, m.rating, m.id, m.backdrop_path, m.title, m.poster_path\n" +
            "FROM movies.movie m INNER JOIN movies.person p on p.id = m.director_id\n" +
            "INNER JOIN movies.movie_genre mg on m.id = mg.movie_id\n" +
            "INNER JOIN movies.genre g on g.id = mg.genre_id\n";

    //language=SQL
    private final static String GET_MOVIE_BY_PERSON_ID =
            "SELECT DISTINCT m.hidden, m.year, d.name, m.rating, m.id, m.backdrop_path, m.title, m.poster_path\n" +
            "FROM movies.movie m JOIN movies.person d ON m.director_id = d.id\n" +
            "INNER JOIN movies.movie_person mp on m.id = mp.movie_id\n"+
            "WHERE mp.person_id = :personId\n";

    //language=SQL
    private final static String GET_MOVIE_BY_ID =
            "SELECT m.hidden, m.year, d.name, m.rating, m.id, m.backdrop_path, m.title, m.poster_path, m.num_votes, m.budget, m.revenue, m.overview,\n" +
            "                (SELECT JSON_ARRAYAGG(JSON_OBJECT('name', genre.name, 'id', genre.id))\n" +
            "                 FROM (SELECT DISTINCT g.id, g.name\n" +
            "                       FROM  movies.movie_genre mg JOIN movies.genre g ON mg.genre_id = g.id\n" +
            "                       WHERE mg.movie_id = m.id\n" +
            "                       ORDER BY g.name) AS genre) AS genres,\n" +
            "                (SELECT JSON_ARRAYAGG(JSON_OBJECT('id', person.id, 'name', person.name))\n" +
            "                 FROM (SELECT DISTINCT p.id, p.name, p.popularity\n" +
            "                       FROM movies.movie_person mp JOIN movies.person p ON mp.person_id = p.id\n" +
            "                       WHERE mp.movie_id = m.id\n" +
            "                       ORDER BY p.popularity DESC, p.id) as person) AS persons\n" +
            "FROM movies.movie m INNER JOIN movies.person d ON m.director_id = d.id\n" +
            "WHERE m.id = :movieId";

    //language=SQL
    private final static String GET_PERSON_SEARCH =
            "SELECT DISTINCT p.id, p.name, p.birthday, p.biography, p.birthplace, p.popularity, p.profile_path\n" +
            "FROM movies.person p\n";

    //language=SQL
    private final static String GET_PERSON_BY_ID =
            "SELECT p.id, p.name, p.birthday, p.biography, p.birthplace, p.popularity, p.profile_path\n" +
            "FROM movies.person p\n" +
            "WHERE p.id = :personId";

    // Functions for duplicated code
    private boolean isAdminOrEmployee(SignedJWT user) {
        List<String> roles = null;

        try {
            roles = user.getJWTClaimsSet().getStringListClaim(JWTManager.CLAIM_ROLES);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return roles != null && (roles.contains("ADMIN") || roles.contains("EMPLOYEE"));
    }
    private void buildSQLDefaultParams(StringBuilder sql, MapSqlParameterSource source, String orderBy, String direction, Integer limit, Integer page, String type) {
        if (type.equals("m")) {
            sql.append(MovieOrderBy.fromString(orderBy).toSQL());
            if (direction != null)
                sql.append(direction);
            sql.append(", m.id ");
        }
        else if (type.equals("p")) {
            sql.append(PersonOrderBy.fromString(orderBy).toSQL());
            if (direction != null)
                sql.append(direction);
            sql.append(", p.id ");
        }

        if (limit != null) {
            sql.append("LIMIT :limit ");
            source.addValue("limit", limit, Types.INTEGER);
        } else {
            sql.append("LIMIT 10 ");
        }

        if (page != null) {
            sql.append("OFFSET :offset");
            int offset = limit != null ? (page - 1) * limit : (page - 1) * 10;
            source.addValue("offset", offset, Types.INTEGER);
        } else {
            sql.append("OFFSET 0");
        }

        sql.append(";");
    }

    @Autowired
    public MovieRepo(ObjectMapper objectMapper, NamedParameterJdbcTemplate template)
    {
        this.mapper = objectMapper;
        this.template = template;
    }

    public List<Movie> searchMovie(MovieSearchRequest request, SignedJWT user) {
        StringBuilder sql = new StringBuilder(GET_MOVIE);
        MapSqlParameterSource source = new MapSqlParameterSource();
        boolean whereAdded = false, privileged = isAdminOrEmployee(user);

        if (request.getTitle() != null) {
            sql.append("WHERE m.title LIKE :title ");

            String wildcard = "%" + request.getTitle() + "%";
            source.addValue("title", wildcard, Types.VARCHAR);

            whereAdded = true;
        }

        if (request.getYear() != null) {
            if (whereAdded) {
                sql.append("AND ");
            } else {
                sql.append("WHERE ");
                whereAdded = true;
            }

            sql.append("m.year = :year ");
            source.addValue("year", request.getYear(), Types.INTEGER);
        }

        if (request.getDirector() != null) {
            if (whereAdded) {
                sql.append("AND ");
            } else {
                sql.append("WHERE ");
                whereAdded = true;
            }

            sql.append("p.name LIKE :director ");

            String wildcard = "%" + request.getDirector() + "%";
            source.addValue("director", wildcard, Types.VARCHAR);
        }

        if (request.getGenre() != null) {
            if (whereAdded) {
                sql.append("AND ");
            } else {
                sql.append("WHERE ");
                whereAdded = true;
            }

            sql.append("g.name LIKE :genre ");

            String wildcard = "%" + request.getGenre() + "%";
            source.addValue("genre", wildcard, Types.VARCHAR);
        }

        if (!privileged) {
            if (whereAdded) {
                sql.append("AND ");
            } else {
                sql.append("WHERE ");
                whereAdded = true;
            }

            sql.append("m.hidden = false ");
        }

        sql.append("\n");
        buildSQLDefaultParams(sql, source, request.getOrderBy(), request.getDirection(), request.getLimit(), request.getPage(), "m");

        System.out.println(sql.toString());

        try {
            return template.query(
                    sql.toString(),
                    source,
                    (rs, rowNum) ->
                            new Movie()
                                    .setId(rs.getLong("m.id"))
                                    .setTitle(rs.getString("m.title"))
                                    .setYear(rs.getInt("m.year"))
                                    .setDirector(rs.getString("p.name"))
                                    .setRating(rs.getDouble("m.rating"))
                                    .setBackdropPath(rs.getString("m.backdrop_path"))
                                    .setPosterPath(rs.getString("m.poster_path"))
                                    .setHidden(rs.getBoolean("m.hidden"))
            );
        } catch (DataAccessException e) {
            return null;
        }
    }

    public List<Movie> searchMovieByPersonId(MovieSearchByPersonIdRequest request, SignedJWT user)
    {
        StringBuilder sql = new StringBuilder(GET_MOVIE_BY_PERSON_ID);
        MapSqlParameterSource source = new MapSqlParameterSource();
        boolean privileged = isAdminOrEmployee(user);

        source.addValue("personId", request.getPersonId(), Types.INTEGER);

        if (!privileged) {
            sql.append("AND m.hidden = false\n");
        }


        buildSQLDefaultParams(sql, source, request.getOrderBy(), request.getDirection(), request.getLimit(), request.getPage(), "m");

        System.out.println(sql.toString());

        try {
            return template.query(
                    sql.toString(),
                    source,
                    (rs, rowNum) ->
                            new Movie()
                                    .setId(rs.getLong("m.id"))
                                    .setTitle(rs.getString("m.title"))
                                    .setYear(rs.getInt("m.year"))
                                    .setDirector(rs.getString("d.name"))
                                    .setRating(rs.getDouble("m.rating"))
                                    .setBackdropPath(rs.getString("m.backdrop_path"))
                                    .setPosterPath(rs.getString("m.poster_path"))
                                    .setHidden(rs.getBoolean("m.hidden"))
            );
        } catch (DataAccessException e) {
            return null;
        }
    }

    public MovieSearchByIdResponse searchMovieByMovieId(MovieSearchByIdRequest request, SignedJWT user)
    {
        StringBuilder sql = new StringBuilder(GET_MOVIE_BY_ID);
        MapSqlParameterSource source = new MapSqlParameterSource();
        boolean privileged = isAdminOrEmployee(user);

        source.addValue("movieId", request.getMovieId(), Types.INTEGER);

        if (!privileged) {
            sql.append("\nAND m.hidden = false");
        }

        sql.append(";");
        System.out.println(sql);
        System.out.println(source);

        try {
            return template.queryForObject(sql.toString(), source, this::movieIdResponseMapper);
        } catch (DataAccessException e) {
            return null;
        }
    }

    private MovieSearchByIdResponse movieIdResponseMapper(ResultSet rs, int rowNumber)
        throws SQLException
    {
        MovieDetail movie = new MovieDetail()
                .setId(rs.getLong("m.id"))
                .setTitle(rs.getString("m.title"))
                .setYear(rs.getInt("m.year"))
                .setDirector(rs.getString("d.name"))
                .setRating(rs.getDouble("m.rating"))
                .setNumVotes(rs.getLong("m.num_votes"))
                .setBudget(rs.getLong("m.budget"))
                .setRevenue(rs.getLong("m.revenue"))
                .setOverview(rs.getString("m.overview"))
                .setBackdropPath(rs.getString("m.backdrop_path"))
                .setPosterPath(rs.getString("m.poster_path"))
                .setHidden(rs.getBoolean("m.hidden"));

        List<Genre> genres = null;
        List<Person> persons = null;

        try {
            String genresString = rs.getString("genres");
            Genre[] genresArray = mapper.readValue(genresString, Genre[].class);

            String personsString = rs.getString("persons");
            Person[] personsArray = mapper.readValue(personsString, Person[].class);

            genres = Arrays.stream(genresArray).collect(Collectors.toList());
            persons = Arrays.stream(personsArray).collect(Collectors.toList());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return new MovieSearchByIdResponse()
                .setMovie(movie)
                .setGenres(genres)
                .setPersons(persons);
    }

    public List<PersonDetail> personSearch(PersonSearchRequest request)
    {
        StringBuilder sql = new StringBuilder(GET_PERSON_SEARCH);
        MapSqlParameterSource source = new MapSqlParameterSource();
        boolean whereAdded = false;

        if (request.getMovieTitle() != null) {
            sql.append("INNER JOIN movies.movie_person mp ON mp.person_id = p.id\n");
            sql.append("INNER JOIN movies.movie m ON m.id = mp.movie_id\n");
        }

        if (request.getName() != null) {
            whereAdded = true;
            sql.append("WHERE p.name LIKE :name\n");
            source.addValue("name", "%" + request.getName() + "%", Types.VARCHAR);
        }

        if (request.getBirthday() != null) {
            if (!whereAdded) {
                sql.append("WHERE ");
                whereAdded = true;
            } else {
                sql.append("AND ");
            }

            sql.append("p.birthday = :birthday\n");
            source.addValue("birthday", request.getBirthday(), Types.DATE);
        }

        if (request.getMovieTitle() != null) {
            if (!whereAdded) {
                sql.append("WHERE ");
                whereAdded = true;
            } else {
                sql.append("AND ");
            }

            sql.append("m.title LIKE :movieTitle\n");
            source.addValue("movieTitle", "%" + request.getMovieTitle() + "%", Types.VARCHAR);
        }

        buildSQLDefaultParams(sql, source, request.getOrderBy(), request.getDirection(), request.getLimit(), request.getPage(), "p");

        System.out.println(sql);
        System.out.println(source);

        try {
            return template.query(
                    sql.toString(),
                    source,
                    (rs, rowNum) -> new PersonDetail()
                            .setId(rs.getLong("p.id"))
                            .setName(rs.getString("p.name"))
                            .setBirthday(rs.getDate("p.birthday") != null ? String.valueOf(rs.getDate("p.birthday")) : null)
                            .setBiography(rs.getString("p.biography"))
                            .setBirthplace(rs.getString("p.birthplace"))
                            .setPopularity(rs.getFloat("p.popularity"))
                            .setProfilePath(rs.getString("p.profile_path"))
            );
        } catch (DataAccessException e) {
            return null;
        }
    }

    public PersonDetail personSearchById(Long id)
    {
        StringBuilder sql = new StringBuilder(GET_PERSON_BY_ID);
        MapSqlParameterSource source = new MapSqlParameterSource();

        source.addValue("personId", id, Types.INTEGER);
        sql.append(";");

        System.out.println(sql);

        try {
            return template.queryForObject(
                    sql.toString(),
                    source,
                    (rs, rowNum) -> new PersonDetail()
                            .setId(rs.getLong("p.id"))
                            .setName(rs.getString("p.name"))
                            .setBirthday(rs.getDate("p.birthday") != null ? String.valueOf(rs.getDate("p.birthday")) : null)
                            .setBiography(rs.getString("p.biography"))
                            .setBirthplace(rs.getString("p.birthplace"))
                            .setPopularity(rs.getFloat("p.popularity"))
                            .setProfilePath(rs.getString("p.profile_path"))
            );
        } catch (DataAccessException e) {
            return null;
        }
    }
}
