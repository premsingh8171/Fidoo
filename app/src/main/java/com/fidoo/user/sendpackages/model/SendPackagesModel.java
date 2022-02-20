package com.fidoo.user.sendpackages.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.fidoo.user.cartview.model.CartModel;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class SendPackagesModel implements Parcelable {
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

    @SerializedName("payment_amt")
    @Expose
    public String paymentAmt;

    @SerializedName("standard_charges")
    @Expose
    public String standard_charges;

    @SerializedName("order_id")
    @Expose
    public String orderId;

    @SerializedName("distance")
    @Expose
    public String distance;

    @SerializedName("time")
    @Expose
    public String time;

    @SerializedName("message")
    @Expose
    public String message;

    @SerializedName("razorPayOrderId")
    @Expose
    public String razorPayOrderId;

    @SerializedName("additional_distance")
    @Expose
    public String additional_distance;

    @SerializedName("base_price")
    @Expose
    public String base_price;

    @SerializedName("additional_charges")
    @Expose
    public String additional_charges;

    @SerializedName("tax")
    @Expose
    public String tax;

    @SerializedName("base_distance")
    @Expose
    public String base_distance;

    @SerializedName("coupon_name")
    @Expose
    public String coupon_name;

    @SerializedName("value_after_discount")
    @Expose
    public String value_after_discount;

    @SerializedName("discount")
    @Expose
    public String discount;

    @SerializedName("charges_one")
    @Expose
    public String charges_one;

    @SerializedName("charges_two")
    @Expose
    public String charges_two;

    @SerializedName("charges_three")
    @Expose
    public String charges_three;

    @SerializedName("deliveryChargesList")
    @Expose
    public List<DeliveryCharges> deliveryChargesList = null;

    @SerializedName("delivery_tax_rate")
    @Expose
    public String delivery_tax_rate;

    public class DeliveryCharges {

        @SerializedName("distance_range")
        @Expose
        public String distanceRange;
        @SerializedName("delivery_charges")
        @Expose
        public String deliveryCharges;

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.error);
        dest.writeValue(this.errorCode);
        dest.writeString(this.accessToken);
        dest.writeString(this.accountId);
        dest.writeString(this.paymentAmt);
        dest.writeString(this.standard_charges);
        dest.writeString(this.orderId);
        dest.writeString(this.distance);
        dest.writeString(this.time);
        dest.writeString(this.message);
        dest.writeString(this.razorPayOrderId);
        dest.writeString(this.additional_distance);
        dest.writeString(this.base_price);
        dest.writeString(this.additional_charges);
        dest.writeString(this.tax);
        dest.writeString(this.base_distance);
        dest.writeString(this.coupon_name);
        dest.writeString(this.value_after_discount);
        dest.writeString(this.discount);
        dest.writeString(this.charges_one);
        dest.writeString(this.charges_two);
        dest.writeString(this.charges_three);
        dest.writeList(this.deliveryChargesList);
        dest.writeString(this.delivery_tax_rate);
    }

    public void readFromParcel(Parcel source) {
        this.error = (Boolean) source.readValue(Boolean.class.getClassLoader());
        this.errorCode = (Integer) source.readValue(Integer.class.getClassLoader());
        this.accessToken = source.readString();
        this.accountId = source.readString();
        this.paymentAmt = source.readString();
        this.standard_charges = source.readString();
        this.orderId = source.readString();
        this.distance = source.readString();
        this.time = source.readString();
        this.message = source.readString();
        this.razorPayOrderId = source.readString();
        this.additional_distance = source.readString();
        this.base_price = source.readString();
        this.additional_charges = source.readString();
        this.tax = source.readString();
        this.base_distance = source.readString();
        this.coupon_name = source.readString();
        this.value_after_discount = source.readString();
        this.discount = source.readString();
        this.charges_one = source.readString();
        this.charges_two = source.readString();
        this.charges_three = source.readString();
        this.deliveryChargesList = new ArrayList<DeliveryCharges>();
        source.readList(this.deliveryChargesList, DeliveryCharges.class.getClassLoader());
        this.delivery_tax_rate = source.readString();
    }

    public SendPackagesModel() {
    }

    protected SendPackagesModel(Parcel in) {
        this.error = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.errorCode = (Integer) in.readValue(Integer.class.getClassLoader());
        this.accessToken = in.readString();
        this.accountId = in.readString();
        this.paymentAmt = in.readString();
        this.standard_charges = in.readString();
        this.orderId = in.readString();
        this.distance = in.readString();
        this.time = in.readString();
        this.message = in.readString();
        this.razorPayOrderId = in.readString();
        this.additional_distance = in.readString();
        this.base_price = in.readString();
        this.additional_charges = in.readString();
        this.tax = in.readString();
        this.base_distance = in.readString();
        this.coupon_name = in.readString();
        this.value_after_discount = in.readString();
        this.discount = in.readString();
        this.charges_one = in.readString();
        this.charges_two = in.readString();
        this.charges_three = in.readString();
        this.deliveryChargesList = new ArrayList<DeliveryCharges>();
        in.readList(this.deliveryChargesList, DeliveryCharges.class.getClassLoader());
        this.delivery_tax_rate = in.readString();
    }

    public static final Parcelable.Creator<SendPackagesModel> CREATOR = new Parcelable.Creator<SendPackagesModel>() {
        @Override
        public SendPackagesModel createFromParcel(Parcel source) {
            return new SendPackagesModel(source);
        }

        @Override
        public SendPackagesModel[] newArray(int size) {
            return new SendPackagesModel[size];
        }
    };
}


