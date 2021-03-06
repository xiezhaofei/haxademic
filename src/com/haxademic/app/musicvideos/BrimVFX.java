package com.haxademic.app.musicvideos;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.constants.PRenderers;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.filters.pgraphics.PixelTriFilter;
import com.haxademic.core.draw.filters.pgraphics.shared.BaseVideoFilter;
import com.haxademic.core.draw.filters.pshader.ContrastFilter;
import com.haxademic.core.draw.image.ImageUtil;

import processing.core.PGraphics;
import processing.video.Movie;

public class BrimVFX 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected float w = 1280;
	protected float h = 720;
	protected BaseVideoFilter vfxPre;
	protected BaseVideoFilter vfx;
	
//	protected int webcamW = 640;
//	protected int webcamH = 480;
	protected PGraphics videoBuffer;
	protected Movie video;
	
	protected boolean frameDirty = false;
	
	protected float videoLength = 0;
	protected float videoFrame = 0;
	protected float videoNumFrames = 0;
	protected int fpss = 24;

	protected void config() {
		Config.setProperty(AppSettings.RENDERING_MOVIE, true);
		Config.setProperty(AppSettings.FULLSCREEN, false);
		if(Config.getBoolean(AppSettings.RENDERING_MOVIE, false) == true) {
			w = 1920;
			h = 1080;
		}
		Config.setProperty(AppSettings.WIDTH, (int) w);
		Config.setProperty(AppSettings.HEIGHT, (int) h);
		Config.setProperty(AppSettings.FPS, fpss);
		Config.setProperty(AppSettings.ALWAYS_ON_TOP, false);
		Config.setProperty(AppSettings.RENDERING_MOVIE_START_FRAME, 1);
		Config.setProperty(AppSettings.RENDERING_MOVIE_STOP_FRAME, 2131);
	}

	protected void firstFrame() {
//		vfx = new GPUParticlesSheetDisplacer(p.width, p.height, 0.5f);
		vfx = new PixelTriFilter(p.width, p.height, p.height / 20);
//		vfx = new ColorDiff8BitRows(p.width, p.height, 20);
//		vfx = new TileRepeat(p.width, p.height);
//		vfx = new BlobLinesFeedback(p.width, p.height);
//		vfx = new HalftoneCamo(p.width, p.height);
//		vfx = new RadialHistory(p.width, p.height);
//		vfxPre = new GPUParticlesLauncher(p.width, p.height);
//		vfx = new SmokeFeedback(p.width, p.height);
		
		// build video buffer for frame processing
		videoBuffer = p.createGraphics((int) w, (int) h, PRenderers.P2D);
		
		// load video 
		String videoPath = "/Users/cacheflowe/Downloads/BrimLiski_Duels_RC01_FirstMinutePt02.mov";
		video = new Movie(p, videoPath);
		video.play();
	}

	protected void drawApp() {
		background(0);
		
			if(videoLength == 0) {
				videoLength = video.duration();
				videoNumFrames = videoLength * fpss;
				DebugView.setValue("videoLength", videoLength);
				DebugView.setValue("videoNumFrames", videoNumFrames);
			}
			
			if(video.width > 20) {
				ImageUtil.cropFillCopyImage(video, videoBuffer, true);
				ContrastFilter.instance(p).setContrast(2.0f);
	//			ContrastFilter.instance(p).applyTo(videoBuffer);
			}
			
			if(vfxPre != null) {
				// chain it up
				vfxPre.newFrame(videoBuffer);
			} else {
				// send new webcam mirror frame to vfx
				vfx.newFrame(videoBuffer);
			}
			frameDirty = false;
			
			if(Config.getBoolean(AppSettings.RENDERING_MOVIE, false) == true) {
				// manually step to next video frame
				videoFrame++;
				video.play();
				video.jump(videoFrame / fpss);
				video.pause();
				DebugView.setValue("video time", videoFrame / fpss);
				DebugView.setValue("last video frame", p.frameCount);
			}
		
		if(vfxPre != null) {
			vfxPre.update();
			vfx.newFrame(vfxPre.image());
			vfx.update();
		} else {
			vfx.update();
		}
		
		// bloom
		pg.beginDraw();
		pg.image(vfx.image(), 0, 0);
		pg.endDraw();
//		BloomFilter.instance(p).setStrength(1f);
//		BloomFilter.instance(p).applyTo(pg);
		
		// draw to screen
		p.image(pg, 0, 0);
	}

	public void movieEvent(Movie m) {
		super.movieEvent(m);
		
//		P.out(p.frameCount);
		frameDirty = true;
	}

}

