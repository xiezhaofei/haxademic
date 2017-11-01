package com.haxademic.sketch.texture;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.draw.filters.shaders.ContrastFilter;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.shapes.Icosahedron;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.file.FileUtil;

import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PShape;
import processing.opengl.PShader;

public class SphereDeformByTextureBrightness 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PShape shapeIcos;
	protected PImage texture;
	protected float _frames = 360;
	protected PShader texShader;

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 1000 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 800 );
		p.appConfig.setProperty( AppSettings.FILLS_SCREEN, false );
	}

	public void setup() {
		super.setup();	

		// load texture
		texture = ImageUtil.imageToGraphics(p.loadImage(FileUtil.getFile("images/moon.png")));
		ContrastFilter.instance(p).setContrast(3);
		ContrastFilter.instance(p).applyTo((PGraphics)texture);
		
		// create icosahedron
		shapeIcos = Icosahedron.createIcosahedron(p.g, 7, texture);
		PShapeUtil.scaleSvgToExtent_DEPRECATE(shapeIcos, p.height/4f);
		
		// sphere deformation shader. uses the sphere's texture as the displacement map
		texShader = loadShader(
			FileUtil.getFile("shaders/vertex/brightness-displace-frag-texture.glsl"), 
			FileUtil.getFile("shaders/vertex/brightness-displace-sphere-vert.glsl")
		);
		texShader.set("displacementMap", texture);
		texShader.set("displaceStrength", 0.3f);
		
	}

	public void drawApp() {
		background(0);
		
		// loop progress
		float percentComplete = ((float)(p.frameCount%_frames)/_frames);
		
		// draw icosahedron
		p.pushMatrix();
		p.translate(p.width/2f, p.height/2f);
		p.rotateY(percentComplete * P.TWO_PI);
		p.rotateZ(0.05f + 0.05f * P.sin(-P.PI/2f + P.TWO_PI * percentComplete));
		
		// apply vertex shader & draw icosahedron
		texShader.set("displaceStrength", 0.3f + 0.3f * P.sin(-P.PI/2f + P.TWO_PI * percentComplete));
		p.shader(texShader);  
		p.shape(shapeIcos);
		p.resetShader();
		p.popMatrix();
	}
		
}