package application;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import gaze.devicemanager.GazeDeviceManager;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Screen;

public class CircleTests extends Pane {

	int numberOfParts;
	final int ANGLE = 360;
	GazeDeviceManager gazeDeviceManager;
	int size = 300;
	Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
	List<Line> lineList;
	Circle c = new Circle();
	Point2D origin;
	boolean start = false;
	boolean trace = false;
	EventHandler<Event> moveevent;
	EventHandler<Event> exitevent;
	Cursor g;
	Circle area;
	ArrayList<Integer> tableNumber;
	Text target = new Text();
	Text[] textsTable;
	ChangeListener<Number> listener;
	double[] textsXTable;

	public CircleTests( int i , int size, Cursor g, GazeDeviceManager gazeDeviceManager) {
		super();
		this.numberOfParts = i;
		initTableNumber();
		this.size = size;
		this.gazeDeviceManager=gazeDeviceManager;
		lineList = new ArrayList<Line>(numberOfParts);
		textsTable = new Text[numberOfParts];
		textsXTable = new double[numberOfParts];
		createCircle();
		this.g = g;
		initTarget();
		listener = createListener();

	}

	public void initTarget() {	
		target.setText("?");
		target.setFont(new Font(size/5));
		target.setX(- target.getFont().getSize()/4);
		target.setY(target.getFont().getSize()/4);

		target.setTextAlignment(TextAlignment.CENTER);
		target.setLayoutX(primaryScreenBounds.getMinX() + primaryScreenBounds.getWidth()/2);
		target.setLayoutY(primaryScreenBounds.getMinY() + primaryScreenBounds.getHeight()/2);

		this.getChildren().add(target);
	}

	public void initTableNumber() {
		tableNumber = new ArrayList<Integer>();
		for(int i = numberOfParts-1; i >= 0; i--) {
			tableNumber.add((i+numberOfParts/2)%numberOfParts);
		}
	}




	public void createCircle() {
		area = new Circle();
		area.setLayoutX(primaryScreenBounds.getMinX() + primaryScreenBounds.getWidth()/2);
		area.setLayoutY(primaryScreenBounds.getMinY() + primaryScreenBounds.getHeight()/2);
		area.setRadius(size/5);
		area.setFill(Color.LIGHTGREY.brighter().darker());


		c.setLayoutX(primaryScreenBounds.getMinX() + primaryScreenBounds.getWidth()/2);
		c.setLayoutY(primaryScreenBounds.getMinY() + primaryScreenBounds.getHeight()/2);
		c.setRadius(size);
		c.setFill(Color.ALICEBLUE);

		this.getChildren().add(c);
		for(int i = 0; i < numberOfParts; i++) {

			double angle = Math.toRadians(i*ANGLE/numberOfParts);

			//Initialisation des sections
			Line arc = new Line();
			arc.setStartX(0);
			arc.setStartY(0);

			double x = (size*Math.sin(angle));
			double y = (size*Math.cos(angle));

			arc.setEndX(x);
			arc.setEndY(y);

			arc.setStroke(Color.LIGHTGRAY);

			arc.setLayoutX(primaryScreenBounds.getMinX() + primaryScreenBounds.getWidth()/2);
			arc.setLayoutY(primaryScreenBounds.getMinY() + primaryScreenBounds.getHeight()/2);
			arc.setMouseTransparent(true);
			lineList.add(arc);

			this.getChildren().add(arc);

			//Initialisation des cercles guide

			Circle c = new Circle();
			c.setRadius(10);
			double anglec = Math.toRadians(i*ANGLE/numberOfParts + ANGLE/(numberOfParts*2));
			double xc = ((size+50)*Math.sin(anglec));
			double yc = ((size+50)*Math.cos(anglec));

			c.setCenterX(xc);
			c.setCenterY(yc);

			c.setFill(Color.ALICEBLUE);

			c.setLayoutX(primaryScreenBounds.getMinX() + primaryScreenBounds.getWidth()/2);
			c.setLayoutY(primaryScreenBounds.getMinY() + primaryScreenBounds.getHeight()/2);

			this.getChildren().add(c);

			//Initialisation des noms de section
			textsTable[i] = new Text(""+tableNumber.get(i));
			double tempSize = (2*Math.PI*size*1)/(3*numberOfParts);
			tempSize = tempSize > size/5 ? size/5 : tempSize;
			textsTable[i].setFont(new Font(tempSize));

			double xt = ((size*2/3)*Math.sin(anglec));
			double yt = ((size*2/3)*Math.cos(anglec));
			textsXTable[i]=xt;

			textsTable[i].setY(yt + textsTable[i].getFont().getSize()/2);

			if(tableNumber.get(i)>=10) {	
				textsTable[i].setX(xt - textsTable[i].getFont().getSize()/2);
			}else {
				textsTable[i].setX(xt - textsTable[i].getFont().getSize()/4);
			}


			textsTable[i].setTextAlignment(TextAlignment.CENTER);
			textsTable[i].setLayoutX(primaryScreenBounds.getMinX() + primaryScreenBounds.getWidth()/2);
			textsTable[i].setLayoutY(primaryScreenBounds.getMinY() + primaryScreenBounds.getHeight()/2);

			this.getChildren().add(textsTable[i]);


		}

		this.getChildren().add(area);

	}


	public void setHandler(){

		g.layoutXProperty().addListener(listener);

	}

	public ChangeListener<Number> createListener() {
		return new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				if (((MainPane)getParent()).click!=2) {
					double[] pos = new double[2];
					pos[0] =newValue.doubleValue() ;
					pos[1] = g.getLayoutY();
					double distance = Point.distance(c.getLayoutX()+c.getCenterX(),c.getLayoutY()+c.getCenterY(),pos[0],pos[1]);

					if (distance <= area.getRadius()) {
						origin = new Point2D(pos[0],pos[1]);
						start = true;
						g.setCursorOn();
					}else if (start && distance > size) {
						Line l = new Line();
						l.setStartX(origin.getX());
						l.setStartY(origin.getY());
						l.setEndX(pos[0]);
						l.setEndY(pos[1]);
						if(trace)
							getChildren().add(l);
						start = false;
						g.setCursorOff();
						((MainPane)getParent()).checkAngle(l,true);
					}
				}
			}
		};
	}

	public int choosePart() {
		int index = (int) (Math.random()*numberOfParts);
		for(int i = 0; i< numberOfParts; i++) {
			textsTable[i].setText(""+tableNumber.get(i));
			if(tableNumber.get(i)>=10) {
				textsTable[i].setX(textsXTable[i] - textsTable[i].getFont().getSize()/2);
			}else {
				textsTable[i].setX(textsXTable[i] - textsTable[i].getFont().getSize()/4);
			}
		}
		target.setText(""+(tableNumber.get(index)));
		if(tableNumber.get(index)>=10) {	
			target.setX(- target.getFont().getSize()/2);
		}else {
			target.setX(- target.getFont().getSize()/4);
		}
		return index;
	}

	/* get the current gaze position offset from menu*/
	public double[] getGazePosition(double x, double y,double rad) {
		double[] res = {x,y};
		Point2D p = c.screenToLocal(res[0],res[1]);
		if (p!=null) {
			res[0] = p.getX() - g.crossOffsetX;
			res[1] =p.getY() - g.crossOffsetY;
		}
		return res;
	}
}
