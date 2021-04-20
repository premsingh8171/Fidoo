package com.fidoo.user.interfaces;


public interface AdapterAddRemoveClick {

    void onItemAddRemoveClick(String productId, String count, String type, String price, String storeId, String cartId);

    void clearCart();
}
