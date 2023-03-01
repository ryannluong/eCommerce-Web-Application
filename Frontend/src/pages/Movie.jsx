import React from "react";
import {useUser} from "hook/User";
import styled from "styled-components";
import {useForm} from "react-hook-form";
import {movieSearch} from "backend/idm";
import {Link, useSearchParams} from "react-router-dom";


const StyledDiv = styled.div`
  display: flex;
  flex-direction: column;
`

const CenterDiv = styled.div`
  display: flex;
  justify-content: space-around;
  align-items: center;
  padding: 5px;
`

// const StyledH1 = styled.h1`
// `
//
// const StyledInput = styled.input`
// `
//
// const StyledButton = styled.button`
// `

const Movie = () => {
    const {
        accessToken, setAccessToken,
        refreshToken, setRefreshToken
    } = useUser();

    const [movies, setMovies] = React.useState([]);
    const [page, setPage] = React.useState(1);
    const [limit, setLimit] = React.useState(10);
    const [searchParams, setSearchParams] = useSearchParams();
    const {register, getValues, handleSubmit} = useForm(
        {
            defaultValues: {
                title: searchParams.get("title") !== "null" ? searchParams.get("title") : "",
                year: searchParams.get("year") !== "null" ? searchParams.get("year") : "",
                director: searchParams.get("director") !== "null" ? searchParams.get("director") : "",
                genre: searchParams.get("genre") !== "null" ? searchParams.get("genre") : "",
                limit: searchParams.get("limit") || 10,
                page: searchParams.get("page") || 1,
                orderBy: searchParams.get("orderBy") || "title",
                direction: searchParams.get("direction") || "asc"
            }
        }
    );

    const getMovies = () => {
        const title = getValues("title");
        const year = getValues("year");
        const director = getValues("director");
        const genre = getValues("genre");
        const orderBy = getValues("orderBy");
        const direction = getValues("direction");
        setLimit(getValues("limit"));

        const payload = {
            accessToken: accessToken,
            transactionId: null,
            title: title == null ? null : title.trim() !== "" ? title.trim() : title,
            year: year !== "" ? year : null,
            director: director !== "" ? director : null,
            genre: genre !== "" ? genre : null,
            limit: limit,
            page: page,
            orderBy: orderBy,
            direction: direction
        };

        // Set search params to payload, except for accessToken
        const temp = {...payload};
        delete temp.accessToken;
        delete temp.transactionId;
        setSearchParams(temp);

        movieSearch(payload)
            .then(res => {
                setMovies(res.data.movies);
                // alert(JSON.stringify(res.data));
            })
            .catch(err => {
                console.log(err);
                console.log(accessToken);
                console.log(payload);
                alert(JSON.stringify(payload, null, 2));
                // alert(JSON.stringify(err.response.data, null, 2));
            });
    }

    React.useEffect(() => {
        getMovies()
    }, [page])

    const prev = () => {
        if (page > 1)
            setPage(page - 1);

        // getMovies();
    };

    const next = () => {
        setPage(page + 1);
        // getMovies();
    }


    // Create search bar and on search, returns a table of movies matching the search. If no movies are returned, display a message
    return (
        <StyledDiv id="body">
            <CenterDiv id="searchBar" style={{height: "25px"}}>
                <input {...register("title")} placeholder="Title"/>
                <input {...register("year")} placeholder="Year"/>
                <input {...register("director")} placeholder="Director"/>
                <input {...register("genre")} placeholder="Genre"/>
                <label>Limit: </label>
                <select {...register("limit")}>
                    <option value="10">10</option>
                    <option value="25">25</option>
                    <option value="50">50</option>
                    <option value="100">100</option>
                </select>
                <label>Sort By: </label>
                <select {...register("orderBy")}>
                    <option value="title">Title</option>
                    <option value="rating">Rating</option>
                    <option value="year">Year</option>
                </select>
                <select {...register("direction")}>
                    <option value="asc">Ascending</option>
                    <option value="desc">Descending</option>
                </select>
                <button onClick={handleSubmit(getMovies)}>Search</button>
            </CenterDiv>
            <div id="movieResults" style={{display: "block"}}>
                <ul className="grid">
                    {!!movies && movies.map(movie => (
                        <li className="grid-li" key={movie.id}>
                            <a href={`/movie/${movie.id}`} title={movie.title}>
                                <div className="center" id="movieImage" style={{alignItems: "center", textDecoration: "none"}}>
                                    <img className="movie-image"
                                         src={`https://image.tmdb.org/t/p/w500${movie.posterPath}`}
                                         alt={movie.title}
                                    />
                                    <div className="movie-rating">
                                        <i className="star-glyph">&#9733;</i>
                                        {movie.rating}
                                    </div>
                                </div>
                                <div style={{textAlign: "center"}}>
                                    <p className="movie-name">{movie.title}</p>
                                    <p className="movie-year">{movie.director}</p>
                                    <p className="movie-year">{movie.year}</p>
                                </div>
                            </a>
                        </li>
                    ))}
                </ul>
            </div>

            <CenterDiv id="page">
                <div>
                    <button onClick={() => {prev()}}> Back </button>
                </div>
                <p>Page {page}</p>
                <div>
                    <button onClick={() => {next()}}> Next </button>
                </div>
            </CenterDiv>

        </StyledDiv>
    );
}

export default Movie;
