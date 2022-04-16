package com.clearscore.credit.client.scoredcards.interfaces;


import com.clearscore.credit.client.scoredcards.model.CreditCard;
import com.clearscore.credit.client.scoredcards.model.ScoredCardsRequest;

import java.util.concurrent.CompletableFuture;

public interface ScoredCardsClient {
    CompletableFuture<CreditCard[]> getScoredCards(ScoredCardsRequest cardsRequest);

}
