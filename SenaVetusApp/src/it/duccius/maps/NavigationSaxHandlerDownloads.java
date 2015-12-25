package it.duccius.maps;

import it.duccius.musicplayer.AudioGuide;

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


public class NavigationSaxHandlerDownloads extends DefaultHandler{ 

 // =========================================================== 
 // Fields 
 // =========================================================== 

 private boolean in_kmltag = false; 
 private boolean in_placemarktag = false; 
 private boolean in_nametag = false;
 private boolean in_descriptiontag = false;
 private boolean in_vers = false;
 private boolean in_audio = false;
 private boolean in_trail = false;
 private boolean in_trail_coords = false;
 
 
 private boolean in_pointtag = false;
 private boolean in_coordinatestag = false;

 private StringBuffer buffer;

 private  ArrayList<AudioGuide> _guides = new  ArrayList<AudioGuide>();
 private  ArrayList<Trail> _trails = new  ArrayList<Trail>(); 
 private Trail newTrail;

 // =========================================================== 
 // Getter & Setter 
 // =========================================================== 

 public  ArrayList<AudioGuide> getParsedData() {
    //  _guides.getCurrentPlacemark().setCoordinates(buffer.toString().trim());
      return this._guides; 
 } 

 // =========================================================== 
 // Methods 
 // =========================================================== 
 @Override 
 public void startDocument() throws SAXException { 
      this._guides = new  ArrayList<AudioGuide>(); 
 } 

 @Override 
 public void endDocument() throws SAXException { 
      // Nothing to do
 } 

 /** Gets be called on opening tags like: 
  * <tag> 
  * Can provide attribute(s), when xml was like: 
  * <tag attribute="attributeValue">*/ 
 @Override 
 public void startElement(String namespaceURI, String localName, 
           String qName, Attributes atts) throws SAXException { 
    if (localName.equals("name")) { 
           this.in_nametag = true;
      } else if (localName.equals("description")) { 
          this.in_descriptiontag = true;
      } else if (localName.equals("vers")) { 
          this.in_vers = true;
      } else if (localName.equals("audio")) { 
          this.in_audio = true;               
          if(atts != null )        	  
          {        	         	  
				AudioGuide song = new AudioGuide();
				song.setTitle(atts.getValue("title") );
				song.setName(atts.getValue("name") );
				song.setPath(atts.getValue("audioBaseUrl")+atts.getValue("name")+".mp3");
				song.setLang(atts.getValue("lang"));
				song.setCoordinates(atts.getValue("coordinates"));
				//song.setGeoPoint(ApplicationData.getPoints().get(i-1));
				// Adding each song to SongList
				_guides.add(song);				 
          }
      }
      else if (localName.equals("trail")) { 
          this.in_trail = true;               
          if(atts != null )        	  
          {        	 
        	  newTrail = new Trail();
        	  newTrail.setName(atts.getValue("name") );
        	  newTrail.setDescription(atts.getValue("description") );
        	  newTrail.setTime(atts.getValue("time"));			 
          }
      } else if (localName.equals("trail_coords")) { 
          this.in_trail_coords = true;               
         
      }
    
 } 

 /** Gets be called on closing tags like: 
  * </tag> */ 
 @Override 
 public void endElement(String namespaceURI, String localName, String qName) 
           throws SAXException { 
      if (localName.equals("name")) { 
           this.in_nametag = false;           
       } else if (localName.equals("description")) { 
           this.in_descriptiontag = false;
       } else if (localName.equals("vers")) { 
           this.in_vers = false;
       } else if (localName.equals("audio")) { 
           this.in_audio = false;              
       } else if (localName.equals("trail")) { 
           this.in_trail = false;  
           get_trails().add(newTrail);
       }  
 } 

 /** Gets be called on the following structure: 
  * <tag>characters</tag> */ 
 @Override 
public void characters(char ch[], int start, int length) { 
	 if (in_trail_coords) {
		 ArrayList<Placemark> trailPlacemarks = new ArrayList<Placemark>();		 
		 
		 String points = new String(ch, start, length);
		 
		 String[] arrPoints = points.split(" ");
		 for (String point: arrPoints){
			 String[] coord = point.split(",");
			 Placemark pm = new Placemark();
			 pm.setCoordinates(coord[0]+","+coord[1]);
			 trailPlacemarks.add(pm);
		 }

         //age element, set Employee age
		 newTrail.setTrailPlacemarks(trailPlacemarks);
         in_trail_coords = false;
     }
}

public ArrayList<Trail> get_trails() {
	return _trails;
}

public void set_trails(ArrayList<Trail> _trails) {
	this._trails = _trails;
} 
}
