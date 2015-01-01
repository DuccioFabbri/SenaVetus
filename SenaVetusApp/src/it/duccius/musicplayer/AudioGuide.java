package it.duccius.musicplayer;

import it.duccius.maps.Placemark;

import java.io.Serializable;

public class AudioGuide implements Serializable, Comparable{
	
	
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
	private String lat;
	private String lng;
	private String h = "0.0";
	
	public String getCoordinates() {
        return this.lat.concat(this.lng.concat(this.h));
    }
	public void setCoordinates(String coordinates) {
		String[] coords = coordinates.split(",");
		this.lat = coords[0];
		this.lng = coords[1];
		this.h = coords[2];
    }
	public void setLat(String lat) {
		this.lat = lat;
    }
	public String getLat() {
        return this.lat;
    }
	public void setLng(String lng) {
		this.lng = lng;
    }
	public String getLng() {
        return this.lng;
    }
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
	@Override
	public int compareTo(Object another) {
		// TODO Auto-generated method stub
		try{
		 return this.getName().compareTo(((AudioGuide)another).getName());
		}
		catch(Exception e )
		{
			return 0;
		}
	}	 
    public AudioGuide clone(AudioGuide origin)
    {
    	AudioGuide copia = new AudioGuide();
		copia.title = this.title;
		copia.name = this.name;
		copia.path = this.path;
		copia.imageId = this.imageId;
		copia.lang = this.lang;    	
		copia.sdPosition = this.sdPosition;
		copia.toBeDownloaded= this.toBeDownloaded;
		copia.geoPoint=this.geoPoint;
		copia.lat = this.lat;
		copia.lng = this.lng;
		copia.h=this.h;
     	
    	return copia;
    }
    
   
}

