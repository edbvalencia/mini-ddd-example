package com.alutarb.backoffice.match.application.create;

public record CreateMatchRequest(
        String id,
        String teamHome,
        String teamAway
) {
}