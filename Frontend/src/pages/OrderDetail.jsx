import React, {useEffect} from "react";
import {useUser} from "hook/User";
import styled from "styled-components";
import {orderDetail} from "backend/idm";
import {useNavigate, useParams} from "react-router-dom";


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

const OrderDetail = () => {
    const {accessToken} = useUser();

    const {id} = useParams();
    const navigate = useNavigate();
    const [post, setPost] = React.useState({items: [], total: 0.0});
    let q;

    useEffect(() => {
        getOrder();
    }, [])

    const getOrder = () => {
        const payload = {
            accessToken: accessToken,
            saleId: id
        };

        orderDetail(payload).then(res => setPost(res.data))
    }

    const totalQuantity = () => {
        let count = 0;
        for (let i = 0; i < post.items.length; i++) {
            count += post.items[i].quantity;
        }

        return count;
    }

    return (
        <StyledDiv>
            <CenterDiv>
                <h1> Order #{id}</h1>
            </CenterDiv>

            {post.items &&
                <>
                    <table className="cart-list" style={{gap: 25}}>
                        <tbody>
                        <tr>
                            <td> </td>
                            <td>Title</td>
                            <td>Quantity</td>
                            <td>Price</td>
                            <td> </td>
                        </tr>

                        {post.items && post.items.map(item => (
                            <tr key={item.movieId}>
                                <td>
                                    <div onClick={() => {navigate(`/movie/${item.movieId}`)}}>
                                        <img style={{border: "3px solid black"}}
                                             src={`https://image.tmdb.org/t/p/w200${item.posterPath}`}
                                             alt={item.movieTitle}
                                        />
                                    </div>
                                </td>
                                <td>
                                    <a href={`/movie/${item.movieId}`}> {item.movieTitle} </a>
                                </td>
                                <td>{item.quantity}</td>
                                <th>${(item.unitPrice).toFixed(2)}</th>
                            </tr>
                        ))}
                        </tbody>
                    </table>

                    <div className="subtotal-label" style={{display: "flex", justifyContent: "flex-end", gap: 25}}>
                        <p>
                            Subtotal ({q = totalQuantity()} item{q > 1 ? "s" : ""}):
                        </p>
                        <h3>
                            ${(post.total).toFixed(2)}
                        </h3>
                    </div>
                </>
            }


        </StyledDiv>
    )
}

export default OrderDetail;