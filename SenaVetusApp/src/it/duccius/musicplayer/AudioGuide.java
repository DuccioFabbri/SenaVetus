package it.duccius.musicplayer;

import java.io.Serializable;

public class AudioGuide implements Serializable{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8693627450220630246L;
	private String title;
	private String name;
	private String path;
	private String imageId;
	private String lang;
	private Integer sdPosition;
	private boolean toBeDownloaded = false;
	private String geoPoint;
	
	public String getName() {
        return this.name;
    }
	public void setName(String name) {
		this.name = name;
    }
	public String getTitle() {
        return this.title;
    }
	public void setTitle(String title) {
        this.title = title;
    }
	public String getPath() {
        return this.path;
    }
	public void setPath(String path) {
        this.path = path;
    }
	public String getImageId() {
        return imageId;
    }
	public String getLang() {
        return this.lang;
    }
    public void setLang(String lang) {
        this.lang = lang;
    }
    public Integer getSdPosition() {
        return this.sdPosition;
    }
    public void setSdPosition(Integer sdPosition) {
        this.sdPosition = sdPosition;
    }
    public boolean getToBeDownloaded() {
        return toBeDownloaded;
    }
    public void setToBeDownloaded(boolean toBeDownload) {
        this.toBeDownloaded = toBeDownload;
    }
	public String getGeoPoint() {
	        return this.geoPoint;
	    }
	 public void setGeoPoint(String geoPoint) {
	        this.geoPoint = geoPoint;
	    }	 
    
}

