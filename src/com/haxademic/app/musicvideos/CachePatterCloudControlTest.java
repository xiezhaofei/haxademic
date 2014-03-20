package com.haxademic.app.musicvideos;

import processing.core.PGraphics;
import processing.opengl.PShader;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.util.OpenGLUtil;
import com.haxademic.core.math.easing.EasingFloat;
import com.haxademic.core.system.FileUtil;

@SuppressWarnings("serial")
public class CachePatterCloudControlTest
extends PAppletHax{
	
	PGraphics _cloudsGraphics;
	PShader _clouds;
	
	protected float _timeConstantInc = 0.1f;
	protected EasingFloat _cloudTimeEaser = new EasingFloat(0, 13);

	float _songLengthFrames = 120f; // really 4519, but we're letting Renderer shut this down at the end of the audio file

	
	protected void overridePropsFile() {
		_appConfig.setProperty( "width", "1280" );
		_appConfig.setProperty( "height", "720" );
		_appConfig.setProperty( "rendering", "false" );
	}
	
	public void setup() {
		super.setup();
		
		_cloudsGraphics = p.createGraphics(p.width, p.height, P.OPENGL);
		_cloudsGraphics.smooth(OpenGLUtil.SMOOTH_HIGH);
		
		_clouds = loadShader( FileUtil.getHaxademicDataPath()+"shaders/textures/clouds-iq.glsl" ); 
	}
	
	public void drawApp() {
		p.background(0);
		
		drawClouds();
		
		// move clouds control in a big half-circle
		float percentComplete = (float) p.frameCount / _songLengthFrames;
		float cloudControlRadians = percentComplete * P.PI;
		float cloudControlX = (float)p.width/2f + 		   P.sin(cloudControlRadians - P.HALF_PI) * (float)p.width/2f;
		float cloudControlY = ((float)-p.height * 0.01f) + P.cos(cloudControlRadians - P.HALF_PI) * (float)p.width/2f * 1.f;

		// show cloud controls
		p.fill(0);
		p.ellipse(cloudControlX, cloudControlY, 10, 10);

		P.println(p.frameCount+" / "+_songLengthFrames);
	}
	
	protected void drawClouds() {
		// move clouds control in a big half-circle
		float percentComplete = (float) p.frameCount / _songLengthFrames;
		float cloudControlRadians = percentComplete * P.PI;
		float cloudControlX = (float)p.width/2f + 		   P.sin(cloudControlRadians - P.HALF_PI) * (float)p.width/2f;
		float cloudControlY = ((float)-p.height * 0.3f) + P.cos(cloudControlRadians - P.HALF_PI) * (float)p.width/2f * 1.f;

		_cloudTimeEaser.update();
		_clouds = loadShader( FileUtil.getHaxademicDataPath()+"shaders/textures/clouds-iq.glsl" ); 
		_clouds.set("resolution", 1f, (float)(p.width/p.height));
		_clouds.set("time", p.frameCount * _timeConstantInc + _cloudTimeEaser.value() );
//		_clouds.set("mouse", 0.5f + p.frameCount/4000f, 0.9f - p.frameCount/4000f);		
//		_clouds.set("mouse", (float)mouseX/p.width, (float)mouseY/p.height);		
		_clouds.set("mouse", cloudControlX/p.width, cloudControlY/p.height);		

		_cloudsGraphics.beginDraw();
		_cloudsGraphics.filter(_clouds);		
		_cloudsGraphics.endDraw();
		p.image( _cloudsGraphics, 0, 0);
	}

}