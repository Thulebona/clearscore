package com.clearscore.credit.controller;

import com.clearscore.credit.controller.model.CreditCard;
import com.clearscore.credit.controller.model.CreditCardRequest;
import com.clearscore.credit.services.interfaces.CreditCardsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.io.FileInputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(CreditCardsController.class)
public class CreditCardsControllerTest {

    @MockBean
    private CreditCardsService creditCardsService;
    @Autowired
    private MockMvc mockMvc;
    private ObjectMapper mapper;
    private List<CreditCard> expectedResult;
    private CreditCardRequest inputRequest;

    @SneakyThrows
    @BeforeEach
    void setUp() {
        mapper = new ObjectMapper();
        var service_expected = new FileInputStream(Paths.get("src", "test", "resources", "testdata", "cscards_test_data.json").toFile());
        expectedResult = mapper.readValue(service_expected, List.class);
        inputRequest = new CreditCardRequest()
                .creditScore(650)
                .salary(55000)
                .name("Rhino Labuschagne");
    }

    @Test
    void given_valid_input_creditcardsPost_should_return_list_and_ok_status() throws Exception {

        doReturn(expectedResult)
                .when(creditCardsService)
                .getCardRecommendations(any(CreditCardRequest.class));


        verify(creditCardsService, never()).getCardRecommendations(any(CreditCardRequest.class));

        mockMvc.perform(post("/creditcards")
                        .content(mapper.writeValueAsString(inputRequest))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(expectedResult), true))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$", hasSize(expectedResult.size())))
                .andReturn();

        verify(creditCardsService).getCardRecommendations(any(CreditCardRequest.class));
    }

    @Test
    void given_invalid_input_creditcardsPost_should_return_status500() throws Exception {

        doReturn(new ArrayList<CreditCard>())
                .when(creditCardsService)
                .getCardRecommendations(any(CreditCardRequest.class));

        verify(creditCardsService, never()).getCardRecommendations(any(CreditCardRequest.class));
        mockMvc.perform(post("/creditcards")
                        .content(mapper.writeValueAsString(new CreditCardRequest()))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().is5xxServerError())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
                .andReturn();
        verify(creditCardsService, never()).getCardRecommendations(any(CreditCardRequest.class));
    }

}
