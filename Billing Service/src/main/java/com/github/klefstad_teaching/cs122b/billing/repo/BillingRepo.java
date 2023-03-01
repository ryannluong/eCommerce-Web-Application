package com.github.klefstad_teaching.cs122b.billing.repo;

import com.github.klefstad_teaching.cs122b.billing.model.data.Item;
import com.github.klefstad_teaching.cs122b.billing.model.data.Sale;
import com.github.klefstad_teaching.cs122b.billing.model.request.OrderCompleteRequest;
import com.github.klefstad_teaching.cs122b.billing.model.response.CartDefaultResponse;
import com.github.klefstad_teaching.cs122b.billing.model.response.CartRetrieveResponse;
import com.github.klefstad_teaching.cs122b.billing.model.response.OrderListResponse;
import com.github.klefstad_teaching.cs122b.billing.model.response.OrderPaymentResponse;
import com.github.klefstad_teaching.cs122b.core.error.ResultError;
import com.github.klefstad_teaching.cs122b.core.result.BillingResults;
import com.github.klefstad_teaching.cs122b.core.security.JWTManager;
import com.nimbusds.jwt.SignedJWT;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.ParseException;
import java.time.Instant;
import java.util.List;

@Component
public class BillingRepo
{
    NamedParameterJdbcTemplate template;

    @Autowired
    public BillingRepo(NamedParameterJdbcTemplate template)
    {
        this.template = template;
    }

    //language=SQL
    private final static String CART_INSERT =
            "INSERT INTO billing.cart (user_id, movie_id, quantity) VALUES (:userId, :movieId, :quantity);";

    //language=SQL
    private final static String CART_UPDATE =
            "UPDATE billing.cart SET quantity = :quantity WHERE user_id = :userId AND movie_id = :movieId;";

    //language=SQL
    private final static String CART_DELETE =
            "DELETE FROM billing.cart WHERE user_id = :userId AND movie_id = :movieId;";

    //language=SQL
    private final static String CART_RETRIEVE =
            "SELECT mp.unit_price, mp.premium_discount, c.quantity, c.movie_id, m.title, m.backdrop_path, m.poster_path\n" +
            "FROM movies.movie m, billing.cart c, billing.movie_price mp\n" +
            "WHERE c.user_id = :userId AND c.movie_id = m.id AND m.id = mp.movie_id";

    //language=SQL
    private final static String CART_CLEAR =
            "DELETE FROM billing.cart WHERE user_id = :userId;";

    //language=SQL
    private final static String INSERT_SALE =
            "INSERT INTO billing.sale (user_id, total, order_date) VALUES (:userId, :total, :orderDate);";

    //language=SQL
    private final static String INSERT_SALE_ITEM =
            "INSERT INTO billing.sale_item (sale_id, movie_id, quantity) VALUES (:saleId, :movieId, :quantity);";

    //language=SQL
    private final static String GET_SALE_ID =
            "SELECT s.id FROM billing.sale s WHERE user_id = :userId ORDER BY s.order_date DESC LIMIT 1";

    //language=SQL
    private final static String ORDER_LIST =
            "SELECT s.id, s.total, s.order_date FROM billing.sale s WHERE s.user_id = :userId ORDER BY s.order_date DESC LIMIT 5;";

    //language=SQL
    private final static String ORDER_DETAIL =
            "SELECT mp.unit_price, mp.premium_discount, si.quantity, si.movie_id, m.title, m.backdrop_path, m.poster_path\n" +
            "FROM movies.movie m, billing.movie_price mp, billing.sale s, billing.sale_item si\n" +
            "WHERE s.user_id = :userId AND si.movie_id = m.id AND m.id = mp.movie_id AND s.id = si.sale_id AND si.sale_id = :saleId;";

    private boolean isAdminOrEmployee(SignedJWT user) {
        List<String> roles = null;

        try {
            roles = user.getJWTClaimsSet().getStringListClaim(JWTManager.CLAIM_ROLES);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return roles != null && (roles.contains("ADMIN") || roles.contains("EMPLOYEE"));
    }

    private List<String> getRoles(SignedJWT user) {
        List<String> roles = null;

        try {
            roles = user.getJWTClaimsSet().getStringListClaim(JWTManager.CLAIM_ROLES);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return roles;
    }

    private Long getUserId(SignedJWT user) {
        Long userId = null;
        try {
            userId = user.getJWTClaimsSet().getLongClaim(JWTManager.CLAIM_ID);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return userId;
    }

    public void cartInsert(SignedJWT user, Long movieId, Integer quantity) {
        Long userId = getUserId(user);
        try {
            template.update(CART_INSERT,
                    new MapSqlParameterSource()
                            .addValue("userId", userId, Types.INTEGER)
                            .addValue("movieId", movieId, Types.INTEGER)
                            .addValue("quantity", quantity, Types.INTEGER));
        } catch (DuplicateKeyException e) {
            throw new ResultError(BillingResults.CART_ITEM_EXISTS);
        }
    }


//    private Integer getQuantity(Long userId, Long movieId){
//        return template.queryForObject("SELECT quantity FROM billing.cart WHERE user_id = :userId AND movie_id = :movieId",
//                new MapSqlParameterSource()
//                        .addValue("userId", userId, Types.INTEGER)
//                        .addValue("movieId", movieId, Types.INTEGER),
//                Integer.class);
//    }

    public int cartUpdate(SignedJWT user, Long movieId, Integer quantity) {
        Long userId = getUserId(user);
        try {
            return template.update(CART_UPDATE,
                    new MapSqlParameterSource()
                            .addValue("userId", userId, Types.INTEGER)
                            .addValue("movieId", movieId, Types.INTEGER)
                            .addValue("quantity", quantity, Types.INTEGER));
        } catch (DataAccessException e) {
            throw new ResultError(BillingResults.CART_ITEM_DOES_NOT_EXIST);
        }
    }


    public int cartDelete(SignedJWT user, String movieId) {
        Long userId = getUserId(user);
        try {
            return template.update(CART_DELETE,
                    new MapSqlParameterSource()
                            .addValue("userId", userId, Types.INTEGER)
                            .addValue("movieId", movieId, Types.INTEGER));
        } catch (DataAccessException e) {
            throw new ResultError(BillingResults.CART_ITEM_DOES_NOT_EXIST);
        }
    }


    public CartRetrieveResponse cartRetrieve(SignedJWT user) {
        Long userId = getUserId(user);
        List<Item> items;
        List<String> roles = getRoles(user);
        boolean isPremium = roles.contains("PREMIUM");
        boolean isAdmin = roles.contains("ADMIN") || roles.contains("EMPLOYEE");

        System.out.println("userId: " + userId);

        StringBuilder sql = new StringBuilder(CART_RETRIEVE);
        if (!isAdmin)
            sql.append("\nAND m.hidden = false");
        sql.append(";");

        System.out.println(sql);

        try {
            if (isPremium) {
                items = template.query(sql.toString(),
                        new MapSqlParameterSource()
                                .addValue("userId", userId, Types.INTEGER),
                        (rs, rowNum) ->
                                new Item()
                                        .setUnitPrice(BigDecimal.valueOf(rs.getDouble("mp.unit_price")
                                                        * (1 - (rs.getDouble("mp.premium_discount") / 100.0)))
                                                .setScale(2, RoundingMode.DOWN))
                                        .setQuantity(rs.getInt("c.quantity"))
                                        .setMovieId(rs.getLong("c.movie_id"))
                                        .setMovieTitle(rs.getString("m.title"))
                                        .setBackdropPath(rs.getString("m.backdrop_path"))
                                        .setPosterPath(rs.getString("m.poster_path")));
            } else {
                items = template.query(sql.toString(),
                        new MapSqlParameterSource()
                                .addValue("userId", userId, Types.INTEGER),
                        (rs, rowNum) ->
                                new Item()
                                        .setUnitPrice(rs.getBigDecimal("mp.unit_price").setScale(2, RoundingMode.DOWN))
                                        .setQuantity(rs.getInt("c.quantity"))
                                        .setMovieId(rs.getLong("c.movie_id"))
                                        .setMovieTitle(rs.getString("m.title"))
                                        .setBackdropPath(rs.getString("m.backdrop_path"))
                                        .setPosterPath(rs.getString("m.poster_path")));
            }
            if (items.isEmpty()) throw new DataAccessException("hehe") {};

            BigDecimal total = BigDecimal.ZERO;
            for (Item item : items) {
                System.out.println("unitPrice: " + item.getUnitPrice() + ", quantity: " + item.getQuantity());

                total = new BigDecimal(String.valueOf(total)).setScale(2, RoundingMode.DOWN)
                        .add(BigDecimal.valueOf(item.getUnitPrice().doubleValue() * item.getQuantity()))
                        .setScale(2, RoundingMode.UP);
            }

            System.out.println("total: " + total);
            return new CartRetrieveResponse()
                    .setResult(BillingResults.CART_RETRIEVED)
                    .setItems(items)
                    .setTotal(total);

        } catch (DataAccessException ignored) {}

        return new CartRetrieveResponse()
                .setResult(BillingResults.CART_EMPTY);
    }

    public ResponseEntity<CartDefaultResponse> cartClear(SignedJWT user) {
        Long userId = getUserId(user);
        int amt = template.update(CART_CLEAR,
                new MapSqlParameterSource()
                        .addValue("userId", userId, Types.INTEGER));

        if (amt < 1)
            return new CartDefaultResponse()
                    .setResult(BillingResults.CART_EMPTY)
                    .toResponse();
        else
            return new CartDefaultResponse()
                    .setResult(BillingResults.CART_CLEARED)
                    .toResponse();
    }

    public OrderPaymentResponse orderPayment(SignedJWT user) {
        Long userId = getUserId(user);
        CartRetrieveResponse temp = cartRetrieve(user);
        if (temp.getResult().equals(BillingResults.CART_EMPTY))
            return new OrderPaymentResponse()
                    .setResult(BillingResults.CART_EMPTY);

        StringBuilder description = new StringBuilder();
        List<Item> items = temp.getItems();
        for (Item item : items) {
            description.append(item.getMovieTitle()).append(", ");
        }

        description = new StringBuilder(description.substring(0, description.length() - 2));
        System.out.println(description);

        BigDecimal amount = temp.getTotal();

        PaymentIntentCreateParams paymentIntentCreateParams =
                PaymentIntentCreateParams
                        .builder()
                        .setCurrency("USD")
                        .setDescription(description.toString())
                        .setAmount(amount.longValue())
                        .putMetadata("userId", userId.toString())
                        .setAutomaticPaymentMethods(
                                PaymentIntentCreateParams.AutomaticPaymentMethods
                                        .builder()
                                        .setEnabled(true)
                                        .build()
                        )
                        .build();

        PaymentIntent paymentIntent;
        try {
            paymentIntent = PaymentIntent.create(paymentIntentCreateParams);
        } catch (StripeException e) {
            return new OrderPaymentResponse().setResult(BillingResults.STRIPE_ERROR);
        }

        return new OrderPaymentResponse()
                .setResult(BillingResults.ORDER_PAYMENT_INTENT_CREATED)
                .setPaymentIntentId(paymentIntent.getId())
                .setClientSecret(paymentIntent.getClientSecret());
    }

    private Integer getSaleId(Long userId) {
        return template.queryForObject(GET_SALE_ID,
                new MapSqlParameterSource()
                        .addValue("userId", userId, Types.INTEGER),
                Integer.class);
    }

    public CartDefaultResponse orderComplete(SignedJWT user, OrderCompleteRequest request) {
        Long userId = getUserId(user);
        PaymentIntent paymentIntent = null;
        try {
            paymentIntent = PaymentIntent.retrieve(request.getPaymentIntentId());
        } catch (StripeException e) {
            return new CartDefaultResponse().setResult(BillingResults.STRIPE_ERROR);
        }

        if (!paymentIntent.getStatus().equals("succeeded"))
            return new CartDefaultResponse().setResult(BillingResults.ORDER_CANNOT_COMPLETE_NOT_SUCCEEDED);
        if (!paymentIntent.getMetadata().get("userId").equals(userId.toString()))
            return new CartDefaultResponse().setResult(BillingResults.ORDER_CANNOT_COMPLETE_WRONG_USER);

        Instant orderDate = Instant.now();

        System.out.println("userId: " + userId + ", total: " + BigDecimal.valueOf(paymentIntent.getAmount()).movePointLeft(2) + ", orderDate: " + orderDate);

        template.update(INSERT_SALE,
                new MapSqlParameterSource()
                        .addValue("userId", userId, Types.INTEGER)
                        .addValue("total", BigDecimal.valueOf(paymentIntent.getAmount()).movePointLeft(2), Types.DECIMAL)
                        .addValue("orderDate", Timestamp.from(orderDate), Types.TIMESTAMP)
        );

        System.out.println("Inserted sale successfully");
        Integer saleId = getSaleId(userId);
        System.out.println("Got sale id");
        List<Item> items = cartRetrieve(user).getItems();
        for (Item item : items) {
            template.update(INSERT_SALE_ITEM,
                    new MapSqlParameterSource()
                            .addValue("saleId", saleId, Types.INTEGER)
                            .addValue("movieId", item.getMovieId(), Types.INTEGER)
                            .addValue("quantity", item.getQuantity(), Types.INTEGER)
            );
        }

        System.out.println("Inserted sale items successfully");

        cartClear(user);

        return new CartDefaultResponse()
                .setResult(BillingResults.ORDER_COMPLETED);
    }


    public OrderListResponse orderList(SignedJWT user) {
        Long userId = getUserId(user);

        List<Sale> sales = null;
        try {
            sales = template.query(ORDER_LIST,
                    new MapSqlParameterSource()
                            .addValue("userId", userId, Types.INTEGER),
                    (rs, rowNum) ->
                            new Sale()
                                    .setSaleId(rs.getLong("s.id"))
                                    .setTotal(rs.getBigDecimal("s.total"))
                                    .setOrderDate(rs.getTimestamp("s.order_date").toInstant())
            );
        } catch (DataAccessException ignored) {}

        if (sales == null || sales.isEmpty())
            return new OrderListResponse()
                    .setResult(BillingResults.ORDER_LIST_NO_SALES_FOUND);

        return new OrderListResponse()
                .setResult(BillingResults.ORDER_LIST_FOUND_SALES)
                .setSales(sales);
    }

    public CartRetrieveResponse orderDetail(SignedJWT user, Long saleId) {
        Long userId = getUserId(user);
        boolean isPremium = getRoles(user).contains("PREMIUM");

        List<Item> items = null;

        StringBuilder sql = new StringBuilder(ORDER_DETAIL);
        System.out.println(sql);

        try {
            if (isPremium) {
                items = template.query(sql.toString(),
                        new MapSqlParameterSource()
                                .addValue("userId", userId, Types.INTEGER)
                                .addValue("saleId", saleId, Types.INTEGER),
                        (rs, rowNum) ->
                                new Item()
                                        .setUnitPrice(BigDecimal.valueOf(rs.getDouble("mp.unit_price")
                                                        * (1 - (rs.getDouble("mp.premium_discount") / 100.0)))
                                                .setScale(2, RoundingMode.DOWN))
                                        .setQuantity(rs.getInt("si.quantity"))
                                        .setMovieId(rs.getLong("si.movie_id"))
                                        .setMovieTitle(rs.getString("m.title"))
                                        .setBackdropPath(rs.getString("m.backdrop_path"))
                                        .setPosterPath(rs.getString("m.poster_path")));
            } else {
                items = template.query(sql.toString(),
                        new MapSqlParameterSource()
                                .addValue("userId", userId, Types.INTEGER)
                                .addValue("saleId", saleId, Types.INTEGER),
                        (rs, rowNum) ->
                                new Item()
                                        .setUnitPrice(rs.getBigDecimal("mp.unit_price").setScale(2, RoundingMode.DOWN))
                                        .setQuantity(rs.getInt("si.quantity"))
                                        .setMovieId(rs.getLong("si.movie_id"))
                                        .setMovieTitle(rs.getString("m.title"))
                                        .setBackdropPath(rs.getString("m.backdrop_path"))
                                        .setPosterPath(rs.getString("m.poster_path")));
            }
            //noinspection DuplicatedCode
            if (items.isEmpty()) throw new DataAccessException("hehe") {};

            BigDecimal total = BigDecimal.ZERO;
            for (Item item : items) {
                System.out.println("unitPrice: " + item.getUnitPrice() + ", quantity: " + item.getQuantity());

                total = new BigDecimal(String.valueOf(total)).setScale(2, RoundingMode.DOWN)
                        .add(BigDecimal.valueOf(item.getUnitPrice().doubleValue() * item.getQuantity()))
                        .setScale(2, RoundingMode.UP);
            }

            System.out.println("total: " + total);
            return new CartRetrieveResponse()
                    .setResult(BillingResults.ORDER_DETAIL_FOUND)
                    .setItems(items)
                    .setTotal(total);

        } catch (DataAccessException ignored) {}

        return new CartRetrieveResponse()
                .setResult(BillingResults.ORDER_DETAIL_NOT_FOUND);
    }
}
