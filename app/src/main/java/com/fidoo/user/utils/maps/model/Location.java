package com.fidoo.user.utils.maps.model;

import com.google.gson.annotations.SerializedName;

public class Location{

	@SerializedName("lng")
	private Double lng;

	@SerializedName("lat")
	private Double lat;

	public Double getLng(){
		return lng;
	}

	public Double getLat(){
		return lat;
	}
}