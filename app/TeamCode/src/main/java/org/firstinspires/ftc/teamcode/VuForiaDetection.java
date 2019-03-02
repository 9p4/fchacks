/*
Copyright 2019 FIRST Tech Challenge Team 11792
Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
associated documentation files (the "Software"), to deal in the Software without restriction,
including without limitation the rights to use, copy, modify, merge, publish, distribute,
sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:
The above copyright notice and this permission notice shall be included in all copies or substantial
portions of the Software.
THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;

import java.util.List;

// Tests
// TODO: JavaDoc
@Autonomous(name="Autonomous")
public class VuForiaDetection extends LinearOpMode {
    private static final String TFOD_MODEL_ASSET = "RoverRuckus.tflite";
    private static final String LABEL_GOLD_MINERAL = "Gold Mineral";
    private static final String LABEL_SILVER_MINERAL = "Silver Mineral";
    /* Declare OpMode members. */
    CRServo leftDrive, rightDrive;
    Servo armLeft, armRight;
    boolean open = true;

    private VuforiaLocalizer vuforia;
    private TFObjectDetector tfod;

    private static final String VUFORIA_KEY = "AcM0K6z/////AAABmeiIHPqExEm6uvdttqzvUM8yc5vG8YPI75H9AWdWhYDwS3uA8rxBOa8gofNaaTRkLfYpu0EcoykMACJ9vm2u9D0uBFlsxkOSGnjSGZOH7jjS2A+rm0WyOyZ7krIdfoNm+2yV+nPqoQwFApuUDVN7d/HDXq+iW1P+21ZG1ahvPeDr4zJqoHLf9AvNaUzDWssKFBshs6MXdHPH7TaNAHebpqOwVvwOriBRaM/2ffxi/676+DEGypvu5pRcTwmzkCiP3BEdFVpG8BH1jUEcZ+GQd0s59hhqKV2tJZIQwQgvzZISTGSLZHZ06Ag5tOA+m9zIW5M8UpkdWrFEO7mGBRZnMmW0Ztle8Lg+lEHd6t5lZwuS";

    @Override
    public void runOpMode() {

        initVuforia();

        leftDrive = hardwareMap.get(CRServo.class, "left_drive");
        rightDrive = hardwareMap.get(CRServo.class, "right_drive");
        armLeft = hardwareMap.get(Servo.class, "left_arm");
        armRight = hardwareMap.get(Servo.class, "right_arm");

        if (ClassFactory.getInstance().canCreateTFObjectDetector()) {
            initTfod();
        } else {
            telemetry.addData("Sorry!", "This device is not compatible with TFOD");
        }

        /*
         * Initialize the standard drive system variables.
         * The init() method of the hardware class does most of the work here
         */

        telemetry.addData(">", "Press Start");
        telemetry.update();


        telemetry.update();
        waitForStart();

        if (opModeIsActive()) {
            /** Activate Tensor Flow Object Detection. */
            if (tfod != null) {
                tfod.activate();
            }
        }
        int position = 0;
        if (tfod != null) {
            // getUpdatedRecognitions() will return null if no new information is available since
            // the last time that call was made.
            List<Recognition> updatedRecognitions = tfod.getUpdatedRecognitions();
            if (updatedRecognitions != null) {
                telemetry.addData("# Object Detected", updatedRecognitions.size());
                if (updatedRecognitions.size() == 2) {
                    int goldMineralX = -1;
                    int silverMineral1X = -1;
                    int silverMineral2X = -1;
                    for (Recognition recognition : updatedRecognitions) {
                        if (recognition.getLabel().equals(LABEL_GOLD_MINERAL)) {
                            goldMineralX = (int) recognition.getLeft();
                        } else if (silverMineral1X == -1) {
                            silverMineral1X = (int) recognition.getLeft();
                        } else {
                            silverMineral2X = (int) recognition.getLeft();
                        }
                    }
                    if (goldMineralX == -1) {
                        telemetry.addData("Gold Mineral Position", "Right");
                        position = 1;
                    } else if ((goldMineralX < silverMineral1X) || (goldMineralX < silverMineral2X)) {
                        telemetry.addData("Gold Mineral Position", "Left");
                        position = 2;
                    } else {
                        telemetry.addData("Gold Mineral Position", "Center");
                        position = 0;
                    }
                }

                if (updatedRecognitions.size() == 3) {
                    int goldMineralX = -1;
                    int silverMineral1X = -1;
                    int silverMineral2X = -1;
                    for (Recognition recognition : updatedRecognitions) {
                        if (recognition.getLabel().equals(LABEL_GOLD_MINERAL)) {
                            goldMineralX = (int) recognition.getLeft();
                        } else if (silverMineral1X == -1) {
                            silverMineral1X = (int) recognition.getLeft();
                        } else {
                            silverMineral2X = (int) recognition.getLeft();
                        }
                    }

                    if (goldMineralX != -1 && silverMineral1X != -1 && silverMineral2X != -1) {
                        if ((goldMineralX < silverMineral1X) && goldMineralX < silverMineral2X) {
                            telemetry.addData("Gold Mineral Position", "Left");
                            position = 2;
                        } else if (goldMineralX > silverMineral1X && goldMineralX > silverMineral2X) {
                            telemetry.addData("Gold Mineral Position", "Right");
                            position = 1;
                        } else {
                            telemetry.addData("Gold Mineral Position", "Center");
                            position = 0;
                        }
                    }
                }
                telemetry.update();
            }
            // Stuff here
            if (position==0) {
                armRight.setPosition(135);
                armLeft.setPosition(45);
                leftDrive.setPower(1);
                rightDrive.setPower(1);
                sleep(3);
                leftDrive.setPower(0);
                rightDrive.setPower(0);
                armRight.setPosition(180);
                armLeft.setPosition(0);
                sleep(250);
                leftDrive.setPower(1);
                rightDrive.setPower(1);
                sleep(3);
            } else if (position==1) {
                leftDrive.setPower(-1);
                rightDrive.setPower(1);
                sleep(250);
                leftDrive.setPower(0);
                rightDrive.setPower(0);
                armRight.setPosition(135);
                armLeft.setPosition(45);
                leftDrive.setPower(1);
                rightDrive.setPower(1);
                sleep(3);
                leftDrive.setPower(0);
                rightDrive.setPower(0);
                armRight.setPosition(180);
                armLeft.setPosition(0);
                sleep(250);
                leftDrive.setPower(1);
                rightDrive.setPower(1);
                sleep(3);
            } else if (position==2) {
                leftDrive.setPower(1);
                rightDrive.setPower(-1);
                sleep(250);
                leftDrive.setPower(0);
                rightDrive.setPower(0);
                armRight.setPosition(135);
                armLeft.setPosition(45);
                leftDrive.setPower(1);
                rightDrive.setPower(1);
                sleep(3);
                leftDrive.setPower(0);
                rightDrive.setPower(0);
                armRight.setPosition(180);
                armLeft.setPosition(0);
                sleep(250);
                leftDrive.setPower(1);
                rightDrive.setPower(1);
                sleep(3);
            }
        }
    }

    private void initTfod() {
        int tfodMonitorViewId = hardwareMap.appContext.getResources().getIdentifier(
                "tfodMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        TFObjectDetector.Parameters tfodParameters = new TFObjectDetector.Parameters(tfodMonitorViewId);
        tfodParameters.minimumConfidence = .6;
        tfod = ClassFactory.getInstance().createTFObjectDetector(tfodParameters, vuforia);

        tfod.loadModelFromAsset(TFOD_MODEL_ASSET, LABEL_GOLD_MINERAL, LABEL_SILVER_MINERAL);
    }

    private void initVuforia() {
        /*
         * Configure Vuforia by creating a Parameter object, and passing it to the Vuforia engine.
         */
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();

        parameters.vuforiaLicenseKey = VUFORIA_KEY;
        parameters.cameraDirection = VuforiaLocalizer.CameraDirection.BACK;

        //  Instantiate the Vuforia engine
        vuforia = ClassFactory.getInstance().createVuforia(parameters);

        // Loading trackables is not necessary for the Tensor Flow Object Detection engine.
    }
}