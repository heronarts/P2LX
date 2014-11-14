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
import processing.core.PGraphics;
import processing.event.MouseEvent;

public class UISlider extends UIParameterControl implements UIFocus {

  public enum Direction {
    HORIZONTAL, VERTICAL
  };

  private final Direction direction;

  private static final float HANDLE_WIDTH = 12;

  public UISlider() {
    this(0, 0, 0, 0);
  }

  public UISlider(float x, float y, float w, float h) {
    this(Direction.HORIZONTAL, x, y, w, h);
  }

  public UISlider(Direction direction, float x, float y, float w, float h) {
    super(x, y, w, h);
    setBackgroundColor(UI.get().theme.getWindowBackgroundColor());
    this.direction = direction;
  }

  @Override
  protected void onDraw(UI ui, PGraphics pg) {
    pg.noStroke();
    pg.fill(ui.theme.getControlBackgroundColor());
    switch (this.direction) {
    case HORIZONTAL:
      pg.rect(4, this.height / 2 - 2, this.width - 8, 4);
      pg.fill(isEnabled() ? ui.theme.getPrimaryColor() : ui.theme.getControlDisabledColor());
      pg.rect(4, this.height / 2 - 2, (int) ((this.width - 8) * getNormalized()), 4);
      pg.fill(ui.theme.getControlDisabledColor());
      pg.stroke(ui.theme.getControlBorderColor());
      pg.rect((int) (4 + getNormalized() * (this.width - 8 - HANDLE_WIDTH)), 4,
          HANDLE_WIDTH, this.height - 8);
      break;
    case VERTICAL:
      pg.rect(this.width / 2 - 2, 4, 4, this.height - 8);
      pg.fill(isEnabled() ? ui.theme.getPrimaryColor() : ui.theme.getControlDisabledColor());
      int fillSize = (int) (getNormalized() * (this.height - 8));
      pg.rect(this.width / 2 - 2, this.height - 4 - fillSize, 4, fillSize);
      pg.fill(ui.theme.getControlDisabledColor());
      pg.stroke(ui.theme.getControlBorderColor());
      pg.rect(4, (int) (4 + (1 - getNormalized())
          * (this.height - 8 - HANDLE_WIDTH)), this.width - 8, HANDLE_WIDTH);
      break;
    }
  }

  private boolean editing = false;
  private float doubleClickMode = 0;
  private float doubleClickP = 0;

  @Override
  protected void onMousePressed(MouseEvent mouseEvent, float mx, float my) {
    float mp, dim;
    double handleEdge;
    boolean isVertical = false;
    switch (this.direction) {
    case VERTICAL:
      handleEdge = 4 + (1 - getNormalized()) * (this.height - 8 - HANDLE_WIDTH);
      mp = my;
      dim = this.height;
      isVertical = true;
      break;
    default:
    case HORIZONTAL:
      handleEdge = 4 + getNormalized() * (this.width - 8 - HANDLE_WIDTH);
      mp = mx;
      dim = this.width;
      break;
    }
    if ((mp >= handleEdge) && (mp < handleEdge + HANDLE_WIDTH)) {
      this.editing = true;
    } else {
      if ((mouseEvent.getCount() > 1) && Math.abs(mp - this.doubleClickP) < 3) {
        setNormalized(this.doubleClickMode);
      }
      this.doubleClickP = mp;
      if (mp < dim * .25) {
        this.doubleClickMode = isVertical ? 1 : 0;
      } else if (mp > dim * .75) {
        this.doubleClickMode = isVertical ? 0 : 1;
      } else {
        this.doubleClickMode = 0.5f;
      }
    }
  }

  @Override
  protected void onMouseReleased(MouseEvent mouseEvent, float mx, float my) {
    this.editing = false;
  }

  @Override
  protected void onMouseDragged(MouseEvent mouseEvent, float mx, float my, float dx, float dy) {
    if (isEnabled() && this.editing) {
      float mp, dim;
      switch (this.direction) {
      case VERTICAL:
        mp = my;
        dim = this.height;
        setNormalized(1 - LXUtils.constrain((mp - HANDLE_WIDTH / 2. - 4)
            / (dim - 8 - HANDLE_WIDTH), 0, 1));
        break;
      default:
      case HORIZONTAL:
        mp = mx;
        dim = this.width;
        setNormalized(LXUtils.constrain((mp - HANDLE_WIDTH / 2. - 4)
            / (dim - 8 - HANDLE_WIDTH), 0, 1));
        break;
      }

    }
  }
}
