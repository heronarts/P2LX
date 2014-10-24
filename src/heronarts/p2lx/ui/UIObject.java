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

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import processing.core.PGraphics;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

public abstract class UIObject {

  UI ui = null;

  private boolean visible = true;

  final List<UIObject> children = new CopyOnWriteArrayList<UIObject>();

  UIObject parent = null;

  UIObject focusedChild = null;

  private UIObject pressedChild = null;

  private boolean hasFocus = false;

  /**
   * Internal method to track the UI that this is a part of
   *
   * @param ui UI context
   */
  void setUI(UI ui) {
    this.ui = ui;
    for (UIObject child : this.children) {
      child.setUI(ui);
    }
  }

  /**
   * Whether the given point is contained by this object
   *
   * @param x
   * @param y
   * @return True if the object contains this point
   */
  protected boolean contains(float x, float y) {
    return true;
  }

  float getX() {
    return 0;
  }

  float getY() {
    return 0;
  }

  public float getWidth() {
    return (this.ui != null) ? this.ui.applet.width : 0;
  }

  public float getHeight() {
    return (this.ui != null) ? this.ui.applet.height : 0;
  }

  /**
   * Whether this object is visible.
   *
   * @return True if this object is being displayed
   */
  public boolean isVisible() {
    return this.visible;
  }

  /**
   * Set whether this object is visible
   *
   * @param visible
   * @return this
   */
  public UIObject setVisible(boolean visible) {
    if (this.visible != visible) {
      this.visible = visible;
      if (!visible) {
        blur();
      }
    }
    return this;
  }

  /**
   * Whether this object has focus
   */
  public boolean hasFocus() {
    return this.hasFocus;
  }

  /**
   * Focuses on this object, giving focus to everything above
   * and whatever was previously focused below.
   *
   * @return this
   */
  public UIObject focus() {
    if (this.focusedChild != null) {
      this.focusedChild.blur();
    }
    _focusParents();
    return this;
  }

  private void _focusParents() {
    if (this.parent != null) {
      if (this.parent.focusedChild != this) {
        if (this.parent.focusedChild != null) {
          this.parent.focusedChild.blur();
        }
        this.parent.focusedChild = this;
      }
      this.parent._focusParents();
    }
    if (!this.hasFocus) {
      this.hasFocus = true;
      _onFocus();
    }
  }

  private void _onFocus() {
    onFocus();
    if (this instanceof UI2dComponent) {
      ((UI2dComponent) this).redraw();
    }
  }

  /**
   * Blur this object. Blurs its children from the bottom of
   * the tree up.
   *
   * @return this
   */
  public UIObject blur() {
    if (this.hasFocus) {
      for (UIObject child : this.children) {
        child.blur();
      }
      this.hasFocus = false;
      if (this.parent.focusedChild == this) {
        this.parent.focusedChild = null;
      }
      onBlur();
      if (this instanceof UI2dComponent) {
        ((UI2dComponent)this).redraw();
      }
    }
    return this;
  }

  /**
   * Brings this object to the front of its container.
   *
   * @return this
   */
  public UIObject bringToFront() {
    if (this.parent == null) {
      throw new IllegalStateException("Cannot bring to front when not in any container");
    }
    synchronized (this.parent.children) {
      this.parent.children.remove(this);
      this.parent.children.add(this);
    }
    return this;
  }

  void draw(UI ui, PGraphics pg) {
    if (isVisible()) {
      onDraw(ui, pg);
      for (UIObject child : this.children) {
        float cx = child.getX();
        float cy = child.getY();
        pg.translate(cx, cy);
        child.draw(ui, pg);
        pg.translate(-cx, -cy);
      }
    }
  }

  /**
   * Subclasses should override this method to perform their drawing functions.
   *
   * @param ui UI context
   * @param pg Graphics context
   */
  protected void onDraw(UI ui, PGraphics pg) {}

  void mousePressed(MouseEvent mouseEvent, float mx, float my) {
    for (int i = this.children.size() - 1; i >= 0; --i) {
      UIObject child = this.children.get(i);
      if (child.isVisible() && child.contains(mx, my)) {
        child.mousePressed(mouseEvent, mx - child.getX(), my - child.getY());
        this.pressedChild = child;
        break;
      }
    }
    if (this instanceof UIFocus) {
      focus();
    }
    onMousePressed(mouseEvent, mx, my);
  }

  void mouseReleased(MouseEvent mouseEvent, float mx, float my) {
    if (this.pressedChild != null) {
      this.pressedChild.mouseReleased(
        mouseEvent,
        mx - this.pressedChild.getX(),
        my - this.pressedChild.getY()
      );
      this.pressedChild = null;
    }
    onMouseReleased(mouseEvent, mx, my);
  }

  void mouseClicked(MouseEvent mouseEvent, float mx, float my) {
    for (int i = this.children.size() - 1; i >= 0; --i) {
      UIObject child = this.children.get(i);
      if (child.isVisible() && child.contains(mx, my)) {
        child.mouseClicked(mouseEvent, mx - child.getX(), my - child.getY());
        break;
      }
    }
    onMouseClicked(mouseEvent, mx, my);
  }

  void mouseDragged(MouseEvent mouseEvent, float mx, float my, float dx, float dy) {
    if (this.pressedChild != null) {
      this.pressedChild.mouseDragged(
        mouseEvent,
        mx - this.pressedChild.getX(),
        my - this.pressedChild.getY(),
        dx,
        dy
      );
    }
    onMouseDragged(mouseEvent, mx, my, dx, dy);
  }

  void mouseWheel(MouseEvent mouseEvent, float mx, float my, float delta) {
    for (int i = this.children.size() - 1; i >= 0; --i) {
      UIObject child = this.children.get(i);
      if (child.isVisible() && child.contains(mx, my)) {
        child.mouseWheel(mouseEvent, mx - child.getX(), my - child.getY(), delta);
        break;
      }
    }
    onMouseWheel(mouseEvent, mx, my, delta);
  }

  void keyPressed(KeyEvent keyEvent, char keyChar, int keyCode) {
    if (this.focusedChild != null) {
      this.focusedChild.keyPressed(keyEvent, keyChar, keyCode);
    }
    onKeyPressed(keyEvent, keyChar, keyCode);
  }

  void keyReleased(KeyEvent keyEvent, char keyChar, int keyCode) {
    if (this.focusedChild != null) {
      this.focusedChild.keyReleased(keyEvent, keyChar, keyCode);
    }
    onKeyReleased(keyEvent, keyChar, keyCode);
  }

  void keyTyped(KeyEvent keyEvent, char keyChar, int keyCode) {
    if (this.focusedChild != null) {
      this.focusedChild.keyTyped(keyEvent, keyChar, keyCode);
    }
    onKeyTyped(keyEvent, keyChar, keyCode);
  }

  /**
   * Subclasses override to receive mouse events
   *
   * @param mouseEvent
   * @param mx
   * @param my
   */
  protected void onMousePressed(MouseEvent mouseEvent, float mx, float my) {
  }

  protected void onMouseReleased(MouseEvent mouseEvent, float mx, float my) {
  }

  protected void onMouseClicked(MouseEvent mouseEvent, float mx, float my) {

  }

  protected void onMouseDragged(MouseEvent mouseEvent, float mx, float my, float dx, float dy) {
  }

  protected void onMouseWheel(MouseEvent mouseEvent, float mx, float my, float delta) {
  }

  protected void onKeyPressed(KeyEvent keyEvent, char keyChar, int keyCode) {
  }

  protected void onKeyReleased(KeyEvent keyEvent, char keyChar, int keyCode) {
  }

  protected void onKeyTyped(KeyEvent keyEvent, char keyChar, int keyCode) {
  }

  protected void onFocus() {
  }

  protected void onBlur() {
  }
}
