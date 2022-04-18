package com.clearscore.credit.controller;

import com.clearscore.credit.controller.model.CreditCard;
import com.clearscore.credit.controller.model.CreditCardRequest;
import com.clearscore.credit.services.interfaces.CreditCardsService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RequiredArgsConstructor
@RestController(value = "creditcards")
public class CreditCardsController implements CreditcardsApi {
    private final CreditCardsService creditCardsService;

    /**
     * creditcardsPost invokes CreditCardsService and get the results
     *
     * @param request (required)
     * @return ResponseEntity.ok() with list of CreditCard in json format or ResponseEntity.noContent() if results are empty
     */
    @Override
    @ApiResponses(@ApiResponse(responseCode = "204", description = "No result were found."))
    public ResponseEntity<List<CreditCard>> creditcardsPost(CreditCardRequest request) {
        var creditCards = creditCardsService.getCardRecommendations(request);

        return creditCards.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(creditCards);
    }

}
