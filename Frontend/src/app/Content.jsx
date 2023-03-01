import React from "react";
import {Route, Routes} from "react-router-dom";

import OrderHistory from "pages/OrderHistory"
import Cart from "pages/Cart";
import Movie from "pages/Movie";
import Login from "pages/Login";
import Register from "pages/Register";
import Home from "pages/Home";
import styled from "styled-components";
import {useUser} from "../hook/User";
import MovieDetail from "../pages/MovieDetail";
import Checkout from "../pages/Checkout";
import OrderComplete from "../pages/OrderComplete";
import OrderDetail from "../pages/OrderDetail";

const StyledDiv = styled.div`
  display: flex;
  justify-content: center;

  width: 100vw;
  height: 100vh;
  padding: 25px;

  background: #ffffff;
  box-shadow: inset 0 3px 5px -3px #000000;
`

/**
 * This is the Component that will switch out what Component is being shown
 * depending on the "url" of the page
 * <br>
 * You'll notice that we have a <Routes> Component and inside it, we have
 * multiple <Route> components. Each <Route> maps a specific "url" to show a
 * specific Component.
 * <br>
 * Whenever you add a Route here make sure to add a corresponding NavLink in
 * the NavBar Component.
 * <br>
 * You can essentially think of this as a switch statement:
 * @example
 * switch (url) {
 *     case "/login":
 *         return <Login/>;
 *     case "/":
 *         return <Home/>;
 * }
 *
 */
const Content = () => {
    const {accessToken} = useUser();

    return (
        <StyledDiv>
            <Routes>
                <Route path="/order/detail/:id" element={<OrderDetail/>}/>
                <Route path="/complete" element={<OrderComplete/>}/>
                <Route path="/checkout" element={<Checkout/>}/>
                <Route path="/orders" element={<OrderHistory/>}/>
                <Route path="/cart" element={<Cart/>}/>
                <Route path="/movie/:id" element={<MovieDetail/>}/>
                <Route path="/movie/search" element={<Movie/>}/>
                {/*{!!accessToken && <Route path="/login" element={<Login/>}/>}*/}
                <Route path="/login" element={<Login/>}/>
                <Route path="/register" element={<Register/>}/>
                <Route path="/" element={<Home/>}/>
            </Routes>
        </StyledDiv>
    );
}

export default Content;
