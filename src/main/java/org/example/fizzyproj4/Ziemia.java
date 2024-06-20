package org.example.fizzyproj4;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;

public class Ziemia {
    public static final double G = 6.6734 * Math.pow(0.1, 11);
    public static double masaM;
    public static double promienR;
    public static double X_CURRENT_FIZ;
    public static double Y_CURRENT_FIZ;
    public static double START_HEIGHT;
    public static double SPACE_SCALE;
    public static double TIME_SCALE;
    public static double Vx;
    public static double Vy;
    public static double lastTick;
    public static Button button;
    public static Button buttonMain;

    public Ziemia(Button b1, Button b2) {
        this.masaM = 5.98 * Math.pow(10, 24);
        this.promienR = 6371000;
        this.START_HEIGHT = 100000;
        this.SPACE_SCALE = 35000;
        this.TIME_SCALE = 1000;
        this.X_CURRENT_FIZ = 0;
        this.Y_CURRENT_FIZ = -(promienR + START_HEIGHT);
        this.Vx = 8000;
        this.Vy = 0;
        this.lastTick = System.currentTimeMillis();
        this.button = b1;
        this.buttonMain = b2;
        b2.setPrefSize(2*promienR/SPACE_SCALE, 2*promienR/SPACE_SCALE);
        b1.setPrefSize(2,2);
        button.setTranslateY(Y_CURRENT_FIZ/SPACE_SCALE);
    }

    public void shoot() {
        double currentTime = System.currentTimeMillis();
        double timeDifference = currentTime - lastTick;
        double timeDifferenceInSeconds = (double) timeDifference / 1000 * TIME_SCALE;

        double odlegloscMiedzySrodkamiCial = (double) Math.sqrt(X_CURRENT_FIZ * X_CURRENT_FIZ + Y_CURRENT_FIZ * Y_CURRENT_FIZ);
        double circulatingAcceleration = (-1) * G * masaM / odlegloscMiedzySrodkamiCial / odlegloscMiedzySrodkamiCial;

        double circulatingSpeedDifference = circulatingAcceleration * timeDifferenceInSeconds;
        double alphaInRadians = Math.atan2(Y_CURRENT_FIZ, X_CURRENT_FIZ);
        double alpha = Math.toDegrees(alphaInRadians);

        double circulatingVelocityXDifference = Math.cos(alphaInRadians) * circulatingSpeedDifference;
        double circulatingVelocityYDifference = Math.sin(alphaInRadians) * circulatingSpeedDifference;

        Vx = Vx + circulatingVelocityXDifference;
        Vy = Vy + circulatingVelocityYDifference;

        double circulatingOffsetX = Vx * timeDifferenceInSeconds;
        double circulatingOffsetY = Vy * timeDifferenceInSeconds;

        X_CURRENT_FIZ = X_CURRENT_FIZ + circulatingOffsetX;
        Y_CURRENT_FIZ = Y_CURRENT_FIZ + circulatingOffsetY;
        button.setTranslateX(X_CURRENT_FIZ/SPACE_SCALE);
        button.setTranslateY(Y_CURRENT_FIZ/SPACE_SCALE);

        lastTick = currentTime;
    }

    public boolean check() {
        if(promienR > Math.sqrt(X_CURRENT_FIZ * X_CURRENT_FIZ + Y_CURRENT_FIZ * Y_CURRENT_FIZ)){
            //showAlert();
            System.out.println(" spadło ! ");
            return false;
        }
        return true;
    }

    private void showAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information Dialog");
        alert.setHeaderText(null);
        alert.setContentText("This is an information alert!");

        alert.showAndWait();
    }
}
