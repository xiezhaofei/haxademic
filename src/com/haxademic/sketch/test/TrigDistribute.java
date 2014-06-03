package com.haxademic.sketch.test;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.util.DrawUtil;

import controlP5.ControlP5;

@SuppressWarnings("serial")
public class TrigDistribute
extends PAppletHax {
	
	public int numPoints = 0;
	public int connectionDivisions = 0;
	public float radius = 0;
	protected ControlP5 _cp5;
	
	protected void overridePropsFile() {
		_appConfig.setProperty( "fps", "60" );
	}

	public void setup() {
		_useLegacyAudio = true;
		super.setup();
		p.smooth( 16 );

		_cp5 = new ControlP5(this);
		_cp5.addSlider("numPoints").setPosition(20,60).setWidth(200).setRange(1,90).setDefaultValue(3);
		_cp5.addSlider("radius").setPosition(20,100).setWidth(200).setRange(0,300).setDefaultValue(100);
		_cp5.addSlider("connectionDivisions").setPosition(20,140).setWidth(200).setRange(0,20).setDefaultValue(2);
	}

	public void drawApp() {
		background(0);
		DrawUtil.setDrawCenter(p);
		p.fill(255);
		p.stroke(255);
		p.strokeWeight(2);

		int centerX = p.width / 2;
		int centerY = p.height / 2;
		float segmentRadians = P.TWO_PI / numPoints;
		for( int i=0; i < numPoints; i++ ) {
			float amp = 1 + 1*_audioInput.getFFT().spectrum[i%numPoints];
			float x = centerX + P.sin(segmentRadians * i) * radius * amp;
			float y = centerY + P.cos(segmentRadians * i) * radius * amp;			
			p.ellipse(x, y, 10, 10);
			
			// connect lines
			for( int j=1; j <= connectionDivisions; j++ ) {
				float xDiv = centerX + P.sin(segmentRadians * ((i+numPoints/j)%numPoints)) * radius * amp;
				float yDiv = centerY + P.cos(segmentRadians * ((i+numPoints/j)%numPoints)) * radius * amp;			
				p.line(x, y, xDiv, yDiv);
			}
		}
	}
}