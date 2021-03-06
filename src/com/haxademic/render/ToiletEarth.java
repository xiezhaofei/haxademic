package com.haxademic.render;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.camera.CameraUtil;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.render.FrameLoop;

import processing.core.PShape;

public class ToiletEarth 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PShape objEarth;
	protected PShape objToilet;
	protected PShape objTP;
	protected int FRAMES = 300;
	
	protected void config() {
		Config.setProperty( AppSettings.LOOP_FRAMES, FRAMES );
		Config.setProperty( AppSettings.WIDTH, 1024 );
		Config.setProperty( AppSettings.HEIGHT, 1024 );
		Config.setProperty( AppSettings.RENDERING_MOVIE, false );
		Config.setProperty( AppSettings.RENDERING_MOVIE_START_FRAME, 1 );
		Config.setProperty( AppSettings.RENDERING_MOVIE_STOP_FRAME, P.round(1 + FRAMES) );
	}

	protected void firstFrame() {
		// earth
		objEarth = p.loadShape( FileUtil.getPath("models/poly/earth/Earth.obj"));
		PShapeUtil.centerShape(objEarth);
		PShapeUtil.meshFlipOnAxis(objEarth, P.Y);
		PShapeUtil.scaleShapeToHeight(objEarth, p.height * 0.5f);

		// toilet
		objToilet = p.loadShape( FileUtil.getPath("models/poly/toilet/Toilet_01.obj"));
		PShapeUtil.centerShape(objToilet);
		PShapeUtil.meshFlipOnAxis(objToilet, P.Y);
		PShapeUtil.scaleShapeToHeight(objToilet, p.height * 1.3f);
		PShapeUtil.meshRotateOnAxis(objToilet, -P.HALF_PI, P.Y);

		// ptp
		objTP = p.loadShape( FileUtil.getPath("models/poly/toilet-paper/toilet-paper.obj"));
		PShapeUtil.centerShape(objTP);
		PShapeUtil.scaleShapeToHeight(objTP, p.height * 0.1f);
	}

	protected void drawApp() {
		p.background(255, 220, 180);
		p.noStroke();
		CameraUtil.setCameraDistance(p.g, 100, 20000);

		p.ortho();
//		p.perspective();

		// setup lights
		PG.setDrawCorner(p);
		PG.setCenterScreen(p);
		p.translate(0, 0, -p.width * 0.75f);
		PG.setBasicLights(p);
		
		// camera
		p.noStroke();
//		p.rotateX(P.map(Mouse.xNorm, 0, 1, -2, 2));
		p.rotateX(-0.5f + 0.05f * P.sin(FrameLoop.progressRads()));
		p.rotateY(-0.75f + 0.1f * P.sin(FrameLoop.progressRads()));
		p.scale(0.5f);
		// earth
		p.push();
		p.translate(0, -p.width * 0.1f + P.sin(FrameLoop.progressRads() * 3f) * p.width * 0.02f, p.width * 0.08f);
		p.rotateY(-FrameLoop.progressRads() * 2f);
		p.shape(objEarth);
		p.pop();
		
		// toilet
		p.shape(objToilet);	
		
		// tp
		p.push();
//		p.rotateX(P.HALF_PI);
		p.translate(p.width * 0.25f, -p.height * 0.7f, -p.width * 0.42f);
		p.rotateY(P.HALF_PI);
		p.shape(objTP);
		p.pop();
		
		
		// water
		PG.setDrawCenter(p);
		p.push();
		p.rotateX(P.HALF_PI);
		p.fill(97, 149, 185);
		p.rect(0, 0, p.width * 0.6f, p.width * 0.75f);
		p.pop();
	}

}