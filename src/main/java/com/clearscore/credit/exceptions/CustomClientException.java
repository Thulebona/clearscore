package com.clearscore.credit.exceptions;


import com.clearscore.credit.enums.CardProvider;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CustomClientException extends RuntimeException {

    private final CardProvider provider;
    private final Throwable throwable;

    public CustomClientException(CardProvider provider, Throwable throwable) {
        super(throwable.getMessage(), throwable);
        this.provider = provider;
        this.throwable = throwable;
    }

}
