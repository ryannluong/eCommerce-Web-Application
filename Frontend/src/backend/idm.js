import Config from "backend/config.json";
import Axios from "axios";
import cart from "../pages/Cart";


/**
 * We use axios to create REST calls to our backend
 *
 * We have provided the login rest call for your
 * reference to build other rest calls with.
 *
 * This is an async function. Which means calling this function requires that
 * you "chain" it with a .then() function call.
 * <br>
 * What this means is when the function is called it will essentially do it "in
 * another thread" and when the action is done being executed it will do
 * whatever the logic in your ".then()" function you chained to it
 * @example
 * login(request)
 * .then(response => alert(JSON.stringify(response.data, null, 2)));
 */
export async function login(loginRequest) {
    const requestBody = {
        email: loginRequest.email,
        password: loginRequest.password
    };

    const options = {
        method: "POST", // Method type ("POST", "GET", "DELETE", ect)
        baseURL: Config.baseUrl, // Base URL (localhost:8081 for example)
        url: Config.idm.login, // Path of URL ("/login")
        data: requestBody // Data to send in Body (The RequestBody to send)
    }

    return Axios.request(options);
}

export async function signup(registerRequest) {
    const requestBody = {
        email: registerRequest.email,
        password: registerRequest.password
    };

    const options = {
        method: "POST",
        baseURL: Config.baseUrl,
        url: Config.idm.register,
        data: requestBody
    }

    return Axios.request(options);
}

export async function movieSearch(searchRequest) {
    const queryParams = {
        title: searchRequest.title,
        director: searchRequest.director,
        genre: searchRequest.genre,
        orderBy: searchRequest.orderBy,
        direction: searchRequest.direction,
        year: searchRequest.year,
        page: searchRequest.page,
        limit: searchRequest.limit
    };

    const options = {
        method: "GET",
        baseURL: Config.baseUrlMovieSearch,
        url: Config.idm.movieSearch,
        headers: {
            Authorization: "Bearer " + searchRequest.accessToken
        },
        params: queryParams
    }

    return Axios.request(options);
}

export async function movieById(searchRequest) {
    const options = {
        method: "GET",
        baseURL: Config.baseUrlMovieSearch,
        url: "/movie/" + searchRequest.movieId,
        headers: {
            Authorization: "Bearer " + searchRequest.accessToken
        }
    };

    return Axios.request(options);
}

export async function cartInsert(cartRequest) {
    const requestBody = {
        movieId: cartRequest.movieId,
        quantity: cartRequest.quantity
    };

    const options = {
        method: "POST",
        baseURL: Config.baseUrlCart,
        url: Config.idm.cartInsert,
        headers: {
            Authorization: "Bearer " + cartRequest.accessToken
        },
        data: requestBody
    };

    return Axios.request(options);
}

export async function cartUpdate(cartRequest) {
    const requestBody = {
        movieId: cartRequest.movieId,
        quantity: cartRequest.quantity
    };

    const options = {
        method: "POST",
        baseURL: Config.baseUrlCart,
        url: Config.idm.cartUpdate,
        headers: {
            Authorization: "Bearer " + cartRequest.accessToken
        },
        data: requestBody
    };

    return Axios.request(options);
}

export async function cartRetrieve(cartRequest) {
    const options = {
        method: "GET",
        baseURL: Config.baseUrlCart,
        url: Config.idm.cartRetrieve,
        headers: {
            Authorization: "Bearer " + cartRequest.accessToken
        }
    };

    return Axios.request(options);
}

export async function cartDelete(cartRequest) {
    const options = {
        method: "DELETE",
        baseURL: Config.baseUrlCart,
        url: "/cart/delete/" + cartRequest.movieId,
        headers: {
            Authorization: "Bearer " + cartRequest.accessToken
        }
    }

    return Axios.request(options);
}

export async function orderList(cartRequest) {
    const options = {
        method: "GET",
        baseURL: Config.baseUrlCart,
        url: Config.idm.orderHistory,
        headers: {
            Authorization: "Bearer " + cartRequest.accessToken
        }
    };

    return Axios.request(options);
}

export async function orderPayment(paymentRequest) {
    const options = {
        method: "GET",
        baseURL: Config.baseUrlCart,
        url: Config.idm.checkout,
        headers: {
            Authorization: "Bearer " + paymentRequest.accessToken
        }
    };

    return Axios.request(options);
}

export async function orderComplete(paymentRequest) {
    const options = {
        method: "POST",
        baseURL: Config.baseUrlCart,
        url: Config.idm.orderComplete,
        headers: {
            Authorization: "Bearer " + paymentRequest.accessToken
        },
        data: paymentRequest
    };

    return Axios.request(options);
}

export async function orderDetail(orderRequest) {
    const options = {
        method: "GET",
        baseURL: Config.baseUrlCart,
        url: "/order/detail/" + orderRequest.saleId,
        headers: {
            Authorization: "Bearer " + orderRequest.accessToken
        }
    };

    return Axios.request(options);
}