package com.haxademic.demo.hardware.artnet;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.filters.pshader.ContrastFilter;
import com.haxademic.core.draw.textures.SimplexNoise3dTexture;
import com.haxademic.core.render.FrameLoop;

public class Demo_ArtNetLedStrip
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected SimplexNoise3dTexture noise3d;
	protected ArtNetLedStrip strip;

	protected void firstFrame() {
		noise3d = new SimplexNoise3dTexture(p.width, p.height);
		strip = new ArtNetLedStrip("192.168.1.100", 0, 100);
	}

	protected void drawApp() {
		// update noise map
		noise3d.offsetZ(p.frameCount / 10f);
		noise3d.update(2f, 0, FrameLoop.count(0.01f), 0, 0, false);
		ContrastFilter.instance(p).setContrast(3f);
		ContrastFilter.instance(p).applyTo(noise3d.texture());
		
		// draw to screen
		p.image(noise3d.texture(), 0, 0);
		
		// update artnet strip
		strip.update(noise3d.texture(), 1, 1);
	}
}