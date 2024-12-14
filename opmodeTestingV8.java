package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name="OpmodeTestingV8")
public class OpmodeTestingV8 extends LinearOpMode {
    // Drive motors
    private DcMotor frontleft = null;
    private DcMotor frontright = null;
    private DcMotor backleft = null;
    private DcMotor backright = null;
    
    // Mechanism motors and servos
    private DcMotor slide = null;
    private DcMotor pivotMotor = null;
    private Servo clawservo = null;

    // Constants
    private static final int SLIDE_HOME = 0;
    private static int SLIDE_MAX = 2800;
    private static final int SLIDE_SPEED = 20; // Reduced for more precise control
    
    private static final int PIVOT_HOME = -2025;
    private static final int PIVOT_MAX = 0;
    private static final int PIVOT_SPEED = 7;

    private static final double SERVO_INCREMENT = 0.01;
    private static final double SERVO_MIN_POS = 0.0;
    private static final double SERVO_MAX_POS = 0.5;

    // Current position trackers
    private int currentSlidePosition = 0;
    private int currentPivotPosition = 0;
    
    // Deadzone for joysticks
    private static final double STICK_DEADZONE = 0.1; // Increased deadzone for more stable control
    
    private double applyDeadzone(double value) {
        return (Math.abs(value) < STICK_DEADZONE) ? 0 : value;
    }

    @Override
    public void runOpMode() {
        // Drive motors initialization
        frontleft = hardwareMap.get(DcMotor.class, "frontleft");
        frontright = hardwareMap.get(DcMotor.class, "frontright");
        backleft = hardwareMap.get(DcMotor.class, "backleft");
        backright = hardwareMap.get(DcMotor.class, "backright");
        slide = hardwareMap.get(DcMotor.class, "slide");
        pivotMotor = hardwareMap.get(DcMotor.class, "pivot");
        clawservo = hardwareMap.get(Servo.class, "claw-servo");

        // Set motor directions
        frontleft.setDirection(DcMotorSimple.Direction.REVERSE);
        backleft.setDirection(DcMotorSimple.Direction.REVERSE);
        frontright.setDirection(DcMotorSimple.Direction.FORWARD);
        backright.setDirection(DcMotorSimple.Direction.FORWARD);
        
        // Reset and configure slide and pivot motors
        slide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        pivotMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        slide.setDirection(DcMotorSimple.Direction.REVERSE);
        
        // Set initial target positions
        slide.setTargetPosition(0);
        pivotMotor.setTargetPosition(0);
        
        // Set run modes
        slide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        pivotMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        
        // Set zero power behavior
        slide.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        pivotMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // Initialize servo position
        double servoPosition = 0.5;
        clawservo.setPosition(servoPosition);

        waitForStart();
        pivotMotor.setPower(0.5);
        while (opModeIsActive()) {
            // Drive controls
            double y = -gamepad1.left_stick_y;
            double x = gamepad1.left_stick_x;
            double rx = gamepad1.right_stick_x;
            
            // Improved slide control
            double slideInput = -applyDeadzone(gamepad2.left_stick_y);
            double pivotInput = -applyDeadzone(gamepad2.right_stick_y);
            
            // Mecanum drive calculations
            double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1);
            double frontleftPower = (y + x + rx) / denominator;
            double backleftPower = (y - x + rx) / denominator;
            double frontrightPower = (y - x - rx) / denominator;
            double backrightPower = (y + x - rx) / denominator;
            
            // Drive motor power with sensitivity
            double sensitivity = 0.75;
            frontleft.setPower(frontleftPower * sensitivity);
            backleft.setPower(backleftPower * sensitivity);
            frontright.setPower(frontrightPower * sensitivity);
            backright.setPower(backrightPower * sensitivity);

            // Improved Slide Control
            if (Math.abs(slideInput) > 0.1) {
                // Calculate new target based on current position and input
                int targetPosition = currentSlidePosition + (int)(slideInput * SLIDE_SPEED);
                
                // Constrain the target within valid range
                targetPosition = Math.max(SLIDE_HOME, Math.min(targetPosition, SLIDE_MAX));
                
                // Set target and power
                slide.setTargetPosition(targetPosition);
                slide.setPower(Math.abs(slideInput));
                
                // Update current position
                currentSlidePosition = targetPosition;
            } else {
                // Hold current position when joystick is neutral
                slide.setTargetPosition(currentSlidePosition);
                slide.setPower(0.3); // Low hold power to maintain position
            }
            
            // Pivot Motor Control
            currentPivotPosition += (int)(pivotInput * PIVOT_SPEED);
            currentPivotPosition = Math.max(PIVOT_HOME, Math.min(currentPivotPosition, PIVOT_MAX));
            pivotMotor.setTargetPosition(currentPivotPosition);
            
            // Servo control
            if (gamepad2.b) {
                servoPosition = SERVO_MAX_POS; // Open claw
            }
            if (gamepad2.a) {
                servoPosition = SERVO_MIN_POS; // Close claw
            }
            clawservo.setPosition(servoPosition);

            // Additional position-based logic
            if (currentPivotPosition >= 100) {
                SLIDE_MAX = 4540;
            }
            
            // Quick pivot position presets
            if (gamepad2.right_bumper) {
                pivotMotor.setTargetPosition(PIVOT_MAX);
            }
            if (gamepad2.left_bumper) {
                pivotMotor.setTargetPosition(0);
            }
            int hoz = 1062;
            int piv =  -currentPivotPosition - hoz;
            double deg = piv * (65.0/1099.0);
            double startlength = 13.5;
            double legnth = 0.01278*currentSlidePosition + 13.5;
            if (servoPosition = 0.5){
                legnth += 3;
            }
            
            
            // Telemetry updates
            telemetry.addData("Status", "Running");
            telemetry.addData("Drive Motors", "FL(%.2f) FR(%.2f) BL(%.2f) BR(%.2f)",
                frontleftPower, frontrightPower, backleftPower, backrightPower);
            telemetry.addData("Pivot position", currentPivotPosition);
            telemetry.addData("slide", legnth);
            telemetry.addData("Slide position", currentSlidePosition);
            telemetry.addData("Servo Position", "%.2f", servoPosition);
            telemetry.update();
        }
    }
}
