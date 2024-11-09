package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import org.firstinspires.ftc.robotcore.external.JavaUtil;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import org.firstinspires.ftc.robotcore.external.JavaUtil;
// import com.qualcomm.robotcore.hardware.servo;

@TeleOp(name = "mecanumdrivesample1")
public class mecanumdrivesample1 extends LinearOpMode {

  private DcMotor frontright;
  private DcMotor backright;
  private DcMotor frontleft;
  private DcMotor backleft;
  // private servo clawservo;
  /**
   * This function is executed when this Op Mode is selected from the Driver Station.
   */
  @Override
  public void runOpMode() {
    float y;
    double x;
    float rx;
    double denominator;

    frontright = hardwareMap.get(DcMotor.class, "frontright");
    backright = hardwareMap.get(DcMotor.class, "backright");
    frontleft = hardwareMap.get(DcMotor.class, "frontleft");
    backleft = hardwareMap.get(DcMotor.class, "backleft");
    // clawservo = hardwareMap.get(servo.class, "claw-servo");
    
    frontleft.setDirection(DcMotorSimple.Direction.REVERSE);
    backleft.setDirection(DcMotorSimple.Direction.REVERSE);
    waitForStart();
    while (opModeIsActive()) {
      y = -gamepad1.left_stick_y;
      x = gamepad1.left_stick_x * 1.1;
      rx = gamepad1.right_stick_x;
      denominator = JavaUtil.maxOfList(JavaUtil.createListWith(JavaUtil.sumOfList(JavaUtil.createListWith(Math.abs(y), Math.abs(x), Math.abs(rx))), 1));
      frontleft.setPower((y + x + rx) / denominator);
      backleft.setPower(((y - x) + rx) / denominator);
      frontright.setPower(((y - x) - rx) / denominator);
      backright.setPower(((y + x) - rx) / denominator);
      
    }
  }
}