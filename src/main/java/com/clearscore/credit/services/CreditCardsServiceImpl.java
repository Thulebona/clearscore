package com.clearscore.credit.services;

import com.clearscore.credit.client.cscards.interfaces.CSCardsClient;
import com.clearscore.credit.client.cscards.model.Card;
import com.clearscore.credit.client.cscards.model.CardSearchRequest;
import com.clearscore.credit.client.scoredcards.interfaces.ScoredCardsClient;
import com.clearscore.credit.client.scoredcards.model.ScoredCardsRequest;
import com.clearscore.credit.controller.model.CreditCard;
import com.clearscore.credit.controller.model.CreditCardRequest;
import com.clearscore.credit.enums.CardProvider;
import com.clearscore.credit.services.interfaces.CreditCardsService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreditCardsServiceImpl implements CreditCardsService {
    private final ModelMapper modelMapper;
    private final CSCardsClient csCardsClient;
    private final ScoredCardsClient scoredCardsClient;
    private final List<CreditCard> creditCards = new CopyOnWriteArrayList<>();

    @Override
    public List<CreditCard> getCardRecommendations(CreditCardRequest request) {

        var searchRequest = modelMapper.map(request, CardSearchRequest.class);
        var cardsRequest = modelMapper.map(request, ScoredCardsRequest.class);

        var csList = csCardsClient
                .getCsCards(searchRequest)
                .thenApplyAsync(this::setCSCardsResponse);

        var sCardsList = scoredCardsClient
                .getScoredCards(cardsRequest)
                .thenApplyAsync(this::setScoredCardsResponse);

        CompletableFuture
                .allOf(csList, sCardsList)
                .handleAsync(this::handleErrors)
                .join(); // block until all future are completed

        return creditCards
                .stream()
                .map(this::formatCardScore)
                .sorted(Comparator.comparing(CreditCard::getCardScore).reversed())
                .collect(Collectors.toList());

    }

    private Boolean setCSCardsResponse(Card[] cards) {
        if (Objects.isNull(cards)) {
            return false;
        }
        var csCards = Stream.of(cards)
                .filter(Objects::nonNull)
                .map(card -> new CreditCard()
                        .provider(CardProvider.CS_CARDS.getName())
                        .name(card.getCardName())
                        .apr(card.getApr())
                        .cardScore(card.getEligibility() / card.getApr()))
                .collect(Collectors.toList());
        return creditCards.addAll(csCards);
    }

    private Boolean setScoredCardsResponse(com.clearscore.credit.client.scoredcards.model.CreditCard[] cards) {

        if (Objects.isNull(cards)) {
            return false;
        }
        var scoredCards=  Arrays.stream(cards)
                .filter(Objects::nonNull)
                .map(cc -> new CreditCard()
                        .provider(CardProvider.SCORED_CARDS.getName())
                        .name(cc.getCard())
                        .apr(cc.getApr())
                        .cardScore(cc.getApprovalRating() / cc.getApr()))
                .collect(Collectors.toList());
        return creditCards.addAll(scoredCards);
    }

    private CreditCard formatCardScore(CreditCard card) {
        var formatted = BigDecimal.valueOf(card.getCardScore())
                .setScale(3, RoundingMode.HALF_EVEN)
                .doubleValue();
        return card.cardScore(formatted);
    }

    private Void handleErrors(Void unused, Throwable throwable) {
        if (Objects.nonNull(throwable)) {
            log.error(throwable.getMessage(), throwable);
        }
        return unused;
    }


}
