package com.github.klefstad_teaching.cs122b.billing.rest;

import com.github.klefstad_teaching.cs122b.billing.model.request.CartDefaultRequest;
import com.github.klefstad_teaching.cs122b.billing.model.request.OrderCompleteRequest;
import com.github.klefstad_teaching.cs122b.billing.model.response.CartDefaultResponse;
import com.github.klefstad_teaching.cs122b.billing.model.response.CartRetrieveResponse;
import com.github.klefstad_teaching.cs122b.billing.model.response.OrderListResponse;
import com.github.klefstad_teaching.cs122b.billing.model.response.OrderPaymentResponse;
import com.github.klefstad_teaching.cs122b.billing.repo.BillingRepo;
import com.github.klefstad_teaching.cs122b.billing.util.Validate;
import com.github.klefstad_teaching.cs122b.core.error.ResultError;
import com.github.klefstad_teaching.cs122b.core.result.BillingResults;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
public class CartController
{
    private final BillingRepo repo;
    private final Validate    validate;

    @Autowired
    public CartController(BillingRepo repo, Validate validate)
    {
        this.repo = repo;
        this.validate = validate;
    }

    @PostMapping("/cart/insert")
    public ResponseEntity<?> cartInsert(@AuthenticationPrincipal SignedJWT user, @RequestBody CartDefaultRequest request) {
        validate.quantity(request.getQuantity());
        repo.cartInsert(user, request.getMovieId(), request.getQuantity());

        System.out.println("movieId: " + request.getMovieId());
        System.out.println("quantity: " + request.getQuantity());

        return new CartDefaultResponse()
                .setResult(BillingResults.CART_ITEM_INSERTED)
                .toResponse();
    }

    @PostMapping("/cart/update")
    public ResponseEntity<CartDefaultResponse> cartUpdate(@AuthenticationPrincipal SignedJWT user, @RequestBody CartDefaultRequest request) {
        validate.quantity(request.getQuantity());
        int amt = repo.cartUpdate(user, request.getMovieId(), request.getQuantity());

        System.out.println("movieId: " + request.getMovieId());
        System.out.println("quantity: " + request.getQuantity());

        if (amt < 1)
            throw new ResultError(BillingResults.CART_ITEM_DOES_NOT_EXIST);

        return new CartDefaultResponse()
                .setResult(BillingResults.CART_ITEM_UPDATED)
                .toResponse();
    }

    @DeleteMapping("/cart/delete/{movieId}")
    public ResponseEntity<CartDefaultResponse> cartDelete(@AuthenticationPrincipal SignedJWT user, @PathVariable String movieId) {
        int amt = repo.cartDelete(user, movieId);

        System.out.println("movieId: " + movieId);

        if (amt < 1)
            throw new ResultError(BillingResults.CART_ITEM_DOES_NOT_EXIST);

        return new CartDefaultResponse()
                .setResult(BillingResults.CART_ITEM_DELETED)
                .toResponse();
    }

    @GetMapping("/cart/retrieve")
    public ResponseEntity<CartRetrieveResponse> cartRetrieve(@AuthenticationPrincipal SignedJWT user) {
        return repo.cartRetrieve(user).toResponse();
    }

    @PostMapping("/cart/clear")
    public ResponseEntity<CartDefaultResponse> cartClear(@AuthenticationPrincipal SignedJWT user) {
        return repo.cartClear(user);
    }

    @GetMapping("/order/payment")
    public ResponseEntity<OrderPaymentResponse> orderPayment(@AuthenticationPrincipal SignedJWT user) {
        return repo.orderPayment(user).toResponse();
    }

    @PostMapping("/order/complete")
    public ResponseEntity<CartDefaultResponse> orderComplete(@AuthenticationPrincipal SignedJWT user, @RequestBody OrderCompleteRequest request) {
        return repo.orderComplete(user, request).toResponse();
    }

    @GetMapping("/order/list")
    public ResponseEntity<OrderListResponse> orderList(@AuthenticationPrincipal SignedJWT user) {
        return repo.orderList(user).toResponse();
    }

    @GetMapping("/order/detail/{saleId}")
    public ResponseEntity<CartRetrieveResponse> orderDetail(@AuthenticationPrincipal SignedJWT user, @PathVariable Long saleId) {
        return repo.orderDetail(user, saleId).toResponse();
    }
}
