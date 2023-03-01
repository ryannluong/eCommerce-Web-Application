package com.github.klefstad_teaching.cs122b.movies.util;

import com.github.klefstad_teaching.cs122b.core.error.ResultError;
import com.github.klefstad_teaching.cs122b.core.result.MoviesResults;
import org.springframework.stereotype.Component;

@Component
public class Validate
{
    public static void direction(String direction) {
        if (direction != null && !direction.equals("asc") && !direction.equals("desc") && !direction.equals("ASC") && !direction.equals("DESC"))
            throw new ResultError(MoviesResults.INVALID_DIRECTION);
    }

    public static void limit(Integer limit) {
        if (limit != null && limit != 10 && limit != 25 && limit != 50 && limit != 100)
            throw new ResultError(MoviesResults.INVALID_LIMIT);
    }

    public static void page(Integer page) {
        if (page != null && page < 1)
            throw new ResultError(MoviesResults.INVALID_PAGE);
    }
}
