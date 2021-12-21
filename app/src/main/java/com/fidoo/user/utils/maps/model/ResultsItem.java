package com.fidoo.user.utils.maps.model;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class ResultsItem{

	@SerializedName("formatted_address")
	private String formattedAddress;

	@SerializedName("types")
	private List<String> types;

	@SerializedName("geometry")
	private Geometry geometry;

	@SerializedName("address_components")
	private List<AddressComponentsItem> addressComponents;

	@SerializedName("plus_code")
	private PlusCode plusCode;

	@SerializedName("place_id")
	private String placeId;

	public String getFormattedAddress(){
		return formattedAddress;
	}

	public List<String> getTypes(){
		return types;
	}

	public Geometry getGeometry(){
		return geometry;
	}

	public List<AddressComponentsItem> getAddressComponents(){
		return addressComponents;
	}

	public PlusCode getPlusCode(){
		return plusCode;
	}

	public String getPlaceId(){
		return placeId;
	}
}