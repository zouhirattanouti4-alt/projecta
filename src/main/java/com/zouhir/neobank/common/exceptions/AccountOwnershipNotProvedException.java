package com.zouhir.neobank.common.exceptions;

public class AccountOwnershipNotProvedException extends RuntimeException {

    public AccountOwnershipNotProvedException(String message) {

        super(message);
    }
}
