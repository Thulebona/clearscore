package com.clearscore.credit.enums;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CardProvider {

    SCORED_CARDS("ScoredCards"),
    CS_CARDS("CSCards");

    private final String name;
}
