package com.haxademic.demo.draw.shapes.shader;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.shapes.Shapes;
import com.haxademic.core.file.FileUtil;

import processing.opengl.PShader;

public class Demo_LightShaderAndToon
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PShader lightShaderBasic;
	protected PShader toonShader;
	protected boolean isToon = false;

	public void setupFirstFrame() {
		lightShaderBasic = p.loadShader(FileUtil.getFile("haxademic/shaders/lights/lightfrag/lightfrag.glsl"), FileUtil.getFile("haxademic/shaders/lights/lightfrag/lightvert.glsl"));
		toonShader = p.loadShader(FileUtil.getFile("haxademic/shaders/lights/toon/frag.glsl"), FileUtil.getFile("haxademic/shaders/lights/toon/vert.glsl"));
		toonShader.set("fraction", 4.0f);
	}

	public void drawApp() {
		p.background(0);

		isToon = (p.mousePercentX() > 0.5f);
		if( isToon == false ) {
			p.shader(lightShaderBasic);
			p.pointLight(0, 255, 255, p.mouseX, p.mouseY, 500);
		} else {
			p.shader(toonShader);
			float dirY = (mouseY / (float)height - 0.5f) * 2f;
			float dirX = (mouseX / (float)width - 0.5f) * 2f;
			p.directionalLight(204, 204, 204, -dirX, -dirY, -1);
		}
		
		// draw shapes
		p.noStroke();
		p.translate(p.width/2, p.height/2, 0);
		p.rotateY(p.frameCount * 0.01f);  
		p.sphereDetail(300);
		Shapes.drawStar(p, 8, 150, 0.5f, 100, 0);
		p.translate(300, 0);
		p.sphere(200);  
	}

}
