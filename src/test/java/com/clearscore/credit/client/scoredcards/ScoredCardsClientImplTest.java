package com.clearscore.credit.client.scoredcards;

import com.clearscore.credit.client.cscards.model.Card;
import com.clearscore.credit.client.scoredcards.model.CreditCard;
import com.clearscore.credit.client.scoredcards.model.ScoredCardsRequest;
import com.clearscore.credit.exceptions.CustomClientException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
class ScoredCardsClientImplTest {

    @Mock
    private RestTemplate restTemplate;
    @InjectMocks
    private ScoredCardsClientImpl scoredCardsClient;
    private ScoredCardsRequest inputRequest;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(scoredCardsClient, "scoreCardsUrl", "https://test.com/scorecards");
        inputRequest = new ScoredCardsRequest()
                .name("John Smith")
                .salary(20000)
                .score(300);
    }

    @Test
    void getScoredCards_should_invoke_postForEntity_and_return_completableFuture_with_cardsArray() {

        doReturn(ResponseEntity.ok(new CreditCard[1]))
                .when(restTemplate)
                .postForEntity(anyString(), any(HttpEntity.class), any());

        var csCards = scoredCardsClient.getScoredCards(inputRequest);

        var parm1 = ArgumentCaptor.forClass(String.class);
        var parm2 = ArgumentCaptor.forClass(HttpEntity.class);

        verify(restTemplate).postForEntity(parm1.capture(), parm2.capture(), any());

        assertThat(csCards)
                .isInstanceOf(CompletableFuture.class)
                .isCompleted()
                .isDone();

        assertThat(csCards.join())
                .isInstanceOf(CreditCard[].class)
                .hasSize(1);

    }

    @Test
    void getScoredCards_should_invoke_postForEntity_and_return_completableFuture_with_exception_if_error_thrown() {

        doThrow(new RestClientResponseException("internal server error", 500, "server error", null, null, null))
                .when(restTemplate)
                .postForEntity(anyString(), any(HttpEntity.class), any());

        var csCards = scoredCardsClient.getScoredCards(inputRequest);

        verify(restTemplate).postForEntity(anyString(), any(HttpEntity.class), any());
        csCards.handle((cards, throwable) -> {
            assertNull(cards);
            assertThat(throwable)
                    .isInstanceOf(CustomClientException.class)
                    .extracting(Throwable::getCause)
                    .isInstanceOf(RestClientResponseException.class)
                    .extracting(Throwable::getMessage)
                    .isEqualTo("internal server error");
            return null;
        }).join();

    }  
    
    @Test
    void getScoredCards_should_invoke_postForEntity_and_return_completableFuture_with_exception_if_body_isnull() {

        doReturn(ResponseEntity.ok(null))
                .when(restTemplate)
                .postForEntity(anyString(), any(HttpEntity.class), any());

        var csCards = scoredCardsClient.getScoredCards(inputRequest);

        verify(restTemplate).postForEntity(anyString(), any(HttpEntity.class), any());
        csCards.handle((cards, throwable) -> {
            assertNull(cards);
            assertThat(throwable)
                    .isInstanceOf(CustomClientException.class)
                    .extracting(Throwable::getMessage)
                    .isEqualTo("response body is empty");
            return null;
        }).join();

    }

}
