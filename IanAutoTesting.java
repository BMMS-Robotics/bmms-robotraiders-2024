package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import org.firstinspires.ftc.robotcore.external.hardware.camera.BuiltinCameraDirection;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.List;

@Autonomous(name="IanAutoTesting")
public class IanAutoTesting extends LinearOpMode {
    // Robot Drive Motors
    private DcMotor frontright;
    private DcMotor backright;
    private DcMotor frontleft;
    private DcMotor backleft;

    // AprilTag Vision
    private static final boolean USE_WEBCAM = true;
    private AprilTagProcessor aprilTag;
    private VisionPortal visionPortal;

    @Override
    public void runOpMode() {
        // Initialize drive motors
        initDriveMotors();

        // Initialize AprilTag detection
        initAprilTag();

        // Wait for start and display telemetry
        telemetry.addData(">", "Robot Ready. Press Play to start");
        telemetry.update();
        waitForStart();

        if (opModeIsActive()) {
            // Perform initial autonomous movement
            right(1.524);

            // Detect and process AprilTags
            List<AprilTagDetection> currentDetections = aprilTag.getDetections();
            
            // Simple example of routing based on AprilTag detection
            for (AprilTagDetection detection : currentDetections) {
                if (detection.metadata != null) {
                    telemetry.addData("Detected AprilTag", "ID %d", detection.id);
                    
                    // Example routing based on AprilTag ID
                    switch (detection.id) {
                        case 1:
                            forward(0.5);  // Move forward slightly
                            break;
                        case 2:
                            rotate(90);    // Rotate 90 degrees
                            break;
                        case 3:
                            right(0.5);    // Strafe right
                            break;
                    }
                }
            }

            // Close vision portal when done
            visionPortal.close();
        }
    }

    // Motor Initialization Method
    private void initDriveMotors() {
        frontright = hardwareMap.get(DcMotor.class, "frontright");
        backright = hardwareMap.get(DcMotor.class, "backright");
        frontleft = hardwareMap.get(DcMotor.class, "frontleft");
        backleft = hardwareMap.get(DcMotor.class, "backleft");
        
        frontright.setDirection(DcMotor.Direction.REVERSE);
        backright.setDirection(DcMotor.Direction.REVERSE);
    }

    // AprilTag Initialization Method
    private void initAprilTag() {
        // Create AprilTag processor
        aprilTag = AprilTagProcessor.easyCreateWithDefaults();

        // Create vision portal
        if (USE_WEBCAM) {
            visionPortal = VisionPortal.easyCreateWithDefaults(
                hardwareMap.get(WebcamName.class, "Webcam 1"), aprilTag);
        } else {
            visionPortal = VisionPortal.easyCreateWithDefaults(
                BuiltinCameraDirection.BACK, aprilTag);
        }
    }

    // Existing Movement Methods (unchanged from original code)
    public void forward(double distance) {
        double speed = 0.5;
        double circumference = 0.32673;
        double rotations = distance / circumference;
        double time = rotations * (circumference / speed);
        time *= 0.51813;
        frontleft.setPower(-speed);
        frontright.setPower(-speed);
        backleft.setPower(-speed);
        backright.setPower(-speed);
        sleep((long)(time * 1000));
        stopMotors();
    }

    public void right(double distance) {
        double speed = 0.5;
        double circumference = 0.301;
        double rotations = distance / circumference;
        double time = rotations * (circumference / speed);
        frontleft.setPower(-speed);
        frontright.setPower(speed);
        backleft.setPower(speed);
        backright.setPower(-speed);
        time *= 0.83278;
        sleep((long)(time * 1000));
        stopMotors();
    }

    public void rotate(double degrees) {
        double speed = 0.5;
        double length = 0.440;
        double width = 0.4517;
        double diagonal = 0.9955;
        double arcLength = diagonal * (Math.PI * 2) * (degrees / 360);
        double time = arcLength / speed;
        if (degrees > 0) {
            frontleft.setPower(speed);
            frontright.setPower(-speed);
            backleft.setPower(-speed);
            backright.setPower(speed);
        } else {
            frontleft.setPower(-speed);
            frontright.setPower(speed);
            backleft.setPower(speed);
            backright.setPower(-speed);
        }
        sleep((long)(time * 1000));
        stopMotors();
    }

    private void stopMotors() {
        frontleft.setPower(0);
        frontright.setPower(0);
        backleft.setPower(0);
        backright.setPower(0);
    }
}