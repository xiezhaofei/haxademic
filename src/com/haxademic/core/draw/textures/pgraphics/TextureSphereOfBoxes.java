package com.haxademic.core.draw.textures.pgraphics;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.textures.pgraphics.shared.BaseTexture;
import com.haxademic.core.math.MathUtil;

public class TextureSphereOfBoxes
extends BaseTexture {

	int _numAverages = 512;
	int _curMode;
	int _numModes = 6;
	final int MODE_DEFAULT = 0;
	protected Boolean _is_wireframe_mode = false;
	float _r, _g, _b;

	// 2D Array of objects
	Cell[] grid;


	// Number of columns and rows in the grid
	int numPoints = _numAverages;
	int numPointRows;
	float startPhi = 0;
	float startR = P.p.random(0,2* P.PI);
	float startIncR = P.p.random(.0001f,.05f);
	float incR = P.p.random(.0001f,.005f);
	float startG = P.p.random(0,2* P.PI);
	float startIncG = P.p.random(.0001f,.05f);
	float incG = P.p.random(.001f,.005f);
	float startB = P.p.random(0,2* P.PI);
	float startIncB = P.p.random(.0001f,.05f);
	float incB = P.p.random(.001f,.005f);
	int _followIndex;
	float _radMultiplier = 1.5f;
	float _masterAngle = 0;
	
	float _linesOuterAlpha = 0;
	float _linesAlpha = 0;

	
	public TextureSphereOfBoxes( int width, int height ) {
		super();
		buildGraphics( width, height );
		init();
	}
	
	protected void init() {
		numPointRows = P.round( _numAverages / 50 );
		// create cells
		float boxW = _texture.width / numPoints;
		float boxH = _texture.height / numPoints;
		grid = new Cell[numPoints];
		for (int i = 0; i < numPoints; i++) {
			grid[i] = new Cell( boxW, boxH, i );
		}

	}
	
	public void updateDraw() { 
		_texture.background(0);
		_texture.translate(0, 0, -_texture.width * 3f);
		
		// context & camera
		DrawUtil.setBetterLights(_texture);
		DrawUtil.setCenterScreen(_texture);
		DrawUtil.setDrawCenter(_texture);
		
		// put it all in a huge cube
		_texture.fill( 255 );
		_texture.stroke( 255, _linesOuterAlpha );
		_texture.strokeWeight(2);
		_texture.sphere(10);
		_texture.noStroke();
		
		// rotate the sphere - no push/pop since we resetMatrix() every frame... kinda weak
		_masterAngle += 0.01f;
		_texture.rotateX( _masterAngle * .1f );
		
		// fade lines back down
		_linesAlpha = ( _linesAlpha > 0 ) ? _linesAlpha - 0.05f : 0;
		_linesOuterAlpha = ( _linesOuterAlpha > 0.1f ) ? _linesOuterAlpha - 0.05f : 0.1f;
		


		// increment color starting points
		startR += startIncR;
		float curR = startR;
		startG += startIncG;
		float curG = startG;
		startB += startIncB;
		float curB = startB;
		
		// sphere coordinate vars
		int u = 1;
		float theta = 0;
		float phi = 0;
		float thetaIncrement = 1f;
		float phiIncrement = startPhi;
		float pointX = 0;
		float pointY = 0;
		float pointZ = 0;

		// for #5
		int pointInc = 1;
		float deltaPhi =  P.PI/(numPoints-1);
		float dia = P.sqrt(2 - 2*P.cos(deltaPhi));
		float horz_stagger = 0;

		if( _curMode == 5 ) numPoints = _numAverages / 80;
		else if( _curMode == 0 || _curMode == 2 ) numPoints = _numAverages / 3;
		else numPoints = _numAverages;
		
		Cell minus1Cell, minus2Cell;
		
		for( int i = 0; i < numPoints - 1; i++ ) {

			// grab previous particles for line drawing
			minus1Cell = ( i > 0 ) ? grid[i-1] : null;
			minus2Cell = ( i > 1 ) ? grid[i-2] : null;

			// decide whether to follow this point
			Boolean follow = false;
			if( i == _followIndex ) follow = true;

			// draw different per mode
			switch( _curMode )
			{
				case MODE_DEFAULT :

					phi = P.acos( -1f + ( 2f * i - 1f ) / numPoints );
		     		theta = P.sqrt( numPoints * P.PI ) * phi;
		     		pointX = _radMultiplier * P.cos(theta)* P.sin(phi);
		     		pointY = _radMultiplier * P.sin(theta)* P.sin(phi);
		     		pointZ = _radMultiplier * P.cos(phi);
	
					break;
				case 1 :
					//phi = ( i*1. / numPoints ) * (  P.PI * (startB * .1) );
					phi = ( i*1f / numPoints ) * (  P.PI * 2f );
					theta = P.sqrt(numPoints* P.PI)*phi;
	
					pointX = _radMultiplier * P.cos(theta)*P.sin(phi);
					pointY = _radMultiplier * P.sin(theta)*P.cos(phi);
					pointZ = _radMultiplier * P.sin(phi);
	
					break;
				case 2 :
					phi = ( i*1f / numPoints ) * (  P.PI * (startB * .05f) );
					theta = P.sqrt(numPoints* P.PI)*phi;
	
					pointX = _radMultiplier * P.cos(theta)*P.sin(phi);
					pointY = _radMultiplier * P.sin(theta)*P.cos(phi);
					pointZ = _radMultiplier * P.sin(phi);
	
					break;
				case 3 :
					//phi = 0;//sin( startPhi ) * 1;
					phi += phiIncrement;
					theta += thetaIncrement;
	
					pointX = _radMultiplier * P.cos( P.sqrt( phi ) ) * P.cos( theta );
					pointY = _radMultiplier * P.cos( P.sqrt( phi ) ) * P.sin( theta );
					pointZ = _radMultiplier * P.sin( P.sqrt( phi ) );
	
					break;
				case 4 :
					//phi = 0;//sin( startPhi ) * 1;
					phi += phiIncrement;
					theta += thetaIncrement;
	
					pointX = _radMultiplier * P.cos( P.sqrt( phi ) ) * P.cos( theta );
					pointY = _radMultiplier * P.cos( P.sqrt( phi ) ) * P.sin( theta );
					pointZ = _radMultiplier * P.sin( P.sqrt( phi ) );
	
					break;
				case 5 :
					float myPhi = i * deltaPhi;
					float rad = P.sin(myPhi);
					float num_balls;
	
					if( 2f * P.pow(rad,2f) == 0) {
						num_balls = 1;
					} else if( P.acos((2f*P.pow(rad,2f)-P.pow(dia,2f))/(2f*P.pow(rad,2f))) == 0 ) {
						num_balls = 1;
					} else {
						num_balls = P.floor(2f* P.PI / P.acos((2f*P.pow(rad,2f)-P.pow(dia,2f))/(2*P.pow(rad,2f))));
					}
	
					float theta_inc = 2f *  P.PI / num_balls;
	
					for( int m = 0; m < num_balls; m++ )
					{	
						float myTheta = m * theta_inc + horz_stagger;
	
						pointX = rad*_radMultiplier * P.sin(myTheta);
						pointY = P.cos(myPhi)*_radMultiplier;
						pointZ = rad*_radMultiplier * P.cos(myTheta);
	
						pointInc = pointInc + 1;
	
						curR += incR;
						curG += incG;
						curB += incB;
	
						if( i == (int)( _followIndex / 50 )-1 && m == 0 ) follow = true;
						else follow = false;
						
						if( pointInc < _numAverages )
						{
							grid[pointInc].oscillate( curR, curG, curB );
							grid[pointInc].setPosition( pointX, pointY, pointZ, P.p.audioFreq(pointInc), follow, minus1Cell, minus2Cell );
						}
					}
	
					break;
			}

			// post-sphere placement incrementing
			if( _curMode == 3 ) startPhi += .0000001;
			if( _curMode == 4 ) startPhi += .0000001;

			if( _curMode != 5 ) {
				// cycle colors
				grid[i].oscillate( curR, curG, curB );
				grid[i].setPosition( pointX, pointY, pointZ, P.p.audioFreq(i), follow, minus1Cell, minus2Cell );
								
				curR += incR;
				curG += incG;
				curB += incB;
			}
		}
	}
	
	public void newColors() {
		_r = P.p.random( 0, .2f );
		_g = P.p.random( 0, .2f );
		_b = P.p.random( 0, .2f );
		startR = P.p.random( 0, 100 );
		startG = P.p.random( 0, 100 );
		startB = P.p.random( 0, 100 );
		startIncR = P.p.random(.0001f,.05f);
		startIncG = P.p.random(.0001f,.05f);
		startIncB = P.p.random(.0001f,.05f);
		
		_is_wireframe_mode = ( P.p.random(0,1) > 0.7 ) ? true : false;
	}
	
	
	public void updateTiming() {
		_curMode = MathUtil.randRange(0, 5);
	}
	
	public void updateTimingSection() {
		newColors();
	}
	
	public void newLineMode() {
	}
	
	
	class Cell {
		// A cell object knows about its location in the grid as well as its size with the variables x,y,w,h.
		float x,y,z;   // 3d location
		float w,h;   // p.width and p.height
		float angle; // angle for oscillating brightness
		float r,g,b;

		// Cell Constructor
		Cell(float tempW, float tempH, float tempAngle) {
			w = tempW;
			h = tempH;
			angle = 0;

		} 

		// Oscillation means increase angle
		void oscillate( float redColor, float greenColor, float blueColor ) {
			r = redColor;
			g = greenColor;
			b = blueColor;
		}

		void setPosition( float newX, float newY, float newZ, float amp, Boolean follow, Cell minus1cell, Cell minus2cell ) {
			
			// get color
			int cellColor = P.p.color( 127 + 127 *P.sin(r), 127 + 127 *P.sin(g), 127 + 127 *P.cos(b) );
			_texture.fill(cellColor);
			
			// use brightness to push radius out
			float brightAdjust = 1 + P.p.brightness( cellColor ) * .75f;
			brightAdjust = 0.15f;
			x = newX * amp * brightAdjust;
			y = newY * amp * brightAdjust;
			z = newZ * amp * brightAdjust;
			
			// use EQ amplitude to 
			float ampSizeMultiplier = amp * .09f;
			
			// draw line to previous sphere point
			_texture.stroke(cellColor, _linesAlpha);
			_texture.strokeWeight(1*ampSizeMultiplier*.1f);
			if( minus1cell != null && minus2cell != null && _linesAlpha > 0 ) {
				_texture.line(x, y, z, minus1cell.x, minus1cell.y, minus1cell.z);				
				_texture.line(x, y, z, minus2cell.x, minus2cell.y, minus2cell.z);			
			}

			_texture.pushMatrix();
			//			_texture.rect(x,y,w*ampSizeMultiplier,h*ampSizeMultiplier); 
			
			// draw shape at sphere point
			_texture.translate( x, y, z );
			if( _is_wireframe_mode == true ) {
				_texture.stroke(cellColor);
				_texture.noFill();
			} else {
				_texture.fill(cellColor);
				_texture.noStroke();
			}
			float size = 10 + 5 * ampSizeMultiplier * .1f;
			_texture.box(size);
//			gfx.sphere(new Sphere(new Vec3D(0,0,0),size), 10);

			
			
			_texture.popMatrix();
		}
	}

}