package com.haxademic.sketch.system;

import java.awt.Robot;
import java.awt.event.KeyEvent;

import com.haxademic.core.app.PAppletHax;

public class RobotKeyPressTest
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected Robot _robot;

	protected void firstFrame() {
		try { _robot = new Robot(); } catch( Exception error ) { println("couldn't init Robot"); }
	}

	protected void drawApp() {
		background(0);

		if( p.frameCount % 2 == 0 ) {
			_robot.keyPress(KeyEvent.VK_A);
		}
		if( p.frameCount % 2 == 1 ) {
			_robot.keyRelease(KeyEvent.VK_A);
		}
	}
}
