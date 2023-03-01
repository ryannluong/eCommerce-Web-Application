import React from "react";
import {useUser} from "hook/User";
import styled from "styled-components";
import {cartRetrieve, cartUpdate, cartDelete} from "backend/idm";
import {useNavigate, useParams} from "react-router-dom";
import {FaTrashAlt} from "react-icons/fa"
import {useForm} from "react-hook-form";


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

const Cart = () => {
    const {
        accessToken, setAccessToken,
        refreshToken, setRefreshToken
    } = useUser();

    const navigate = useNavigate();
    const [cart, setCart] = React.useState({items: [], total: 0.0});
    let q;

    React.useEffect(() => {
        getCart();
    }, [q]);

    const getCart = () => {
        const payload = {
            accessToken: accessToken
        };

        cartRetrieve(payload).then(res => setCart(res.data))
    };

    if (cart.items)
        console.log(cart);

    const totalQuantity = () => {
        let count = 0;
        for (let i = 0; i < cart.items.length; i++) {
            count += cart.items[i].quantity;
        }

        return count;
    }

    const updateQuantity = (id) => {
        let select = document.getElementById(id);
        let option = select.options[select.selectedIndex];

        const payload = {
            accessToken: accessToken,
            movieId: id,
            quantity: option.value
        };

        console.log(payload);

        cartUpdate(payload).then(res => setCart(res.data))
    }

    const update = (id) => {
        updateQuantity(id);
        getCart();
    }

    const remove = (id) => {
        const payload = {
            accessToken: accessToken,
            movieId: id
        }

        cartDelete(payload).then(res => {
            if (res.data.result.code === 3030)
                getCart();
        })
    }


    return (
        <StyledDiv>
            <CenterDiv>
                <h1> Shopping Cart </h1>
            </CenterDiv>

            {cart.items &&
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

                        {cart.items.map(item => (
                            <tr key={item.movieId}>
                                <td>
                                    <div onClick={() => {navigate(`/movie/${item.movieId}`)}}>
                                        <img style={{border: "3px solid black"}}
                                             src={`https://image.tmdb.org/t/p/w200${item.posterPath}`}
                                             alt={item.movieTitle}
                                        />
                                    </div>
                                </td>
                                <td>{item.movieTitle}</td>
                                <td>
                                    {/*{item.quantity}*/}
                                    <select id={item.movieId} defaultValue={item.quantity} onChange={() => {
                                        update(item.movieId)
                                    }}>
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
                                </td>
                                <th>${(item.unitPrice).toFixed(2)}</th>
                                <td>
                                    <FaTrashAlt onClick={() => {remove(item.movieId)}}/>
                                </td>
                            </tr>
                        ))}
                    </tbody>
                    </table>

                    <div className="subtotal-label" style={{display: "flex", justifyContent: "flex-end", gap: 25}}>
                        <p>
                            Subtotal ({q = totalQuantity()} item{q > 1 ? "s" : ""}):
                        </p>
                        <h3>
                            ${(cart.total).toFixed(2)}
                        </h3>
                    </div>

                    <div className="checkout-button" style={{display: "flex", justifyContent: "flex-end", gap: 25}}>
                        <button onClick={() => navigate("/checkout")}> Proceed to checkout </button>
                    </div>

                </>
            }
            {!cart.items &&
                <>
                    <h1> Cart is empty. Return to search to add movies to your cart. </h1>
                    <CenterDiv>
                        <button onClick={() => navigate("/movie/search")}> Go to search </button>
                    </CenterDiv>
                </>
            }
        </StyledDiv>
    )
}

export default Cart;