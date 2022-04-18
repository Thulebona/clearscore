package com.clearscore.credit.client.scoredcards;

import com.clearscore.credit.client.scoredcards.interfaces.ScoredCardsClient;
import com.clearscore.credit.client.scoredcards.model.CreditCard;
import com.clearscore.credit.client.scoredcards.model.ScoredCardsRequest;
import com.clearscore.credit.enums.CardProvider;
import com.clearscore.credit.exceptions.CustomClientException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;
import org.webjars.NotFoundException;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScoredCardsClientImpl implements ScoredCardsClient {
    private final RestTemplate restTemplate;
    @Value("${client.score.cards.url}")
    private String scoreCardsUrl;

    /**
     * calls a third part using restTemplate and handles upstream errors
     *
     * @param cardsRequest
     * @return CompletableFuture.completedFuture of Card[] or CompletableFuture.failedFuture with CustomClientException if any error
     * @throws CustomClientException
     */
    @Async
    @Override
    public CompletableFuture<CreditCard[]> getScoredCards(ScoredCardsRequest cardsRequest) {
        var httpEntity = new HttpEntity<>(cardsRequest);

        ResponseEntity<CreditCard[]> resp;
        try {
            resp = restTemplate.postForEntity(scoreCardsUrl, httpEntity, CreditCard[].class);
        } catch (Exception exception) {
            return CompletableFuture.failedFuture(new CustomClientException(CardProvider.SCORED_CARDS, exception));
        }

        if (ObjectUtils.isEmpty(resp.getBody())) {
            var exception = new NotFoundException("response body is empty");
            return CompletableFuture.failedFuture(new CustomClientException(CardProvider.SCORED_CARDS, exception));
        }

        return CompletableFuture.completedFuture(resp.getBody());
    }

}
