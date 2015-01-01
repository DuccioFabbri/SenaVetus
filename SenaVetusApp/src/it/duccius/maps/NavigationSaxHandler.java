package it.duccius.maps;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/*
Questa classe analizza una stream xml e ne estrae i valori.
Questa classe è stata copiata e modificata da un esempio per leggere un file .kml
rispetto all'esempio ho tolto alcune cose e ho aggiunto la parte che analizza gli attributi del tag audio.
Questo l'ho fatto perchè non vorrei usare file kml, ma un formato xml che mi sono inventato e che uso per gestire gli audio.
In un primo momento usavo 2 file distinti: uno per i file mp3 e un altro per i POI da caricare sulla mappa.
Alla fine mi è sembrato meglio usare un solo file, quello degli audio, che racchiude ance le info sulle coordinate.
*/


public class NavigationSaxHandler extends DefaultHandler{ 

 // =========================================================== 
 // Fields 
 // =========================================================== 

 private boolean in_kmltag = false; 
 private boolean in_placemarktag = false; 
 private boolean in_nametag = false;
 private boolean in_descriptiontag = false;
 private boolean in_geometrycollectiontag = false;
 private boolean in_linestringtag = false;
 private boolean in_pointtag = false;
 private boolean in_coordinatestag = false;
 
 private boolean in_audio = false;

 private StringBuffer buffer;
 
 private String _coordinates;
 private String _title;
 private String _description;
 private String _address;
 ;

 private NavigationDataSet navigationDataSet = new NavigationDataSet(); 

 // =========================================================== 
 // Getter & Setter 
 // =========================================================== 

 public NavigationDataSet getParsedData() {
      //navigationDataSet.getCurrentPlacemark().setCoordinates(buffer.toString().trim());
      return this.navigationDataSet; 
 } 

 // =========================================================== 
 // Methods 
 // =========================================================== 
 @Override 
 public void startDocument() throws SAXException { 
      this.navigationDataSet = new NavigationDataSet(); 
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
      if (localName.equals("kml")) { 
           this.in_kmltag = true;
      } else if (localName.equals("Placemark")) { 
           this.in_placemarktag = true; 
           navigationDataSet.setCurrentPlacemark(new Placemark());
      } else if (localName.equals("name")) { 
           this.in_nametag = true;
      } else if (localName.equals("description")) { 
          this.in_descriptiontag = true;
      } else if (localName.equals("GeometryCollection")) { 
          this.in_geometrycollectiontag = true;
      } else if (localName.equals("LineString")) { 
          this.in_linestringtag = true;              
      } else if (localName.equals("point")) { 
          this.in_pointtag = true;          
      } else if (localName.equals("coordinates")) {
          buffer = new StringBuffer();
          this.in_coordinatestag = true;                        
      }
      else if (localName.equals("audio")) {          
          navigationDataSet.setCurrentPlacemark(new Placemark());
          _title = atts.getValue(atts.getIndex("title"));
          _description = atts.getValue(atts.getIndex("name"));
          _coordinates = atts.getValue(atts.getIndex("coordinates"));
          _address = atts.getValue(atts.getIndex("address"));          
          this.in_audio = true;                        
      }
 } 

 /** Gets be called on closing tags like: 
  * </tag> */ 
 @Override 
 public void endElement(String namespaceURI, String localName, String qName) 
           throws SAXException { 
       if (localName.equals("kml")) {
           this.in_kmltag = false; 
       } else if (localName.equals("Placemark")) { 
           this.in_placemarktag = false;

       if ("Route".equals(navigationDataSet.getCurrentPlacemark().getTitle())) 
               navigationDataSet.setRoutePlacemark(navigationDataSet.getCurrentPlacemark());
        else navigationDataSet.addCurrentPlacemark();

       } else if (localName.equals("name")) { 
           this.in_nametag = false;           
       } else if (localName.equals("description")) { 
           this.in_descriptiontag = false;
       } else if (localName.equals("GeometryCollection")) { 
           this.in_geometrycollectiontag = false;
       } else if (localName.equals("LineString")) { 
           this.in_linestringtag = false;              
       } else if (localName.equals("point")) { 
           this.in_pointtag = false;          
       } else if (localName.equals("coordinates")) { 
           this.in_coordinatestag = false;
       }
       
       else if (localName.equals("audio")) { 
    	   navigationDataSet.addCurrentPlacemark();
           this.in_audio = false;
       }
 } 

 /** Gets be called on the following structure: 
  * <tag>characters</tag> */ 
 @Override 
public void characters(char ch[], int start, int length) { 
    if(this.in_nametag){ 
        if (navigationDataSet.getCurrentPlacemark()==null) navigationDataSet.setCurrentPlacemark(new Placemark());
        navigationDataSet.getCurrentPlacemark().setTitle(new String(ch, start, length));            
    } else 
    if(this.in_descriptiontag){ 
        if (navigationDataSet.getCurrentPlacemark()==null) navigationDataSet.setCurrentPlacemark(new Placemark());
        navigationDataSet.getCurrentPlacemark().setDescription(new String(ch, start, length));          
    } else
    if(this.in_coordinatestag){        
        if (navigationDataSet.getCurrentPlacemark()==null) navigationDataSet.setCurrentPlacemark(new Placemark());
        navigationDataSet.getCurrentPlacemark().setCoordinates(new String(ch, start, length));
        buffer.append(ch, start, length);
    }
    
    else
        if(this.in_audio){        
            if (navigationDataSet.getCurrentPlacemark()==null) navigationDataSet.setCurrentPlacemark(new Placemark());
            navigationDataSet.getCurrentPlacemark().setTitle(_title);
            navigationDataSet.getCurrentPlacemark().setCoordinates(_coordinates);
            navigationDataSet.getCurrentPlacemark().setDescription(_description);
            navigationDataSet.getCurrentPlacemark().setAddress(_address);
        }
} 
}
