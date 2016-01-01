package it.duccius.maps;

import java.io.Serializable;

public class Placemark implements Comparable, Serializable{

String title;
String description;
String coordinates;
String address;

public String getTitle() {
    return title;
}
public void setTitle(String title) {
    this.title = title;
}
public String getDescription() {
    return description;
}
public void setDescription(String description) {
    this.description = description;
}
public String getCoordinates() {
    return coordinates;
}
public void setCoordinates(String coordinates) {
    this.coordinates = coordinates;
}
public String getAddress() {
    return address;
}
public void setAddress(String address) {
    this.address = address;
}
public double getLatitude() {
	// TODO Auto-generated method stub
	return  Double.parseDouble(this.coordinates.split(",")[0]);
	
}
public double getLongitude() {
	// TODO Auto-generated method stub
	return  Double.parseDouble(this.coordinates.split(",")[1]);
}

public int compareTo(Object arg0) {
	 
     /* For Ascending order*/
     
     return this.getTitle().compareTo(((Placemark)arg0).getTitle());
     
     /* For Descending order do like this */
     //return compareage-this.studentage;
}

}
