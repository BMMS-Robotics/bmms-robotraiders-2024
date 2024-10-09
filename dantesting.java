package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name = "DanTesting (Blocks to Java)")
public class DanTesting extends LinearOpMode {

  private DcMotor back_leftAsDcMotor;

  /**
   * This function is executed when this OpMode is selected from the Driver Station.
   */
  @Override
  public void runOpMode() {
    double i;

    back_leftAsDcMotor = hardwareMap.get(DcMotor.class, "back_leftAsDcMotor");

    // Put initialization blocks here.
    waitForStart();
    if (opModeIsActive()) {
      // Put run blocks here.
      while (opModeIsActive()) {
        // Put loop blocks here.
        telemetry.update();
        for (i = -1; i <= 1; i += 0.001) {
          back_leftAsDcMotor.setPower(i);
        }
      }
    }
  }
}
