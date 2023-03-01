package com.github.klefstad_teaching.cs122b.gateway.repo;

import com.github.klefstad_teaching.cs122b.gateway.model.data.GatewayRequestObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.sql.Timestamp;
import java.sql.Types;
import java.util.List;

@Component
public class GatewayRepo
{
    private NamedParameterJdbcTemplate template;

    //language=SQL
    private static final String INSERT_REQUEST =
        "INSERT INTO gateway.request (ip_address, call_time, path)\n" +
        "VALUES (:ipAddress, :callTime, :path);";

    @Autowired
    public GatewayRepo(NamedParameterJdbcTemplate template)
    {
        this.template = template;
    }

    public int[] insert(List<GatewayRequestObject> requests)
    {
        return template.batchUpdate(INSERT_REQUEST,
                requests.stream()
                        .map(object -> new MapSqlParameterSource()
                                .addValue("ipAddress", object.getIpAddress(), Types.VARCHAR)
                                .addValue("callTime", Timestamp.from(object.getCallTime()), Types.TIMESTAMP)
                                .addValue("path", object.getPath(), Types.VARCHAR))
                        .toArray(MapSqlParameterSource[]::new)
        );
    }

    public Mono<int[]> insertRequests(List<GatewayRequestObject> requests)
    {
        return Mono.fromCallable(() -> insert(requests));
    }
}
