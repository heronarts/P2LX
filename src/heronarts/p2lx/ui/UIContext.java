/**
 * ##library.name##
 * ##library.sentence##
 * ##library.url##
 *
 * Copyright ##copyright## ##author##
 * All Rights Reserved
 *
 * @author      ##author##
 * @modified    ##date##
 * @version     ##library.prettyVersion## (##library.version##)
 */

package heronarts.p2lx.ui;

import processing.core.PConstants;
import processing.core.PGraphics;
import processing.event.KeyEvent;

/**
 * A UIContext is a container that owns a graphics buffer. This buffer is
 * persistent across frames and is only redrawn as necessary. It is simply
 * bitmapped onto the UI that is a part of.
 */
public class UIContext extends UIContainer implements UILayer {

  /**
   * UI instance
   */
  protected final UI ui;

  /**
   * Graphics context for this container.
   */
  private final PGraphics pg;

  /**
   * Previous mouse x position
   */
  private float px;

  /**
   * Previous mouse y position
   */
  private float py;

  /**
   * Whether this context is currently taking mouse drag events.
   */
  private boolean dragging = false;

  /**
   * Constructs a new UIContext
   *
   * @param ui the UI to place it in
   * @param x x-position
   * @param y y-position
   * @param w width
   * @param h height
   */
  public UIContext(UI ui, float x, float y, float w, float h) {
    super(x, y, w, h);
    this.ui = ui;
    this.pg = this.ui.applet.createGraphics((int) this.width,
        (int) this.height, PConstants.JAVA2D);
    this.pg.smooth();
  }

  @Override
  protected void onResize() {
    this.pg.setSize((int) this.width, (int) this.height);
  }

  public final void draw() {
    draw(this.ui, this.ui.applet.g);
  }

  @Override
  void draw(UI ui, PGraphics pg) {
    if (!this.visible) {
      return;
    }
    if (this.needsRedraw || this.childNeedsRedraw) {
      this.pg.beginDraw();
      super.draw(ui, this.pg);
      this.pg.endDraw();
    }
    pg.image(this.pg, this.x, this.y);
  }

  @Override
  void _focus(UIObject focus) {
    this.ui.willFocus(this, focus);
    if (!this.hasFocus()) {
      super._focus(focus);
    }
  }

  @Override
  void _blur() {
    if (this.hasFocus()) {
      super._blur();
      this.ui.didBlur(this);
    }
  }

  public final boolean mousePressed(float mx, float my) {
    if (!this.visible) {
      return false;
    }
    if (contains(mx, my)) {
      this.dragging = true;
      this.px = mx;
      this.py = my;
      _mousePressed(mx - this.x, my - this.y);
      return true;
    }
    return false;
  }

  public final boolean mouseReleased(float mx, float my) {
    if (!this.visible) {
      return false;
    }
    this.dragging = false;
    _mouseReleased(mx - this.x, my - this.y);
    return true;
  }

  public final boolean mouseClicked(float mx, float my) {
    if (!this.visible) {
      return false;
    }
    if (contains(mx, my)) {
      _mouseClicked(mx - this.x, my - this.y);
      return true;
    }
    return false;
  }

  public final boolean mouseDragged(float mx, float my) {
    if (!this.visible) {
      return false;
    }
    if (this.dragging) {
      float dx = mx - this.px;
      float dy = my - this.py;
      _mouseDragged(mx - this.x, my - this.y, dx, dy);
      this.px = mx;
      this.py = my;
      return true;
    }
    return false;
  }

  public final boolean mouseWheel(float mx, float my, float delta) {
    if (!this.visible) {
      return false;
    }
    if (contains(mx, my)) {
      _mouseWheel(mx - this.x, my - this.y, delta);
      return true;
    }
    return false;
  }

  public final boolean keyPressed(KeyEvent keyEvent, char keyChar, int keyCode) {
    _keyPressed(keyEvent, keyChar, keyCode);
    return true;
  }

  public final boolean keyReleased(KeyEvent keyEvent, char keyChar, int keyCode) {
    _keyReleased(keyEvent, keyChar, keyCode);
    return true;
  }

  public final boolean keyTyped(KeyEvent keyEvent, char keyChar, int keyCode) {
    _keyTyped(keyEvent, keyChar, keyCode);
    return true;
  }
}
