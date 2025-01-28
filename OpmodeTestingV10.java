package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name="OpmodeTestingV10")
public class OpmodeTestingV10 extends LinearOpMode {
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
    private static final int SLIDE_SPEED = 10;
    
    private static final int PIVOT_HOME = -2242;
    private static final int PIVOT_MAX = 0;
    private static final int PIVOT_SPEED = 7;

    private static final double SERVO_INCREMENT = 0.01;
    private static final double SERVO_MIN_POS = 0.0;
    private static final double SERVO_MAX_POS = 0.5;

    // Current position trackers
    private int currentSlidePosition = 0;
    private int currentPivotPosition = 0;
    
    private static final double STICK_DEADZONE = 0.1;
    
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
        
        double verticalPosition = -15.0;
        
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
        double servoPosition = 0.0;
        clawservo.setPosition(servoPosition);

        boolean aButtonPressed = false;
        boolean clawOpen = false;

        waitForStart();
        pivotMotor.setPower(0.5);
        
        while (opModeIsActive()) {
            // Drive controls
            double y = -gamepad1.left_stick_y;
            double x = gamepad1.left_stick_x;
            double rx = gamepad1.right_stick_x;
            
            // Improved slide control
            double slideInput = -applyDeadzone(gamepad2.left_stick_y);
            double pivotInput = applyDeadzone(gamepad2.right_stick_y);
            
            // Mecanum drive calculations
            double rotateSensitivity = 0.75;
            double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1);
            double frontleftPower = (y + x + rx * rotateSensitivity) / denominator;
            double backleftPower = (y - x + rx * rotateSensitivity) / denominator;
            double frontrightPower = (y - x - rx * rotateSensitivity) / denominator;
            double backrightPower = (y + x - rx * rotateSensitivity) / denominator;
            
            // Drive motor power with sensitivity
            double sensitivity = 0.75;
            frontleft.setPower(frontleftPower * sensitivity);
            backleft.setPower(backleftPower * sensitivity);
            frontright.setPower(frontrightPower * sensitivity);
            backright.setPower(backrightPower * sensitivity);
            
            if (verticalPosition <= -5) {  // Fixed syntax error in comparison
                if (gamepad2.left_stick_y < 0 || gamepad2.right_stick_y >0) {
                    slideInput = 0;
                    pivotInput = 0;
                }
            }
            
            // Improved Slide Control
            if (Math.abs(slideInput) > 0.1) {
                int targetPosition = currentSlidePosition + (int)(slideInput * SLIDE_SPEED);
                targetPosition = Math.max(SLIDE_HOME, Math.min(targetPosition, SLIDE_MAX));
                slide.setTargetPosition(targetPosition);
                slide.setPower(Math.abs(slideInput));
                currentSlidePosition = targetPosition;
            } else {
                slide.setTargetPosition(currentSlidePosition);
            }
            
            // Pivot Motor Control
            currentPivotPosition += (int)(pivotInput * PIVOT_SPEED);
            currentPivotPosition = Math.max(PIVOT_HOME, Math.min(currentPivotPosition, PIVOT_MAX));
            pivotMotor.setTargetPosition(currentPivotPosition);
            
            // Servo control
            if (gamepad2.a) {
                if (!aButtonPressed) {
                    clawOpen = !clawOpen;
                    servoPosition = clawOpen ? SERVO_MAX_POS : SERVO_MIN_POS;
                }
                aButtonPressed = true;
            } else {
                aButtonPressed = false;
            }

            clawservo.setPosition(servoPosition);
            
            // Quick pivot position presets
            if (gamepad2.right_bumper) {
                pivotMotor.setTargetPosition(PIVOT_MAX);
            }
            if (gamepad2.left_bumper) {
                pivotMotor.setTargetPosition(0);
            }

            // Position calculations
            int horizontalOffset = -831;;
            int pivotAngle = -currentPivotPosition - horizontalOffset;
            double degrees = pivotAngle * (65.0/1099.0);
            double startLength = 13;
            double length = 0.00704568 * currentSlidePosition + 13.5;
            
            if (servoPosition == 0.5) {
                length += 3;
            }
            
            verticalPosition = length * Math.sin(Math.toRadians(degrees));  // Fixed Math.PI180 to proper conversion
            
            if (degrees <= 45) {
                SLIDE_MAX = 2207;
            } else {
                SLIDE_MAX = 3187;
            }
            
            // Telemetry updates
            telemetry.addData("Status", "Running");
            telemetry.addData("Drive Motors", "FL(%.2f) FR(%.2f) BL(%.2f) BR(%.2f)",
                frontleftPower, frontrightPower, backleftPower, backrightPower);
            telemetry.addData("Pivot position", currentPivotPosition);
            telemetry.addData("Slide position", currentSlidePosition);
            telemetry.addData("Servo Position", "%.2f", servoPosition);
            telemetry.addData("Claw State", clawOpen ? "Open" : "Closed");
            telemetry.addData("Vertical Position", "%.2f", verticalPosition);
            telemetry.addData("Degrees", "%.2f", degrees);
            telemetry.addData("legtnth", "%.2f", length);
            telemetry.update();
        }
    }
}
