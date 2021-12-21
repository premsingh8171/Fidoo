package com.fidoo.user.utils.maps.model;

import com.google.gson.annotations.SerializedName;

public class Geometry{

	@SerializedName("viewport")
	private Viewport viewport;

	@SerializedName("bounds")
	private Bounds bounds;

	@SerializedName("location")
	private Location location;

	@SerializedName("location_type")
	private String locationType;

	public Viewport getViewport(){
		return viewport;
	}

	public Bounds getBounds(){
		return bounds;
	}

	public Location getLocation(){
		return location;
	}

	public String getLocationType(){
		return locationType;
	}
}