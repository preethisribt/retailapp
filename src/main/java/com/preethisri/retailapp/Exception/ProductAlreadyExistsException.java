package com.preethisri.retailapp.Exception;

public class ProductAlreadyExistsException extends RuntimeException{
    public ProductAlreadyExistsException(String message) {
        super(message);
    }
}
