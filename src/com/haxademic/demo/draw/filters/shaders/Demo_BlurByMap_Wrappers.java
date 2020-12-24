package com.haxademic.demo.draw.filters.shaders;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.BlurHFilter;
import com.haxademic.core.draw.filters.pshader.BlurHMapFilter;
import com.haxademic.core.draw.filters.pshader.BlurVFilter;
import com.haxademic.core.draw.filters.pshader.BlurVMapFilter;
import com.haxademic.core.draw.filters.pshader.BrightnessStepFilter;
import com.haxademic.core.draw.filters.pshader.ContrastFilter;
import com.haxademic.core.draw.filters.pshader.DisplacementMapFilter;
import com.haxademic.core.draw.filters.pshader.FXAAFilter;
import com.haxademic.core.draw.filters.pshader.GrainFilter;
import com.haxademic.core.draw.filters.pshader.SaturationFilter;
import com.haxademic.core.draw.filters.pshader.SharpenMapFilter;
import com.haxademic.core.draw.filters.pshader.ThresholdFilter;
import com.haxademic.core.draw.textures.SimplexNoise3dTexture;
import com.haxademic.core.hardware.mouse.Mouse;
import com.haxademic.core.render.FrameLoop;

import processing.core.PGraphics;

public class Demo_BlurByMap_Wrappers
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected SimplexNoise3dTexture noiseTexture;
	protected PGraphics basicMap;

	protected void config() {
		Config.setProperty( AppSettings.WIDTH, 1024 );
		Config.setProperty( AppSettings.HEIGHT, 1024 );
	}
	
	protected void firstFrame() {
		// better pg?
		pg = PG.newPG32(p.width, p.height, true, false);
		
		// init noise object
		noiseTexture = new SimplexNoise3dTexture(p.width, p.height, true);
		noiseTexture.update(0.07f, 0, 0, 0, 0, false);
		DebugView.setTexture("noise", noiseTexture.texture());
		
		// extra map
		basicMap = PG.newPG32(256, 256, true, false);
		DebugView.setTexture("basicMap", basicMap);
	}
	
	protected void drawApp() {
		if(p.frameCount == 1) PG.setTextureRepeat(p.g, true);
		p.background(0);
		PG.setDrawCorner(p.g);
		
		///////////////////////
		// update basic map
		///////////////////////
		basicMap.beginDraw();
		PG.setCenterScreen(basicMap);
		PG.setDrawCenter(basicMap);
		basicMap.background(0);
		basicMap.noStroke();
		basicMap.fill(255);
		basicMap.ellipse(0, 0, basicMap.width / 2, basicMap.height / 2);
		basicMap.endDraw();
		
		BlurHFilter.instance(p).setBlurByPercent(1f, basicMap.width);
		BlurVFilter.instance(p).setBlurByPercent(1f, basicMap.height);
		for (int i = 0; i < 10; i++) {
			BlurHFilter.instance(p).applyTo(basicMap);
			BlurVFilter.instance(p).applyTo(basicMap);
		}
		
		///////////////////////
		// update noise map
		///////////////////////
		noiseTexture.update(
				FrameLoop.osc(0.004f, 0.8f, 1f),	// zoom
				FrameLoop.count(0.004f),			// rotation
				0,									// offset x
				0,									// offset y
				FrameLoop.count(0.002f),			// offset z
				false								// fractal mode
		);
		ContrastFilter.instance(p).setContrast(3.f);
		ContrastFilter.instance(p).applyTo(noiseTexture.texture());
		
		// Draw seed
		pg.beginDraw();
		PG.setDrawCorner(pg);
		float colorOsc = FrameLoop.osc(0.01f, 0, 255);
		colorOsc = Mouse.xNorm * 255;
//		if(colorOsc > 50 || frameCount % 3000 == 0) {
////			PG.drawGrid(pg, p.color(0, colorOsc), p.color(255, colorOsc), pg.width/40, pg.height/40, 5);
//			PG.setPImageAlpha(pg, colorOsc/255f);
//			ImageUtil.drawImageCropFill(DemoAssets.justin(), pg, true);
//			PG.resetPImageAlpha(pg);
//		}
		
		///////////////////////
		// add text
		///////////////////////
		/*
		String fontFile = DemoAssets.fontOpenSansPath;
		PFont font = FontCacher.getFont(fontFile, 320);
		FontCacher.setFontOnContext(pg, font, p.color(255), 1f, PTextAlign.CENTER, PTextAlign.CENTER);
//		pg.text("FAKE", 0, 0, pg.width, pg.height);
		StrokeText.draw(pg, "DRIP", 0, -40, pg.width, pg.height, p.color(255), p.color(0), 10, 36);
		 */
		
		///////////////////////
		// set R/D uniforms
		///////////////////////
		GrainFilter.instance(p).setCrossfade(0.1f);
		GrainFilter.instance(p).setTime(p.frameCount);

		PGraphics rdMap = (Mouse.yNorm > 0.5f) ? basicMap : noiseTexture.texture();
		BlurHMapFilter.instance(p).setMap(rdMap);
		BlurHMapFilter.instance(p).setAmpMin(0.6f);
		BlurHMapFilter.instance(p).setAmpMax(1.5f);
		BlurVMapFilter.instance(p).setMap(rdMap);
		BlurVMapFilter.instance(p).setAmpMin(0.6f);
		BlurVMapFilter.instance(p).setAmpMax(1.5f);
		SharpenMapFilter.instance(p).setMap(rdMap);
		SharpenMapFilter.instance(p).setAmpMin(3f);
		SharpenMapFilter.instance(p).setAmpMax(20f);
		
		DisplacementMapFilter.instance(p).setMap(noiseTexture.texture());
		DisplacementMapFilter.instance(p).setMode(3);
		DisplacementMapFilter.instance(p).setRotRange(P.TWO_PI * 2f);
		DisplacementMapFilter.instance(p).setAmp(0.001f);

		BrightnessStepFilter.instance(p).setBrightnessStep((-255f * Mouse.xNorm)/255f);

		///////////////////////
		// auto loop
		///////////////////////
//		BrightnessStepFilter.instance(p).setBrightnessStep(FrameLoop.osc(0.003f, -200, -40)/255f);
//		DisplacementMapFilter.instance(p).setAmp(FrameLoop.osc(0.003f, 0f, 0.004f));
		
		///////////////////////
		// Do R/D
		///////////////////////
		for (int i = 0; i < 1; i++) {
			BrightnessStepFilter.instance(p).applyTo(pg);
			DisplacementMapFilter.instance(p).applyTo(pg);
			GrainFilter.instance(p).applyTo(pg);	// add jitter
			BlurHMapFilter.instance(p).applyTo(pg);
			BlurVMapFilter.instance(p).applyTo(pg);
			BlurHMapFilter.instance(p).applyTo(pg);
			BlurVMapFilter.instance(p).applyTo(pg);
			SharpenMapFilter.instance(p).applyTo(pg);
		}
		
		SaturationFilter.instance(p).setSaturation(0f);
		SaturationFilter.instance(p).applyTo(pg);
		ThresholdFilter.instance(p).applyTo(pg);
		pg.endDraw();
		
		///////////////////////
		// Draw to screen
		///////////////////////
		p.image(pg, 0, 0);
		
		///////////////////////
		// Post processing
		///////////////////////
//		FakeLightingFilter.instance(p).applyTo(p.g);
//		EdgesFilter.instance(p).applyTo(p.g);
		FXAAFilter.instance(p).applyTo(p.g);
	}

}
