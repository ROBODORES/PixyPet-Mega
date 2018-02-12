package org.usfirst.frc.team4255.robot;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.Joystick;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import edu.wpi.first.wpilibj.Timer;

public class Robot extends IterativeRobot {
	private static final String kDefaultAuto = "Default";
	private static final String kCustomAuto = "My Auto";
	private String m_autoSelected;
	private SendableChooser<String> m_chooser = new SendableChooser<>();
	
	Timer time = new Timer();
	
	I2C pixyPort = new I2C(edu.wpi.first.wpilibj.I2C.Port.kOnboard, 0x45);
	Pixy pixy = new Pixy(pixyPort);
	int pan =0;
	int tilt = 0;
	
	boolean h = false;
	boolean reverse = false;
	boolean clicked = false;
	Joystick jL = new Joystick (0);
    Joystick jR = new Joystick (1);
    Joystick jS = new Joystick (2);
    
    double maxA = 0.0;
    double minA = 0.0;
    double angle = 0.0;
    
    boolean go = false;
	
    AHRS navX = new AHRS(SPI.Port.kMXP);
    
    //TalonSRX[] leftDrive = {new TalonSRX(0), new TalonSRX(1)};
    TalonSRX leftDrive = new TalonSRX(0);
    TalonSRX leftFollow1 = new TalonSRX(1);
    TalonSRX leftFollow2 = new TalonSRX(2);
    TalonSRX rightDrive = new TalonSRX(3);
    TalonSRX rightFollow1 = new TalonSRX(4);
    TalonSRX rightFollow2 = new TalonSRX(5);
    
    Drive drive = new Drive(leftDrive, FeedbackDevice.CTRE_MagEncoder_Relative,
    		rightDrive, FeedbackDevice.None);
    NavDrive navDrive = new NavDrive(navX, drive);
    
    DigitalInput limit = new DigitalInput(9);
	Spark pickup = new Spark(0);
	Solenoid push = new Solenoid(4);

	@Override
	public void robotInit() {
		m_chooser.addDefault("Default Auto", kDefaultAuto);
		m_chooser.addObject("My Auto", kCustomAuto);
		SmartDashboard.putData("Auto choices", m_chooser);
	    rightDrive.setInverted(true);
	    rightFollow1.setInverted(true);
	    rightFollow2.setInverted(true);
		leftFollow1.follow(leftDrive);
		leftFollow2.follow(leftDrive);
		rightFollow1.follow(rightDrive);
		rightFollow2.follow(rightDrive);
	}

	@Override
	public void autonomousInit() {
		m_autoSelected = m_chooser.getSelected();
		System.out.println("Auto selected: " + m_autoSelected);
		navX.reset();
	}

	@Override
	public void autonomousPeriodic() {
		switch (m_autoSelected) {
			case kCustomAuto:
				break;
			case kDefaultAuto:
			default:
				break;
		}
	}
	
	@Override
	public void teleopInit() {
		h = false;
		go = false;
		time.start();
		maxA = 0.0;
		minA = 0.0;
		pan = 500;
		tilt = 500;
	}
	
	@Override
	public void teleopPeriodic() {
		if (pixy.update()) {
			pan += (160 - pixy.objectX[0])/2.0;
			pan  = (int)etc.constrain((double)pan, 0, 1000);
			tilt -= (100 - pixy.objectY[0])/2.0;
			tilt  = (int)etc.constrain((double)tilt, 0, 1000);
			
			//Calculate the differential and forward speed
			double turnSpeed = etc.map(pan, 0, 1000, -1.0, 1.0);
			double forwardSpeed = etc.map((double)pixy.objectWidth[0], -20.0, 180.0, 1.0, -1.0);
			
			//Calculate and constrain the left and right motor speeds
			double leftSpeed = forwardSpeed-(turnSpeed*0.6);
			leftSpeed = etc.constrain(leftSpeed, -1.0, 1.0);
			double rightSpeed = forwardSpeed+(turnSpeed*0.6);
			rightSpeed = etc.constrain(rightSpeed, -1.0, 1.0);
			
			drive.setDrive(leftSpeed, rightSpeed, false);
			
			time.reset();
		} else {
			if (time.get() > 0.25) {
				pan = 500;
				tilt = 600;
				drive.setDrive(0.0, 0.0, false);
			}
		}
		pixy.setPanTilt(pan, tilt);
	}
	
	@Override
	public void testPeriodic() {
	}
}
