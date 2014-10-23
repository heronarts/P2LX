/**
 * Copyright 2013- Mark C. Slee, Heron Arts LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ##library.name##
 * ##library.sentence##
 * ##library.url##
 *
 * @author      ##author##
 * @modified    ##date##
 * @version     ##library.prettyVersion## (##library.version##)
 */

package heronarts.p2lx.ui;

import java.util.ArrayList;
import java.util.List;

import processing.core.PApplet;
import processing.core.PFont;
import processing.event.KeyEvent;

/**
 * Top-level container for all overlay UI elements.
 */
public class UI {

  private static UI instance = null;

  /**
   * PApplet that this UI belongs to
   */
  final PApplet applet;

  /**
   * All the layers in this UI
   */
  private final List<UILayer> layers = new ArrayList<UILayer>();

  private final List<UIObject> focusables = new ArrayList<UIObject>();

  private int focusIndex = -1;

  /**
   * Layer that was pressed on
   */
  private UILayer pressedLayer = null;

  /**
   * Layer that has focus
   */
  private UILayer focusedLayer = null;

  /**
   * Default item font in this UI
   */
  private PFont itemFont;

  /**
   * Default title font in this UI
   */
  private PFont titleFont;

  /**
   * Default text color
   */
  private int textColor = 0xff999999;

  /**
   * Default background color
   */
  private int backgroundColor = 0xff444444;

  /**
   * Default border color
   */
  private int windowBorderColor = 0xff292929;

  /**
   * Default focus color
   */
  private int focusColor = 0xff669966;

  /**
   * Default selected highlight color
   */
  private int highlightColor = 0xff669966;

  /**
   * Default active highlight color
   */
  private int selectionColor = 0xff666699;

  /**
   * White color
   */
  public final int WHITE = 0xffffffff;

  /**
   * Black color
   */
  public final int BLACK = 0xff000000;

  /**
   * Creates a new UI instance
   *
   * @param applet The PApplet
   */
  public UI(PApplet applet) {
    this.applet = applet;
    this.itemFont = applet.createFont("Lucida Grande", 11);
    this.titleFont = applet.createFont("Myriad Pro", 10);
    UI.instance = this;
  }

  public static UI get() {
    return UI.instance;
  }

  /**
   * Add a context to this UI
   *
   * @param layer UI layer
   * @return this UI
   */
  public UI addLayer(UILayer layer) {
    this.layers.add(layer);
    if (layer instanceof UIObject) {
      addFocusables((UIObject) layer);
    }
    return this;
  }

  private void addFocusables(UIObject o) {
    if (o instanceof UIFocus) {
      this.focusables.add(o);
    }
    for (UIObject child : o.children) {
      addFocusables(child);
    }
  }

  private void removeFocusables(UIObject o) {
    if (o instanceof UIFocus) {
      this.focusables.remove(o);
    }
    for (UIObject child : o.children) {
      removeFocusables(child);
    }
  }

  void willFocus(UILayer layer, UIObject object) {
    if (this.focusedLayer != layer) {
      if (this.focusedLayer instanceof UIContext) {
        ((UIObject) this.focusedLayer)._blur();
      }
      this.focusedLayer = layer;
    }
    if (object != null) {
      int index = this.focusables.indexOf(object);
      if (index >= 0) {
        this.focusIndex = index;
      }
    }
  }

  void didBlur(UILayer layer) {
    if (this.focusedLayer != layer) {
      throw new IllegalStateException("Tried to blur non-focused layer");
    }
    this.focusedLayer = null;
  }

  private void focusNext() {
    int fsz = this.focusables.size();
    if (fsz > 0) {
      this.focusIndex = (this.focusIndex + 1) % fsz;
      this.focusables.get(this.focusIndex).focus();
    }
  }

  private void focusPrevious() {
    int fsz = this.focusables.size();
    if (fsz > 0) {
      --this.focusIndex;
      if (this.focusIndex < 0) {
        this.focusIndex = (fsz + (this.focusIndex % fsz)) % fsz;
      }
      this.focusables.get(this.focusIndex).focus();
    }
  }

  /**
   * Remove a context from this UI
   *
   * @param layer UI layer
   * @return this UI
   */
  public UI removeLayer(UILayer layer) {
    if (layer instanceof UIContext) {
      ((UIObject) layer)._blur();
    }
    this.layers.remove(layer);
    if (layer instanceof UIObject) {
      removeFocusables((UIObject) layer);
    }
    return this;
  }

  /**
   * Brings a layer to the top of the UI stack
   *
   * @param layer UI layer
   * @return this UI
   */
  public UI bringToTop(UILayer layer) {
    this.layers.remove(layer);
    this.layers.add(layer);
    return this;
  }

  /**
   * Gets the default item font for this UI
   *
   * @return The default item font for this UI
   */
  public PFont getItemFont() {
    return this.itemFont;
  }

  /**
   * Sets the default item font for this UI
   *
   * @param font Font to use
   * @return this UI
   */
  public UI setItemFont(PFont font) {
    this.itemFont = font;
    return this;
  }

  /**
   * Gets the default title font for this UI
   *
   * @return default title font for this UI
   */
  public PFont getTitleFont() {
    return this.titleFont;
  }

  /**
   * Sets the default title font for this UI
   *
   * @param font Default title font
   * @return this UI
   */
  public UI setTitleFont(PFont font) {
    this.titleFont = font;
    return this;
  }

  /**
   * Gets the default text color
   *
   * @return default text color
   */
  public int getTextColor() {
    return this.textColor;
  }

  /**
   * Sets the default text color for UI
   *
   * @param color Color
   * @return this UI
   */
  public UI setTextColor(int color) {
    this.textColor = color;
    return this;
  }

  /**
   * Gets background color
   *
   * @return background color
   */
  public int getBackgroundColor() {
    return this.backgroundColor;
  }

  /**
   * Sets default background color
   *
   * @param color color
   * @return this UI
   */
  public UI setBackgroundColor(int color) {
    this.backgroundColor = color;
    return this;
  }

  /**
   * Gets border color
   *
   * @return bordercolor
   */
  public int getWindowBorderColor() {
    return this.windowBorderColor;
  }

  /**
   * Sets default border color
   *
   * @param color color
   * @return this UI
   */
  public UI setWindowBorderColor(int color) {
    this.windowBorderColor = color;
    return this;
  }

  /**
   * Gets highlight color
   *
   * @return Highlight color
   */
  public int getHighlightColor() {
    return this.highlightColor;
  }

  /**
   * Sets highlight color
   *
   * @param color
   * @return this UI
   */
  public UI setHighlightColor(int color) {
    this.highlightColor = color;
    return this;
  }

  /**
   * Gets focus color
   *
   * @return focus color
   */
  public int getFocusColor() {
    return this.focusColor;
  }

  /**
   * Sets highlight color
   *
   * @param color
   * @return this UI
   */
  public UI setFocusColor(int color) {
    this.focusColor = color;
    return this;
  }

  /**
   * Get active color
   *
   * @return Selection color
   */
  public int getSelectionColor() {
    return this.selectionColor;
  }

  /**
   * Set active color
   *
   * @param color Color
   * @return this UI
   */
  public UI setSelectionColor(int color) {
    this.selectionColor = color;
    return this;
  }

  /**
   * Draws the UI
   */
  public final void draw() {
    for (UILayer layer : this.layers) {
      layer.draw();
    }
  }

  public final void mousePressed(int x, int y) {
    this.pressedLayer = null;
    for (int i = this.layers.size() - 1; i >= 0; --i) {
      UILayer layer = this.layers.get(i);
      if (layer.mousePressed(x, y)) {
        this.pressedLayer = layer;
        break;
      }
    }
  }

  public final void mouseReleased(int x, int y) {
    if (this.pressedLayer != null) {
      this.pressedLayer.mouseReleased(x, y);
      this.pressedLayer = null;
    }
  }

  public final void mouseClicked(int x, int y) {
    for (int i = this.layers.size() - 1; i >= 0; --i) {
      UILayer layer = this.layers.get(i);
      if (layer.mouseClicked(x, y)) {
        break;
      }
    }
  }

  public final void mouseDragged(int x, int y) {
    if (this.pressedLayer != null) {
      this.pressedLayer.mouseDragged(x, y);
    }
  }

  public final void mouseWheel(int x, int y, int rotation) {
    for (int i = this.layers.size() - 1; i >= 0; --i) {
      UILayer layer = this.layers.get(i);
      if (layer.mouseWheel(x, y, rotation)) {
        break;
      }
    }
  }

  public final void keyPressed(KeyEvent keyEvent, char keyChar, int keyCode) {
    if (keyCode == java.awt.event.KeyEvent.VK_TAB) {
      if (keyEvent.isShiftDown()) {
        focusPrevious();
      } else {
        focusNext();
      }
    }
    if (this.focusedLayer != null) {
      this.focusedLayer.keyPressed(keyEvent, keyChar, keyCode);
    }
  }

  public final void keyReleased(KeyEvent keyEvent, char keyChar, int keyCode) {
    if (this.focusedLayer != null) {
      this.focusedLayer.keyReleased(keyEvent, keyChar, keyCode);
    }
  }

  public final void keyTyped(KeyEvent keyEvent, char keyChar, int keyCode) {
    if (this.focusedLayer != null) {
      this.focusedLayer.keyTyped(keyEvent, keyChar, keyCode);
    }
  }

  public static String uiClassName(Object o, String suffix) {
    String s = o.getClass().getName();
    int li;
    if ((li = s.lastIndexOf(".")) > 0) {
      s = s.substring(li + 1);
    }
    if ((li = s.indexOf("$")) != -1) {
      s = s.substring(li + 1);
    }
    if ((suffix != null) && ((li = s.indexOf(suffix)) != -1)) {
      s = s.substring(0, li);
    }
    return s;
  }
}
