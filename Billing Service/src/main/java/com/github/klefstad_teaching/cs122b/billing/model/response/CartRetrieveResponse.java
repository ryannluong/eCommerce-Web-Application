package com.github.klefstad_teaching.cs122b.billing.model.response;

import com.github.klefstad_teaching.cs122b.billing.model.data.Item;
import com.github.klefstad_teaching.cs122b.core.base.ResponseModel;
import java.util.List;

import java.math.BigDecimal;

public class CartRetrieveResponse extends ResponseModel<CartRetrieveResponse> {
    BigDecimal total;
    List<Item> items;

    public BigDecimal getTotal() {
        return total;
    }

    public CartRetrieveResponse setTotal(BigDecimal total) {
        this.total = total;
        return this;
    }

    public List<Item> getItems() {
        return items;
    }

    public CartRetrieveResponse setItems(List<Item> items) {
        this.items = items;
        return this;
    }
}
