package com.clearscore.credit.client.cscards.interfaces;


import com.clearscore.credit.client.cscards.model.Card;
import com.clearscore.credit.client.cscards.model.CardSearchRequest;

import java.util.concurrent.CompletableFuture;

public interface CSCardsClient {
    CompletableFuture<Card[]> getCsCards(CardSearchRequest searchRequest);

}
