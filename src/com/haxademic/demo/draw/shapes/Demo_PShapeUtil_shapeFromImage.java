package com.haxademic.demo.draw.shapes;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.media.DemoAssets;

import processing.core.PShape;

public class Demo_PShapeUtil_shapeFromImage 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PShape shape;
	
	protected void config() {
		Config.setProperty( AppSettings.WIDTH, 1200 );
		Config.setProperty( AppSettings.HEIGHT, 900 );
	}
	
	protected void firstFrame() {
		shape = PShapeUtil.shapeFromImage(DemoAssets.textureCursor());
		PShapeUtil.centerShape(shape);
		PShapeUtil.scaleVertices(shape, 1, 1, 4);
		PShapeUtil.scaleShapeToExtent(shape, p.height * 0.2f);
	}
	
	protected void drawApp() {
		p.background(200, 255, 200);
		PG.setCenterScreen(p);
		PG.setBetterLights(p);
		PG.basicCameraFromMouse(p.g);
		p.ortho();

		// draw
		p.shape(shape);
	}
}