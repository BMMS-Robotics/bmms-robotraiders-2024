package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(name = "ianopmode3 (Blocks to Java)")
public class ianopmode extends LinearOpMode {

  /**
   * This function is executed when this OpMode is selected from the Driver Station. comments!
   */
  @Override
  public void runOpMode() {
    // Put initialization blocks here.
    waitForStart();
    if (opModeIsActive()) {
      // Put run blocks here.
      while (opModeIsActive()) {
        // Put loop blocks here.
        telemetry.update();
      }
    }
  }
}
