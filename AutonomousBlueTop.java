package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

@Autonomous
public class AutonomousBlueTop extends LinearOpMode {
    private DcMotor frontright;
    private DcMotor backright;
    private DcMotor frontleft;
    private DcMotor backleft;

    @Override
    public void runOpMode() {
        frontright = hardwareMap.get(DcMotor.class, "frontright");
        backright = hardwareMap.get(DcMotor.class, "backright");
        frontleft = hardwareMap.get(DcMotor.class, "frontleft");
        backleft = hardwareMap.get(DcMotor.class, "backleft");

        frontright.setDirection(DcMotor.Direction.REVERSE);
        backright.setDirection(DcMotor.Direction.REVERSE);

        waitForStart();

        right(1.524);
    }

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
