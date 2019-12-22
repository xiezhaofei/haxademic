package com.haxademic.core.ui;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

import com.haxademic.core.app.P;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.constants.PRegisterableMethods;
import com.haxademic.core.data.constants.PRenderers;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.hardware.keyboard.KeyboardState;
import com.haxademic.core.net.UIControlsHandler;
import com.haxademic.core.net.WebServer;
import com.haxademic.core.ui.UIButton.IUIButtonDelegate;

import processing.data.JSONArray;
import processing.data.JSONObject;

public class UI
implements IUIButtonDelegate {

	protected static LinkedHashMap<String, IUIControl> controls;
	
	public static int controlX = 1;
	protected static int controlY = 1;
	public static final int controlW = 250;
	public static final int controlH = 24;
	public static final int controlSpacing = 24;
	protected static float controlSpacingH = 0;

	protected static boolean active = false;

	protected static WebServer server;
	public static final String KEY_CONTROLS = "ui_controls";
	public static final String KEY_TYPE = "type";
	public static final String KEY_ID = "id";
	public static final String KEY_VALUE = "value";
	public static final String KEY_VALUE_MIN = "value_low";
	public static final String KEY_VALUE_MAX = "value_high";
	public static final String KEY_VALUE_STEP = "value_step";
	public static final String KEY_VALUE_TOGGLES = "value_toggles";
	public static final String KEY_VALUE_LAYOUT_W = "layout_width";
	
	// Singleton instance
	
	public static UI instance;
	
	public static UI instance() {
		if(instance != null) return instance;
		instance = new UI();
		return instance;
	}
	
	// Constructor

	public UI() {
		active = Config.getBoolean(AppSettings.SHOW_UI, false);
		controls = new LinkedHashMap<String, IUIControl>();
		P.p.registerMethod(PRegisterableMethods.pre, this);
		P.p.registerMethod(PRegisterableMethods.post, this);
	}
	
	////////////////////////
	// ADD web controls
	////////////////////////
	
	public static void addWebInterface(boolean debugWebRequests) {
		if(server != null) return;
		server = new WebServer(new UIControlsHandler(), debugWebRequests);
	}
	
	////////////////////////
	// ADD controls
	////////////////////////
	
	public static void addTitle(String title) {
		controls.put(title, new UITitle(title, controlX, controlY, controlW, controlH));
		controlY += controlH;
		if(controlY > P.p.height - controlH) nextCol();
	}
	
	public static void addToggle(String key, boolean value, boolean saves) {
		int valInt = (value == true) ? 1 : 0; 
		addSlider(key, valInt, 0, 1, 1, saves);
	}
	
	public static void addSlider(String key, float value, float valueLow, float valueHigh, float dragStep) {
		addSlider(key, value, valueLow, valueHigh, dragStep, true);
	}
	
	public static void addSlider(String key, float value, float valueLow, float valueHigh, float dragStep, boolean saves) {
		addSlider(key, value, valueLow, valueHigh, dragStep, true, -1);
	}
	
	public static void addSlider(String key, float value, float valueLow, float valueHigh, float dragStep, boolean saves, int midiCCNote) {
		controls.put(key, new UISlider(key, value, valueLow, valueHigh, dragStep, controlX, controlY, controlW, controlH, saves, midiCCNote));
		controlY += controlH;
		if(controlY > P.p.height - controlH) nextCol();
	}
	
	public static void addSliderVector(String key, float value, float valueLow, float valueHigh, float dragStep, boolean saves) {
		addSliderVector(key, value, valueLow, valueHigh, dragStep, saves, -1, -1, -1);
	}
	
	public static void addSliderVector(String key, float value, float valueLow, float valueHigh, float dragStep, boolean saves, int midiCCNote1, int midiCCNote2, int midiCCNote3) {
		float controlWidthDivided = (float) controlW / 3f;
		int controlHStack = P.round(controlH * 1.5f);
		controls.put(key + "_X", new UISlider(key + "_X", value, valueLow, valueHigh, dragStep, P.round(controlX + 0 * controlWidthDivided), controlY, P.round(controlWidthDivided), controlHStack, saves, midiCCNote1));
		controls.put(key + "_Y", new UISlider(key + "_Y", value, valueLow, valueHigh, dragStep, P.round(controlX + 1 * controlWidthDivided), controlY, P.round(controlWidthDivided), controlHStack, saves, midiCCNote2));
		controls.put(key + "_Z", new UISlider(key + "_Z", value, valueLow, valueHigh, dragStep, P.round(controlX + 2 * controlWidthDivided), controlY, P.round(controlWidthDivided), controlHStack, saves, midiCCNote3));
		controls.get(key + "_X").layoutW(0.3333f);
		controls.get(key + "_Y").layoutW(0.3333f);
		controls.get(key + "_Z").layoutW(0.3333f);
		controlY += controlHStack;
		if(controlY > P.p.height - controlHStack) nextCol();
	}
	
	protected static void nextCol() {
		controlY = 1;
		controlX += controlW + 1;
	}
	
	public static void removeControl(String key) {
		controls.remove(key);
	}
	
	////////////////////////
	// ADD BUTTONS
	////////////////////////
	
	public static void addButton(String key, boolean toggles) {
		addButton(key, toggles, -1);
	}
	
	public static void addButton(String key, boolean toggles, int midiNote) {
		controls.put(key, new UIButton(instance, key, controlX, controlY, controlW, controlH, toggles, midiNote));
		controlY += controlSpacing;
	}
	
	public static void addButtons(String[] keys, boolean toggles) {
		addButtons(keys, toggles, null);
	}
	
	public static void addButtons(String[] keys, boolean toggles, int[] midiNotes) {
		float layoutW = 1f / keys.length;
		float controlWidthDivided = (controlW - controlSpacingH * (keys.length - 1)) / keys.length;
		for (int i = 0; i < keys.length; i++) {
			int buttonX = P.round(controlX + i * controlWidthDivided + controlSpacingH * i);
			int midiNote = (midiNotes != null && midiNotes.length > i) ? midiNotes[i] : -1;
			UIButton newButton = new UIButton(instance, keys[i], buttonX, controlY, P.round(controlWidthDivided), controlH, toggles, midiNote);
			newButton.layoutW(layoutW);
			controls.put(keys[i], newButton);
		}
		controlY += controlSpacing;
	}
	
	////////////////////////
	// GET/SET VALUES
	////////////////////////
	
	public static IUIControl get(String key) {
		return controls.get(key);
	}

	public static boolean has(String key) {
		return controls.containsKey(key);
	}
	
	public static void setValue(String key, float val) {
		controls.get(key).set(val);
	}
	
	public static float value(String key) {
		return controls.get(key).value();
	}
	
	public static float valueEased(String key) {
		return controls.get(key).valueEased();
	}
	
	public static int valueInt(String key) {
		return P.round(controls.get(key).value());
	}
	
	public static boolean valueToggle(String key) {
		return P.round(controls.get(key).value()) == 1;
	}
	
	public static float valueX(String key) {
		return controls.get(key+"_X").value();
	}
	
	public static float valueY(String key) {
		return controls.get(key+"_Y").value();
	}
	
	public static float valueZ(String key) {
		return controls.get(key+"_Z").value();
	}
	
	public static float valueXEased(String key) {
		return controls.get(key+"_X").valueEased();
	}
	
	public static float valueYEased(String key) {
		return controls.get(key+"_Y").valueEased();
	}
	
	public static float valueZEased(String key) {
		return controls.get(key+"_Z").valueEased();
	}
	
	////////////////////////
	// DRAW/ACTIVATE/DEACTIVATE
	////////////////////////
	
	public void pre() {
		checkKeyCommands();
		// update control values whether UI is showing or not 
		for (IUIControl control : controls.values()) control.update();
	}
	
	public void post() {
		// draw if UI is active
		if(active && P.renderer != PRenderers.PDF) {
			PG.setDrawFlat2d(P.p.g, true);
			P.p.g.noLights();
			for (IUIControl control : controls.values()) {
				control.draw(P.p.g);
			}
			PG.setDrawFlat2d(P.p.g, false);
		}
	}

	public static void active(boolean val) {
		active = val;
	}

	public static boolean active() {
		return active;
	}
	
	////////////////////////
	// Key commands
	////////////////////////
	
	public void checkKeyCommands() {
		if(KeyboardState.instance().isKeyTriggered('\\')) active = !active;
		if(KeyboardState.instance().isKeyTriggered('/')) active = false;
	}
	
	////////////////////////
	// EXPORT
	////////////////////////
	
	public static String configToJSON() {
		// build JSON array
		JSONArray array = new JSONArray();
		for (HashMap.Entry<String, IUIControl> entry : controls.entrySet()) {	// With LinkedHashMap, keys are in order
			// String key = entry.getKey();
			IUIControl control = entry.getValue();

			JSONObject controlJson = new JSONObject();
			controlJson.setString(KEY_TYPE, control.type());
			controlJson.setString(KEY_ID, control.id());
			controlJson.setFloat(KEY_VALUE, control.value());
			controlJson.setFloat(KEY_VALUE_MIN, control.valueMin());
			controlJson.setFloat(KEY_VALUE_MAX, control.valueMax());
			controlJson.setFloat(KEY_VALUE_STEP, control.step());
			controlJson.setFloat(KEY_VALUE_TOGGLES, control.toggles());
			controlJson.setFloat(KEY_VALUE_LAYOUT_W, control.layoutW());
			array.append(controlJson);
		}
		JSONObject outerJsonObject = new JSONObject();
		outerJsonObject.setJSONArray(KEY_CONTROLS, array);
		return outerJsonObject.toString();
	}
	
	public static String valuesToJSON() {
		JSONObject json = new JSONObject();
		for (IUIControl control : controls.values()) {
			json.setFloat(control.id(), control.value());
		}
		return json.toString();
	}
	
	public static void loadValuesFromJSON(JSONObject jsonData) {
//		P.out(jsonData.toString());
//		P.out(JsonUtil.isValid(jsonData.toString()));
		Iterator<?> iterator = jsonData.keys().iterator();
		while(iterator.hasNext()) {
		    String key = (String) iterator.next();
	    	controls.get(key).set(jsonData.getFloat(key));
	    	// P.out(key, jsonData.getFloat(key));
		}
	}

	////////////////////////
	// IUIButtonDelegate
	////////////////////////
	
	public void clicked(UIButton button) {
		P.p.uiButtonClicked(button);
	}
	
}