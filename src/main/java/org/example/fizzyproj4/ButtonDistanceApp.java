package org.example.fizzyproj4;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

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

    private static double radiousMain = 200;
    private static double radiousCirulating = 10;

    Button mainBody = new Button("Main");
    Button circulatingBody = new Button("Circulating");


    long lastTick;
    double lastVelocityX;
    double lastVelocityY;

    private static double X_CURRENT = 0;
    private static double Y_CURRENT = -(radiousMain+radiousCirulating);

    private static double x3 = 0;
    private static double y3 = 280;


    @Override
    public void start(Stage primaryStage){

        System.out.println(G);
        mainBody.setPrefSize(2 * radiousMain, 2 * radiousMain);
        mainBody.setShape(new Circle(radiousMain));

        circulatingBody.setPrefSize(2 * radiousCirulating, 2 * radiousCirulating);
        circulatingBody.setShape(new Circle(radiousCirulating));

        Button button3 = new Button("Ayo, SHOOT THAT BITCH");

        TextField velocityXField = new TextField();
        velocityXField.setPromptText("składowa pozioma Vx");
        TextField velocityYField = new TextField();
        velocityYField.setPromptText("składowa pionowa Vy");
        TextField mainRField = new TextField();
        mainRField.setPromptText("Promien Planety R");
        TextField circulatingMassField = new TextField();
        circulatingMassField.setPromptText("Circulating Mass");



        StackPane stackPane = new StackPane();

        stackPane.getChildren().addAll(mainBody, circulatingBody, button3, velocityXField, velocityYField, mainRField, circulatingMassField);
        ArrayList<TextField> textFields = new ArrayList<>(Arrays.asList(
                velocityXField, velocityYField, mainRField, circulatingMassField));

        // Petla ustawiajaca przyciski
        int i1 = -1;
        for(TextField tx : textFields){
            tx.setMaxSize(TEXT_FIELD_WIDTH, TEXT_FIELD_HEIGHT);
            double translateX = (-(WINDOW_WIDTH/2-TEXT_FIELD_WIDTH/2-TEXT_FIELD_SPACE)) + i1 *(TEXT_FIELD_SPACE+TEXT_FIELD_WIDTH);
            double translateY = -(WINDOW_HEIGHT/2-TEXT_FIELD_HEIGHT/2-TEXT_FIELD_SPACE);
            double translateXMax = WINDOW_WIDTH/2-TEXT_FIELD_WIDTH/2-TEXT_FIELD_SPACE;
            if(translateX>translateXMax){
                translateX = translateX - translateXMax;
                translateY = translateY + TEXT_FIELD_SPACE + TEXT_FIELD_HEIGHT;
            }
            tx.setTranslateX(translateX + 300);
            tx.setTranslateY(translateY);
            i1++;
            System.out.println("x: " + translateX + " y: " + translateY);
        }


        circulatingBody.setTranslateX(X_CURRENT);
        circulatingBody.setTranslateY(Y_CURRENT);

        button3.setTranslateX(x3);
        button3.setTranslateY(y3);
        button3.setOnAction(e-> {
            String velocityXFieldValue = velocityXField.getText();
            String velocityYFieldValue = velocityYField.getText();
            String mainRFieldValue = mainRField.getText();
            if (mainRFieldValue != ""){
                try{
                    double mainR = Double.parseDouble(mainRFieldValue);
                    radiousMain = mainR;
                    mainBody.setPrefSize(2*radiousMain, 2*radiousMain);
                    System.out.println(radiousMain);
                }catch (NumberFormatException error){
                    radiousMain = Rz;
                    mainBody.setPrefSize(2*radiousMain, 2*radiousMain);
                    System.out.println("serio? promien to " + error + " ?");
                }
            }   else {
                radiousMain = Rz;
                mainBody.setPrefSize(2*radiousMain, 2*radiousMain);
            }

            String circulatingMassFieldValue = circulatingMassField.getText();
            try {
                long initialVX = Long.parseLong(velocityXFieldValue);
                long initialVY = Long.parseLong(velocityYFieldValue);
                long circulatingMass = Long.parseLong(circulatingMassFieldValue);

                shootTime = System.currentTimeMillis();
                lastTick = System.currentTimeMillis();
                lastVelocityX = initialVX;
                lastVelocityY = -initialVY;

                Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(0.1), event -> {
                    try {
                        if(Math.sqrt(Y_CURRENT*Y_CURRENT + X_CURRENT*X_CURRENT) >= radiousMain) {
                            shootThatBitch(Mz, circulatingMass, 0);
                        }
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
                }));
                timeline.setCycleCount(Timeline.INDEFINITE);
                timeline.play();



                //shootThatBitch(mainMass, circulatingMass, 0);

            } catch (NumberFormatException error) {
                mainBody.setText("Tu1");
            }

        });


        Scene scene = new Scene(stackPane, WINDOW_WIDTH, WINDOW_HEIGHT);


        primaryStage.setScene(scene);
        primaryStage.setTitle("Bitch Shooter");

        primaryStage.show();
    }

    private void shootThatBitch(double mainMass, double circulatingMass, int depth) throws InterruptedException {
            long currentTime = System.currentTimeMillis();
            long timeDifference = currentTime - lastTick;
            double timeDifferenceInSeconds = (double) timeDifference / 1000;

            long wektor = (long) Math.sqrt(X_CURRENT * X_CURRENT + Y_CURRENT * Y_CURRENT);       // wartosc odleglosci miedzy planetami (trzeba sprawdzic czy to jest miedzy ich srodkami)

            double avgForce = G * mainMass * circulatingMass / wektor * wektor;   // GMm/r^2 -> dobry wzorek


            double circulatingAcceleration = avgForce / circulatingMass;      // tu jest juz problem, bo powinnismy zalozyc ze v jest stale


            double circulatingSpeedDifference = circulatingAcceleration * (timeDifferenceInSeconds);


            double alphaInRadians = Math.atan2(Y_CURRENT, X_CURRENT);
            double alpha = Math.toDegrees(alphaInRadians);


            double circulatingVelocityXDifference = Math.cos(alphaInRadians) * circulatingSpeedDifference;
            double circulatingVelocityYDifference = Math.sin(alphaInRadians) * circulatingSpeedDifference;

            lastVelocityX = lastVelocityX + circulatingVelocityXDifference;
            lastVelocityY = lastVelocityY + circulatingVelocityYDifference;

            double circulatingOffsetX = lastVelocityX * timeDifferenceInSeconds;
            double circulatingOffsetY = lastVelocityY * timeDifferenceInSeconds;

            X_CURRENT = X_CURRENT + circulatingOffsetX;
            Y_CURRENT = Y_CURRENT + circulatingOffsetY;
            circulatingBody.setTranslateX(X_CURRENT);
            circulatingBody.setTranslateY(Y_CURRENT);


            System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
            System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
            System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
            System.out.println("timeDifference: " + timeDifferenceInSeconds + " $$$$$ circulatingAcceleration: " + circulatingAcceleration);
            System.out.println("X: " + X_CURRENT + " Y: " + Y_CURRENT + " d: " + wektor);
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
}

