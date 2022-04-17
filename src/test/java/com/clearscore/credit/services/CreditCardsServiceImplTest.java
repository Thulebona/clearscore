package com.clearscore.credit.services;

import com.clearscore.credit.client.cscards.CSCardsClientImpl;
import com.clearscore.credit.client.cscards.model.CardSearchRequest;
import com.clearscore.credit.client.scoredcards.ScoredCardsClientImpl;
import com.clearscore.credit.client.scoredcards.model.ScoredCardsRequest;
import com.clearscore.credit.config.ApplicationConfig;
import com.clearscore.credit.controller.model.CreditCard;
import com.clearscore.credit.controller.model.CreditCardRequest;
import com.clearscore.credit.services.providers.CreditCardsServiceTestProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

//@WebMvcTest(CreditCardsController.class)
@ContextConfiguration(classes = {
        ModelMapper.class,
        ObjectMapper.class,
        ApplicationConfig.class,
})
@ExtendWith(MockitoExtension.class)
public class CreditCardsServiceImplTest {

    @Mock
    private RestTemplate restTemplate;
    @Mock
    private CSCardsClientImpl csCardsClient;
    @Mock
    private ScoredCardsClientImpl scoredCardsClient;
    @InjectMocks
    private CreditCardsServiceImpl creditCardsService;
    @Spy
    private ModelMapper modelMapper;


    @ParameterizedTest
    @ArgumentsSource(CreditCardsServiceTestProvider.ProviderForClientCall.class)
    void getCardRecommendations_should_call_both_client_when_invoke(CreditCardRequest creditCardRequest, Object cs_expected, Object scored_expected) {

        doReturn(CompletableFuture.completedFuture(cs_expected))
                .when(csCardsClient)
                .getCsCards(any(CardSearchRequest.class));

        doReturn(CompletableFuture.completedFuture(scored_expected))
                .when(scoredCardsClient)
                .getScoredCards(any(ScoredCardsRequest.class));

        var cardRecommendations = creditCardsService.getCardRecommendations(creditCardRequest);

        verify(csCardsClient).getCsCards(modelMapper.map(creditCardRequest, CardSearchRequest.class));
        verify(scoredCardsClient).getScoredCards(modelMapper.map(creditCardRequest, ScoredCardsRequest.class));

        assertThat(cardRecommendations).isInstanceOf(List.class);

    }

    @ParameterizedTest
    @ArgumentsSource(CreditCardsServiceTestProvider.ProviderForCombinedResult.class)
    void getCardRecommendations_should_call_both_client_and_return_sorted_results(CreditCardRequest creditCardRequest,
                                                                                  Object cs_expected,
                                                                                  Object scored_expected,
                                                                                  int expected_size) {

        doReturn(CompletableFuture.completedFuture(cs_expected))
                .when(csCardsClient)
                .getCsCards(any(CardSearchRequest.class));

        doReturn(CompletableFuture.completedFuture(scored_expected))
                .when(scoredCardsClient)
                .getScoredCards(any(ScoredCardsRequest.class));

        var cardRecommendations = creditCardsService.getCardRecommendations(creditCardRequest);

        verify(csCardsClient).getCsCards(modelMapper.map(creditCardRequest, CardSearchRequest.class));
        verify(scoredCardsClient).getScoredCards(modelMapper.map(creditCardRequest, ScoredCardsRequest.class));

        assertThat(cardRecommendations)
                .isInstanceOf(List.class)
                .hasSize(expected_size);

        if (expected_size > 1) {
            double max = cardRecommendations.stream().mapToDouble(CreditCard::getCardScore).max().orElse(0.00);
            double min = cardRecommendations.stream().mapToDouble(CreditCard::getCardScore).min().orElse(0.00);

            assertThat(cardRecommendations)
                    .isInstanceOf(List.class)
                    .hasSize(expected_size)
                    .first()
                    .extracting(CreditCard::getCardScore)
                    .isNotEqualTo(min)
                    .isEqualTo(max);
        }

    }

}
