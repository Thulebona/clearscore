package com.clearscore.credit.services.providers;

import com.clearscore.credit.client.cscards.model.Card;
import com.clearscore.credit.client.scoredcards.model.CreditCard;
import com.clearscore.credit.controller.model.CreditCardRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.stream.Stream;


public class CreditCardsServiceTestProvider {

    public static class ProviderForClientCall implements ArgumentsProvider {
        private final InputStream cs_expected;
        private final InputStream scored_expected;
        private final ObjectMapper mapper;

        public ProviderForClientCall() throws FileNotFoundException {
            cs_expected = new FileInputStream(Paths.get("src", "test", "resources", "testdata", "cscards_test_data.json").toFile());
            scored_expected = new FileInputStream(Paths.get("src", "test", "resources", "testdata", "scoredcards_test_data.json").toFile());
            mapper = new ObjectMapper();
        }

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) throws Exception {

            var creditCardRequest = new CreditCardRequest()
                    .creditScore(650)
                    .salary(55000)
                    .name("Rhino Labuschagne");

            return Stream.of(
                    Arguments.of(creditCardRequest, mapper.readValue(cs_expected, Card[].class), mapper.readValue(scored_expected, CreditCard[].class)),
                    Arguments.of(creditCardRequest, new Card[1], new CreditCard[1]),
                    Arguments.of(creditCardRequest, null, null),
                    Arguments.of(creditCardRequest, null, new NoSuchFileException("ignore test"))
            );
        }

    }


    public static class ProviderForCombinedResult implements ArgumentsProvider {
        private final InputStream cs_expected;
        private final InputStream scored_expected;
        private final ObjectMapper mapper;

        public ProviderForCombinedResult() throws FileNotFoundException {
            cs_expected = new FileInputStream(Paths.get("src", "test", "resources", "testdata", "cscards_test_data.json").toFile());
            scored_expected = new FileInputStream(Paths.get("src", "test", "resources", "testdata", "scoredcards_test_data.json").toFile());
            mapper = new ObjectMapper();
        }

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) throws Exception {

            var creditCardRequest = new CreditCardRequest()
                    .creditScore(650)
                    .salary(55000)
                    .name("Rhino Labuschagne");

            var cs = mapper.readValue(cs_expected, Card[].class);
            var scored = mapper.readValue(scored_expected, CreditCard[].class);

            return Stream.of(
                    Arguments.of(creditCardRequest, cs, scored, 3),
                    Arguments.of(creditCardRequest, cs, new RuntimeException("scored_exception"), 2),
                    Arguments.of(creditCardRequest, null, scored, 1),
                    Arguments.of(creditCardRequest, null, null, 0)
            );
        }

    }

}
