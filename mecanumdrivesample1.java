package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import org.firstinspires.ftc.robotcore.external.JavaUtil;

@TeleOp(name = "mecanumdrivesample1 (Blocks to Java)")
public class mecanumdrivesample1 extends LinearOpMode {

  private DcMotor frontright;
  private DcMotor backright;
  private DcMotor frontleft;
  private DcMotor backleft;

  /**
   * This function is executed when this Op Mode is selected from the Driver Station.
   */
  @Override
  public void runOpMode() {
    float y;
    double x;
    float rx;
    double denominator;

    frontRight = hardwareMap.get(DcMotor.class, "frontRight");
    backRight = hardwareMap.get(DcMotor.class, "backRight");
    frontLeft = hardwareMap.get(DcMotor.class, "frontLeft");
    backLeft = hardwareMap.get(DcMotor.class, "backLeft");

    frontRight.setDirection(DcMotorSimple.Direction.REVERSE);
    backRight.setDirection(DcMotorSimple.Direction.REVERSE);
    waitForStart();
    while (opModeIsActive()) {
      y = -gamepad1.left_stick_y;
      x = gamepad1.left_stick_x * 1.1;
      rx = gamepad1.right_stick_x;
      denominator = JavaUtil.maxOfList(JavaUtil.createListWith(JavaUtil.sumOfList(JavaUtil.createListWith(Math.abs(y), Math.abs(x), Math.abs(rx))), 1));
      frontLeft.setPower((y + x + rx) / denominator);
      backLeft.setPower(((y - x) + rx) / denominator);
      frontRight.setPower(((y - x) - rx) / denominator);
      backRight.setPower(((y + x) - rx) / denominator);
    }
  }
}
