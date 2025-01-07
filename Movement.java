package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name = "Movement", group = "Linear Opmode")
public class Movement extends LinearOpMode {
    private DcMotor frontright;
    private DcMotor backright;
    private DcMotor frontleft;
    private DcMotor backleft;
    private DcMotor slide;
    
    // Constants for linear slide
    private static final int SLIDE_HOME = 0;
    private static int SLIDE_MAX = 2800;
    private static final int SLIDE_SPEED = 10;
    private int currentSlidePosition = 0;
    
    // Constants for wheel calculations
    private static final double WHEEL_CIRCUMFERENCE = 0.32673; // meters
    private static final double TICKS_PER_REVOLUTION = 537.7;  // Pulses per revolution at gearbox output
    private static final double TICKS_PER_METER = TICKS_PER_REVOLUTION / WHEEL_CIRCUMFERENCE;
    
    // Motor power settings
    private static final double DRIVE_POWER = 0.5;
    private static final double TURN_POWER = 0.4;
    
    @Override
    public void runOpMode() {
        initializeHardware();
        configureMotors();
        
        telemetry.addData("Status", "Initialized");
        telemetry.update();
        
        waitForStart();
        
        // Example movement sequence
        forward(1.0);  // Move forward 1 meter
        sleep(500);    // Wait half a second between movements
        right(1.0);    // Move right 1 meter
        sleep(500);
        rotate(90);    // Rotate 90 degrees clockwise
    }
    
    private void initializeHardware() {
        frontright = hardwareMap.get(DcMotor.class, "frontright");
        backright = hardwareMap.get(DcMotor.class, "backright");
        frontleft = hardwareMap.get(DcMotor.class, "frontleft");
        backleft = hardwareMap.get(DcMotor.class, "backleft");
        slide = hardwareMap.get(DcMotor.class, "slide");
        
        telemetry.addData("Status", "Hardware Mapped");
        telemetry.update();
    }
    
    private void configureMotors() {
        // Configure motor directions
        frontright.setDirection(DcMotor.Direction.FORWARD);
        backright.setDirection(DcMotor.Direction.FORWARD);
        frontleft.setDirection(DcMotor.Direction.REVERSE);
        backleft.setDirection(DcMotor.Direction.REVERSE);
        
        // Configure drive motors
        DcMotor[] motors = {frontright, backright, frontleft, backleft};
        for (DcMotor motor : motors) {
            motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }
        
        // Configure slide motor
        slide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        slide.setDirection(DcMotorSimple.Direction.REVERSE);
        slide.setTargetPosition(0);
        slide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        slide.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        
        telemetry.addData("Status", "Motors Configured");
        telemetry.update();
    }
    
    public void forward(double distanceMeters) {
        int targetTicks = (int)(distanceMeters * TICKS_PER_METER);
        
        // Reset encoders and set mode
        DcMotor[] motors = {frontright, backright, frontleft, backleft};
        for (DcMotor motor : motors) {
            motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }
        
        // Drive until we reach the target position
        while (opModeIsActive() && 
               Math.abs(frontright.getCurrentPosition()) < targetTicks) {
            for (DcMotor motor : motors) {
                motor.setPower(DRIVE_POWER);
            }
            
            telemetry.addData("Target Ticks", targetTicks);
            telemetry.addData("Front Left Position", frontleft.getCurrentPosition());
            telemetry.addData("Front Right Position", frontright.getCurrentPosition());
            telemetry.addData("Back Left Position", backleft.getCurrentPosition());
            telemetry.addData("Back Right Position", backright.getCurrentPosition());
            telemetry.update();
        }
        
        stopMotors();
    }
    
    public void right(double distanceMeters) {
        int targetTicks = (int)(distanceMeters * TICKS_PER_METER);
        
        // Reset encoders
        DcMotor[] motors = {frontright, backright, frontleft, backleft};
        for (DcMotor motor : motors) {
            motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }
        
        // Drive until we reach the target position
        while (opModeIsActive() && 
               Math.abs(frontright.getCurrentPosition()) < targetTicks) {
            // For strafing right:
            // Front left and back right go forward
            // Front right and back left go backward
            frontleft.setPower(DRIVE_POWER);
            backright.setPower(DRIVE_POWER);
            frontright.setPower(-DRIVE_POWER);
            backleft.setPower(-DRIVE_POWER);
            
            telemetry.addData("Target Ticks", targetTicks);
            telemetry.addData("Front Left Position", frontleft.getCurrentPosition());
            telemetry.addData("Front Right Position", frontright.getCurrentPosition());
            telemetry.addData("Back Left Position", backleft.getCurrentPosition());
            telemetry.addData("Back Right Position", backright.getCurrentPosition());
            telemetry.update();
        }
        
        stopMotors();
    }
    
    public void rotate(double degrees) {
        // Calculate the arc length and convert to ticks
        double robotDiameter = 0.9955; // meters
        double arcLength = robotDiameter * Math.PI * (degrees / 360.0);
        int targetTicks = (int)(arcLength * TICKS_PER_METER);
        
        // Reset encoders
        DcMotor[] motors = {frontright, backright, frontleft, backleft};
        for (DcMotor motor : motors) {
            motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }
        
        // Drive until we reach the target position
        while (opModeIsActive() && 
               Math.abs(frontright.getCurrentPosition()) < targetTicks) {
            if (degrees > 0) {
                // Clockwise rotation
                frontleft.setPower(TURN_POWER);
                backleft.setPower(TURN_POWER);
                frontright.setPower(-TURN_POWER);
                backright.setPower(-TURN_POWER);
            } else {
                // Counter-clockwise rotation
                frontleft.setPower(-TURN_POWER);
                backleft.setPower(-TURN_POWER);
                frontright.setPower(TURN_POWER);
                backright.setPower(TURN_POWER);
            }
            
            telemetry.addData("Target Ticks", targetTicks);
            telemetry.addData("Front Left Position", frontleft.getCurrentPosition());
            telemetry.addData("Front Right Position", frontright.getCurrentPosition());
            telemetry.addData("Back Left Position", backleft.getCurrentPosition());
            telemetry.addData("Back Right Position", backright.getCurrentPosition());
            telemetry.update();
        }
        
        stopMotors();
    }
    
    private void stopMotors() {
        DcMotor[] motors = {frontright, backright, frontleft, backleft};
        for (DcMotor motor : motors) {
            motor.setPower(0);
        }
    }
}
