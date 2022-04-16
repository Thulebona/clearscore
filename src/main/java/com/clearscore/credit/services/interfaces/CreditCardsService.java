package com.clearscore.credit.services.interfaces;

import com.clearscore.credit.controller.model.CreditCard;
import com.clearscore.credit.controller.model.CreditCardRequest;

import java.util.List;

public interface CreditCardsService {
    List<CreditCard> getCardRecommendations(CreditCardRequest body);

}
