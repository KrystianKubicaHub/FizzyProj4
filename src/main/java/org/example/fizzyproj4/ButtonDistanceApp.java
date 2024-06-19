package org.example.fizzyproj4;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Arrays;

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

    Button mainBody = new Button("Main");
    Button circulatingBody = new Button("Circulating");


    long lastTick;
    double lastVelocityX;
    double lastVelocityY;
    double masaM;

    private static double X_CURRENT = 0;
    private static double Y_CURRENT = -(radiousMain + 2 * radiousCirulating);

    private static double x3 = 0;
    private static double y3 = 280;


    @Override
    public void start(Stage primaryStage){

        mainBody.setPrefSize(2 * initRadiousMain, 2 * initRadiousMain);
        mainBody.setShape(new Circle(radiousMain));

        circulatingBody.setPrefSize(2 * radiousCirulating, 2 * radiousCirulating);
        circulatingBody.setShape(new Circle(radiousCirulating));

        Button button3 = new Button("Ayo, SHOOT THAT BITCH");
        Button buttonCNR = new Button("Potwierdź promień");

        TextField velocityXField = new TextField();
        velocityXField.setPromptText("składowa pozioma Vx");
        TextField velocityYField = new TextField();
        velocityYField.setPromptText("składowa pionowa Vy");
        TextField mainRField = new TextField();
        mainRField.setPromptText("Promien Planety R");
        TextField mainMassField = new TextField();
        mainMassField.setPromptText("masa planety M");
        TextField circulatingMassField = new TextField();
        circulatingMassField.setPromptText("Circulating Mass");


        // slider przyspiesza lub spowalnia czas animacji
        Slider sliderT = new Slider(1, 10000, 1);   //min, max, pierwotna

        sliderT.setTranslateY(-450);
        sliderT.setLabelFormatter(new SliderLabelFormatter());
        Label valueLabelT = new Label("skala czsu: " + (int)sliderT.getValue());
        sliderT.valueProperty().addListener((observable, oldValue, newValue) ->{
            valueLabelT.setText("skala czasu: " + newValue.intValue());
            TIME_SCALE = newValue.intValue();
            }
        );
        valueLabelT.setTranslateY(-430);
        sliderT.setMaxWidth(500);


        // slider ktory zmienia skale przestrzeni w czasie rzeczywistym
        Slider sliderS = new Slider(1, 100000, 1);   //min, max, pierwotna

        sliderS.setTranslateY(-410);
        sliderS.setLabelFormatter(new SliderLabelFormatter());
        Label valueLabelS = new Label("skala przestrzeni: " + (int)sliderS.getValue());
        sliderS.valueProperty().addListener((observable, oldValue, newValue) -> {
            valueLabelS.setText("skala przestrzeni: " + newValue.intValue());
            SPACE_SCALE = newValue.intValue();
            String mainRFieldValue = mainRField.getText();
            if (mainRFieldValue.equals("")){
                radiousMain = Rz;
            } else {
                try{
                    double mainR = Double.parseDouble(mainRFieldValue);
                    radiousMain = mainR;
                }catch (NumberFormatException error){
                    radiousMain = Rz;
                    System.out.println("serio? promien to " + error + " ?");
                }
            }
            mainBody.setPrefSize(2 * radiousMain/SPACE_SCALE, 2 * radiousMain/SPACE_SCALE);
            circulatingBody.setTranslateX(X_CURRENT/SPACE_SCALE);
            circulatingBody.setTranslateY(Y_CURRENT/SPACE_SCALE);
            circulatingBody.setPrefSize(2* radiousCirulating/SPACE_SCALE, 2 * radiousCirulating/SPACE_SCALE);
            }
        );
        valueLabelS.setTranslateY(-390);
        sliderS.setMaxWidth(1000);



        StackPane stackPane = new StackPane();

        stackPane.getChildren().addAll(mainBody, circulatingBody, button3, buttonCNR, velocityXField, velocityYField, mainRField, mainMassField,
                circulatingMassField, sliderT, valueLabelT, sliderS, valueLabelS);
        ArrayList<TextField> textFields = new ArrayList<>(Arrays.asList(
                velocityXField, velocityYField, mainRField, mainMassField, circulatingMassField));

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

        buttonCNR.setTranslateX(x3);
        buttonCNR.setTranslateY(y3 + 40);


        // przycisk potwierdz promien
        buttonCNR.setOnAction(e -> {
            String mainRFieldValue = mainRField.getText();
            if (mainRFieldValue.equals("")){
                radiousMain = Rz;
            } else {
                try{
                    double mainR = Double.parseDouble(mainRFieldValue);
                    radiousMain = mainR;
                }catch (NumberFormatException error){
                    radiousMain = Rz;
                    System.out.println("serio? promien to " + error + " ?");
                }
            }
            X_CURRENT = 0;
            Y_CURRENT = -(radiousMain + 2 * radiousCirulating);
            mainBody.setPrefSize(2 * radiousMain/SPACE_SCALE, 2 * radiousMain/SPACE_SCALE);
            circulatingBody.setTranslateY(Y_CURRENT/SPACE_SCALE);
        }
        );


        button3.setOnAction(e-> {
            String velocityXFieldValue = velocityXField.getText();
            String velocityYFieldValue = velocityYField.getText();
            String mainRFieldValue = mainRField.getText();
            if (mainRFieldValue.equals("")){
                radiousMain = Rz;
            } else {
                try{
                    double mainR = Double.parseDouble(mainRFieldValue);
                    radiousMain = mainR;
                }catch (NumberFormatException error){
                    radiousMain = Rz;
                    System.out.println("serio? promien to " + error + " ?");
                }
            }

            String mainMassFieldValue = mainMassField.getText();
            if (mainMassFieldValue.equals("")){
                masaM = Mz;
            } else {
                try{
                    double masa = Double.parseDouble(mainRFieldValue);
                    masaM = masa;
                }catch (NumberFormatException error){
                    masaM = Mz;
                    System.out.println("pomidor");
                }
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
                        if(Math.sqrt(Y_CURRENT*Y_CURRENT + X_CURRENT*X_CURRENT) >= initRadiousMain) {               //Math.sqrt(Y_CURRENT*Y_CURRENT + X_CURRENT*X_CURRENT) >= radiousMain
                            shootThatBitch(masaM, circulatingMass, 0);
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

            long odlegloscMiedzySrodkamiCial = (long) Math.sqrt(X_CURRENT * X_CURRENT + Y_CURRENT * Y_CURRENT);
            double circulatingAcceleration = (-1) * G * mainMass / odlegloscMiedzySrodkamiCial * odlegloscMiedzySrodkamiCial;
            double circulatingSpeedDifference = circulatingAcceleration * timeDifferenceInSeconds;


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
            circulatingBody.setTranslateX(X_CURRENT/SPACE_SCALE);
            circulatingBody.setTranslateY(Y_CURRENT/SPACE_SCALE);


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

