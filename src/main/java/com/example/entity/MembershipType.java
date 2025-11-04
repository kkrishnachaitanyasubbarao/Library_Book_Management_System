package com.example.entity;


public enum MembershipType {

    BASIC(2),
    PREMIUM(5);

    private final int maxBorrowLimit;

    MembershipType(int maxBorrowLimit) {
        this.maxBorrowLimit = maxBorrowLimit;
    }

    public int getMaxBorrowLimit() {
        return maxBorrowLimit;
    }

}
