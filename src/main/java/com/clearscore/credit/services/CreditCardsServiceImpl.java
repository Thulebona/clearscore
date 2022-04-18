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
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
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
    private final List<CreditCard> creditCards = new CopyOnWriteArrayList<>(); // thread safe list

    /**
     * getCardRecommendations invokes multiple client asynchronous and process results
     *
     * @param request
     * @return list of CreditCard sorted by client CardScore
     */
    @Override
    public List<CreditCard> getCardRecommendations(CreditCardRequest request) {

        // maps api request to clients request
        var searchRequest = modelMapper.map(request, CardSearchRequest.class);
        var cardsRequest = modelMapper.map(request, ScoredCardsRequest.class);

        //region invoke clients asynchronous
        var csList = csCardsClient
                .getCsCards(searchRequest)
                .thenApplyAsync(this::setCSCardsResponse);

        var sCardsList = scoredCardsClient
                .getScoredCards(cardsRequest)
                .thenApplyAsync(this::setScoredCardsResponse);
        //endregion

        // block until all future are completed and handle upstream errors
        CompletableFuture
                .allOf(csList, sCardsList)
                .handleAsync(this::handleErrors)
                .join();

        // return sorted list with formatted card score
        return creditCards
                .stream()
                .map(this::formatCardScore)
                .sorted(Comparator.comparing(CreditCard::getCardScore).reversed())
                .collect(Collectors.toList());

    }

    /**
     * gets a list of cards, maps them to CreditCards, calculate the score and add to asynchronous list
     *
     * @param cards
     * @return true of added to list or false if cards is blank
     */
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

    /**
     * gets a list of creditCards, maps them to CreditCards, calculate the score and add to asynchronous list
     *
     * @param creditCards
     * @return true of added to list or false if creditCards is blank
     */

    private Boolean setScoredCardsResponse(com.clearscore.credit.client.scoredcards.model.CreditCard[] creditCards) {

        if (Objects.isNull(creditCards)) {
            return false;
        }
        var scoredCards = Arrays.stream(creditCards)
                .filter(Objects::nonNull)
                .map(cc -> new CreditCard()
                        .provider(CardProvider.SCORED_CARDS.getName())
                        .name(cc.getCard())
                        .apr(cc.getApr())
                        .cardScore(cc.getApprovalRating() / cc.getApr()))
                .collect(Collectors.toList());
        return this.creditCards.addAll(scoredCards);
    }

    /**
     * format card score to 3 decimal places
     *
     * @param card
     * @return CreditCard
     */
    private CreditCard formatCardScore(CreditCard card) {
        var formatted = BigDecimal.valueOf(card.getCardScore())
                .setScale(3, RoundingMode.HALF_EVEN)
                .doubleValue();
        return card.cardScore(formatted);
    }

    /**
     * logs errors throw by client futures if any
     *
     * @param unused
     * @param throwable
     * @return unused
     */
    private Void handleErrors(Void unused, Throwable throwable) {
        if (Objects.nonNull(throwable)) {
            log.error(throwable.getMessage(), throwable);
        }
        return unused;
    }


}
