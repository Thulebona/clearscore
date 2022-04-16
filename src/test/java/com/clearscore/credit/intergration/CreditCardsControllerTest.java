package com.clearscore.credit.intergration;

import com.clearscore.credit.controller.CreditCardsController;
import com.clearscore.credit.controller.model.CreditCardRequest;
import com.clearscore.credit.services.interfaces.CreditCardsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CreditCardsController.class)
class CreditCardsControllerTest {

    private String url;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    private CreditCardRequest inputRequest;
    @MockBean
    private CreditCardsService creditCardsService;


    private void assertExpectedResultsSame(HttpStatus status, Object expectedResults) throws Exception {
        mockMvc.perform(post(url)
                        .content(objectMapper.writeValueAsString(inputRequest))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().is(status.value()))
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResults), true))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();
    }

}
