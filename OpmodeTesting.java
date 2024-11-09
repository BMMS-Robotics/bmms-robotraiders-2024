package org.firstinspires.ftc.teamcode;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import org.firstinspires.ftc.robotcore.external.JavaUtil;

@TeleOp(name = "OpmodeTesting")
public class OpmodeTesting extends LinearOpMode {
    // Drive motors
    private DcMotor frontright;
    private DcMotor backright;
    private DcMotor frontleft;
    private DcMotor backleft;
    
    // Linear Slide motor
    private DcMotor slideMotor;
    private final double SLIDE_POWER = 1.0;  // Maximum slide power
    
    // Servo configuration
    private Servo clawServo;
    private double currentPosition = 0.5;
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
        slideMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);  // Prevents slide from falling
        
        // Initialize servo
        clawServo = hardwareMap.get(Servo.class, "claw-servo");
        clawServo.setPosition(currentPosition);

        // Set motor directions
        frontleft.setDirection(DcMotorSimple.Direction.REVERSE);
        backleft.setDirection(DcMotorSimple.Direction.REVERSE);
        slideMotor.setDirection(DcMotorSimple.Direction.FORWARD);  // Adjust if needed

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            // Drive control
            float y = -gamepad1.left_stick_y;
            double x = gamepad1.left_stick_x * 1.1;
            float rx = gamepad1.right_stick_x;
            
            double denominator = JavaUtil.maxOfList(JavaUtil.createListWith(
                JavaUtil.sumOfList(JavaUtil.createListWith(Math.abs(y), Math.abs(x), Math.abs(rx))), 
                1
            ));

            // Set motor powers
            frontleft.setPower((y + x + rx) / denominator);
            backleft.setPower((y - x + rx) / denominator);
            frontright.setPower((y - x - rx) / denominator);
            backright.setPower((y + x - rx) / denominator);

            // Linear Slide Control using triggers
            double slidePower = gamepad1.right_trigger - gamepad1.left_trigger;
            slideMotor.setPower(slidePower * SLIDE_POWER);

            // Servo control
            if (gamepad1.left_bumper && currentPosition < MAX_POSITION) {
                currentPosition += SERVO_SPEED;
            }
            if (gamepad1.right_bumper && currentPosition > MIN_POSITION) {
                currentPosition -= SERVO_SPEED;
            }
            
            // Ensure servo position stays within bounds
            currentPosition = Math.min(Math.max(currentPosition, MIN_POSITION), MAX_POSITION);
            clawServo.setPosition(currentPosition);

            // Update telemetry
            telemetry.addData("Drive Motors", "FL (%.2f), FR (%.2f), BL (%.2f), BR (%.2f)", 
                frontleft.getPower(), frontright.getPower(), 
                backleft.getPower(), backright.getPower());
            telemetry.addData("Servo Position", "%.2f", currentPosition);
            telemetry.addData("Servo Degrees", "%.2f", currentPosition * 180);
            telemetry.addData("Slide Power", "%.2f", slidePower);
            telemetry.addData("Slide Position", "%d", slideMotor.getCurrentPosition());
            telemetry.update();
        }
    }
}