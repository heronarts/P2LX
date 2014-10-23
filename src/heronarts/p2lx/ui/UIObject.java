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

import processing.core.PGraphics;
import processing.event.KeyEvent;

/**
 * Object in a UI hierarchy. UIObjects all have coordinate in an x-y space that
 * uses standard graphics coordinate system of (0,0) representing the top-left
 * corner. Increasing x-values move to the right, y-values move down. Object
 * positions use x, y to refer to the origin of the object at the top left
 * corner, with width and height forming a bounding rectangle.
 */
public abstract class UIObject {

  protected final static int DOUBLE_CLICK_THRESHOLD = 300;

  private UI ui = null;

  /**
   * Children of this object, latest elements are drawn on top.
   */
  final List<UIObject> children = new ArrayList<UIObject>();

  /**
   * Internal state, true if this object needs to be redrawn.
   */
  boolean needsRedraw = true;

  /**
   * Internal state, true if a child of this object needs to be redrawn.
   */
  boolean childNeedsRedraw = true;

  /**
   * Position of the object, relative to parent, top left corner
   */
  protected float x;

  /**
   * Position of the object, relative to parent, top left corner
   */
  protected float y;

  /**
   * Width of the object
   */
  protected float width;

  /**
   * Height of the object
   */
  protected float height;

  /**
   * Parent object.
   */
  public UIContainer parent = null;

  /**
   * Whether this object is visible or not.
   */
  protected boolean visible = true;

  /**
   * Which child mouse events are pressed to, if any.
   */
  private UIObject pressedChild = null;

  /**
   * Which child has focus.
   */
  private UIObject focusedChild = null;

  /**
   * If this object has focus.
   */
  private boolean hasFocus = false;

  /**
   * Subclasses may use to offset children
   */
  float scrollX = 0;

  /**
   * Subclasses may use to offset children
   */
  float scrollY = 0;

  private boolean hasBackground = false;

  private int backgroundColor = 0xFF000000;

  private boolean hasBorder = false;

  private int borderColor = 0xFF000000;

  private int borderWeight = 1;

  /**
   * Constructs a UIObject with no size.
   */
  protected UIObject() {
    this(0, 0, 0, 0);
  }

  /**
   * Constructs a UIObject
   *
   * @param x x-position
   * @param y y-position
   * @param w width
   * @param h height
   */
  protected UIObject(float x, float y, float w, float h) {
    this.x = x;
    this.y = y;
    this.width = w;
    this.height = h;
  }

  /**
   * Whether this object is visible.
   *
   * @return True if this object is being displayed
   */
  public boolean isVisible() {
    return visible;
  }

  /**
   * Set whether this object should be displayed
   *
   * @param visible Whether to display this object
   * @return this object
   */
  public UIObject setVisible(boolean visible) {
    if (visible != this.visible) {
      this.visible = visible;
      if (visible) {
        redraw();
      } else if (this.parent != null) {
        this.parent.redraw();
      }
    }
    return this;
  }

  /**
   * Sets the position of this object, relative to its parent
   *
   * @param x x-position, relative to parent's coordinate space
   * @param y y-position, relative to parent's coordinate space
   * @return this object
   */
  public final UIObject setPosition(float x, float y) {
    if ((this.x != x) || (this.y != y)) {
      this.x = x;
      this.y = y;
      if (this.parent != null) {
        this.parent.redraw();
      } else {
        redraw();
      }
    }
    return this;
  }

  /**
   * Sets the size of this object.
   *
   * @param width Width of object
   * @param height Height of object
   * @return this object
   */
  public final UIObject setSize(float width, float height) {
    if ((this.width != width) || (this.height != height)) {
      this.width = width;
      this.height = height;
      onResize();
      if (this.parent != null) {
        this.parent.redraw();
      } else {
        redraw();
      }
    }
    return this;
  }

  /**
   * X position
   *
   * @return x position
   */
  public final float getX() {
    return this.x;
  }

  /**
   * Y position
   *
   * @return y position
   */
  public final float getY() {
    return this.y;
  }

  /**
   * Width
   *
   * @return width
   */
  public final float getWidth() {
    return this.width;
  }

  /**
   * Height
   *
   * @return height
   */
  public final float getHeight() {
    return this.height;
  }

  /**
   * Whether a given point, in the container's coordinate space, is within this
   * object.
   *
   * @param x x-coordinate, in parent's coordinate space
   * @param y y-coordinate, in parent's coordinate space
   * @return true if the point is inside this object's bounds
   */
  public final boolean contains(float x, float y) {
    return (x >= this.x && x < (this.x + this.width))
        && (y >= this.y && y < (this.y + this.height));
  }

  /**
   * Sets the UI that this object belongs to, recursively
   * applies to all children.
   */
  void setUI(UI ui) {
    if (this.ui != ui) {
      this.ui = ui;
      redraw();
      for (UIObject child : this.children) {
        child.setUI(ui);
      }
    }
  }

  UI getUI() {
    return this.ui;
  }

  /**
   * Places this object inside a container.
   *
   * @param container The object in which to place this
   * @return this object
   */
  public final UIObject addToContainer(UIContainer container) {
    if (this.parent != null) {
      removeFromContainer();
    }
    container.children.add(this);
    this.parent = container;
    setUI(((UIObject)this.parent).ui);
    container.redraw();
    return this;
  }

  /**
   * Removes this object from a container that it is in.
   *
   * @return this object
   */
  public final UIObject removeFromContainer() {
    if (this.parent != null) {
      this._blur();
      this.parent.children.remove(this);
      this.parent = null;
    }
    return this;
  }

  /**
   * Whether this object has a background
   *
   * @return true or false
   */
  public boolean hasBackground() {
    return this.hasBackground;
  }

  /**
   * The background color, if there is a background
   *
   * @return color
   */
  public int getBackgroundColor() {
    return this.backgroundColor;
  }

  /**
   * Sets whether the object has a background
   *
   * @param hasBackground true or false
   * @return this
   */
  public UIObject setBackground(boolean hasBackground) {
    if (this.hasBackground != hasBackground) {
      this.hasBackground = hasBackground;
      redraw();
    }
    return this;
  }

  /**
   * Sets a background color
   *
   * @param backgroundColor color
   * @return this
   */
  public UIObject setBackgroundColor(int backgroundColor) {
    if (!this.hasBackground || (this.backgroundColor != backgroundColor)) {
      this.hasBackground = true;
      this.backgroundColor = backgroundColor;
      redraw();
    }
    return this;
  }

  /**
   * Whether this object has a border
   *
   * @return true or false
   */
  public boolean hasBorder() {
    return this.hasBorder;
  }

  /**
   * Current border color
   *
   * @return color
   */
  public int getBorderColor() {
    return this.borderColor;
  }

  /**
   * The weight of the border
   *
   * @return weight
   */
  public int getBorderWeight() {
    return this.borderWeight;
  }

  /**
   * Sets whether there is a border
   *
   * @param hasBorder true or false
   * @return this
   */
  public UIObject setBorder(boolean hasBorder) {
    if (this.hasBorder != hasBorder) {
      this.hasBorder = hasBorder;
      redraw();
    }
    return this;
  }

  /**
   * Sets the color of the border
   *
   * @param borderColor color
   * @return this
   */
  public UIObject setBorderColor(int borderColor) {
    if (!this.hasBorder || (this.borderColor != borderColor)) {
      this.hasBorder = true;
      this.borderColor = borderColor;
      redraw();
    }
    return this;
  }

  /**
   * Sets the weight of the border
   *
   * @param borderWeight weight
   * @return this
   */
  public UIObject setBorderWeight(int borderWeight) {
    if (!this.hasBorder || (this.borderWeight != borderWeight)) {
      this.hasBorder = true;
      this.borderWeight = borderWeight;
      redraw();
    }
    return this;
  }

  /**
   * Redraws this object.
   *
   * @return this object
   */
  public final UIObject redraw() {
    if (this.ui != null) {
      this.ui.redraw(this);
    }
    return this;
  }

  final void _redraw() {
    // Mark object and children as needing redraw
    _markRedraw();

    // Mark parent containers as needing a child redrawn
    UIObject p = this.parent;
    while (p != null) {
      p.childNeedsRedraw = true;
      p = p.parent;
    }
  }

  /**
   * Internal helper. Marks this object and all of its children as needing to be
   * redrawn.
   */
  private final void _markRedraw() {
    this.needsRedraw = true;
    this.childNeedsRedraw = (this.children.size() > 0);
    for (UIObject child : this.children) {
      child._markRedraw();
    }
  }

  /**
   * Draws this object to the graphics context.
   *
   * @param ui UI
   * @param pg graphics buffer
   */
  void draw(UI ui, PGraphics pg) {
    if (!this.visible) {
      return;
    }
    boolean needsBorder = this.needsRedraw || this.childNeedsRedraw;
    if (this.needsRedraw) {
      this.needsRedraw = false;
      drawBackground(ui, pg);
      onDraw(ui, pg);
    }
    if (this.childNeedsRedraw) {
      this.childNeedsRedraw = false;
      pg.translate(this.scrollX, this.scrollY);
      for (UIObject child : children) {
        if (this.needsRedraw || child.needsRedraw || child.childNeedsRedraw) {
          pg.translate(child.x, child.y);
          child.draw(ui, pg);
          pg.translate(-child.x, -child.y);
        }
      }
      pg.translate(-this.scrollX, -this.scrollY);
    }
    if (needsBorder) {
      drawBorder(ui, pg);
    }
  }

  private void drawBackground(UI ui, PGraphics pg) {
    if (this.hasBackground) {
      pg.noStroke();
      pg.fill(this.backgroundColor);
      pg.rect(0, 0, width, height);
    }
  }

  private void drawBorder(UI ui, PGraphics pg) {
    if (this.hasBorder) {
      int border = this.borderWeight;
      pg.strokeWeight(border);
      pg.stroke(this.borderColor);
      pg.noFill();
      pg.rect(border / 2, border / 2, this.width - border, this.height - border);

      // Reset stroke weight
      pg.strokeWeight(1);
    }
    if ((this instanceof UIFocus) && this.hasFocus) {
      drawFocus(ui, pg);
    }
  }

  /**
   * Draws focus on this object. May be overridden by subclasses.
   *
   * @param ui UI
   * @param pg PGraphics
   */
  protected void drawFocus(UI ui, PGraphics pg) {
    int focusSize = (int) Math.min(8, Math.min(this.width, this.height) / 8);
    pg.stroke(ui.theme.getFocusColor());
    pg.noFill();
    // Top left
    pg.line(0, 0, focusSize, 0);
    pg.line(0, 0, 0, focusSize);
    // Top right
    pg.line(this.width - focusSize - 1, 0, this.width - 1, 0);
    pg.line(this.width - 1, 0, this.width - 1, focusSize);
    // Bottom right
    pg.line(this.width - focusSize - 1, this.height - 1, this.width - 1,
        this.height - 1);
    pg.line(this.width - 1, this.height - 1, this.width - 1, this.height - 1
        - focusSize);
    // Bottom left
    pg.line(0, this.height - 1, focusSize, this.height - 1);
    pg.line(0, this.height - 1, 0, this.height - 1 - focusSize);
  }

  /**
   * Whether this object has focus
   */
  public boolean hasFocus() {
    return this.hasFocus;
  }

  /**
   * Focuses this UIObject
   *
   * @return this
   */
  public UIObject focus() {
    if (!(this instanceof UIFocus)) {
      throw new UnsupportedOperationException(getClass().getName()
          + " does not implement UIFocus");
    }
    if (this.focusedChild != null) {
      this.focusedChild._blur();
    }
    _focus(this);
    return this;
  }

  void _focus(UIObject object) {
    if (this.parent != null) {
      UIObject parentsFocusedChild = ((UIObject) this.parent).focusedChild;
      if ((parentsFocusedChild != null) && (parentsFocusedChild != this)) {
        parentsFocusedChild._blur();
      }
      this.parent._focus(object);
      ((UIObject) this.parent).focusedChild = this;
    }
    if (!this.hasFocus) {
      this.hasFocus = true;
      onFocus();
      redraw();
    }
  }

  /**
   * Takes focus away from this UIObject
   *
   * @return this
   */
  public UIObject blur() {
    if (!(this instanceof UIFocus)) {
      throw new UnsupportedOperationException(getClass().getName()
          + " does not implement UIFocus");
    }
    _blur();
    return this;
  }

  void _blur() {
    if (this.hasFocus) {
      if (this.focusedChild != null) {
        this.focusedChild._blur();
      }
      this.hasFocus = false;
      onBlur();
      redraw();
      if (this.parent != null) {
        ((UIObject) this.parent).focusedChild = null;
      }
    }
  }

  void _mousePressed(float mx, float my) {
    for (int i = this.children.size() - 1; i >= 0; --i) {
      UIObject child = this.children.get(i);
      if (child.visible && child.contains(mx, my)) {
        child._mousePressed(mx - child.x, my - child.y);
        this.pressedChild = child;
        break;
      }
    }
    if (this instanceof UIFocus) {
      focus();
    }
    onMousePressed(mx, my);
  }

  void _mouseClicked(float mx, float my) {
    for (int i = this.children.size() - 1; i >= 0; --i) {
      UIObject child = this.children.get(i);
      if (child.visible && child.contains(mx, my)) {
        child._mouseClicked(mx - child.x, my - child.y);
        break;
      }
    }
    onMouseClicked(mx, my);
  }

  void _mouseReleased(float mx, float my) {
    if (this.pressedChild != null) {
      this.pressedChild._mouseReleased(mx - this.pressedChild.x, my
          - this.pressedChild.y);
      this.pressedChild = null;
    }
    onMouseReleased(mx, my);
  }

  void _mouseDragged(float mx, float my, float dx, float dy) {
    if (this.pressedChild != null) {
      this.pressedChild._mouseDragged(mx - this.pressedChild.x, my
          - this.pressedChild.y, dx, dy);
    }
    onMouseDragged(mx, my, dx, dy);
  }

  void _mouseWheel(float mx, float my, float delta) {
    for (int i = this.children.size() - 1; i >= 0; --i) {
      UIObject child = this.children.get(i);
      if (child.visible && child.contains(mx, my)) {
        child._mouseWheel(mx - child.x, my - child.y, delta);
        break;
      }
    }
    onMouseWheel(mx, my, delta);
  }

  void _keyPressed(KeyEvent keyEvent, char keyChar, int keyCode) {
    if (this.focusedChild != null) {
      this.focusedChild._keyPressed(keyEvent, keyChar, keyCode);
    }
    onKeyPressed(keyEvent, keyChar, keyCode);
  }

  void _keyReleased(KeyEvent keyEvent, char keyChar, int keyCode) {
    if (this.focusedChild != null) {
      this.focusedChild._keyReleased(keyEvent, keyChar, keyCode);
    }
    onKeyReleased(keyEvent, keyChar, keyCode);
  }

  void _keyTyped(KeyEvent keyEvent, char keyChar, int keyCode) {
    if (this.focusedChild != null) {
      this.focusedChild._keyTyped(keyEvent, keyChar, keyCode);
    }
    onKeyTyped(keyEvent, keyChar, keyCode);
  }

  /**
   * Invoked whenever this object needs to draw itself - subclasses should
   * override to implement their drawing functionality.
   *
   * @param ui UI
   * @param pg PGraphics context
   */
  protected void onDraw(UI ui, PGraphics pg) {
  }

  /**
   * Invoked whenever this object is resized.
   */
  protected void onResize() {
  }

  /**
   * Invoked when this object is focused
   */
  protected void onFocus() {
  }

  /**
   * Invoked when this object loses focus
   */
  protected void onBlur() {
  }

  /**
   * Invoked when the mouse is pressed within the bounds of this object -
   * subclasses should override.
   *
   * @param mx x-position in this object's coordinate space
   * @param my y-position in this object's coordinate space
   */
  protected void onMousePressed(float mx, float my) {
  }

  /**
   * Invoked when the mouse is released in this object, or after being initially
   * pressed inside this object - subclasses should override.
   *
   * @param mx x-position in this object's coordinate space
   * @param my y-position in this object's coordinate space
   */
  protected void onMouseReleased(float mx, float my) {
  }

  /**
   * Invoked when the mouse is clicked in this object - subclasses should
   * override.
   *
   * @param mx x-position in this object's coordinate space
   * @param my y-position in this object's coordinate space
   */
  protected void onMouseClicked(float mx, float my) {
  }

  /**
   * Invoked when the mouse is dragged in this object, or after being initially
   * pressed inside this object - subclasses should override.
   *
   * @param mx x-position in this object's coordinate space
   * @param my y-position in this object's coordinate space
   * @param dx relative change in x-position since last invocation
   * @param dy relative change in y-position since last invocation
   */
  protected void onMouseDragged(float mx, float my, float dx, float dy) {
  }

  /**
   * Invoked when the mouse wheel is scrolled inside this object - subclasses
   * should override.
   *
   * @param mx x-position in this object's coordinate space
   * @param my y-position in this object's coordinate space
   * @param dx relative change in mouse wheel position
   */
  protected void onMouseWheel(float mx, float my, float dx) {
  }

  /**
   * Invoked when key is pressed and this object has focus - subclasses should
   * override.
   *
   * @param keyChar
   * @param keyCode
   */
  protected void onKeyPressed(KeyEvent keyEvent, char keyChar, int keyCode) {
  }

  /**
   * Invoked when key is released and this object has focus - subclasses should
   * override.
   *
   * @param keyChar
   * @param keyCode
   */
  protected void onKeyReleased(KeyEvent keyEvent, char keyChar, int keyCode) {
  }

  /**
   * Invoked when key is typed and this object has focus - subclasses should
   * override.
   *
   * @param keyChar
   * @param keyCode
   */
  protected void onKeyTyped(KeyEvent keyEvent, char keyChar, int keyCode) {
  }
}
