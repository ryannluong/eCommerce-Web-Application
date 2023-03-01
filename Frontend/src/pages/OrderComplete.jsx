import React, {useEffect, useState} from "react";
import {useUser} from "hook/User";
import styled from "styled-components";
import {orderComplete} from "backend/idm";
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

const OrderComplete = () => {
    const {accessToken} = useUser();
    const navigate = useNavigate();
    const [result, setResult] = useState({});

    const complete = (paymentIntent) => {
        const payload = {
            accessToken: accessToken,
            paymentIntentId: paymentIntent
        }

        orderComplete(payload).then(res => {setResult(res.data)})
    }

    let paymentIntent = new URLSearchParams(window.location.search).get(
        "payment_intent"
    );

    useEffect(() => {

        console.log("Payment Intent: " + paymentIntent);

        if (paymentIntent === null) { return; }

        complete(paymentIntent);
        console.log(result);
        paymentIntent = null;
    }, [])

    return (
        <StyledDiv>
            <h1> Order Complete </h1>
            <button onClick={() => {navigate("/orders")}}> View Orders </button>
        </StyledDiv>
    )
}

export default OrderComplete;