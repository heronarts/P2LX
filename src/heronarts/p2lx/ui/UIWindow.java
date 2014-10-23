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

import heronarts.lx.LXUtils;
import heronarts.p2lx.ui.component.UILabel;

/**
 * A UIWindow is a UIContext that by default has a title bar and can be dragged
 * around when the mouse is pressed on the title bar.
 */
public class UIWindow extends UIContext {

  public final static int TITLE_LABEL_HEIGHT = 24;

  private final static int TITLE_PADDING = 6;

  /**
   * The label object
   */
  private final UILabel label;

  /**
   * Constructs a window object
   *
   * @param ui UI to place in
   * @param title Title for this window
   * @param x
   * @param y
   * @param w
   * @param h
   */
  public UIWindow(final UI ui, String title, float x, float y, float w, float h) {
    super(ui, x, y, w, h);
    setBackgroundColor(ui.getBackgroundColor());
    setBorderColor(ui.getWindowBorderColor());
    this.label = new UILabel(0, 0, w, TITLE_LABEL_HEIGHT)
      .setLabel(title)
      .setPadding(TITLE_PADDING)
      .setColor(ui.getTextColor());
    this.label.setFont(ui.getTitleFont()).addToContainer(this);
  }

  private boolean movingWindow = false;

  @Override
  protected void onFocus() {
    this.label.setColor(ui.getFocusColor());
  }

  @Override
  protected void onBlur() {
    this.label.setColor(ui.getTextColor());
  }

  @Override
  protected void onMousePressed(float mx, float my) {
    this.movingWindow = (my < TITLE_LABEL_HEIGHT);
    this.ui.bringToTop(this);
    _focus(this);
  }

  @Override
  protected void onMouseDragged(float mx, float my, float dx, float dy) {
    if (this.movingWindow) {
      float newX = LXUtils.constrainf(this.x + dx, 0, this.ui.applet.width
          - this.width);
      float newY = LXUtils.constrainf(this.y + dy, 0, this.ui.applet.height
          - this.height);
      setPosition(newX, newY);
    }
  }

  /**
   * Set the title of the window.
   *
   * @param title Title of the window
   * @return this window
   */
  public UIWindow setTitle(String title) {
    this.label.setLabel(title);
    return this;
  }
}
