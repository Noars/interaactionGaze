package application;

import gaze.devicemanager.GazeDeviceManager;
import gaze.devicemanager.GazeEvent;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Path;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GazeMenu extends Pane{

	/*parameters*/
	double radius;

	public Circle centralArea,mainArea,intArea;

	private List<Line> lineList;

	private ArrayList items;


	public Circle[] circles;
	Circle[] shadows;
	private Path[] paths;
	private Text[] texts;

	BooleanProperty enabled;

	private ObjectProperty<Color> mainColor,selectColor;

	public ObjectProperty<Integer> activatedSection;

	Cursor cursor;

	private double minRad = 20 ;

	GazeDeviceManager gazeDeviceManager;

	public GazeMenu(double rad,Cursor cursor, GazeDeviceManager gd) {
		gazeDeviceManager = gd;
		this.cursor = cursor;
		radius = rad;
		intArea = new Circle();
		intArea.setCenterX(0);
		intArea.setCenterY(0);
		mainArea = new Circle(radius - minRad*2); // radius - minRad*2 - 50
		intArea.radiusProperty().bind(mainArea.radiusProperty().multiply(6));;
		mainArea.setCenterX(0);
		mainArea.setCenterY(0);
		centralArea = new Circle((radius - minRad*2)/5);
		centralArea.setCenterX(0);
		centralArea.setCenterY(0);
		items = new ArrayList();
		lineList = new ArrayList<Line>();

		enabled = new SimpleBooleanProperty();
		enabled.set(true);

		mainColor= new SimpleObjectProperty<Color>();
		mainColor.set(Color.ALICEBLUE);

		selectColor= new SimpleObjectProperty<Color>();
		selectColor.set(Color.GOLD);

	}

	public void colorSelected(int index, Color c) {
		int numberOfChildren = items.size();
		double angle = Math.toRadians(index*360/numberOfChildren + 360/(numberOfChildren*2));
		double xstart = ((mainArea.getRadius()+minRad)*Math.sin(angle));
		double ystart = ((mainArea.getRadius()+minRad)*Math.cos(angle));
		double xend = ((mainArea.getRadius()-minRad)*Math.sin(angle));
		double yend = ((mainArea.getRadius()-minRad)*Math.cos(angle));
		Stop[] stops = new Stop[] { new Stop(0, c),new Stop(0.7, mainColor.get()), new Stop(1, mainColor.get())};
		LinearGradient lg1 = new LinearGradient(xstart, ystart, xend,yend, false, CycleMethod.NO_CYCLE, stops);
		circles[index].setFill(lg1);
		circles[index].toFront();
		texts[index].toFront();
	}

	public final void mouseListener() {
		EventHandler<Event> enterevent = e -> {
			double[] pos = new double[2];
			if (e.getEventType() == MouseEvent.MOUSE_MOVED) {
				pos[0] = ((MouseEvent)e).getX();
				pos[1] = ((MouseEvent)e).getY();

			}else if (e.getEventType() == GazeEvent.GAZE_MOVED) {
				Point2D p = intArea.screenToLocal(((GazeEvent)e).getX(),((GazeEvent)e).getY());
				pos[0] = p.getX()- cursor.crossOffsetX;
				pos[1] = p.getY()- cursor.crossOffsetY;
			}
			double distance = Point.distance(0, 0, pos[0], pos[1]);



			centralArea.setFill(selectColor.get());

			double[] res = checkAngle(pos);
			int index = (int) res[0] ;
			cleanCirclesExept(index);
			clearShadowsExcept(index);

			if(distance > mainArea.getRadius()*1.25) {

				shadows[index].setOpacity(1);
				activatedSection.set(index);

			}else if (distance <= mainArea.getRadius()*1.25) {
				colorSelected(index,selectColor.get());
				clearShadowsExcept(-1);
				activatedSection.set(-1);
			}
		};

		intArea.addEventFilter(MouseEvent.MOUSE_MOVED, enterevent);
		//intArea.addEventFilter(GazeEvent.GAZE_MOVED, enterevent);

	}


	public final double[] checkAngle(double[] pos) {
		int numberOfChildren = items.size();
		Point2D val =new Point2D(pos[0],pos[1]);
		double angle1 = calculateAngle(0,0,	0, 50, val.getX(),val.getY());
		double basicAngle = 360.0/(double)numberOfChildren;
		int index = (int) (angle1/basicAngle)%numberOfChildren;

		double[] res = {index,angle1};

		return res;

	}


	public final void draw() {

		activatedSection = new SimpleObjectProperty<Integer>();
		mainArea.setRadius(radius); // radius - minRad*2 - 50

		mainArea.setFill(mainColor.get());

		this.getChildren().add(mainArea);

		minRad = (2*Math.PI*mainArea.getRadius())/ (items.size()*2);

		drawChildren();

		centralArea.setRadius(mainArea.getRadius()/5);
		centralArea.setFill(selectColor.get());
		centralArea.setStroke(Color.DARKGREY);
		centralArea.setStrokeWidth(0);
		this.getChildren().add(centralArea);

		this.getChildren().add(intArea);
		intArea.setOpacity(0);
		mouseListener();

		gazeDeviceManager.addEventFilter(intArea);
	}

	public final void drawChildren() {
		int numberOfChildren = items.size();
		circles = new Circle[numberOfChildren];
		paths  = new Path[numberOfChildren];
		texts  = new Text[numberOfChildren];
		shadows = new Circle[numberOfChildren];
		for(int index = 0; index < numberOfChildren; index++) {

			//Section initialization
			Line arc = new Line();
			double angle = Math.toRadians(((double)index)*360./((double)numberOfChildren));
			arc.setStartX(0);
			arc.setStartY(0);
			arc.endXProperty().bind(mainArea.radiusProperty().multiply(Math.sin(angle)));
			arc.endYProperty().bind(mainArea.radiusProperty().multiply(Math.cos(angle)));
			arc.setStroke(Color.DARKGREY);
			arc.setMouseTransparent(true);
			arc.setOpacity(0.3);
			arc.setStrokeWidth(1);
			lineList.add(arc);
			this.getChildren().add(arc);
			arc.toFront();

			//Guide-circle initialization
			circles[index] = new Circle();
			double anglec = Math.toRadians(((double)index)*360./((double)numberOfChildren) + 360./(((double)numberOfChildren)*2.));
			double xc = (mainArea.getRadius()*Math.sin(anglec));
			double yc = (mainArea.getRadius()*Math.cos(anglec));
			//circles[index].setRadius(minRad);
			circles[index].radiusProperty().bind(mainArea.radiusProperty().multiply(2).multiply(Math.PI).divide(items.size()*2));
			circles[index].setCenterX(xc);
			circles[index].setCenterY(yc);
			circles[index].setFill(mainColor.get());
			circles[index].setStroke(Color.DARKGREY);
			circles[index].setStrokeWidth(0);
			this.getChildren().add(circles[index]);
			circles[index].toFront();		

			shadows[index] = new Circle();
			shadows[index].radiusProperty().bind(mainArea.radiusProperty().multiply(2).multiply(Math.PI).divide(items.size()*2).divide(2));

			double maxRad = 1.5*mainArea.getRadius() +  shadows[index].getRadius();//TODO 1.5 avant
			xc = (maxRad*Math.sin(anglec));
			yc = (maxRad*Math.cos(anglec));

			shadows[index].setCenterX(xc);
			shadows[index].setCenterY(yc);
			shadows[index].setFill(mainColor.get());
			shadows[index].setStrokeWidth(0);
			shadows[index].setOpacity(0.3);
			this.getChildren().add(shadows[index]);
			shadows[index].toBack();

			paths[index] = new Path();
			this.getChildren().add(paths[index]);


			texts[index] = new Text( ""+ getChat(index));
			texts[index].setFont(Font.font (2.*circles[index].getRadius()/3.));
			texts[index].setX(circles[index].getCenterX() - texts[index].getFont().getSize()/3);
			texts[index].setY(circles[index].getCenterY() + texts[index].getFont().getSize()/3);

			this.getChildren().add(texts[index]);

		}
	}


	public final void add(Node Element) {
		items.add(Element);
	}

	public  double calculateAngle(double fixedX, double fixedY, double x1, double y1, double x2, double y2){
		double angle = Math.atan2(y1 - fixedY, x1-fixedX) - Math.atan2(y2-fixedY, x2-fixedX);
		angle = Math.toDegrees(angle);
		if(angle < 0){
			angle += 360;
		}
		return angle;
	}

	public void clearShadowsExcept(int index) {
		int numberOfChildren = items.size();
		for(int i = 0; i < numberOfChildren; i++) {
			if (i!=index) {
				shadows[i].setFill(mainColor.get());
				shadows[i].setOpacity(0.3);
			}
		}
	}

	public void cleanCirclesExept(int i) {
		int numberOfChildren = items.size();
		for(int index = 0; index < numberOfChildren; index++) {
			if (index !=i ) {
				circles[index].setFill(mainColor.get());
			}
		}
	}



	public final char getChat(int index) {
		if(index == -1) {
			return '\0';
		}else {
			if (index >= items.size()/2) {
				return (char)(65 + items.size()+ items.size()/2 -1 -index);
			}else {
				return (char)(65 + items.size()/2 -1 - index);
			}
		}
	}


}
