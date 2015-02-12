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
import processing.event.MouseEvent;

public class UIKnob extends UIParameterControl implements UIFocus {

  public final static int KNOB_SIZE = 28;

  private final static float KNOB_INDENT = .4f;

  public UIKnob() {
    this(0, 0);
  }

  public UIKnob(float x, float y) {
    this(x, y, KNOB_SIZE, KNOB_SIZE);
  }

  public UIKnob(float x, float y, float w, float h) {
    super(x, y, w, h);
  }

  @Override
  protected void onDraw(UI ui, PGraphics pg) {
    float knobValue = (float) getNormalized();

    pg.ellipseMode(PConstants.CENTER);

    pg.noStroke();
    pg.fill(ui.theme.getWindowBackgroundColor());
    pg.rect(0, 0, KNOB_SIZE, KNOB_SIZE);

    // Full outer dark ring
    int arcCenter = KNOB_SIZE / 2;
    float arcStart = PConstants.HALF_PI + KNOB_INDENT;
    float arcRange = (PConstants.TWO_PI - 2 * KNOB_INDENT);

    pg.fill(ui.theme.getControlBackgroundColor());
    pg.stroke(ui.theme.getWindowBackgroundColor());
    pg.arc(arcCenter, arcCenter, KNOB_SIZE, KNOB_SIZE, arcStart,
        arcStart + arcRange);

    // Light ring indicating value
    pg.fill(isEnabled() ? ui.theme.getPrimaryColor() : ui.theme.getControlDisabledColor());
    pg.arc(arcCenter, arcCenter, KNOB_SIZE, KNOB_SIZE, arcStart,
        arcStart + knobValue * arcRange);

    // Center circle of knob
    pg.noStroke();
    pg.fill(0xff333333);
    pg.ellipse(arcCenter, arcCenter, arcCenter, arcCenter);

    super.onDraw(ui, pg);
  }

  private double dragValue;

  @Override
  protected void onMousePressed(MouseEvent mouseEvent, float mx, float my) {
    super.onMousePressed(mouseEvent, mx, my);
    this.dragValue = getNormalized();
    if ((this.parameter != null) && (mouseEvent.getCount() > 1)) {
      this.parameter.reset();
    }
  }

  @Override
  protected void onMouseDragged(MouseEvent mouseEvent, float mx, float my, float dx, float dy) {
    if (!isEnabled()) {
      return;
    }
    this.dragValue = LXUtils.constrain(this.dragValue - dy / 100., 0, 1);
    setNormalized(this.dragValue);
  }

}
