package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvPipeline;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

@TeleOp(name = "Red Object Detection", group = "Vision")
public class CameraTest extends LinearOpMode {

    OpenCvCamera webcam;

    @Override
    public void runOpMode() {
        // Get the camera view ID
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier(
                "cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        
        // Initialize the webcam
        webcam = OpenCvCameraFactory.getInstance().createWebcam(
                hardwareMap.get(WebcamName.class, "Webcam 1"), cameraMonitorViewId);

        // Set up the OpenCV pipeline
        webcam.setPipeline(new RedDetectionPipeline());

        // Open the camera asynchronously and start streaming when done
        webcam.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener() {
            @Override
            public void onOpened() {
                webcam.startStreaming(640, 480, OpenCvCameraRotation.UPRIGHT);
            }

            @Override
            public void onError(int errorCode) {
                telemetry.addData("Camera Error", "Error code: " + errorCode);
                telemetry.update();
            }
        });

        telemetry.addLine("Waiting for start...");
        telemetry.update();
        waitForStart();

        while (opModeIsActive()) {
            telemetry.addData("Frame Width", webcam.getFrameCount());
            telemetry.addLine("Detecting bright red objects...");
            telemetry.update();
        }

        // Stop the camera after the OpMode ends
        webcam.stopStreaming();
        webcam.closeCameraDevice();
    }

    // Define the pipeline for red detection
    static class RedDetectionPipeline extends OpenCvPipeline {

        Mat hsvMat = new Mat();
        Mat mask = new Mat();
        Mat hierarchy = new Mat();

        @Override
        public Mat processFrame(Mat input) {
            // Convert the frame to HSV color space
            Imgproc.cvtColor(input, hsvMat, Imgproc.COLOR_RGB2HSV);

            // Define range for bright red color in HSV
            Scalar lowerBound = new Scalar(0, 100, 100);  // Lower bound for red
            Scalar upperBound = new Scalar(10, 255, 255); // Upper bound for red
            Core.inRange(hsvMat, lowerBound, upperBound, mask);

            // Find contours
            Mat hierarchy = new Mat();
            java.util.List<MatOfPoint> contours = new java.util.ArrayList<>();
            Imgproc.findContours(mask, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

            // Draw rectangles around detected contours
            for (MatOfPoint contour : contours) {
                Rect rect = Imgproc.boundingRect(contour);

                // Filter by size if needed (e.g., ignore small noise)
                if (rect.area() > 500) { // Adjust threshold as necessary
                    Imgproc.rectangle(input, rect, new Scalar(0, 255, 0), 2);
                }
            }

            // Return the processed frame
            return input;
        }
    }
}
