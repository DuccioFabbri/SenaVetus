package it.duccius.maps;

import java.util.ArrayList;

public class Trail {
	private static final long serialVersionUID = 8693627450220630246L;
	private String name;
	private String description;
	private String time;
	private ArrayList<Placemark> trailPlacemarks;
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public ArrayList<Placemark> getTrailPlacemarks() {
		return trailPlacemarks;
	}
	public void setTrailPlacemarks(ArrayList<Placemark> trailPlacemarks) {
		this.trailPlacemarks = trailPlacemarks;
	}

}
