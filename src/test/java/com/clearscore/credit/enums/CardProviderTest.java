package com.clearscore.credit.enums;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

public class CardProviderTest {

    @ParameterizedTest
    @EnumSource(CardProvider.class)
    public void cardProvider_getName_should_return_string(CardProvider cardProvider) {
        assertThat(cardProvider.getName())
                .isInstanceOf(String.class)
                .endsWith("Cards");
    }


    @ParameterizedTest
    @MethodSource("provideCardProviderNames")
    void testFormatUnitMethodOfAreaUnit(CardProvider cardProvider, String expected) {
        assertThat(cardProvider.getName())
                .isNotNull()
                .isEqualTo(expected);
    }

    private static Stream<Arguments> provideCardProviderNames() {
        return Stream.of(
                arguments(CardProvider.SCORED_CARDS, "ScoredCards"),
                arguments(CardProvider.CS_CARDS, "CSCards")
        );
    }

}
