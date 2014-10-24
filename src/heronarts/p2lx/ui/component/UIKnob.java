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

import heronarts.lx.LXUtils;
import heronarts.p2lx.ui.UI;
import heronarts.p2lx.ui.UIFocus;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

/**
 * TODO(mcslee): make the label a UILabel subcomponent and optional
 */
public class UIKnob extends UIParameterControl implements UIFocus {

  public final static int DEFAULT_SIZE = 28;

  private int knobSize = DEFAULT_SIZE;

  private final float knobIndent = .4f;

  private final int knobLabelHeight = 14;

  private boolean showValue = false;

  public UIKnob() {
    this(0, 0);
  }

  public UIKnob(float x, float y) {
    this(x, y, 0, 0);
    setSize(this.knobSize, this.knobSize + this.knobLabelHeight);
  }

  public UIKnob(float x, float y, float w, float h) {
    super(x, y, w, h);
  }

  private void setShowValue(boolean showValue) {
    if (showValue != this.showValue) {
      this.showValue = showValue;
      redraw();
    }
  }

  @Override
  protected void onDraw(UI ui, PGraphics pg) {
    float knobValue = (float) getNormalized();

    pg.ellipseMode(PConstants.CENTER);

    pg.noStroke();
    pg.fill(ui.theme.getWindowBackgroundColor());
    pg.rect(0, 0, this.knobSize, this.knobSize);

    // Full outer dark ring
    int arcCenter = this.knobSize / 2;
    float arcStart = PConstants.HALF_PI + this.knobIndent;
    float arcRange = (PConstants.TWO_PI - 2 * this.knobIndent);

    pg.fill(ui.theme.getControlBackgroundColor());
    pg.stroke(ui.theme.getWindowBackgroundColor());
    pg.arc(arcCenter, arcCenter, this.knobSize, this.knobSize, arcStart,
        arcStart + arcRange);

    // Light ring indicating value
    pg.fill(isEnabled() ? ui.theme.getPrimaryColor() : ui.theme.getControlDisabledColor());
    pg.arc(arcCenter, arcCenter, this.knobSize, this.knobSize, arcStart,
        arcStart + knobValue * arcRange);

    // Center circle of knob
    pg.noStroke();
    pg.fill(0xff333333);
    pg.ellipse(arcCenter, arcCenter, arcCenter, arcCenter);

    String knobLabel;
    if (this.showValue) {
      knobLabel = (this.parameter != null) ? ("" + this.parameter.getValue())
          : null;
    } else {
      knobLabel = (this.parameter != null) ? this.parameter.getLabel() : null;
    }
    if (knobLabel == null) {
      knobLabel = "-";
    } else if (knobLabel.length() > 4) {
      knobLabel = knobLabel.substring(0, 4);
    }
    pg.noStroke();
    pg.fill(ui.theme.getControlBackgroundColor());
    pg.rect(0, this.knobSize + 2, this.knobSize, this.knobLabelHeight - 2);
    pg.fill(ui.theme.getControlTextColor());
    pg.textAlign(PConstants.CENTER);
    pg.textFont(ui.theme.getLabelFont());
    pg.text(knobLabel, arcCenter, this.knobSize + this.knobLabelHeight - 2);
  }

  private double dragValue;

  @Override
  protected void onKeyPressed(KeyEvent keyEvent, char keyChar, int keyCode) {
    super.onKeyPressed(keyEvent, keyChar, keyCode);
    if ((keyChar == ' ') || (keyCode == java.awt.event.KeyEvent.VK_ENTER)) {
      setShowValue(true);
    }
  }

  @Override
  protected void onKeyReleased(KeyEvent keyEvent, char keyChar, int keyCode) {
    super.onKeyReleased(keyEvent, keyChar, keyCode);
    if ((keyChar == ' ') || (keyCode == java.awt.event.KeyEvent.VK_ENTER)) {
      setShowValue(false);
    }
  }

  @Override
  protected void onMousePressed(MouseEvent mouseEvent, float mx, float my) {
    this.dragValue = getNormalized();
    if ((this.parameter != null) && (mouseEvent.getCount() > 1)) {
      this.parameter.reset();
    }
    this.showValue = true;
    redraw();
  }

  @Override
  protected void onMouseReleased(MouseEvent mouseEvent, float mx, float my) {
    this.showValue = false;
    redraw();
  }

  @Override
  protected void onMouseDragged(MouseEvent mouseEvent, float mx, float my, float dx, float dy) {
    if (!isEnabled()) {
      return;
    }
    this.dragValue = LXUtils.constrain(this.dragValue - dy / 100., 0, 1);
    setNormalized(this.dragValue);
  }

  @Override
  protected void onBlur() {
    setShowValue(false);
  }
}
