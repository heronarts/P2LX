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

import processing.core.PFont;

/**
 * Objects that render text use this base class to handle the text font and color
 */
public abstract class UITextObject extends UIObject {

  private PFont font = null;

  private boolean hasFontColor = false;

  private int fontColor = 0xff000000;

  protected UITextObject(float x, float y, float w, float h) {
    super(x, y, w, h);
  }

  /**
   * Whether a font is set on this object
   *
   * @return true or false
   */
  public boolean hasFont() {
    return this.font != null;
  }

  /**
   * Get default font, may be null
   *
   * @return
   */
  public PFont getFont() {
    return this.font;
  }

  /**
   * Sets the default font for this object to use, null indicates component may
   * use its own default behavior.
   *
   * @param font Font
   * @return this
   */
  public UIObject setFont(PFont font) {
    if (this.font != font) {
      this.font = font;
      redraw();
    }
    return this;
  }

  /**
   * Whether this object has a specific color
   *
   * @return true or false
   */
  public boolean hasFontColor() {
    return this.hasFontColor;
  }

  /**
   * The font color, if there is a color specified
   *
   * @return color
   */
  public int getFontColor() {
    return this.fontColor;
  }

  /**
   * Sets whether the object has a font color
   *
   * @param hasFontColor true or false
   * @return this
   */
  public UIObject setFontColor(boolean hasFontColor) {
    if (this.hasFontColor != hasFontColor) {
      this.hasFontColor = hasFontColor;
      redraw();
    }
    return this;
  }

  /**
   * Sets a font color
   *
   * @param fontColor color
   * @return this
   */
  public UIObject setFontColor(int fontColor) {
    if (!this.hasFontColor|| (this.fontColor != fontColor)) {
      this.hasFontColor = true;
      this.fontColor = fontColor;
      redraw();
    }
    return this;
  }
}
