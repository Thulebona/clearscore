package com.clearscore.credit.client.cscards;

import com.clearscore.credit.client.cscards.interfaces.CSCardsClient;
import com.clearscore.credit.client.cscards.model.Card;
import com.clearscore.credit.client.cscards.model.CardSearchRequest;
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
public class CSCardsClientImpl implements CSCardsClient {

    private final RestTemplate restTemplate;
    @Value("${client.cs.cards.url}")
    private String cardsSearchUrl;


    /**
     * calls a third part using restTemplate and handles upstream errors
     *
     * @param searchRequest
     * @return CompletableFuture.completedFuture of Card[] or CompletableFuture.failedFuture with CustomClientException if any error
     */
    @Async
    @Override
    public CompletableFuture<Card[]> getCsCards(CardSearchRequest searchRequest) {
        var httpEntity = new HttpEntity<>(searchRequest);

        ResponseEntity<Card[]> resp;
        try {
            resp = restTemplate.postForEntity(cardsSearchUrl, httpEntity, Card[].class);
        } catch (Exception exception) {
            return CompletableFuture.failedFuture(new CustomClientException(CardProvider.CS_CARDS, exception));
        }

        if (ObjectUtils.isEmpty(resp.getBody())) {
            var exception = new NotFoundException("response body is empty");
            return CompletableFuture.failedFuture(new CustomClientException(CardProvider.CS_CARDS, exception));
        }

        return CompletableFuture.completedFuture(resp.getBody());
    }

}
