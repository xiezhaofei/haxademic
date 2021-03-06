package com.haxademic.sketch.shader;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.context.OpenGLUtil;
import com.haxademic.core.draw.filters.pshader.BrightnessFilter;
import com.haxademic.core.draw.filters.pshader.ColorDistortionFilter;
import com.haxademic.core.draw.filters.pshader.ContrastFilter;
import com.haxademic.core.draw.filters.pshader.CubicLensDistortionFilterOscillate;
import com.haxademic.core.draw.filters.pshader.DeformTunnelFanFilter;
import com.haxademic.core.draw.filters.pshader.EdgesFilter;
import com.haxademic.core.draw.filters.pshader.KaleidoFilter;
import com.haxademic.core.draw.filters.pshader.RadialRipplesFilter;
import com.haxademic.core.draw.filters.pshader.SphereDistortionFilter;
import com.haxademic.core.draw.filters.pshader.VignetteFilter;
import com.haxademic.core.file.FileUtil;

import processing.opengl.PShader;

public class ShaderRender
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }


	PShader texShader;
	float _frames = 400;


	protected void config() {
		Config.setProperty( AppSettings.WIDTH, "640" );
		Config.setProperty( AppSettings.HEIGHT, "640" );
		Config.setProperty( AppSettings.RENDERING_MOVIE, false );
		Config.setProperty( AppSettings.RENDERING_MOVIE_STOP_FRAME, Math.round(_frames) );
		Config.setProperty( AppSettings.RENDERING_GIF, false );
		Config.setProperty( AppSettings.RENDERING_GIF_FRAMERATE, "40" );
		Config.setProperty( AppSettings.RENDERING_GIF_QUALITY, "15" );
		Config.setProperty( AppSettings.RENDERING_GIF_START_FRAME, "3" );
		Config.setProperty( AppSettings.RENDERING_GIF_STOP_FRAME, Math.round(_frames) );

	}

	protected void firstFrame() {
	
		p.smooth( OpenGLUtil.SMOOTH_HIGH );

		texShader = loadShader(FileUtil.getPath("haxademic/shaders/textures/square-twist.glsl"));
	}

	protected void drawApp() {
		background(255);
		OpenGLUtil.setTextureRepeat(g);
		
		// rendering progress
		float percentComplete = ((float)(p.frameCount%_frames)/_frames);
		float radsComplete = P.TWO_PI * percentComplete;
		
//		texShader.set("time", 90000 +  P.sin(radsComplete) * 0.20f );
		texShader.set("time", p.frameCount / 40f );
		p.filter(texShader); 
		
//		PixelateFilter.instance(p).setDivider(64f, p.width, p.height);
//		PixelateFilter.instance(p).applyTo(p);
		
		DeformTunnelFanFilter.instance(p).setTime(p.frameCount / 40f);
		DeformTunnelFanFilter.instance(p).applyTo(p);
		
		KaleidoFilter.instance(p).setSides(4);
		KaleidoFilter.instance(p).applyTo(p);
		
		SphereDistortionFilter.instance(p).setTime(P.sin(radsComplete) * 1.70f);
		SphereDistortionFilter.instance(p).applyTo(p);
		
		RadialRipplesFilter.instance(p).setTime(p.frameCount / 140f);
		RadialRipplesFilter.instance(p).setAmplitude(0.5f + 0.5f * P.sin(radsComplete));
		RadialRipplesFilter.instance(p).applyTo(p);

		CubicLensDistortionFilterOscillate.instance(p).setTime(1f + P.sin(radsComplete));
		CubicLensDistortionFilterOscillate.instance(p).applyTo(p);
		
//		DeformBloomFilter.instance(p).setTime(p.frameCount / 40f);
//		DeformBloomFilter.instance(p).applyTo(p);
		
		VignetteFilter.instance(p).setDarkness(1f);
		VignetteFilter.instance(p).applyTo(p);

		BrightnessFilter.instance(p).setBrightness(2f);
		BrightnessFilter.instance(p).applyTo(p);
		ContrastFilter.instance(p).setContrast(2f);
		ContrastFilter.instance(p).applyTo(p);

		EdgesFilter.instance(p).applyTo(p);
		ColorDistortionFilter.instance(p).setTime(p.frameCount / 140f);
		ColorDistortionFilter.instance(p).setAmplitude(0.5f + 0.5f * P.sin(radsComplete));
		ColorDistortionFilter.instance(p).applyTo(p);

//		SaturationFilter.instance(p).setSaturation(0);
//		SaturationFilter.instance(p).applyTo(p);
	}
}

