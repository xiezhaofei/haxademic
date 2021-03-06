package com.haxademic.demo.draw.shapes;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.shapes.Shapes;
import com.haxademic.core.hardware.mouse.Mouse;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.EasingFloat;
import com.haxademic.core.render.FrameLoop;

public class Demo_Shapes_drawDashedLine_Wave 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected WaveOscillator wave;
	
	protected void config() {
		int FRAMES = 250;
		boolean rendering = false;
		Config.setProperty( AppSettings.WIDTH, 1280 ); // 1140
		Config.setProperty( AppSettings.HEIGHT, 720 );
		Config.setProperty( AppSettings.FULLSCREEN, rendering );
		Config.setProperty( AppSettings.SCREEN_X, 0 );
		Config.setProperty( AppSettings.SCREEN_Y, 0 );
		Config.setProperty( AppSettings.ALWAYS_ON_TOP, false );
		Config.setProperty( AppSettings.LOOP_FRAMES, FRAMES );
		Config.setProperty( AppSettings.RENDERING_MOVIE, rendering );
		Config.setProperty( AppSettings.RENDERING_MOVIE_START_FRAME, (FRAMES * 2) + 1 );
		Config.setProperty( AppSettings.RENDERING_MOVIE_STOP_FRAME, (FRAMES * 8) + 1 );
		Config.setProperty( AppSettings.SMOOTHING, AppSettings.SMOOTH_HIGH );
	}

	protected void firstFrame() {
		wave = new WaveOscillator();
	}
	
	protected void drawApp() {
		// debug input
		DebugView.setValue("Mouse.xNorm", Mouse.xNorm);
		DebugView.setValue("Mouse.yNorm", Mouse.yNorm);

		// context & camera
		p.background(0);
		p.stroke(255);
		
		wave.update(p.height, 0);
		if(FrameLoop.loopCurFrame() == 1) {
			wave.randomize();
		}
	}
	
	public class WaveOscillator {
		
		protected EasingFloat spacing = new EasingFloat(1, 0.1f);
		protected EasingFloat scrollMult = new EasingFloat(0, 0.1f);
		protected EasingFloat ampOscSpeed = new EasingFloat(0, 0.1f);
		protected EasingFloat freqBase = new EasingFloat(0, 0.1f);
		protected EasingFloat freqBase2 = new EasingFloat(0, 0.1f);
		protected EasingFloat freqBase3 = new EasingFloat(0, 0.1f);
		protected EasingFloat freqMultRange = new EasingFloat(0, 0.1f);
		
		public WaveOscillator() {
			randomize();
		}
		
		public void randomize() {
			// 20, 3, 0.15f, 0.0025f, 0.002f
			spacing.setTarget(MathUtil.randRangeDecimal(8, 16));
			scrollMult.setTarget(MathUtil.randRange(1, 2));
			ampOscSpeed.setTarget(MathUtil.randRangeDecimal(0.05f, 0.4f));
			freqBase.setTarget(MathUtil.randRangeDecimal(0.0005f, 0.005f));
			freqBase2.setTarget(MathUtil.randRangeDecimal(0.005f, 0.015f));
			freqBase3.setTarget(MathUtil.randRangeDecimal(0.005f, 0.015f));
			freqMultRange.setTarget(MathUtil.randRangeDecimal(0.001f, 0.8f));

			spacing.setCompleteThreshold(0.000001f);
			scrollMult.setCompleteThreshold(0.000001f);
			ampOscSpeed.setCompleteThreshold(0.000001f);
			freqBase.setCompleteThreshold(0.000001f);
			freqBase2.setCompleteThreshold(0.000001f);
			freqBase3.setCompleteThreshold(0.000001f);
			freqMultRange.setCompleteThreshold(0.000001f);
		}
		
		public void update(float rowHeight, float i) {
			spacing.update(true);
			scrollMult.update(true);
			ampOscSpeed.update(true);
			freqBase.update(true);
			freqBase2.update(true);
			freqBase3.update(true);
			freqMultRange.update(true);

			float waveAmp = rowHeight * 0.4f; // * (0.25f + ampOscSpeed * P.sin(AnimationLoop.progressRads()));
			float waveFreqMult = freqBase.value() + freqMultRange.value();// * P.sin(AnimationLoop.progressRads());
//			float waveFreqMult2 = freqBase2.value() + freqMultRange.value();// * P.sin(AnimationLoop.progressRads());
//			float waveFreqMult3 = freqBase3.value() + freqMultRange.value();// * P.sin(AnimationLoop.progressRads());

			float centerY = rowHeight * i + rowHeight / 2f;
			for (int x = 0; x < p.width; x += spacing.value()) {
				float oscValue = P.sin(FrameLoop.progressRads() * scrollMult.value() + x * waveFreqMult);
//				float oscValue2 = P.sin(AnimationLoop.progressRads() * scrollMult.value() + p.noise(x * waveFreqMult2));
//				float oscValue3 = P.sin(AnimationLoop.progressRads() * scrollMult.value() + p.noise(x * waveFreqMult3));
//				oscValue *= oscValue2;
//				oscValue *= oscValue3;
				float waveY = centerY + oscValue * waveAmp;
//				p.point(x, waveY);
				Shapes.drawDashedLine(p.g, x, centerY, 0, x, waveY, 0, 10, false);
//				p.line(x, centerY, 0, x, waveY, 0);
			}
		}
		
	}
		
}