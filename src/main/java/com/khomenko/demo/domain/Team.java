package com.khomenko.demo.domain;

import lombok.Builder;
import lombok.Data;


/**
 * Game entity is representation of real game from Football World Cup Score Board
 * <p>
 * Contains basic value validation
 *
 * @param countryOfOrigin team country of Origin
 */

@Data
@Builder
public class Team {
    private String countryOfOrigin;
}
