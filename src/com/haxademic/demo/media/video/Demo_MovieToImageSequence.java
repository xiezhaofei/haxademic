package com.haxademic.demo.media.video;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.image.ImageSequenceMovieClip;
import com.haxademic.core.hardware.mouse.Mouse;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.media.video.MovieToImageSequence;

public class Demo_MovieToImageSequence 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected MovieToImageSequence movieToImageSequence;
	protected ImageSequenceMovieClip movieClip;
	
	protected void config() {
		Config.setProperty(AppSettings.WIDTH, 1400 );
		Config.setProperty(AppSettings.HEIGHT, 700 );
		Config.setProperty(AppSettings.SHOW_DEBUG, true );
	}
		
	protected void firstFrame () {
		movieToImageSequence = new MovieToImageSequence(DemoAssets.movieFractalCube(), 0.5f);
	}
	
	protected void convertToMoviePlayer() {
		movieClip = new ImageSequenceMovieClip(movieToImageSequence.imageSequence(), 30);
		movieClip.loop();
//		movieClip.pause();
	}
	
	protected void drawApp() {
		movieToImageSequence.update();
		if(movieClip == null && movieToImageSequence.complete()) convertToMoviePlayer();
		DebugView.setValue("movie convert progress", movieToImageSequence.progress());
		
		// show relatime comverstion
		if(movieToImageSequence.imageSequence() != null) {
			int sequenceFrame = p.frameCount % movieToImageSequence.imageSequence().size();
			p.image(movieToImageSequence.imageSequence().get(sequenceFrame), 0, 0);
		}
		
		// show looped movieclip after capture
		if(movieClip != null) {
			movieClip.update();
			movieClip.setFrameByProgress(Mouse.xNorm * 4f);
			p.image(movieClip.image(), 700, 0);
		}
	}
	
}

