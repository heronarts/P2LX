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

import heronarts.lx.LXLoopTask;
import heronarts.p2lx.P2LX;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import processing.core.PApplet;
import processing.event.Event;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

/**
 * Top-level container for all overlay UI elements.
 */
public class UI {

  private static UI instance = null;

  /**
   * Redraw may be called from any thread
   */
  private final List<UIObject> otherThreadRedrawList =
    Collections.synchronizedList(new ArrayList<UIObject>());

  /**
   * Objects to redraw on current pass thru animation thread
   */
  private final List<UIObject> localThreadRedrawList =
    new ArrayList<UIObject>();

  /**
   * Input events coming from the event thread
   */
  private final List<Event> eventThreadInputEventQueue =
    Collections.synchronizedList(new ArrayList<Event>());

  /**
   * Events on the local processing thread
   */
  private final List<Event> localThreadInputEvents = new ArrayList<Event>();

  public class Timer {
    public long drawNanos = 0;
  }

  public final Timer timer = new Timer();

  private final P2LX lx;

  /**
   * PApplet that this UI belongs to
   */
  final PApplet applet;

  /**
   * All the layers in this UI
   */
  private final List<UILayer> layers = new ArrayList<UILayer>();

  private final List<UILayer> localThreadLayers = new ArrayList<UILayer>();

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
   * UI look and feel
   */
  public final UITheme theme;

  /**
   * White color
   */
  public final int WHITE = 0xffffffff;

  /**
   * Black color
   */
  public final int BLACK = 0xff000000;

  public UI(P2LX lx) {
    this(lx.applet, lx);
  }

  /**
   * Creates a new UI instance
   *
   * @param applet The PApplet
   */
  public UI(PApplet applet) {
    this(applet, null);
  }

  private UI(PApplet applet, P2LX lx) {
    this.lx = lx;
    this.applet = applet;
    this.theme = new UITheme(applet);
    applet.registerMethod("draw", this);
    applet.registerMethod("keyEvent", this);
    applet.registerMethod("mouseEvent", this);
    if (lx != null) {
      lx.engine.addLoopTask(new EngineUILoopTask());
    }
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
    synchronized (this.layers) {
      this.layers.add(layer);
    }
    if (layer instanceof UIObject) {
      UIObject object = (UIObject) layer;
      object.setUI(this);
      object.redraw();
      addFocusables(object);
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
    synchronized (this.layers) {
      this.layers.remove(layer);
    }
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
    synchronized (this.layers) {
      this.layers.remove(layer);
      this.layers.add(layer);
    }
    return this;
  }

  void redraw(UIObject object) {
    this.otherThreadRedrawList.add(object);
  }

  /**
   * Draws the UI
   */
  public final void draw() {
    long drawStart = System.nanoTime();
    this.localThreadRedrawList.clear();
    synchronized (this.otherThreadRedrawList) {
      this.localThreadRedrawList.addAll(this.otherThreadRedrawList);
      this.otherThreadRedrawList.clear();
    }
    for (UIObject object : this.localThreadRedrawList) {
      object._redraw();
    }

    // Make a local copy of the layers to draw in this pass, other threads
    // could modify the layer stack as we're drawing
    this.localThreadLayers.clear();
    synchronized (this.layers) {
      this.localThreadLayers.addAll(this.layers);
    }
    for (UILayer layer : this.localThreadLayers) {
      layer.draw();
    }

    this.timer.drawNanos = System.nanoTime() - drawStart;
  }

  boolean isThreaded() {
    return (this.lx != null) && (this.lx.engine.isThreaded());
  }

  private class EngineUILoopTask implements LXLoopTask {

    @Override
    public void loop(double deltaMs) {
      // This is invoked on the LXEngine thread, which may be different
      // from the Processing Animation thread. Events need to be
      // processed on that thread to avoid threading bugs
      localThreadInputEvents.clear();
      synchronized (eventThreadInputEventQueue) {
        localThreadInputEvents.addAll(eventThreadInputEventQueue);
        eventThreadInputEventQueue.clear();
      }
      for (Event event : localThreadInputEvents) {
        if (event instanceof KeyEvent) {
          _keyEvent((KeyEvent) event);
        } else if (event instanceof MouseEvent) {
          _mouseEvent((MouseEvent) event);
        }
      }
    }

  }

  public void mouseEvent(MouseEvent mouseEvent) {
    if (isThreaded()) {
      this.eventThreadInputEventQueue.add(mouseEvent);
    } else {
      _mouseEvent(mouseEvent);
    }
  }

  private void _mouseEvent(MouseEvent mouseEvent) {
    switch (mouseEvent.getAction()) {
    case MouseEvent.WHEEL:
      mouseWheel(mouseEvent.getX(), mouseEvent.getY(), mouseEvent.getCount());
      return;
    case MouseEvent.PRESS:
      mousePressed(mouseEvent.getX(), mouseEvent.getY());
      break;
    case processing.event.MouseEvent.RELEASE:
      mouseReleased(mouseEvent.getX(), mouseEvent.getY());
      break;
    case processing.event.MouseEvent.CLICK:
      mouseClicked(mouseEvent.getX(), mouseEvent.getY());
      break;
    case processing.event.MouseEvent.DRAG:
      mouseDragged(mouseEvent.getX(), mouseEvent.getY());
      break;
    }
  }

  private void mousePressed(int x, int y) {
    this.pressedLayer = null;
    for (int i = this.layers.size() - 1; i >= 0; --i) {
      UILayer layer = this.layers.get(i);
      if (layer.mousePressed(x, y)) {
        this.pressedLayer = layer;
        break;
      }
    }
  }

  private void mouseReleased(int x, int y) {
    if (this.pressedLayer != null) {
      this.pressedLayer.mouseReleased(x, y);
      this.pressedLayer = null;
    }
  }

  public void mouseClicked(int x, int y) {
    for (int i = this.layers.size() - 1; i >= 0; --i) {
      UILayer layer = this.layers.get(i);
      if (layer.mouseClicked(x, y)) {
        break;
      }
    }
  }

  private void mouseDragged(int x, int y) {
    if (this.pressedLayer != null) {
      this.pressedLayer.mouseDragged(x, y);
    }
  }

  private void mouseWheel(int x, int y, int rotation) {
    for (int i = this.layers.size() - 1; i >= 0; --i) {
      UILayer layer = this.layers.get(i);
      if (layer.mouseWheel(x, y, rotation)) {
        break;
      }
    }
  }

  public void keyEvent(KeyEvent keyEvent) {
    if (isThreaded()) {
      this.eventThreadInputEventQueue.add(keyEvent);
    } else {
      _keyEvent(keyEvent);
    }
  }

  private void _keyEvent(KeyEvent keyEvent) {
    char keyChar = keyEvent.getKey();
    int keyCode = keyEvent.getKeyCode();
    switch (keyEvent.getAction()) {
    case KeyEvent.RELEASE:
      keyReleased(keyEvent, keyChar, keyCode);
      break;
    case KeyEvent.PRESS:
      keyPressed(keyEvent, keyChar, keyCode);
      break;
    case KeyEvent.TYPE:
      keyTyped(keyEvent, keyChar, keyCode);
      break;
    default:
      throw new RuntimeException("Invalid keyEvent type: " + keyEvent.getAction());
    }
  }

  private void keyPressed(KeyEvent keyEvent, char keyChar, int keyCode) {
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

  private void keyReleased(KeyEvent keyEvent, char keyChar, int keyCode) {
    if (this.focusedLayer != null) {
      this.focusedLayer.keyReleased(keyEvent, keyChar, keyCode);
    }
  }

  private void keyTyped(KeyEvent keyEvent, char keyChar, int keyCode) {
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
