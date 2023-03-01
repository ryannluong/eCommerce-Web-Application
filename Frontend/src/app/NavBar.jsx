import React from "react";
import {NavLink} from "react-router-dom";
import styled from "styled-components";
import {useUser} from "../hook/User";
import {FaShoppingCart, FaHistory} from "react-icons/fa"
import {AiFillHome} from "react-icons/ai"
import {FiSearch} from "react-icons/fi"

const StyledNav = styled.nav`
  display: flex;
  justify-content: center;

  width: calc(100vw - 10px);
  height: 50px;
  padding: 5px;

  background-color: #fff;
`;

const StyledNavLink = styled(NavLink)`
  position: flex;
  padding: 10px;
  font-size: 25px;
  color: #FFFFFFFF;
  text-decoration: none;
`;

/**
 * To be able to navigate around the website we have these NavLink's (Notice
 * that they are "styled" NavLink's that are now named StyledNavLink)
 * <br>
 * Whenever you add a NavLink here make sure to add a corresponding Route in
 * the Content Component
 * <br>
 * You can add as many Link as you would like here to allow for better navigation
 * <br>
 * Below we have two Links:
 * <li>Home - A link that will change the url of the page to "/"
 * <li>Login - A link that will change the url of the page to "/login"
 */
const NavBar = () => {
    const {accessToken} = useUser();

    return (
        <div className="navbar" style={{display: "flex", justifyContent: "center"}}>
            <div align="left" style={{display: "inline-block"}}>
                <StyledNavLink to="/"><AiFillHome/></StyledNavLink>
                <StyledNavLink to="/movie/search"><FiSearch/></StyledNavLink>
            </div>
            <div align="right" style={{paddingLeft: '2160px', display: "inline-block"}}>
                {!accessToken && <StyledNavLink to="/login">Login</StyledNavLink>}
                {!accessToken && <StyledNavLink to="/register">Register</StyledNavLink>}
                {accessToken && <StyledNavLink to="/orders"> <FaHistory/> </StyledNavLink>}
                {accessToken && <StyledNavLink to="/cart"><FaShoppingCart/></StyledNavLink>}
            </div>
        </div>
    );
}



export default NavBar;
