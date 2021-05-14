package com.fidoo.user.interfaces;


public interface AdapterReviewClick {
    void onReviewDoneClick(String orderId,
                           String storeRating,
                           String reviewStore,
                           String ratingDriver,
                           String reviewDriver
    );

    void onReviewSubmit(String orderId,
                           String star,
                           String improvement,
                           String message,
                           String type
    );

}
