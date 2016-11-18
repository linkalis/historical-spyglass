// Java libraries
import java.util.*; // to get ArrayList & HashMap functionality
import static java.lang.Math.abs;

// Processing libraries
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PGraphics;

// Unfolding Maps libraries
import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.marker.*;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.utils.*;

public class OverlayImageMarker extends AbstractShapeMarker {
	PApplet parent;
	
	public PImage img;
	Location northWestCoords;
	Location southEastCoords;

	OverlayImageMarker(PApplet p, PImage loadedImg, Location north_west, Location south_east, HashMap<String, Object> properties) {
		parent = p;
		img = loadedImg;

		// Define all four corners to make a rectangular-shaped marker
		northWestCoords = north_west;
		southEastCoords = south_east;
		Location northEastCoords = new Location(northWestCoords.getLat(), southEastCoords.getLon());
		Location southWestCoords = new Location(southEastCoords.getLat(), northWestCoords.getLon());
		this.addLocations(northWestCoords, northEastCoords, southEastCoords, southWestCoords, northWestCoords);

		// Set additional properties (i.e. spyglass location data)
		setProperties(properties);
	}

	private void displayCutout(PGraphics pg, List<MapPosition> mapPositions) {
		//System.out.println("Displaying cutout:" + this.getProperties());

		int spyglassScreenLocX = (int) this.getProperty("spyglassScreenLocX");
		int spyglassScreenLocY = (int) this.getProperty("spyglassScreenLocY");
		int spyglassScreenWidth = (int) this.getProperty("spyglassScreenWidth");
		int spyglassScreenHeight = (int) this.getProperty("spyglassScreenHeight");

		float projectedImgWidth = abs(mapPositions.get(1).x - mapPositions.get(0).x);
		float projectedImgHeight = abs(mapPositions.get(2).y - mapPositions.get(1).y);

		// Calculate values from spyglass location to grab equivalent cutout from overlay image
		float cutoutLocX = (spyglassScreenLocX - mapPositions.get(0).x) * (img.width / projectedImgWidth);
		float cutoutLocY = (spyglassScreenLocY - mapPositions.get(0).y) * (img.height / projectedImgHeight);

		float widthToCut = (spyglassScreenWidth / projectedImgWidth) * img.width;
		float heightToCut = (spyglassScreenHeight / projectedImgHeight) * img.height;

		PImage cutoutImg = img.get((int) cutoutLocX, (int) cutoutLocY, (int) widthToCut, (int) heightToCut);
		pg.image(cutoutImg, spyglassScreenLocX, spyglassScreenLocY, spyglassScreenWidth, spyglassScreenHeight);
	}
	
	// displayImage function to show whole image in correct location; used for troubleshooting
	/*
	private void displayImage(PGraphics pg, List<MapPosition> mapPositions) {
		System.out.println("Displaying image");
		float displayImgWidth = abs(mapPositions.get(1).x - mapPositions.get(0).x);
		float displayImgHeight = abs(mapPositions.get(2).y - mapPositions.get(1).y);
		pg.image(img, mapPositions.get(0).x, mapPositions.get(0).y, displayImgWidth, displayImgHeight);
	}
	*/

	@Override
	protected void draw(PGraphics pg, List<MapPosition> mapPositions, HashMap<String, Object> properties,
			UnfoldingMap baseMap) {		
		// mapPositions is a list of screen coordinates
		//System.out.println(mapPositions);

		pg.pushStyle();

		// If selected, get the appropriate portion of the associated overlay image
		if (this.isSelected() == true) {
			System.out.println("Marker is selected!");
			//this.displayImage(pg, mapPositions);
			this.displayCutout(pg, mapPositions);
		} else {
			// If not selected, then draw generic marker
			pg.strokeWeight(1);
			pg.stroke(0, 0, 0);
			pg.fill(0, 0, 0, 127);
			pg.beginShape();
			for (MapPosition mapPosition : mapPositions) {
				pg.vertex(mapPosition.x, mapPosition.y);
			}
			pg.endShape();
		}

		pg.popStyle();
	}

	@Override
	public void draw(PGraphics pg, List<MapPosition> objectPositions) {
	}

}
