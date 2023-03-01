import React, { useState, useEffect } from "react";
import { loadStripe } from "@stripe/stripe-js";
import { Elements } from "@stripe/react-stripe-js";

import CheckoutForm from "./CheckoutForm";
import "./Checkout.css";
import {useUser} from "../hook/User";
import {orderPayment} from "../backend/idm";

// Make sure to call loadStripe outside of a component’s render to avoid
// recreating the Stripe object on every render.
// This is a public sample test API key.
// Don’t submit any personally identifiable information in requests made with this key.
// Sign in to see your own test API key embedded in code samples.
const stripePromise = loadStripe("pk_test_51Ky0ymHZ5eznCgZMKGBShelGxVyrSbeZlInVi9h1DSK1Bbq5mzyrb9apSyR25tuNg89MNLIbt01XPToMIGJ7SFUW006sgW7Cfl");

export default function App() {
    const [clientSecret, setClientSecret] = useState("");
    const {accessToken} = useUser();

    useEffect(() => {
        const payload = {
            accessToken: accessToken,
        }

        orderPayment(payload).then(res => {
            setClientSecret(res.data.clientSecret);
        })
    }, []);

    const appearance = {
        theme: 'stripe',
        variables: {
            colorBackground: '#ffffff',
            colorText: '#30313d',
        },
    };
    const options = {
        clientSecret,
        appearance
    };

    return (
        <div className="App">
            {clientSecret && (
                <Elements options={options} stripe={stripePromise}>
                    <CheckoutForm />
                </Elements>
            )}
        </div>
    );
}