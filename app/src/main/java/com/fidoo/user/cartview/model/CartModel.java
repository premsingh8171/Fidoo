package com.fidoo.user.cartview.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import kotlin.jvm.JvmSuppressWildcards;

public class CartModel {

    @SerializedName("message")
    @Expose
    public String message;

    @SerializedName("error")
    @Expose
    public Boolean error;
    @SerializedName("error_code")
    @Expose
    public Integer errorCode;
    @SerializedName("accessToken")
    @Expose
    public String accessToken;
    @SerializedName("accountId")
    @Expose
    public String accountId;

    @SerializedName("store_status")
    @Expose
    public String store_status;

    @SerializedName("charges_one")
    @Expose
    public String charges_one;

    @SerializedName("charges_two")
    @Expose
    public String charges_two;

    @SerializedName("store_open_close_status")
    @Expose
    public String store_open_close_status;

    //Cart Coupon
    @SerializedName("coupon_name")
    @Expose
    public String coupon_name;
    @SerializedName("coupon_desc")
    @Expose
    public String coupon_desc;
    @SerializedName("discount_amount")
    @Expose
    public String discount_amount;
    @SerializedName("tax")
    @Expose
    public String tax;
    @SerializedName("distance")
    @Expose
    public int distance;

    @SerializedName("store_lat")
    @Expose
    public String store_lat;

    @SerializedName("store_long")
    @Expose
    public String store_long;

    @SerializedName("delivery_charge")
    @Expose
    public int deliveryCharge;

    // delivery coupon
    @SerializedName("delivery_coupon_name")
    @Expose
    public String delivery_coupon_name;
    @SerializedName("delivery_discount")
    @Expose
    public int deliveryDiscount;
    @SerializedName("coupon_id")
    @Expose
    public String coupon_id;
    @SerializedName("grand_total")
    @Expose
    public String grand_total;
    @SerializedName("after_discount_value")
    @Expose
    public String after_discount_value;

    @SerializedName("taxes")
    @Expose
    public List<Tax> taxes;

    //outstanding balance
    @SerializedName("have_standing_balance")
    @Expose
    public String have_standing_balance;

    @SerializedName("balance_order")
    @Expose
    public BalanceOrder balance_order;

    public class BalanceOrder {
        @SerializedName("order_id")
        @Expose
        public String order_id;

        @SerializedName("cart_coupon_name")
        @Expose
        public String cart_coupon_name;

        @SerializedName("delivery_coupon_name")
        @Expose
        public String delivery_coupon_name;

        @SerializedName("placed_at")
        @Expose
        public String placed_at;

        @SerializedName("payment_amount")
        @Expose
        public String payment_amount;

        @SerializedName("subtotal")
        @Expose
        public String subtotal;

        @SerializedName("cart_discount")
        @Expose
        public String cart_discount;

        @SerializedName("delivery_charges_with_tax")
        @Expose
        public String delivery_charges_with_tax;

        @SerializedName("delivery_discount")
        @Expose
        public String delivery_discount;

        @SerializedName("res_tax_and_charges")
        @Expose
        public String restaurent_taxes;

        @SerializedName("items")
        @Expose
        public List<BalanceItem> items;

        public class BalanceItem {
            @SerializedName("product_name")
            @Expose
            public String product_name;

            @SerializedName("quantity")
            @Expose
            public String quantity;

            @SerializedName("price")
            @Expose
            public String price;

            @SerializedName("offer_price")
            @Expose
            public String offer_price;

            @SerializedName("date_time")
            @Expose
            public String date_time;

            @SerializedName("customize-item")
            @Expose
            public List<CustomizeItem> customize_item;

            @SerializedName("price_with_customization")
            @Expose
            public String price_with_customization;
        }
    }

    public class Tax {

        @SerializedName("title")
        @Expose
        public String title;
        @SerializedName("value")
        @Expose
        public Double value;
        @SerializedName("percentage")
        @Expose
        public String percentage;

    }

    @SerializedName("charges")
    @Expose
    public List<Charge> charges;

    public class Charge {

        @SerializedName("title")
        @Expose
        public String title;

        @SerializedName("value")
        @Expose
        public String value;

        @SerializedName("taxes")
        @Expose
        public List<Tax> taxes = null;

        public class Tax {

            @SerializedName("title")
            @Expose
            public String title;

            @SerializedName("value")
            @Expose
            public Double value;

            @SerializedName("percentage")
            @Expose
            public String percentage;

        }

        @SerializedName("charge_with_tax")
        @Expose
        public Double chargeWithTax;

    }

    @SerializedName("all_item_charge_total")
    @Expose
    public Double allItemChargeTotal;

    @SerializedName("all_item_tax_total")
    @Expose
    public Double allItemTaxTotal;

    @SerializedName("total_tax_and_charges")
    @Expose
    public Double totalTaxAndCharges;

    @SerializedName("eligible_for_cart_coupon")
    @Expose
    public String eligible_for_cart_coupon;

    @SerializedName("charges_three")
    @Expose
    public String charges_three;

    @SerializedName("deliveryChargesList")
    @Expose
    public List<DeliveryCharges> deliveryChargesList = null;

    @SerializedName("delivery_tax_rate")
    @Expose
    public String delivery_tax_rate;

    @SerializedName("cart")
    @Expose
    public List<Cart> cart = null;

    public class Cart {

        @SerializedName("cart_id")
        @Expose
        public String cart_id;

        @SerializedName("service_id")
        @Expose
        public String service_id;

        @SerializedName("product_id")
        @Expose
        public String productId;

        @SerializedName("quantity")
        @Expose
        public String quantity;

        @SerializedName("store_id")
        @Expose
        public String storeId;

        @SerializedName("store_name")
        @Expose
        public String storeName;

        @SerializedName("store_image")
        @Expose
        public String store_image;

        @SerializedName("store_address")
        @Expose
        public String store_address;

        @SerializedName("is_nonveg")
        @Expose
        public String is_nonveg;

        @SerializedName("contains_egg")
        @Expose
        public String contains_egg;

        @SerializedName("company_name")
        @Expose
        public String companyName;

        @SerializedName("product_name")
        @Expose
        public String productName;

        @SerializedName("product_image")
        @Expose
        public String productImage;

        @SerializedName("price")
        @Expose
        public String price;

        @SerializedName("offer_price")
        @Expose
        public String offerPrice;

        @SerializedName("message")
        @Expose
        public String message;

        @SerializedName("is_prescription")
        @Expose
        public String isPrescription;

        @SerializedName("cod")
        @Expose
        public String cod;

        @SerializedName("online")
        @Expose
        public String online;

        @SerializedName("is_customize")
        @Expose
        public String is_customize;

        @SerializedName("customize_item")
        @Expose
        public List<CustomizeItem> customizeItem = null;

        @SerializedName("is_customize_quantity")
        @Expose
        public String is_customize_quantity;

        @SerializedName("product_desc")
        @Expose
        public String product_desc;

        @SerializedName("weight")
        @Expose
        public String weight;

        @SerializedName("unit")
        @Expose
        public String unit;

        @SerializedName("in_out_of_stock_status")
        @Expose
        public String in_out_of_stock_status;

    }

    public class CustomizeItem {
        @SerializedName("id")
        @Expose
        public String id;

        @SerializedName("sub_cat_name")
        @Expose
        public String subCatName;

        @SerializedName("price")
        @Expose
        public String price;

        @SerializedName("product_customize_id")
        @Expose
        public String productCustomizeId;

    }

    public class DeliveryCharges {

        @SerializedName("distance_range")
        @Expose
        public String distanceRange;
        @SerializedName("delivery_charges")
        @Expose
        public String deliveryCharges;

    }
}
