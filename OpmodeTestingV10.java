package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

@TeleOp(name="OpmodeTestingV10")
public class OpmodeTestingV10 extends LinearOpMode {
    // Drive motors
    private DcMotor frontleft, frontright, backleft, backright;
    private DcMotor slide, pivotMotor;
    private Servo clawservo;

    // Constants
    private static final int SLIDE_HOME = 0;
    private static final int SLIDE_EXTENDED_MAX = 2464;
    private static final int SLIDE_FULL_MAX = 3614;
    private static final int SLIDE_SPEED = 10;

    private static final int PIVOT_HOME = -1368;
    private static final int PIVOT_MAX = 1086;
    private static final int PIVOT_SPEED = 7;
    private static final int PIVOT_HORIZANTAL = 0;
    private static final double SERVO_INCREMENT = 0.01;
    private static final double SERVO_MIN_POS = 0.0;
    private static final double SERVO_MAX_POS = 0.5;

    private double verticalPosition = -15.0;
    private int currentSlidePosition = 0;
    private int currentPivotPosition = PIVOT_HORIZANTAL;
    private static final double STICK_DEADZONE = 0.1;
    private boolean aButtonPressed = false;
    private boolean clawOpen = false;
    private int SLIDE_MAX = SLIDE_FULL_MAX;

    private double applyDeadzone(double value) {
        return (Math.abs(value) < STICK_DEADZONE) ? 0 : value;
    }

    @Override
    public void runOpMode() {
        frontleft = hardwareMap.get(DcMotor.class, "frontleft");
        frontright = hardwareMap.get(DcMotor.class, "frontright");
        backleft = hardwareMap.get(DcMotor.class, "backleft");
        backright = hardwareMap.get(DcMotor.class, "backright");
        slide = hardwareMap.get(DcMotor.class, "slide");
        pivotMotor = hardwareMap.get(DcMotor.class, "pivot");
        clawservo = hardwareMap.get(Servo.class, "claw-servo");

        frontleft.setDirection(DcMotorSimple.Direction.REVERSE);
        backleft.setDirection(DcMotorSimple.Direction.REVERSE);
        frontright.setDirection(DcMotorSimple.Direction.FORWARD);
        backright.setDirection(DcMotorSimple.Direction.FORWARD);
        slide.setDirection(DcMotorSimple.Direction.REVERSE);

        // Reset encoders
        slide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        pivotMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        slide.setTargetPosition(SLIDE_HOME);
        pivotMotor.setTargetPosition(PIVOT_HORIZANTAL);

        slide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        pivotMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        slide.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        pivotMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        pivotMotor.setPower(0.75);
        clawservo.setPosition(0.0);

        waitForStart();

        while (opModeIsActive()) {
            double y = -gamepad1.left_stick_y;
            double x = gamepad1.left_stick_x;
            double rx = gamepad1.right_stick_x;

            double rotateSensitivity = 0.75;
            double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1);
            frontleft.setPower((y + x + rx * rotateSensitivity) / denominator * 0.75);
            backleft.setPower((y - x + rx * rotateSensitivity) / denominator * 0.75);
            frontright.setPower((y - x - rx * rotateSensitivity) / denominator * 0.75);
            backright.setPower((y + x - rx * rotateSensitivity) / denominator * 0.75);

            double slideInput = -applyDeadzone(gamepad2.left_stick_y);
            double pivotInput = applyDeadzone(gamepad2.right_stick_y);

            // Calculate pivot degrees
            double degrees = (currentPivotPosition * (-0.048754)) - 0.455517;

            // **Update SLIDE_MAX before moving the slide**
            if (degrees <= 45) {
                SLIDE_MAX = SLIDE_EXTENDED_MAX;
            } else {
                SLIDE_MAX = SLIDE_FULL_MAX;
            }

            // If slide is out of bounds, force it within SLIDE_MAX
            if (currentSlidePosition > SLIDE_MAX) {
                currentSlidePosition = SLIDE_MAX;
                slide.setTargetPosition(SLIDE_MAX);
            }

            // Slide Control
            if (Math.abs(slideInput) > 0.1) {
                int targetPosition = currentSlidePosition + (int) (slideInput * SLIDE_SPEED);
                targetPosition = Range.clip(targetPosition, SLIDE_HOME, SLIDE_MAX);

                slide.setTargetPosition(targetPosition);
                slide.setPower(Math.abs(slideInput));
                currentSlidePosition = targetPosition;
            }

            // Pivot Control
            currentPivotPosition += (int) (pivotInput * PIVOT_SPEED);
            currentPivotPosition = Math.max(PIVOT_HOME, Math.min(currentPivotPosition, PIVOT_MAX));
            pivotMotor.setTargetPosition(currentPivotPosition);

            // Claw Toggle
            if (gamepad2.a) {
                if (!aButtonPressed) {
                    clawOpen = !clawOpen;
                    clawservo.setPosition(clawOpen ? SERVO_MAX_POS : SERVO_MIN_POS);
                }
                aButtonPressed = true;
            } else {
                aButtonPressed = false;
            }

            // Arm Length and Vertical Position Calculations
            double length = 0.00704568 * currentSlidePosition + 13.5;
            if (clawservo.getPosition() == SERVO_MAX_POS) {
                length += 3;
            }

            verticalPosition = length * Math.sin(Math.toRadians(degrees));

            telemetry.addData("Pivot position", currentPivotPosition);
            telemetry.addData("Slide position", currentSlidePosition);
            telemetry.addData("Slide Max", SLIDE_MAX);
            telemetry.addData("Servo Position", clawservo.getPosition());
            telemetry.addData("Claw State", clawOpen ? "Closed" : "Open");
            telemetry.addData("Vertical Position", "%.2f", verticalPosition);
            telemetry.addData("Degrees", "%.2f", degrees);
            telemetry.addData("Arm Length", "%.2f", length);
            telemetry.update();
        }
    }
}
