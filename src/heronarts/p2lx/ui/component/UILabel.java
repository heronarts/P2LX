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

package heronarts.p2lx.ui.component;

import heronarts.p2lx.ui.UI;
import heronarts.p2lx.ui.UITextObject;
import processing.core.PConstants;
import processing.core.PGraphics;

/**
 * A simple text label object. Draws a string aligned top-left to its x-y
 * position.
 */
public class UILabel extends UITextObject {

  private int horizontalAlignment = PConstants.LEFT;

  private int verticalAlignment = PConstants.TOP;

  private int padding = 0;

  /**
   * Label text
   */
  private String label = "";

  public UILabel() {
    this(0, 0, 0, 0);
  }

  public UILabel(float x, float y, float w, float h) {
    super(x, y, w, h);
  }

  public UILabel setPadding(int padding) {
    if (this.padding != padding) {
      this.padding = padding;
      redraw();
    }
    return this;
  }

  public UILabel setAlignment(int horizontalAlignment) {
    setAlignment(horizontalAlignment, PConstants.BASELINE);
    return this;
  }

  public UILabel setAlignment(int horizontalAlignment, int verticalAlignment) {
    if ((this.horizontalAlignment != horizontalAlignment)
        || (this.verticalAlignment != verticalAlignment)) {
      this.horizontalAlignment = horizontalAlignment;
      this.verticalAlignment = verticalAlignment;
      redraw();
    }
    return this;
  }

  @Override
  protected void onDraw(UI ui, PGraphics pg) {
    pg.textFont(hasFont() ? getFont() : ui.theme.getLabelFont());
    pg.fill(hasFontColor() ? getFontColor() : ui.theme.getLabelColor());
    float tx = this.padding, ty = this.padding;
    switch (this.horizontalAlignment) {
    case PConstants.CENTER:
      tx = this.width / 2;
      break;
    case PConstants.RIGHT:
      tx = this.width - this.padding;
      break;
    }
    switch (this.verticalAlignment) {
    case PConstants.BASELINE:
      ty = this.height - this.padding;
      break;
    case PConstants.BOTTOM:
      ty = this.height - this.padding;
      break;
    case PConstants.CENTER:
      ty = this.height / 2;
      break;
    }
    pg.textAlign(this.horizontalAlignment, this.verticalAlignment);
    pg.text(this.label, tx, ty);
  }

  public UILabel setLabel(String label) {
    if (this.label != label) {
      this.label = label;
      redraw();
    }
    return this;
  }
}
