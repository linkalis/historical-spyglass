// HISTORICAL SPYGLASS

/* Adapted from Unfolding Maps examples:
http://unfoldingmaps.org/examples/30_provider-satellite-overlay
http://unfoldingmaps.org/examples/30_image-overlay
*/

// Java libraries
import static java.lang.Math.abs;
import java.util.Random;
import java.util.*; // to get ArrayList & HashMap functionality

// Processing libraries
import processing.core.PApplet; 
import processing.core.PShape;
import processing.core.PImage;
import processing.core.PGraphics;

// Unfolding Maps libraries
import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.marker.*;
import de.fhpotsdam.unfolding.events.EventDispatcher;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.utils.MapPosition;
import de.fhpotsdam.unfolding.utils.MapUtils;
import de.fhpotsdam.unfolding.providers.StamenMapProvider;



public class HistoricalSpyglass extends PApplet {
	private Random randomGenerator;
	
	// BASEMAP
	UnfoldingMap baseMap;

	Marker selectedMarker;

	// SPYGLASS - starting location & dimensions of the spyglass
	PShape spyglass;
	int spyglassScreenLocX;
	int spyglassScreenLocY;
	int spyglassScreenWidth = 250;
	int spyglassScreenHeight = 250;
	Location spyglassGeoLoc; // We may not need this!
	HashMap<String, Object> spyglassProperties;
	
	// BERLIN
	/*
	Location homeBase = new Location(52.5096, 13.3759);
	int defaultZoom = 12;
	*/

	// US
	Location homeBase = new Location(35.678, -97.447);
	int defaultZoom = 4;
	
	public void settings() {
		size(800, 600);
	}

	public void setup() {
		
		// BASEMAP setup  
		baseMap = new UnfoldingMap(this, "static", 0, 0, 800, 600);
		//baseMap = new UnfoldingMap(this, new StamenMapProvider.TonerLite());
		baseMap.zoomToLevel(defaultZoom);
		baseMap.panTo(homeBase);
		baseMap.setZoomRange(3, 14);
		  
		// SPYGLASS setup: initialize a hashmap of values containing information on the spyglass size & location.
		// We'll pass these values as properties to overlay image markers to decide which parts of the image to render.
		spyglassScreenLocX = width / 2;
		spyglassScreenLocY = height / 2;
		spyglassProperties = new HashMap();
		spyglassProperties.put("spyglassScreenWidth", new Integer(250));
		spyglassProperties.put("spyglassScreenHeight", new Integer(250));
		spyglassProperties.put("spyglassScreenLocX", new Integer(0));
		spyglassProperties.put("spyglassScreenLocY", new Integer(0));
  
		// OVERLAY IMAGE MARKER setup
		baseMap.addMarker(new OverlayImageMarker(this, loadImage("us_historic.jpg"), new Location(51.512, -128.077), new Location(13.424, -64.734), spyglassProperties)); // US historic map: https://www.loc.gov/item/98688304/ 
		baseMap.addMarker(new OverlayImageMarker(this, loadImage("berlin_underground.jpg"), new Location(52.58, 13.16), new Location(52.44, 13.62), spyglassProperties));
		
		MapUtils.createDefaultEventDispatcher(this, baseMap);
	}

	public void draw() {
		background(0);

	    // BASEMAP draw
	    baseMap.draw();
	  
	    // SPYGLASS draw
	    // Draw rectangle outlining the spyglass region
	    noFill();
	    strokeWeight(5);
	    stroke(204, 102, 0);
	    rect(spyglassScreenLocX, spyglassScreenLocY, spyglassScreenWidth, spyglassScreenHeight);
	    
	    // OVERLAY IMAGE draw
	    // If a marker is selected, update the spyglass coords, pass them into the marker class, and render the resulting cutout on the map
	    if (selectedMarker != null) {
	      selectedMarker.setProperties(spyglassProperties);
	    }
	}
	
	public void mouseDragged() {
		moveSpyglass(mouseX, mouseY);
	}

	public void mouseMoved() {
		moveSpyglass(mouseX, mouseY);
		  
		Marker selectedMarker = baseMap.getFirstHitMarker(mouseX, mouseY);
		if (selectedMarker != null) {
		  // Select current marker 
		  selectedMarker.setSelected(true);
		} else {
		  // Deselect all other markers
		  for (Marker marker : baseMap.getMarkers()) {
		      marker.setSelected(false);
		  }
		}
	}
	
	private void moveSpyglass(int x, int y) {
	    // Move the spyglass to mouse position, but center it around the mouse
	    spyglassScreenLocX = x - spyglassScreenWidth / 2;
	    spyglassScreenLocY = y - spyglassScreenHeight / 2;
	    spyglassProperties.put("spyglassScreenLocX", new Integer(spyglassScreenLocX));
	    spyglassProperties.put("spyglassScreenLocY", new Integer(spyglassScreenLocY));
	}
	
	/* TODO: Implement a keyPressed() function that will randomly switch and zoom between overlay images 
	public void keyPressed() {
		// If key is pressed, pick a random item from locationsList and move to it
		if (key == CODED) {
		    if (keyCode == RIGHT) {
		    	int index = randomGenerator.nextInt(locationsList.size());
		    	System.out.println(index);
		        HashMap<String, Object> newLocation = locationsList.get(index);
		        System.out.println(newLocation);
		        int newZoom = (Integer) newLocation.get("zoom");
		        Location newHomeBase = (Location) newLocation.get("loc");
		        System.out.println(newHomeBase);
		        baseMap.zoomToLevel(newZoom);
		        baseMap.panTo(newHomeBase);
		    }
		 }
	}
	*/
	
	
	public static void main(String[] args) {
		// Add this to get rid of log4j errors and print error logging to the console
		org.apache.log4j.BasicConfigurator.configure();
		
		// Processing 3 no longer extends Applet class, so we need to tweak the main() function to get it to launch
		// http://stackoverflow.com/questions/34716460/java-error-cannot-cast-to-java-applet-applet-in-eclipse
		String[] pArgs = {"HistoricalSpyglass"};
	    HistoricalSpyglass mp = new HistoricalSpyglass ();
	    PApplet.runSketch(pArgs, mp);
	}

}
