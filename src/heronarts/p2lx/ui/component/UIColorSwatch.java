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

import processing.core.PGraphics;
import processing.event.KeyEvent;
import heronarts.lx.LXUtils;
import heronarts.lx.color.ColorParameter;
import heronarts.lx.color.LXColor;
import heronarts.lx.parameter.LXParameter;
import heronarts.lx.parameter.LXParameterListener;
import heronarts.p2lx.ui.UI;
import heronarts.p2lx.ui.UIFocus;
import heronarts.p2lx.ui.UIObject;

public class UIColorSwatch extends UIObject implements UIFocus {

  public final ColorParameter color;

  private final static int RECT_SIZE = 3;

  public UIColorSwatch(ColorParameter color, float x, float y, float w, float h) {
    super(x, y, w, h);
    this.color = color;
    color.addListener(new LXParameterListener() {
      public void onParameterChanged(LXParameter p) {
        redraw();
      }
    });
  }

  @Override
  protected void onDraw(UI ui, PGraphics pg) {
    // Gradient
    pg.noStroke();
    for (int x = 0; x < this.width; ++x) {
      for (int y = 0; y < this.height; ++y) {
        pg.fill(LXColor.hsb(
          360 * x / this.width,
          100 * (1 - y / this.height),
          this.color.brightness.getValuef()
        ));
        pg.rect(x, y, 1, 1);
      }
    }

    // Cursor
    pg.strokeWeight(1);
    pg.noFill();
    pg.stroke(ui.theme.getWindowBorderColor());
    pg.rect(
      this.color.hue.getNormalizedf() * (this.width-RECT_SIZE-1),
      (1-this.color.saturation.getNormalizedf()) * (this.height-RECT_SIZE-1),
      RECT_SIZE, RECT_SIZE
    );
  }

  @Override
  protected void onMousePressed(float mx, float my) {
    updateColor(mx, my);
  }

  @Override
  protected void onMouseDragged(float mx, float my, float dx, float dy) {
    updateColor(mx, my);
  }

  private void updateColor(float mx, float my) {
    double mxn = LXUtils.constrain(mx / (this.width-RECT_SIZE-1), 0, 1);
    double myn = LXUtils.constrain(my / (this.height-RECT_SIZE-1), 0, 1);
    this.color.hue.setNormalized(mxn);
    this.color.saturation.setNormalized(1 - myn);
  }

  @Override
  protected void onKeyPressed(KeyEvent keyEvent, char keyChar, int keyCode) {
    double amount = keyEvent.isShiftDown() ? .05 : .01;
    if (keyCode == java.awt.event.KeyEvent.VK_LEFT) {
      this.color.hue.setNormalized(this.color.hue.getNormalized() - amount);
    } else if (keyCode == java.awt.event.KeyEvent.VK_RIGHT) {
      this.color.hue.setNormalized(this.color.hue.getNormalized() + amount);
    } else if (keyCode == java.awt.event.KeyEvent.VK_UP) {
      this.color.saturation.setNormalized(this.color.saturation.getNormalized() + amount);
    } else if (keyCode == java.awt.event.KeyEvent.VK_DOWN) {
      this.color.saturation.setNormalized(this.color.saturation.getNormalized() - amount);
    }
  }
}
