package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name="OpmodeTestingV4")
public class OpmodeTestingV4 extends LinearOpMode {
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
    private static final double SLIDE_POWER = 0.8;
    private static final double PIVOT_POWER = 0.3;
    private static final double PIVOT_HOLDING_POWER = 0.005;
    private static final double SERVO_INCREMENT = 0.01;
    private static final double SERVO_MIN_POS = 0.0;
    private static final double SERVO_MAX_POS = 0.5;

    private void updatePivot(double power) {
        if (Math.abs(power) < 0.1) {
            pivotMotor.setPower(PIVOT_HOLDING_POWER);
        } else {
            pivotMotor.setPower(power);
        }
    }

    @Override
    public void runOpMode() {
        // Drive motors initialization
        frontleft = hardwareMap.get(DcMotor.class, "frontleft");
        frontright = hardwareMap.get(DcMotor.class, "frontright");
        backleft = hardwareMap.get(DcMotor.class, "backleft");
        backright = hardwareMap.get(DcMotor.class, "backright");
        
        // Mechanism motors/servos initialization
        slide = hardwareMap.get(DcMotor.class, "slide");
        pivotMotor = hardwareMap.get(DcMotor.class, "pivot");
        clawservo = hardwareMap.get(Servo.class, "claw-servo");

        // Set motor directions
        frontleft.setDirection(DcMotorSimple.Direction.REVERSE);
        backleft.setDirection(DcMotorSimple.Direction.REVERSE);
        frontright.setDirection(DcMotorSimple.Direction.FORWARD);
        backright.setDirection(DcMotorSimple.Direction.FORWARD);
        slide.setDirection(DcMotorSimple.Direction.FORWARD);
        pivotMotor.setDirection(DcMotorSimple.Direction.REVERSE);

        // Setup pivot motor settings
        pivotMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        pivotMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // Initialize servo position
        double servoPosition = 0.5;
        clawservo.setPosition(servoPosition);

        waitForStart();

        while (opModeIsActive()) {
            // Drive controls
            double y = -gamepad1.left_stick_y;
            double x = gamepad1.left_stick_x;
            double rx = gamepad1.right_stick_x;
            
            double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1);
            double frontleftPower = (y + x + rx) / denominator;
            double backleftPower = (y - x + rx) / denominator;
            double frontrightPower = (y - x - rx) / denominator;
            double backrightPower = (y + x - rx) / denominator;
            double sensitivity = 0.75;
            frontleft.setPower(frontleftPower * sensitivity);
            backleft.setPower(backleftPower * sensitivity);
            frontright.setPower(frontrightPower * sensitivity);
            backright.setPower(backrightPower * sensitivity);

            // Slide control
            double slideInput = gamepad2.left_trigger - gamepad2.right_trigger;
            slide.setPower(slideInput * SLIDE_POWER);

            // Pivot control with holding power
            double pivotPower = 0.005;
            if (gamepad2.dpad_up) {
                pivotPower = PIVOT_POWER;
            } else if (gamepad2.dpad_down) {
                pivotPower = -PIVOT_POWER;
            }
            updatePivot(pivotPower);

            // Servo control
            if (gamepad2.b) {
                servoPosition = SERVO_MAX_POS; // Open claw
            }
            if (gamepad2.a) {
                servoPosition = SERVO_MIN_POS; // Close claw
            }
            clawservo.setPosition(servoPosition);

            // Telemetry updates
            telemetry.addData("Status", "Running");
            telemetry.addData("Drive Motors", "FL(%.2f) FR(%.2f) BL(%.2f) BR(%.2f)",
                frontleftPower, frontrightPower, backleftPower, backrightPower);
            telemetry.addData("Slide Power", "%.2f", slideInput);
            telemetry.addData("Pivot Power", "%.2f", pivotPower);
            telemetry.addData("Servo Position", "%.2f", servoPosition);
            telemetry.update();
        }
    }
}
