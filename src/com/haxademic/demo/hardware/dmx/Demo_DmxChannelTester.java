package com.haxademic.demo.hardware.dmx;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.data.constants.PTextAlign;
import com.haxademic.core.draw.text.FontCacher;
import com.haxademic.core.hardware.dmx.DMXWrapper;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.media.audio.analysis.AudioIn;

public class Demo_DmxChannelTester
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected DMXWrapper dmx;

	protected int value = 0;
	
	enum DMXTestmode {
		SINGLE_CHANNEL,
		RGB,
		ALL,
		NONE,
	}
	DMXTestmode[] modes = new DMXTestmode[] {
		DMXTestmode.RGB, 
		DMXTestmode.SINGLE_CHANNEL, 
		DMXTestmode.ALL, 
		DMXTestmode.NONE
	};
	
	protected String R = "R";
	protected String G = "G";
	protected String B = "B";
	protected String CHANNEL = "CHANNEL";
	protected String MODE = "MODE";
	protected String AUDIOREACTIVE = "AUDIOREACTIVE";
	protected String BOOLEAN_MODE = "BOOLEAN_MODE";
	protected String BRIGHTNESS_CAP = "BRIGHTNESS_CAP";
	protected String MANUAL_BRIGHTNESS = "MANUAL_BRIGHTNESS";
	
	protected void overridePropsFile() {
		p.appConfig.setProperty(AppSettings.SHOW_SLIDERS, true );
//		p.appConfig.setProperty(AppSettings.INIT_ESS_AUDIO, false );
	}

	public void setupFirstFrame() {
		AudioIn.instance();
		// dmx = new DMXWrapper();
		dmx = new DMXWrapper("COM3", 9600);
		
		// ui
		p.ui.addSlider(CHANNEL, 1, 1, 512, 0.25f, false);
		p.ui.addSlider(MODE, 0, 0, 3, 0.25f, false);
		p.ui.addSlider(R, 100, 0, 255, 1, false);
		p.ui.addSlider(G, 100, 0, 255, 1, false);
		p.ui.addSlider(B, 100, 0, 255, 1, false);
		p.ui.addSlider(MANUAL_BRIGHTNESS, 0, 0, 1, 1, false);
		p.ui.addSlider(BRIGHTNESS_CAP, 255, 0, 255, 1, false);
		p.ui.addSlider(AUDIOREACTIVE, 0, 0, 1, 1, false);
		p.ui.addSlider(BOOLEAN_MODE, 0, 0, 1, 1, false);
	}
	
	protected void addKeyCommandInfo() {
		super.addKeyCommandInfo();
		p.debugView.setHelpLine("__ Key Commands", "__\n");
		p.debugView.setHelpLine("SPACE |", "Reset all");
	}

	public void drawApp() {
		background(0);
		FontCacher.setFontOnContext(p.g, FontCacher.getFont(DemoAssets.fontOpenSansPath, 40), p.color(255), 1f, PTextAlign.LEFT, PTextAlign.TOP);
		
		int startChannel = p.ui.valueInt(CHANNEL);
		DMXTestmode testMode = modes[p.ui.valueInt(MODE)];

		// choose channel with mouse
//		if(p.mouseX != p.pmouseX) {
//			startChannel = 1 + P.round(Mouse.xNorm * 512);
//		}
		
		// oscillate by default
		float freq = 0.1f;
		int valueR = P.round(127 + 127 * P.sin(p.frameCount * freq));
		int valueG = P.round(127 + 127 * P.sin(p.frameCount * freq + P.HALF_PI));
		int valueB = P.round(127 + 127 * P.sin(p.frameCount * freq + P.PI));
		
		// manual control
		if(p.ui.valueInt(MANUAL_BRIGHTNESS) == 1) {
			valueR = p.ui.valueInt(R);
			valueG = p.ui.valueInt(G);
			valueB = p.ui.valueInt(B);
		}
		
		if(p.ui.valueInt(AUDIOREACTIVE) == 1) {
			valueR = P.round(AudioIn.audioFreq(10) * 255);
			valueG = P.round(AudioIn.audioFreq(20) * 255);
			valueB = P.round(AudioIn.audioFreq(30) * 255);
		}
		
		// temp: brightness cap
		if(p.ui.valueInt(BRIGHTNESS_CAP) < 255) {
			valueR = P.constrain(valueR, 0, p.ui.valueInt(BRIGHTNESS_CAP));
			valueG = P.constrain(valueG, 0, p.ui.valueInt(BRIGHTNESS_CAP));
			valueB = P.constrain(valueB, 0, p.ui.valueInt(BRIGHTNESS_CAP));
		}
		
		if(p.ui.valueInt(BOOLEAN_MODE) == 1) {
			valueR = (valueR >= 127) ? 255 : 0;
			valueG = (valueG >= 127) ? 255 : 0;
			valueB = (valueB >= 127) ? 255 : 0;
		}
		
		// debug info
		String debugInfo = "Mode: " + testMode + "\n\n";
		
		// do all channels at once
		switch (testMode) {
			case SINGLE_CHANNEL:
				dmx.setValue(startChannel, valueR);
				debugInfo += "Channel: " + startChannel + "\n";
				debugInfo += "Value: " + valueR;
				break;
			case RGB:
				dmx.setValue(startChannel + 0, valueR);
				dmx.setValue(startChannel + 1, valueG);
				dmx.setValue(startChannel + 2, valueB);
				debugInfo += "ChannelR: " + startChannel + "\n";
				debugInfo += "ValueR: " + valueR + "\n\n";
				debugInfo += "ChannelG: " + (startChannel + 1) + "\n";
				debugInfo += "ValueG: " + valueG + "\n\n";
				debugInfo += "ChannelB: " + (startChannel + 2) + "\n";
				debugInfo += "ValueB: " + valueB + "\n\n";
				break;
			case ALL:
				for (int i = 1; i < 512; i++) {
					dmx.setValue(i, valueR);
				}
				debugInfo += "Channels 1 - " + dmx.universeSize() + "\n";
				debugInfo += "Value: " + valueR + "\n";
				break;
			case NONE:
				debugInfo += "NONE";
				break;
			default:
				break;
		}

		// show dmx channel
		p.fill(255f);
		p.text(debugInfo, 300, 40, p.width, p.height);
		
		// draw color at bottom for debug
		p.fill(valueR, valueG, valueB);
		p.rect(0, 0, 252, p.height);
	}
	
	protected void resetAllChannels() {
		for (int i = 1; i < 512; i++) {
			dmx.setValue(i, 0);
		}
	}
	
	public void keyPressed() {
		super.keyPressed();
		if(p.key == ' ') resetAllChannels();
	}
}
