package com.clearscore.credit.controller;

import com.clearscore.credit.controller.model.CreditCard;
import com.clearscore.credit.controller.model.CreditCardRequest;
import com.clearscore.credit.services.interfaces.CreditCardsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;


@RequiredArgsConstructor
@RestController(value = "creditcards")
public class CreditCardsController implements CreditcardsApi {
    private final CreditCardsService creditCardsService;

    @Override
    public ResponseEntity<List<CreditCard>> creditcardsPost(CreditCardRequest request) {
        var creditCards = creditCardsService.getCardRecommendations(request);


        return creditCards.isEmpty()
                ? ResponseEntity.notFound().build()
                : ResponseEntity.ok(creditCards);
    }

}
