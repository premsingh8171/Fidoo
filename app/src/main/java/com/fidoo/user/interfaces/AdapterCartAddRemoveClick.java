package com.fidoo.user.interfaces;


public interface AdapterCartAddRemoveClick {

    void onAddItemClick(String productId, String quantity, String offerPrice, String customizeid, String prodcustCustomizeId, String cart_id);
    void onRemoveItemClick(String productId, String quantity, String customizeid, String prodcustCustomizeId, String cart_id);
}
