package com.haxademic.demo.hardware.webcam;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.data.constants.PRenderers;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.image.BufferThresholdMonitor;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.hardware.webcam.WebCam;
import com.haxademic.core.hardware.webcam.WebCam.IWebCamCallback;

import processing.core.PGraphics;
import processing.core.PImage;

public class Demo_BufferThresholdMonitor
extends PAppletHax
implements IWebCamCallback {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PGraphics flippedCamera;
	protected BufferThresholdMonitor thresholdMonitor;

	protected void overridePropsFile() {
		p.appConfig.setProperty(AppSettings.WIDTH, 1280 );
		p.appConfig.setProperty(AppSettings.HEIGHT, 720 );
		p.appConfig.setProperty(AppSettings.SHOW_DEBUG, true );
	}

	public void setupFirstFrame () {
		// build black/white monitor
		thresholdMonitor = new BufferThresholdMonitor();

		// capture webcam frames
		WebCam.instance().setDelegate(this);
	}

	@Override
	public void newFrame(PImage frame) {
		if(flippedCamera == null) flippedCamera = p.createGraphics(frame.width, frame.height, PRenderers.P2D);
		ImageUtil.copyImageFlipH(frame, flippedCamera);

		// calculate activity monitor with new frame
		thresholdMonitor.update(flippedCamera);
		p.debugView.setTexture("flippedCamera", flippedCamera);
	}

	public void drawApp() {
		// set up context
		p.background( 0 );
		PG.setDrawCenter(p);
		PG.setCenterScreen(p);

		// show activity calculation and texture in debug panel
		thresholdMonitor.setCutoff(p.mousePercentX());
		p.debugView.setValue("threshold cutoff", p.mousePercentX());
		p.debugView.setValue("threshold calculation", thresholdMonitor.thresholdCalc());
		p.debugView.setTexture("thresholdBuffer", thresholdMonitor.thresholdBuffer());

		// show diff buffer
		ImageUtil.cropFillCopyImage(thresholdMonitor.thresholdBuffer(), p.g, true);
	}

}
