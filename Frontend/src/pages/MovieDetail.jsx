import React from "react";
import {useUser} from "hook/User";
import styled from "styled-components";
import {cartInsert, movieById} from "backend/idm";

import {useNavigate, useParams} from "react-router-dom";
import {useForm} from "react-hook-form";

const StyledDiv = styled.div`
  display: flex;
  flex-direction: column;
`;

const CenterDiv = styled.div`
  display: flex;
  justify-content: space-around;
  align-items: center;
  padding: 5px;
`;

const MovieDetail = () => {
    const {
        accessToken, setAccessToken,
        refreshToken, setRefreshToken
    } = useUser();

    const {id} = useParams();
    const navigate = useNavigate();
    const [post, setPost] = React.useState();
    const {register, getValues, handleSubmit} = useForm(
        {defaultValues: {quantity : 1}}
    );

    React.useEffect(() => {
        getMovie();
    }, []);

    const getMovie = () => {
        const payload = {
            accessToken: accessToken,
            movieId: id
        };

        movieById(payload).then(res => setPost(res.data));
    };

    const addMovie = () => {
        const payload = {
            accessToken: accessToken,
            movieId: id,
            quantity: getValues("quantity")
        };

        console.log(payload);

        cartInsert(payload).then(resp => {
            console.log(resp.data);
            alert(JSON.stringify("Movie added to cart."));
        })
                           .catch(error => console.log(error)
        );
    };


    return (
        <StyledDiv>
            {post &&
                <React.Fragment>
                    {/*<div>*/}
                    {/*    style={{backgroundImage: `url(https://image.tmdb.org/t/p/w200${post.movie.backdropPath})`}}*/}
                    {/*</div>*/}
                    <div style={{display:"flex", flexDirection:"column", gap:15}}>
                        <div style={{display:"flex", flexDirection:"row", gap:25}}>
                            <h2 >{post.movie.title}</h2>
                            <p>({post.movie.year})</p>
                            <p>Rating: {post.movie.rating}</p>
                        </div>
                        <div style={{display:"flex", flexDirection:"row",gap:25}}>
                            <img style = {{border: "3px solid black"}}
                                 src={`https://image.tmdb.org/t/p/w200${post.movie.posterPath}`}
                                 alt={post.movie.title}
                            />
                            <div style={{display:"flex", flexDirection:"column", gap:25}}>
                                <h2> Overview </h2>
                                <p>{post.movie.overview}</p>
                                <div>
                                    <select {...register("quantity")}>
                                        <option value="1">1</option>
                                        <option value="2">2</option>
                                        <option value="3">3</option>
                                        <option value="4">4</option>
                                        <option value="5">5</option>
                                        <option value="6">6</option>
                                        <option value="7">7</option>
                                        <option value="8">8</option>
                                        <option value="9">9</option>
                                        <option value="10">10</option>
                                    </select>
                                    <button style = {{backgroundColor:"lightskyblue" ,height: 40, width:100, fontSize: 18}} onClick={
                                        handleSubmit(addMovie)
                                    }>Add to Cart</button>
                                </div>
                                <div>
                                    <button style = {{backgroundColor:"lightgray" ,height: 50, width:150, fontSize: 18}} onClick={() => navigate(-1)}>Back</button>
                                </div>
                            </div>
                        </div>
                    </div>
                </React.Fragment>
            }
        </StyledDiv>
    )
}

export default MovieDetail;


// const MovieDetail = () => {
//     const {
//         accessToken, setAccessToken,
//         refreshToken, setRefreshToken
//     } = useUser();
//     const {id} = useParams();
//     const navigate = useNavigate();
//     const [ message, setMessage ] = React.useState("");
//     const [result, setResult] = React.useState({});
//
//     const showMovie = () =>
//     {
//         getMovie(accessToken, id)
//             .then(resp =>
//             {
//                 // console.log(resp);
//                 resp = resp.data;
//                 if(resp.result.code == 2011)
//                 {
//                     alert(resp.result.message);
//                     return;
//                 }
//                 setMessage(JSON.stringify(resp.movie));
//                 setResult(resp.movie);
//             })
//     }
//
//     const addMovie = (id) =>
//     {
//         cart_insert(accessToken, {movieId:id, quantity:1}).then(resp =>
//         {
//             alert("Item Inserted");
//             console.log(resp.data);
//         }).catch(error =>
//         {
//             alert(JSON.stringify("Item already in cart."));
//         });
//     }
//
//     return(
//         <div style={{display:"flex", flexDirection:"column", gap:15}}>
//             <div style={{display:"flex", flexDirection:"row", gap:25}}>
//                 <h2 >{result.title}</h2>
//                 <p>({result.year})</p>
//                 <p>Rating: {result.rating}</p>
//             </div>
//             <div style={{display:"flex", flexDirection:"row",gap:25}}>
//                 <img style = {{border: "3px solid black"}} src={"https://image.tmdb.org/t/p/w200" + result.posterPath}/>
//                 <div style={{display:"flex", flexDirection:"column", gap:25}}>
//                     <p>{result.overview}</p>
//                     <div>
//                         <button style = {{backgroundColor:"lightskyblue" ,height: 75, width:200, fontSize: 20}} onClick={() => addMovie(result.id)}>Add to Cart</button>
//                     </div>
//                     <div>
//                         <button style = {{backgroundColor:"lightgray" ,height: 50, width:150, fontSize: 18}} onClick={() => navigate("/")}>Home</button>
//                     </div>
//                 </div>
//             </div>
//             {showMovie()}
//         </div>
//
//     );
//
//
// }
//
// export default MovieDetail;