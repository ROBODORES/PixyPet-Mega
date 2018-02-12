package org.usfirst.frc.team4255.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced;

public class Drive {
	public static TalonSRX leftDrive, rightDrive;
	private static long leftZero, rightZero;
	
	public Drive(TalonSRX leftDrive, FeedbackDevice leftFeedback, TalonSRX rightDrive, FeedbackDevice rightFeedback) {
		this.leftDrive = leftDrive;
		this.rightDrive = rightDrive;
		leftZero = 0;
		rightZero = 0;
		leftDrive.setStatusFramePeriod(StatusFrameEnhanced.Status_2_Feedback0, 1, 10);
		leftDrive.configSelectedFeedbackSensor(leftFeedback, 0, 10);
		rightDrive.setStatusFramePeriod(StatusFrameEnhanced.Status_2_Feedback0, 1, 10);
		rightDrive.configSelectedFeedbackSensor(rightFeedback, 0, 10);
	}
	
	public static void setDrive(double leftSpeed, double rightSpeed, boolean reverse) {
		if (reverse) {
			rightSpeed = -rightSpeed;
			leftSpeed = -leftSpeed;
		}
		
		leftDrive.set(ControlMode.PercentOutput, leftSpeed);
		rightDrive.set(ControlMode.PercentOutput, rightSpeed);
	}
	
	public static void singleJoystickDrive(double yVal, double xVal, boolean reverse) {
		double leftSpeed = yVal-xVal;
		if (leftSpeed > 1.0) leftSpeed = 1.0; if (leftSpeed < -1.0) leftSpeed = -1.0;
		double rightSpeed = yVal+xVal;
		if (leftSpeed > 1.0) leftSpeed = 1.0; if (leftSpeed < -1.0) leftSpeed = -1.0;
		
		if (reverse) {
			rightSpeed = -rightSpeed;
			leftSpeed = -leftSpeed;
		}
		
		leftDrive.set(ControlMode.PercentOutput, leftSpeed);
		rightDrive.set(ControlMode.PercentOutput, rightSpeed);
	}
	
	public static void zeroLeftDist() {leftZero = leftDrive.getSelectedSensorPosition(0);}
	
	public static void zeroRightDist() {rightZero = rightDrive.getSelectedSensorPosition(0);}
	
	public static double leftDist() { //returns traveled distance in feet
		return (double)(leftDrive.getSelectedSensorPosition(0)-leftZero)/4096.0*(6*Math.PI)/12.0;
	}
	
	public static double rightDist() { //returns traveled distance in feet
		return (double)(rightDrive.getSelectedSensorPosition(0)-rightZero)/4096.0*(6*Math.PI)/12.0;
	}
	
	public static double leftVelocity() { //returns velocity in feet/second
		return ((double)(leftDrive.getSelectedSensorVelocity(0)*10)/4096.0)*(6*Math.PI)/12.0;
	}
	
	public static double rightVelocity() { //returns velocity in feet/second
		return ((double)(rightDrive.getSelectedSensorVelocity(0)*10)/4096.0)*(6*Math.PI)/12.0;
	}
}
