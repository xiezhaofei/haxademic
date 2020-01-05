package com.haxademic.demo.media.video;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.media.video.MovieBuffer;

public class Demo_MovieBuffer 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected MovieBuffer movieBuffer;
	
	protected void firstFrame() {
//		movieBuffer = new MovieBuffer(DemoAssets.movieFractalCube());
//		movieBuffer = new MovieBuffer(FileUtil.getPath(DemoAssets.movieFractalCubePath));
		movieBuffer = new MovieBuffer("D:\\workspace\\cobra-2020-pga-show-sculpture\\data\\video\\video-1.mp4");
		movieBuffer.movie.loop();
	}

	protected void drawApp() {
		p.background(0);
		if(movieBuffer.buffer != null) {
			// ImageUtil.cropFillCopyImage(movieBuffer.buffer, p.g, false);
//			p.image(movieBuffer.buffer, 0, 0);
			p.image(movieBuffer.movie, 0, 0);
		}
	}
	
}
