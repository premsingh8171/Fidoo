package com.fidoo.user.utils.maps.model;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class GeocoderModel{

	@SerializedName("plus_code")
	private PlusCode plusCode;

	@SerializedName("results")
	private List<ResultsItem> results;

	@SerializedName("status")
	private String status;

	public PlusCode getPlusCode(){
		return plusCode;
	}

	public List<ResultsItem> getResults(){
		return results;
	}

	public String getStatus(){
		return status;
	}
}