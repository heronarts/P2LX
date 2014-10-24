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

import processing.core.PGraphics;

public abstract class UI2dComponent extends UIObject {

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

  float scrollX = 0;

  float scrollY = 0;

  private boolean hasBackground = false;

  private int backgroundColor = 0xFF000000;

  private boolean hasBorder = false;

  private int borderColor = 0xFF000000;

  private int borderWeight = 1;

  boolean needsRedraw = true;

  boolean childNeedsRedraw = true;

  protected UI2dComponent() {
    this(0, 0, 0, 0);
  }

  protected UI2dComponent(float x, float y, float width, float height) {
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
  }

  /**
   * X position
   *
   * @return x position
   */
  @Override
  public final float getX() {
    return this.x;
  }

  /**
   * Y position
   *
   * @return y position
   */
  @Override
  public final float getY() {
    return this.y;
  }

  /**
   * Width
   *
   * @return width
   */
  @Override
  public final float getWidth() {
    return this.width;
  }

  /**
   * Height
   *
   * @return height
   */
  @Override
  public final float getHeight() {
    return this.height;
  }

  /**
   * Whether the given coordinate, in the parent-space, is contained
   * by this object.
   *
   * @param x X-coordinate in parent's coordinate space
   * @param y Y-coordinate in parent's coordinate space
   * @return Whether this object's bounds contain that point
   */
  @Override
  public boolean contains(float x, float y) {
    return
      (x >= this.x && x < (this.x + this.width)) &&
      (y >= this.y && y < (this.y + this.height));
  }

  /**
   * Set the visibility state of this component
   *
   * @param visible Whether this should be visible
   * @return this
   */
  @Override
  public UIObject setVisible(boolean visible) {
    if (isVisible() != visible) {
      super.setVisible(visible);
      if (visible) {
        redraw();
      } else {
        redrawContainer();
      }
    }
    return this;
  }

  /**
   * Set the position of this component in its parent coordinate space
   *
   * @param x X-position in parents coordinate space
   * @param y Y-position in parents coordinate space
   * @return this
   */
  public UI2dComponent setPosition(float x, float y) {
    if ((this.x != x) || (this.y != y)) {
      this.x = x;
      this.y = y;
      redrawContainer();
    }
    return this;
  }

  /**
   * Set the dimensions of this component
   *
   * @param width Width of component
   * @param height Height of component
   * @return this
   */
  public UI2dComponent setSize(float width, float height) {
    if ((this.width != width) || (this.height != height)) {
      this.width = width;
      this.height = height;
      onResize();
      redrawContainer();
    }
    return this;
  }

  /**
   * Subclasses may override this method, invoked when the component is resized
   */
  protected void onResize() {

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
  public UI2dComponent setBackground(boolean hasBackground) {
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
  public UI2dComponent setBackgroundColor(int backgroundColor) {
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
  public UI2dComponent setBorder(boolean hasBorder) {
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
  public UI2dComponent setBorderColor(int borderColor) {
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
  public UI2dComponent setBorderWeight(int borderWeight) {
    if (!this.hasBorder || (this.borderWeight != borderWeight)) {
      this.hasBorder = true;
      this.borderWeight = borderWeight;
      redraw();
    }
    return this;
  }

  /**
   * Removes this components from the container is is held by
   *
   * @return this
   */
  public UI2dComponent removeFromContainer() {
    if (this.parent == null) {
      throw new IllegalStateException("Cannot remove parentless UIObject from container");
    }
    this.parent.children.remove(this);
    redrawContainer();
    this.parent = null;
    return this;
  }

  /**
   * Adds this component to a container, also removing it from any other container that
   * is currently holding it.
   *
   * @param container Container to place in
   * @return this
   */
  public UI2dComponent addToContainer(UI2dContainer container) {
    if (this.parent != null) {
      removeFromContainer();
    }
    UIObject containerObject = (UIObject) container;
    containerObject.children.add(this);
    this.parent = containerObject;
    setUI(containerObject.ui);
    redraw();
    return this;
  }

  /**
   * Redraws this object.
   *
   * @return this object
   */
  public final UI2dComponent redraw() {
    if (this.ui != null) {
      this.ui.redraw(this);
    }
    return this;
  }

  private void redrawContainer() {
    if ((this.parent != null) && (this.parent instanceof UI2dComponent)) {
      ((UI2dComponent)this.parent).redraw();
    }
  }

  final void _redraw() {
    // Mark object and children as needing redraw
    _redrawChildren();

    // Mark parent containers as needing a child redrawn
    UIObject p = this.parent;
    while ((p != null) && (p instanceof UI2dComponent)) {
      UI2dComponent p2d = (UI2dComponent) p;
      p2d.childNeedsRedraw = true;
      p = p2d.parent;
    }
  }

  /**
   * Internal helper. Marks this object and all of its children as needing to be
   * redrawn.
   */
  private final void _redrawChildren() {
    this.needsRedraw = true;
    this.childNeedsRedraw = (this.children.size() > 0);
    for (UIObject child : this.children) {
      ((UI2dComponent)child)._redrawChildren();
    }
  }

  /**
   * Draws this object to the graphics context.
   *
   * @param ui UI
   * @param pg graphics buffer
   */
  @Override
  void draw(UI ui, PGraphics pg) {
    if (!isVisible()) {
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
      float sx = this.scrollX;
      float sy = this.scrollY;
      pg.translate(sx, sy);
      for (UIObject childObject : this.children) {
        UI2dComponent child = (UI2dComponent) childObject;
        if (this.needsRedraw || child.needsRedraw || child.childNeedsRedraw) {
          float cx = child.x;
          float cy = child.y;
          pg.translate(cx, cy);
          child.draw(ui, pg);
          pg.translate(-cx, -cy);
        }
      }
      pg.translate(-sx, -sy);
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
    if (hasFocus() && (this instanceof UIFocus)) {
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

}
