package application;

import java.util.LinkedList;

import gaze.devicemanager.GazeDeviceManager;
import gaze.devicemanager.GazeEvent;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

public class CircleCalibration extends Pane {

	GazeDeviceManager gazeDeviceManager;
	Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
	double x, y;
	Group g;
	int test = 0;
	Pane p;
	Scene mainScene;
	Stage primaryStage;

	double crossOffsetX =0;
	double crossOffsetY =0;

	LinkedList xs = new LinkedList();
	LinkedList ys = new LinkedList();

	Cursor g1;


	Group crossTable[] = new Group[5];
	Circle cercleTable[] = new Circle[5];

	public CircleCalibration(Pane p , Scene mainScene, Stage primaryStage,Cursor g1, GazeDeviceManager gazeDeviceManager) {
		super();
		this.gazeDeviceManager=gazeDeviceManager;
		this.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
		this.setFocusTraversable(true);
		this.p = p;
		this.mainScene = mainScene;
		this.primaryStage = primaryStage;
		this.g1 = g1;
		installEventHandler(this);
	}


	public void startCalibration() {
		Line l1 = new Line();
		Line l2 = new Line();
		l1.setStartX(-10);
		l1.setEndX(10);
		l2.setStartY(-10);
		l2.setEndY(10);
		g = new Group(l1,l2);
		getChildren().add(g);

		EventHandler<Event> event = e -> {
			double[] pos = new double[2];
			if (e.getEventType() == GazeEvent.GAZE_MOVED) {
				pos = getGazePosition( ((GazeEvent)e).getX(),((GazeEvent)e).getY());
			}else if (e.getEventType() == MouseEvent.MOUSE_MOVED) {
				pos[0] = ((MouseEvent)e).getX();
				pos[1] = ((MouseEvent)e).getY();
			}
			x = pos[0];
			y = pos[1];

		};

		//this.addEventHandler(GazeEvent.GAZE_MOVED, event);
		this.addEventHandler(MouseEvent.MOUSE_MOVED, event);
		gazeDeviceManager.addEventFilter(this);

		testNext();

	}

	public void calibrAnim() {

		double width = primaryScreenBounds.getWidth()/2;
		double height = primaryScreenBounds.getHeight()/2;

		Timeline t = new Timeline();

		for(int i = 0 ; i < 5; i++) {
			t.getKeyFrames().add(new KeyFrame(Duration.millis(500),
					new KeyValue (crossTable[i].layoutXProperty(), width),
					new KeyValue (crossTable[i].layoutYProperty(), height),
					new KeyValue (cercleTable[i].centerXProperty(), width + cercleTable[i].getCenterX() - crossTable[i].getLayoutX()),
					new KeyValue (cercleTable[i].centerYProperty(),  height + cercleTable[i].getCenterY() - crossTable[i].getLayoutY())));
		}
		t.play();

		t.setOnFinished(e->{

			Timeline t2 = new Timeline();

			double moyenneX = 	(cercleTable[0].getCenterX() + 
					cercleTable[1].getCenterX() +
					cercleTable[2].getCenterX()	+
					cercleTable[3].getCenterX()	+
					cercleTable[4].getCenterX())/5;
			double moyenneY = 	(cercleTable[0].getCenterY() + 
					cercleTable[1].getCenterY() +
					cercleTable[2].getCenterY()	+
					cercleTable[3].getCenterY()	+
					cercleTable[4].getCenterY())/5;

			for(int i = 0 ; i < 5; i++) {
				t2.getKeyFrames().add(new KeyFrame(Duration.millis(500),
						new KeyValue (cercleTable[i].centerXProperty(),moyenneX)));
				t2.getKeyFrames().add(new KeyFrame(Duration.millis(500),
						new KeyValue (cercleTable[i].centerYProperty(),moyenneY)));
				t2.getKeyFrames().add(new KeyFrame(Duration.millis(500),
						new KeyValue (cercleTable[i].fillProperty(),Color.INDIANRED)));

			}
			t2.play();

			t2.setOnFinished(e2->{

				Timeline t3 = new Timeline();

				for(int i = 0 ; i < 5; i++) {
					t3.getKeyFrames().add(new KeyFrame(Duration.millis(500),
							//new KeyValue (cercleTable[i].centerXProperty(),width),
							//new KeyValue (cercleTable[i].centerYProperty(),height),
							new KeyValue (cercleTable[i].fillProperty(),Color.DARKSEAGREEN)));

				}
				t3.play();

				t3.setOnFinished(e3->{


					primaryStage.setScene(mainScene);


					primaryStage.setFullScreen(true);


					g1.crossOffsetX = moyenneX - width;
					g1.crossOffsetY =moyenneY - height;

				});

			});

		});


	}


	public void testNext() {

		double width = primaryScreenBounds.getWidth()/5;
		double height = primaryScreenBounds.getHeight()/5;

		if (test==0) {
			g.setLayoutX(width);
			g.setLayoutY(height);
			nextCross();
		}else if (test==1) {
			g.setLayoutX(primaryScreenBounds.getWidth()-width);
			g.setLayoutY(height);
			nextCross();
		}else if (test==2) {
			g.setLayoutX(width);
			g.setLayoutY(primaryScreenBounds.getHeight()-height);
			nextCross();
		}else if (test==3) {
			g.setLayoutX(primaryScreenBounds.getWidth()-width);
			g.setLayoutY(primaryScreenBounds.getHeight()-height);
			nextCross();
		}else if (test==4) {
			g.setLayoutX(primaryScreenBounds.getWidth()/2);
			g.setLayoutY(primaryScreenBounds.getHeight()/2);
			nextCross();
		}else if (test==5) {
			calibrAnim();
		}
		test++;
	}

	public void nextCross() {
		Line l1 = new Line();
		Line l2 = new Line();
		l1.setStartX(-10);
		l1.setEndX(10);
		l2.setStartY(-10);
		l2.setEndY(10);
		Group g1 = new Group();
		g1.getChildren().addAll(l1,l2);
		getChildren().add(g1);
		g1.setLayoutX(g.getLayoutX());
		g1.setLayoutY(g.getLayoutY());   
		crossTable[test] = g1;
	}

	public void addValue(int iteration) {
		if (xs.size()<iteration) {
			Circle c = new Circle();
			c.setRadius(5);
			c.setCenterX(x);
			c.setCenterY(y);
			c.setFill(Color.LIGHTGOLDENRODYELLOW);
			c.setOpacity(0);

			xs.add(x);
			ys.add(y);

			getChildren().add(c);
		}else if(xs.size()==iteration && cercleTable[test-1] ==null) {
			double sumxs = 0 , sumys = 0;

			for(int i = 0; i < xs.size(); i++) {

				sumxs = sumxs + (double)xs.get(i);
				sumys = sumys + (double)ys.get(i);
			}
			sumxs = sumxs / (double)xs.size();
			sumys = sumys / (double)ys.size();


			Circle c = new Circle();
			c.setRadius(5);
			c.setCenterX(sumxs);
			c.setCenterY(sumys);

			c.setFill(Color.DARKRED);

			getChildren().add(c);

			cercleTable[test-1]=c;
		}
	}

	public void displayCircle() {

		if(cercleTable[test-1] !=null) {

		xs.clear();
		ys.clear();

		testNext();
		}
		
	}

	public void installEventHandler(final Node keyNode) {
		final EventHandler<KeyEvent> keyEventHandler = new EventHandler<KeyEvent>() {
			public void handle(final KeyEvent keyEvent) {
				if (keyEvent.getCode() == KeyCode.SPACE) {
					if (keyEvent.getEventType() == KeyEvent.KEY_PRESSED) {
						if(test<=5) {
							addValue(1);
						}
					}else if (keyEvent.getEventType() == KeyEvent.KEY_RELEASED) {
						if(test<=5) {
							displayCircle();
						}
					}
				}
			}
		};

		keyNode.setOnKeyPressed(keyEventHandler);
		keyNode.setOnKeyReleased(keyEventHandler);
	}

	public double[] getGazePosition(double x, double y) {


		double[] res = {x,y};

		Point2D p = this.screenToLocal(res[0],res[1]);
		if (p!=null) {
			res[0] = p.getX();
			res[1] = p.getY();
		}

		return res;
	}
}
