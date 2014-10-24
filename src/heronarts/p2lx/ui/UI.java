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

  private class UIRoot extends UIObject implements UI2dContainer {

    private UIRoot() {
      this.ui = UI.this;
    }

    @Override
    protected void onKeyPressed(KeyEvent keyEvent, char keyChar, int keyCode) {
      if (keyCode == java.awt.event.KeyEvent.VK_TAB) {
        if (keyEvent.isShiftDown()) {
          focusPrev();
        } else {
          focusNext();
        }
      }
    }

    private void focusPrev() {
      UIObject focusTarget = findPrevFocusable();
      if (focusTarget != null) {
        focusTarget.focus();
      }
    }

    private void focusNext() {
      UIObject focusTarget = findNextFocusable();
      if (focusTarget != null) {
        focusTarget.focus();
      }
    }

    private UIObject findCurrentFocus() {
      UIObject currentFocus = this;
      while (currentFocus.focusedChild != null) {
        currentFocus = currentFocus.focusedChild;
      }
      return currentFocus;
    }

    private UIObject findNextFocusable() {
      // Identify the deepest focused object
      UIObject focus = findCurrentFocus();

      // Check if it has a child that is eligible for focus
      UIObject focusableChild = findNextFocusableChild(focus, 0);
      if (focusableChild != null) {
        return focusableChild;
      }

      // Work up the tree, trying siblings at each level
      while (focus.parent != null) {
        int focusIndex = focus.parent.children.indexOf(focus);
        focusableChild = findNextFocusableChild(focus.parent, focusIndex + 1);
        if (focusableChild != null) {
          return focusableChild;
        }
        focus = focus.parent;
      }

      // We ran out! Loop around from the front...
      return findNextFocusableChild(this, 0);
    }

    private UIObject findNextFocusableChild(UIObject focus, int startIndex) {
      for (int i = startIndex; i < focus.children.size(); ++i) {
        UIObject child = focus.children.get(i);
        if (child.isVisible()) {
          if (child instanceof UIFocus) {
            return child;
          }
          UIObject recurse = findNextFocusableChild(child, 0);
          if (recurse != null) {
            return recurse;
          }
        }
      }
      return null;
    }

    private UIObject findPrevFocusable() {
      // Identify the deepest focused object
      UIObject focus = findCurrentFocus();

      // Check its previous siblings, depth-first
      while (focus.parent != null) {
        int focusIndex = focus.parent.children.indexOf(focus);
        UIObject focusableChild = findPrevFocusableChild(focus.parent, focusIndex - 1);
        if (focusableChild != null) {
          return focusableChild;
        }
        if (focus.parent instanceof UIFocus) {
          return focus.parent;
        }
        focus = focus.parent;
      }

      // We failed! Wrap around to the end
      return findPrevFocusableChild(this, this.children.size() - 1);
    }

    private UIObject findPrevFocusableChild(UIObject focus, int startIndex) {
      for (int i = startIndex; i >= 0; --i) {
        UIObject child = focus.children.get(i);
        if (child.isVisible()) {
          UIObject recurse = findPrevFocusableChild(child, child.children.size() - 1);
          if (recurse != null) {
            return recurse;
          }
          if (child instanceof UIFocus) {
            return child;
          }
        }
      }
      return null;
    }
  }

  /**
   * Redraw may be called from any thread
   */
  private final List<UI2dComponent> otherThreadRedrawList =
    Collections.synchronizedList(new ArrayList<UI2dComponent>());

  /**
   * Objects to redraw on current pass thru animation thread
   */
  private final List<UI2dComponent> localThreadRedrawList =
    new ArrayList<UI2dComponent>();

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

  final PApplet applet;

  private UIRoot root;

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
    this.root = new UIRoot();
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
   * Add a 2d context to this UI
   *
   * @param layer UI layer
   * @return this
   */
  public UI addLayer(UI2dContext layer) {
    layer.addToContainer(this.root);
    return this;
  }

  /**
   * Remove a 2d context from this UI
   *
   * @param layer UI layer
   * @return this UI
   */
  public UI removeLayer(UI2dContext layer) {
    layer.removeFromContainer();
    return this;
  }

  /**
   * Add a 3d context to this UI
   *
   * @param layer 3d context
   * @return this UI
   */
  public UI addLayer(UI3dContext layer) {
    this.root.children.add(layer);
    layer.parent = this.root;
    layer.setUI(this);
    return this;
  }

  public UI removeLayer(UI3dContext layer) {
    if (layer.parent != this.root) {
      throw new IllegalStateException("Cannot remove 3d layer which is not present");
    }
    this.root.children.remove(layer);
    layer.parent = null;
    return this;
  }

  /**
   * Brings a layer to the top of the UI stack
   *
   * @param layer UI layer
   * @return this UI
   */
  public UI bringToTop(UI2dContext layer) {
    this.root.children.remove(layer);
    this.root.children.add(layer);
    return this;
  }

  void redraw(UI2dComponent object) {
    this.otherThreadRedrawList.add(object);
  }

  /**
   * Draws the UI
   */
  public final void draw() {
    long drawStart = System.nanoTime();

    // Iterate through all objects that need redraw state marked
    this.localThreadRedrawList.clear();
    synchronized (this.otherThreadRedrawList) {
      this.localThreadRedrawList.addAll(this.otherThreadRedrawList);
      this.otherThreadRedrawList.clear();
    }
    for (UI2dComponent object : this.localThreadRedrawList) {
      object._redraw();
    }

    // Draw from the root
    this.root.draw(this, this.applet.g);

    this.timer.drawNanos = System.nanoTime() - drawStart;
  }

  private boolean isThreaded() {
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

  private float pmx, pmy;

  private void _mouseEvent(MouseEvent mouseEvent) {
    switch (mouseEvent.getAction()) {
    case MouseEvent.WHEEL:
      this.root.mouseWheel(mouseEvent, mouseEvent.getX(), mouseEvent.getY(), mouseEvent.getCount());
      return;
    case MouseEvent.PRESS:
      this.pmx = mouseEvent.getX();
      this.pmy = mouseEvent.getY();
      this.root.mousePressed(mouseEvent, this.pmx, this.pmy);
      break;
    case processing.event.MouseEvent.RELEASE:
      this.root.mouseReleased(mouseEvent, mouseEvent.getX(), mouseEvent.getY());
      break;
    case processing.event.MouseEvent.CLICK:
      this.root.mouseClicked(mouseEvent, mouseEvent.getX(), mouseEvent.getY());
      break;
    case processing.event.MouseEvent.DRAG:
      float mx = mouseEvent.getX();
      float my = mouseEvent.getY();
      this.root.mouseDragged(mouseEvent, mx, my, mx - this.pmx, my - this.pmy);
      this.pmx = mx;
      this.pmy = my;
      break;
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
      this.root.keyReleased(keyEvent, keyChar, keyCode);
      break;
    case KeyEvent.PRESS:
      this.root.keyPressed(keyEvent, keyChar, keyCode);
      break;
    case KeyEvent.TYPE:
      this.root.keyTyped(keyEvent, keyChar, keyCode);
      break;
    default:
      throw new RuntimeException("Invalid keyEvent type: " + keyEvent.getAction());
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
