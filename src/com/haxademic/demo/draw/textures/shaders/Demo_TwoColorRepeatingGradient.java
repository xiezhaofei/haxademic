package com.haxademic.demo.draw.textures.shaders;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.constants.PRenderers;
import com.haxademic.core.draw.color.EasingColor;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.render.FrameLoop;

import processing.core.PGraphics;
import processing.opengl.PShader;

public class Demo_TwoColorRepeatingGradient
extends PAppletHax { 
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PGraphics gradientBuffer;
	protected PShader gradientShader;
	protected float numColors = 2;
	protected EasingColor[] colorStops = new EasingColor[2];
	protected int FRAMES = 500;
	
	protected void config() {
		Config.setProperty( AppSettings.LOOP_FRAMES, FRAMES );
		Config.setProperty( AppSettings.RENDERING_MOVIE, false );
		Config.setProperty( AppSettings.RENDERING_MOVIE_START_FRAME, 1 + FRAMES );
		Config.setProperty( AppSettings.RENDERING_MOVIE_STOP_FRAME, 1 + FRAMES * 2 );
		Config.setProperty( AppSettings.WIDTH, 800 );
		Config.setProperty( AppSettings.HEIGHT, 800 );
	}

	protected void firstFrame() {
		// create noise buffer
		gradientBuffer = p.createGraphics(p.width, p.height, PRenderers.P3D);
		gradientShader = p.loadShader(FileUtil.getPath("haxademic/shaders/textures/cacheflowe-two-color-repeating-gradient.glsl"));
		buildColors();
	}

	protected void buildColors() {
		for (int i = 0; i < colorStops.length; i++) {
			colorStops[i] = new EasingColor("#ff000000", 20);
		}
		setColors("#ffbd03d0", "#ffe8d74d");
	}
	
	public void setColors(String hex1, String hex2) {
		colorStops[0].setTargetHex(hex1);
		colorStops[1].setTargetHex(hex2);
	}
	
	protected void drawApp() {
		background(0);

		// change colors
		if(FrameLoop.progress() < 0.5f)  setColors("#ff000000", "#ffffffff");
		else                        setColors("#ff000000", "#ff00dd44");
		
		// update colors in shader
		for (int i = 0; i < colorStops.length; i++) {
			colorStops[i].update();
			gradientShader.set("color"+(i+1), colorStops[i].r.value()/255f, colorStops[i].g.value()/255f, colorStops[i].b.value()/255f);
		}
		
		// update other shader properties
		gradientShader.set("zoom", 30f + 20f * P.sin(FrameLoop.progressRads()));
		gradientShader.set("scrollY", FrameLoop.progress() * 4f);
		gradientShader.set("oscFreq", P.PI * 8f);
		gradientShader.set("oscAmp", 0.04f + 0.04f * P.sin(FrameLoop.progressRads()));
		gradientShader.set("fade", 0.7f);
		gradientShader.set("rotate", FrameLoop.progressRads());

		// update gradient
		gradientBuffer.filter(gradientShader);
		
		// draw to screen
		p.image(gradientBuffer, 0, 0);  
	}
	
}