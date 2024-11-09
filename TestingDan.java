package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name = "TestingDan")
public class TestingDan extends LinearOpMode {

    // Drive motors
    private DcMotor frontright;
    private DcMotor backright;
    private DcMotor frontleft;
    private DcMotor backleft;

    // Linear Slide motor and constants
    private DcMotor slideMotor;
    private final double SLIDE_POWER = 1.0;
    private final int TICKS_PER_MOTOR_REV = 537;
    private final double SPOOL_DIAMETER_INCHES = 1.5;
    private final double INCHES_PER_MOTOR_REV = SPOOL_DIAMETER_INCHES * Math.PI;
    private final double TICKS_PER_INCH = TICKS_PER_MOTOR_REV / INCHES_PER_MOTOR_REV;
    private final double MAX_HEIGHT_INCHES = 42.0;
    private final int MAX_TICKS = (int)(MAX_HEIGHT_INCHES * TICKS_PER_INCH);

    // Servo configuration
    private Servo clawServo;
    private double clawPosition = 0.5;
    private final double SERVO_SPEED = 0.01;
    private final double MIN_POSITION = 0.0;
    private final double MAX_POSITION = 0.5;

    @Override
    public void runOpMode() {
        // Initialize drive motors
        frontright = hardwareMap.get(DcMotor.class, "frontright");
        backright = hardwareMap.get(DcMotor.class, "backright");
        frontleft = hardwareMap.get(DcMotor.class, "frontleft");
        backleft = hardwareMap.get(DcMotor.class, "backleft");

        // Initialize linear slide motor
        slideMotor = hardwareMap.get(DcMotor.class, "slide");
        slideMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        slideMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        slideMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // Ensure slide motor power is set to zero at the start
        slideMotor.setPower(0);

        // Initialize servo
        clawServo = hardwareMap.get(Servo.class, "claw-servo");
        clawServo.setPosition(clawPosition);

        // Set motor directions
        frontleft.setDirection(DcMotorSimple.Direction.REVERSE);
        backleft.setDirection(DcMotorSimple.Direction.REVERSE);
        slideMotor.setDirection(DcMotorSimple.Direction.FORWARD);

        telemetry.addData("Status", "Initialized");
        telemetry.addData("Max Height", "%.2f inches (%d ticks)", MAX_HEIGHT_INCHES, MAX_TICKS);
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            // Drive control
            float y = -gamepad1.left_stick_y;
            double x = gamepad1.left_stick_x * 1.1;
            float rx = gamepad1.right_stick_x;

            double denominator = Math.max(1.0, Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1));

            // Set motor powers
            frontleft.setPower((y + x + rx) / denominator);
            backleft.setPower((y - x + rx) / denominator);
            frontright.setPower((y - x - rx) / denominator);
            backright.setPower((y + x - rx) / denominator);

            // Linear Slide Control using triggers with safety limits
            double requestedSlidePower = gamepad1.right_trigger - gamepad1.left_trigger; // Right trigger up, left trigger down
            double slidePower = 0;  // Default to zero power

            // Get the current position (in encoder ticks)
            int slidePosition = slideMotor.getCurrentPosition();

            // Apply safety limits and update slide motor power only if triggers are pressed
            if (requestedSlidePower > 0 && slidePosition < MAX_TICKS) {  // Going up
                slidePower = requestedSlidePower * SLIDE_POWER;
            } else if (requestedSlidePower < 0 && slidePosition > 0) {  // Going down
                slidePower = requestedSlidePower * SLIDE_POWER;
            }

            // Set slide motor power
            slideMotor.setPower(slidePower);

            // Add debug telemetry for slide
            telemetry.addData("Requested Slide Power", "%.2f", requestedSlidePower);
            telemetry.addData("Actual Slide Power", "%.2f", slidePower);

            // Servo control
            if (gamepad1.left_bumper && clawPosition < MAX_POSITION) {
                clawPosition += SERVO_SPEED;
            }
            if (gamepad1.right_bumper && clawPosition > MIN_POSITION) {
                clawPosition -= SERVO_SPEED;
            }

            // Ensure servo position stays within bounds
            clawPosition = Math.min(Math.max(clawPosition, MIN_POSITION), MAX_POSITION);
            clawServo.setPosition(clawPosition);

            // Update telemetry
            telemetry.addData("Drive Motors", "FL (%.2f), FR (%.2f), BL (%.2f), BR (%.2f)", 
                frontleft.getPower(), frontright.getPower(), 
                backleft.getPower(), backright.getPower());
            telemetry.addData("Servo Position", "%.2f", clawPosition);
            telemetry.addData("Servo Degrees", "%.2f", clawPosition * 180);

            // Calculate slide height in inches
            double currentHeightInches = (double) slidePosition / TICKS_PER_INCH;
            telemetry.addData("Slide Height", "%.2f inches", currentHeightInches);
            telemetry.addData("Slide Position", "%d ticks", slidePosition);

            telemetry.update();
        }
    }
}
