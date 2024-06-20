package org.example.fizzyproj4;

import javafx.application.Application;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.util.concurrent.atomic.AtomicReference;

public class ButtonDistanceApp extends Application {


    private static final double TEXT_FIELD_HEIGHT = 35;
    private static final double TEXT_FIELD_WIDTH = 150;
    private static final double TEXT_FIELD_SPACE = 5;

    private static final double WINDOW_WIDTH = 1920;
    private static final double WINDOW_HEIGHT = 1000;

    private static final double G = 6.6734 * Math.pow(0.1, 11);
    private static final double Rz = 6371000;
    private static final double Mz = 5.98 * Math.pow(10,24);

    private long shootTime;

    private static final double initRadiousMain = 200;
    private static double radiousMain = initRadiousMain;        // nowa fizyczna wartosc
    private static double radiousCirulating = 10;
    private static double TIME_SCALE = 1;
    private static double SPACE_SCALE = 1;




    long lastTick;
    double lastVelocityX;
    double lastVelocityY;
    double masaM;

    private static double X_CURRENT = 0;
    private static double Y_CURRENT = -(radiousMain/SPACE_SCALE + 2 * radiousCirulating);

    private static double x3 = 0;
    private static double y3 = 280;


    @Override
    public void start(Stage primaryStage){
        Slider sliderMainMass = new Slider(1000, 1000000000, 1000);
        Slider sliderMainRadius = new Slider(1000, 1000000000, 1000);
        Slider sliderInitialHeight = new Slider(10, 100000, 10);

        Slider sliderTimeScale = new Slider(1, 10000, 1);
        Slider sliderScaleSpace = new Slider(1, 100000, 1);

        Slider sliderInitialVx = new Slider(0, 1000, 10);
        Slider sliderInitialVy = new Slider(0, 1000, 10);
        sliderInitialVy.setOrientation(Orientation.VERTICAL);

        Button buttonShotThatBitch = new Button("Ayo, SHOOT THAT BITCH");
        Button buttonConfirmRadius = new Button("Confirm Radius");

        Button buttonDefault = new Button("Default [Ziemia]");
        Button buttonReset = new Button("Reset");

        Button mainBody = new Button("Main");
        Button circulatingBody = new Button("Circulating");


        mainBody.setShape(new Circle(radiousMain));
        circulatingBody.setShape(new Circle(radiousCirulating));

        Label labelMainMass = new Label("Main mass: " + (int)sliderMainMass.getValue());
        Label labelMainRadius = new Label("Radius: " + (int)sliderMainMass.getValue());
        Label labelInitialHeight = new Label("Initial Height: " + (int)sliderMainMass.getValue());
        Label labelTimeScale = new Label("Time Scale: " + (int)sliderMainMass.getValue());
        Label labelSpaceScale = new Label("Space Scale: " + (int)sliderMainMass.getValue());
        Label labelInitialVx = new Label("Initial Vx: " + (int)sliderMainMass.getValue());
        Label labelInitialVy = new Label("Initial Vy: " + (int)sliderMainMass.getValue());

        labelInitialVy.setRotate(-90);


        sliderMainMass.setLabelFormatter(new SliderLabelFormatter());

        turnOnLabelForSlider(sliderMainMass, labelMainMass, "Main Mass: ");
        turnOnLabelForSlider(sliderMainRadius, labelMainRadius, "Radius: ");
        turnOnLabelForSlider(sliderInitialHeight, labelInitialHeight, "Initial height: ");
        turnOnLabelForSlider(sliderScaleSpace, labelSpaceScale, "Space scale: ");
        turnOnLabelForSlider(sliderTimeScale, labelTimeScale, "Time scale: ");
        turnOnLabelForSlider(sliderInitialVx, labelInitialVx, "Initial Vx: ");
        turnOnLabelForSlider(sliderInitialVy, labelInitialVy, "Initial Vy: ");



        StackPane stackPane = new StackPane();
        stackPane.getChildren().addAll(labelMainMass, labelMainRadius, labelInitialHeight, labelSpaceScale, labelTimeScale, labelInitialVx, labelInitialVy,
                sliderMainMass, sliderMainRadius, sliderInitialHeight, sliderScaleSpace, sliderTimeScale, sliderInitialVx, sliderInitialVy, buttonShotThatBitch,
                buttonConfirmRadius, buttonReset, buttonDefault, mainBody, circulatingBody);

        Scene scene = new Scene(stackPane, WINDOW_WIDTH, WINDOW_HEIGHT);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Bitch Shooter");

        primaryStage.show();
        AtomicReference<Double> width = new AtomicReference<>(primaryStage.getWidth());
        AtomicReference<Double> height = new AtomicReference<>(primaryStage.getHeight());


        setElementsPositions(width.get(), height.get(), labelMainMass, labelMainRadius, labelInitialHeight, labelSpaceScale, labelTimeScale, labelInitialVx, labelInitialVy,
                sliderMainMass, sliderMainRadius, sliderInitialHeight, sliderScaleSpace, sliderTimeScale, sliderInitialVx, sliderInitialVy, buttonShotThatBitch,
                buttonConfirmRadius, buttonReset, buttonDefault, mainBody, circulatingBody);

        primaryStage.widthProperty().addListener((observable, oldValue, newValue) -> {
            setElementsPositions(newValue.doubleValue(), height.get(), labelMainMass, labelMainRadius, labelInitialHeight, labelSpaceScale, labelTimeScale, labelInitialVx, labelInitialVy,
                    sliderMainMass, sliderMainRadius, sliderInitialHeight, sliderScaleSpace, sliderTimeScale, sliderInitialVx, sliderInitialVy, buttonShotThatBitch,
                    buttonConfirmRadius, buttonReset, buttonDefault, mainBody, circulatingBody);
            width.set(newValue.doubleValue());
        });

        primaryStage.heightProperty().addListener((observable, oldValue, newValue) -> {
            setElementsPositions(width.get(), height.get(), labelMainMass, labelMainRadius, labelInitialHeight, labelSpaceScale, labelTimeScale, labelInitialVx, labelInitialVy,
                    sliderMainMass, sliderMainRadius, sliderInitialHeight, sliderScaleSpace, sliderTimeScale, sliderInitialVx, sliderInitialVy, buttonShotThatBitch,
                    buttonConfirmRadius, buttonReset, buttonDefault, mainBody, circulatingBody);
            height.set(newValue.doubleValue());
        });


        buttonShotThatBitch.setOnAction(event ->{

        });




    }

    private void turnOnLabelForSlider(Slider slider, Label label, String string) {
        slider.valueProperty().addListener((observable, oldValue, newValue) ->{
                    label.setText(string + newValue.intValue());
                }
        );
    }

    private void setElementsPositions(double windowWidth, double windowHeight, Label labelMainMass, Label labelMainRadius, Label labelInitialHeight,
                                      Label labelSpaceScale, Label labelTimeScale, Label labelInitialVx, Label labelInitialVy,
                                      Slider sliderMainMass, Slider sliderMainRadius, Slider sliderInitialHeight,
                                      Slider sliderScaleSpace, Slider sliderTimeScale, Slider sliderInitialVx, Slider sliderInitialVy,
                                      Button buttonShotThatBitch, Button buttonConfirmRadius, Button buttonReset, Button buttonDefault,
                                      Button mainBody,Button circulatingBody) {
        sliderMainMass.setMaxWidth(PARAMS.SLIDER_WIDTH*windowWidth);
        sliderMainRadius.setMaxWidth(PARAMS.SLIDER_WIDTH*windowWidth);
        sliderInitialHeight.setMaxWidth(PARAMS.SLIDER_WIDTH*windowWidth);
        sliderScaleSpace.setMaxWidth(PARAMS.SLIDER_WIDTH*windowWidth);
        sliderTimeScale.setMaxWidth(PARAMS.SLIDER_WIDTH*windowWidth);
        sliderInitialVx.setMaxWidth(PARAMS.SLIDER_WIDTH*windowWidth);
        sliderInitialVy.setMaxHeight(PARAMS.SLIDER_WIDTH*windowWidth);
        circulatingBody.setPrefSize(2 * PARAMS.INITIAL_CIRCULATING_BODY_RADIUS * windowWidth, 2 * PARAMS.INITIAL_CIRCULATING_BODY_RADIUS * windowWidth);
        mainBody.setPrefSize(2 * PARAMS.INITIAL_MAIN_BODY_RADIUS * windowWidth, 2 * PARAMS.INITIAL_MAIN_BODY_RADIUS * windowWidth);


        sliderMainMass.setTranslateX(PARAMS.slidersOnTheLeft*((double) windowWidth /2));
        labelMainMass.setTranslateX(PARAMS.slidersOnTheLeft*((double) windowWidth /2));
        sliderMainRadius.setTranslateX(PARAMS.slidersOnTheLeft*((double) windowWidth /2));
        labelMainRadius.setTranslateX(PARAMS.slidersOnTheLeft*((double) windowWidth /2));
        sliderInitialHeight.setTranslateX(PARAMS.slidersOnTheLeft*((double) windowWidth /2));
        labelInitialHeight.setTranslateX(PARAMS.slidersOnTheLeft*((double) windowWidth /2));

        sliderMainMass.setTranslateY(PARAMS.topSlidersBeginning *windowHeight/2);
        labelMainMass.setTranslateY(PARAMS.topSlidersBeginning *windowHeight/2+PARAMS.labelOffSet*windowHeight/2);
        sliderMainRadius.setTranslateY((PARAMS.topSlidersBeginning *windowHeight-PARAMS.slidersSpacing *windowHeight)/2);
        labelMainRadius.setTranslateY((PARAMS.topSlidersBeginning *windowHeight-PARAMS.slidersSpacing *windowHeight)/2+PARAMS.labelOffSet*windowHeight/2);
        sliderInitialHeight.setTranslateY((PARAMS.topSlidersBeginning *windowHeight-PARAMS.slidersSpacing *2*windowHeight)/2);
        labelInitialHeight.setTranslateY((PARAMS.topSlidersBeginning *windowHeight-PARAMS.slidersSpacing *2*windowHeight)/2+PARAMS.labelOffSet*windowHeight/2);

        sliderScaleSpace.setTranslateX( PARAMS.sliderOnTheRight * (windowWidth / 2));
        labelSpaceScale.setTranslateX( PARAMS.sliderOnTheRight * (windowWidth / 2));
        sliderTimeScale.setTranslateX( PARAMS.sliderOnTheRight * (windowWidth / 2));
        labelTimeScale.setTranslateX( PARAMS.sliderOnTheRight * (windowWidth / 2));

        sliderScaleSpace.setTranslateY(PARAMS.topSlidersBeginning *windowHeight/2);
        labelSpaceScale.setTranslateY(PARAMS.topSlidersBeginning *windowHeight/2+PARAMS.labelOffSet*windowHeight/2);
        sliderTimeScale.setTranslateY((PARAMS.topSlidersBeginning *windowHeight-PARAMS.slidersSpacing *windowHeight)/2);
        labelTimeScale.setTranslateY((PARAMS.topSlidersBeginning *windowHeight-PARAMS.slidersSpacing *windowHeight)/2+PARAMS.labelOffSet*windowHeight/2);


        sliderInitialVx.setTranslateX(PARAMS.vSlidersBeginningX*windowWidth/2);
        sliderInitialVx.setTranslateY(PARAMS.vSlidersBeginningY*windowHeight/2);
        labelInitialVx.setTranslateX(PARAMS.vSlidersBeginningX*windowWidth/2);
        labelInitialVx.setTranslateY(PARAMS.vSlidersBeginningY*windowHeight/2 + PARAMS.labelOffSet*WINDOW_HEIGHT);

        sliderInitialVy.setTranslateX(PARAMS.vSlidersBeginningX*windowWidth/2 + (PARAMS.slidersOnTheLeft * (windowWidth / 2))/4.8);
        sliderInitialVy.setTranslateY(PARAMS.vSlidersBeginningY*windowHeight/2 + (PARAMS.slidersOnTheLeft * (windowWidth / 2))/4);
        labelInitialVy.setTranslateX(PARAMS.vSlidersBeginningX*windowWidth/2 + (PARAMS.slidersOnTheLeft * (windowWidth / 2))/4.8 - PARAMS.labelOffSet*windowHeight);
        labelInitialVy.setTranslateY(PARAMS.vSlidersBeginningY*windowHeight/2 + (PARAMS.slidersOnTheLeft * (windowWidth / 2))/4);


        buttonShotThatBitch.setTranslateY(PARAMS.MIDDLE_BUTTONS*windowHeight/2);
        buttonConfirmRadius.setTranslateY(PARAMS.MIDDLE_BUTTONS*windowHeight/2 + PARAMS.BUTTONS_SPACING*WINDOW_HEIGHT);

        buttonReset.setTranslateX(PARAMS.cornerButtonsX*windowWidth/2);
        buttonReset.setTranslateY(PARAMS.cornerButtonsY*windowHeight/2);

        buttonDefault.setTranslateX(PARAMS.cornerButtonsX*windowWidth/2-PARAMS.cornerButtonsSpacing *windowWidth);
        buttonDefault.setTranslateY(PARAMS.cornerButtonsY*windowHeight/2);

        circulatingBody.setTranslateY(-(PARAMS.INITIAL_MAIN_BODY_RADIUS*windowWidth + PARAMS.INITIAL_CIRCULATING_BODY_RADIUS*windowWidth));


    }

    private void shootThatBitch(double mainMass) throws InterruptedException {
            System.out.println("cokolwiek: " + SPACE_SCALE);
            long currentTime = System.currentTimeMillis();
            long timeDifference = currentTime - lastTick;
            double timeDifferenceInSeconds = (double) timeDifference / 1000;

            long odlegloscMiedzySrodkamiCial = (long) Math.sqrt(X_CURRENT * X_CURRENT + Y_CURRENT * Y_CURRENT);
            System.out.println("faktyczna odleglosc: " + odlegloscMiedzySrodkamiCial);
            double circulatingAcceleration = G * mainMass / odlegloscMiedzySrodkamiCial * odlegloscMiedzySrodkamiCial;
            double circulatingSpeedDifference = circulatingAcceleration * timeDifferenceInSeconds;
            double alphaInRadians = Math.atan2(Y_CURRENT, X_CURRENT);
            double alpha = Math.toDegrees(alphaInRadians);

            double circulatingVelocityXDifference = Math.cos(alphaInRadians) * circulatingSpeedDifference;
            System.out.println(circulatingVelocityXDifference);
            double circulatingVelocityYDifference = Math.sin(alphaInRadians) * circulatingSpeedDifference;
            System.out.println(circulatingVelocityYDifference);

            lastVelocityX = lastVelocityX + circulatingVelocityXDifference;
            lastVelocityY = lastVelocityY + circulatingVelocityYDifference;

            double circulatingOffsetX = lastVelocityX * timeDifferenceInSeconds;
            double circulatingOffsetY = lastVelocityY * timeDifferenceInSeconds;

            X_CURRENT = X_CURRENT + circulatingOffsetX;
            Y_CURRENT = Y_CURRENT + circulatingOffsetY;
            //circulatingBody.setTranslateX(X_CURRENT/SPACE_SCALE);
            //circulatingBody.setTranslateY(Y_CURRENT/SPACE_SCALE);


            System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
            System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
            System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
            System.out.println("timeDifference: " + timeDifferenceInSeconds + " $$$$$ circulatingAcceleration: " + circulatingAcceleration);
            System.out.println("X: " + X_CURRENT + " Y: " + Y_CURRENT + " d: " + odlegloscMiedzySrodkamiCial);
            System.out.println("circulatingSpeedDifference: " + circulatingSpeedDifference);
            System.out.println("VX: " + lastVelocityX + " & VY: " + lastVelocityY);
            System.out.println("alpha: " + alpha);
            System.out.println("speedDiff_in_X: " + circulatingVelocityXDifference + "     speedDiff_in_Y: " + circulatingVelocityYDifference);
            System.out.println("offsetX: " + circulatingOffsetX + "  offsetY: " + circulatingOffsetY);
            System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
            System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
            System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");


            lastTick = currentTime;



        /*

        double avgForceX = (-1)*G*mainMass*circulatingMass/x;
        double avgForceY = (-1)*G*mainMass*circulatingMass/y;
        double circulatingAccelerationX = avgForceX/circulatingMass;
        double circulatingAccelerationY = avgForceY/circulatingMass;
        double velocityXDifference = circulatingAccelerationX*((double) timeDifference /1000);
        double velocityYDifference = circulatingAccelerationY*((double) timeDifference/1000);


        System.out.println("old: " + lastVelocityX);
        lastVelocityX =  lastVelocityX + velocityXDifference;
        lastVelocityY = lastVelocityY + velocityYDifference;
        //System.out.println("lastVelocityX " + lastVelocityX + " time diff " + timeDifference);


        double offsetX = lastVelocityX*(timeDifference /1000);
        double offsetY = lastVelocityY*(timeDifference /1000);

        System.out.println("X lastVelocityX = " + lastVelocityY + "..........velocityXDifference  "  + velocityYDifference);


        x += offsetX;
        y += offsetY;
        //System.out.println("lastVelocityX " + lastVelocityX + " time diff " + timeDifference);
        circulatingBody.setTranslateX(x);
        circulatingBody.setTranslateY(y);
        circulatingBody.setText(String.valueOf(depth));



        double r = Math.sqrt(x*x+y*y);
       // System.out.println("X: " + x + " Y: " + y + " R:" + r);
        lastTick = currentTime;

        //Thread.sleep(250);
        if(r<initialR){
            long flight_time = currentTime-shootTime;
            //System.out.println("She ain't handle this: " + flight_time);
        }

        /*else if(depth < 10){
            shootThatBitch(mainMass, circulatingMass, depth + 1);
        }else{
            System.out.println("the maximum depth was reached, the whore flew off into the unknown");
        }

         */

    }

    public static void main(String[] args) {
        launch(args);
    }

    // Custom label formatter class
    private static class SliderLabelFormatter extends javafx.util.StringConverter<Double> {
        @Override
        public String toString(Double value) {
            // Define custom labels here
            if (value == 0) return "Min";
            if (value == 100) return "Max";
            return value.intValue() + "%"; // Default label
        }

        @Override
        public Double fromString(String string) {
            // This method is not needed for this example
            return null;
        }
    }
}

