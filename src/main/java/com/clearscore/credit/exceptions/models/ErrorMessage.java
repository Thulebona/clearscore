package com.clearscore.credit.exceptions.models;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.http.HttpStatus;

import java.util.Date;

@Data
@Accessors(fluent = true)
public class ErrorMessage {
    @JsonProperty("status")
    private HttpStatus status;
    @JsonProperty("message")
    private String message;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private final Date timeStamp = new Date();

}
