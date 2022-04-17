package com.clearscore.credit.integration;

import com.clearscore.credit.controller.model.CreditCardRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class CreditCardsIntegrationTest {

    private final String url = "/creditcards";
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;
    private CreditCardRequest inputRequest;

    @SneakyThrows
    @BeforeEach
    void setUp() {
        inputRequest = new CreditCardRequest()
                .creditScore(680)
                .name("shaka zulu")
                .salary(90000);
    }

    @Test
    void creditcardsPost_should_throw_exception_if_request_method_is_not_Post() throws Exception {
        mockMvc.perform(get(url)
                        .content(mapper.writeValueAsString(inputRequest))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().is5xxServerError())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof HttpRequestMethodNotSupportedException))
                .andExpect(result -> assertEquals("Request method 'GET' not supported", result.getResolvedException().getMessage()))
                .andReturn();
    }


    @Test
    void creditcardsPost_should_return_status200_with_results_if_method_is_Post() throws Exception {
        mockMvc.perform(post(url)
                        .content(mapper.writeValueAsString(inputRequest))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();
    }

    @Test
    void creditcardsPost_should_return_status500_with_results_if_request_is_empty() throws Exception {
        mockMvc.perform(post(url)
                        .content(mapper.writeValueAsString(new CreditCardRequest()))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().is5xxServerError())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
                .andReturn();
    }


}
