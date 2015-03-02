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

import processing.core.PConstants;
import processing.core.PGraphics;
import processing.event.KeyEvent;
import processing.event.MouseEvent;
import heronarts.lx.LXUtils;
import heronarts.lx.parameter.BooleanParameter;
import heronarts.lx.parameter.DiscreteParameter;
import heronarts.lx.parameter.LXListenableNormalizedParameter;
import heronarts.lx.parameter.LXParameter;
import heronarts.lx.parameter.LXParameterListener;
import heronarts.p2lx.ui.UI;
import heronarts.p2lx.ui.UI2dComponent;

public abstract class UIParameterControl extends UI2dComponent implements
    LXParameterListener {

  protected final static int LABEL_MARGIN = 2;

  protected final static int LABEL_HEIGHT = 12;

  private final static int TEXT_MARGIN = 2;

  private boolean showValue = false;

  protected LXListenableNormalizedParameter parameter = null;

  protected boolean enabled = true;

  private String label = null;

  private boolean showLabel = true;

  protected UIParameterControl(float x, float y, float w, float h) {
    super(x, y, w, h + LABEL_MARGIN + LABEL_HEIGHT);
  }

  public UIParameterControl setEnabled(boolean enabled) {
    if (enabled != this.enabled) {
      this.enabled = enabled;
      redraw();
    }
    return this;
  }

  public boolean isEnabled() {
    return (this.parameter != null) && this.enabled;
  }

  public UIParameterControl setShowLabel(boolean showLabel) {
    if (this.showLabel != showLabel) {
      this.showLabel = showLabel;
      if (this.showLabel) {
        setSize(this.width, this.height + LABEL_MARGIN + LABEL_HEIGHT);
      } else {
        setSize(this.width, this.height - LABEL_MARGIN - LABEL_HEIGHT);
      }
      redraw();
    }
    return this;
  }

  public UIParameterControl setLabel(String label) {
    if (this.label != label) {
      this.label = label;
      redraw();
    }
    return this;
  }

  @Override
  protected int getFocusColor(UI ui) {
    if (!isEnabled()) {
      return ui.theme.getControlDisabledColor();
    }
    return super.getFocusColor(ui);
  }

  public void onParameterChanged(LXParameter parameter) {
    redraw();
  }

  protected double getNormalized() {
    if (this.parameter != null) {
      return this.parameter.getNormalized();
    }
    return 0;
  }

  protected UIParameterControl setNormalized(double normalized) {
    if (this.parameter != null) {
      this.parameter.setNormalized(normalized);
    }
    return this;
  }

  public LXListenableNormalizedParameter getParameter() {
    return this.parameter;
  }

  public UIParameterControl setParameter(LXListenableNormalizedParameter parameter) {
    if (this.parameter != null) {
      this.parameter.removeListener(this);
    }
    this.parameter = parameter;
    if (this.parameter != null) {
      this.parameter.addListener(this);
    }
    redraw();
    return this;
  }

  private void setShowValue(boolean showValue) {
    if (showValue != this.showValue) {
      this.showValue = showValue;
      redraw();
    }
  }

  @Override
  protected void onDraw(UI ui, PGraphics pg) {
    if (this.showLabel) {
      drawLabel(ui, pg);
    }
  }

  private void drawLabel(UI ui, PGraphics pg) {
    String labelText;
    if (this.showValue && (this.parameter != null)) {
      if (this.parameter instanceof DiscreteParameter) {
        labelText = "" + (int) this.parameter.getValue();
      } else if (this.parameter instanceof BooleanParameter) {
        labelText = (this.parameter.getValue() > 0) ? "ON" : "OFF";
      } else {
        labelText = String.format("%.2f", this.parameter.getValue());
      }
    } else {
      labelText = (this.label != null) ? this.label :
        ((this.parameter != null) ? this.parameter.getLabel() : null);
    }
    if (labelText == null) {
      labelText = "-";
    }

    pg.noStroke();
    pg.fill(ui.theme.getControlBackgroundColor());
    pg.rect(0, this.height - LABEL_HEIGHT, this.width, LABEL_HEIGHT);
    pg.fill(ui.theme.getControlTextColor());
    pg.textAlign(PConstants.CENTER);
    pg.textFont(ui.theme.getLabelFont());

    while (pg.textWidth(labelText) > (this.width - TEXT_MARGIN)) {
      labelText = labelText.substring(0, labelText.length() - 1);
    }

    pg.text(labelText, this.width/2, this.height - TEXT_MARGIN);
  }

  @Override
  protected void onKeyPressed(KeyEvent keyEvent, char keyChar, int keyCode) {
    if ((keyChar == ' ') || (keyCode == java.awt.event.KeyEvent.VK_ENTER)) {
      setShowValue(true);
    }
    if (!isEnabled()) {
      return;
    }
    if (this.parameter instanceof DiscreteParameter) {
      DiscreteParameter dp = (DiscreteParameter) this.parameter;
      int times = keyEvent.isShiftDown() ? Math.max(1, dp.getRange() / 10) : 1;
      if ((keyCode == java.awt.event.KeyEvent.VK_LEFT)
          || (keyCode == java.awt.event.KeyEvent.VK_DOWN)) {
        dp.setValue(dp.getValuei() - times);
      } else if ((keyCode == java.awt.event.KeyEvent.VK_RIGHT)
          || (keyCode == java.awt.event.KeyEvent.VK_UP)) {
        dp.setValue(dp.getValuei() + times);
      }
    } else if (this.parameter instanceof BooleanParameter) {
      BooleanParameter bp = (BooleanParameter) this.parameter;
      if ((keyCode == java.awt.event.KeyEvent.VK_LEFT)
        || (keyCode == java.awt.event.KeyEvent.VK_DOWN)) {
        bp.setValue(false);
      } else if ((keyCode == java.awt.event.KeyEvent.VK_RIGHT)
        || (keyCode == java.awt.event.KeyEvent.VK_UP)) {
        bp.setValue(true);
      }
    } else {
      double amount = keyEvent.isShiftDown() ? .05f : .01f;
      if ((keyCode == java.awt.event.KeyEvent.VK_LEFT)
          || (keyCode == java.awt.event.KeyEvent.VK_DOWN)) {
        setNormalized(LXUtils.constrain(getNormalized() - amount, 0, 1));
      } else if ((keyCode == java.awt.event.KeyEvent.VK_RIGHT)
          || (keyCode == java.awt.event.KeyEvent.VK_UP)) {
        setNormalized(LXUtils.constrain(getNormalized() + amount, 0, 1));
      }
    }
  }

  @Override
  protected void onKeyReleased(KeyEvent keyEvent, char keyChar, int keyCode) {
    if ((keyChar == ' ') || (keyCode == java.awt.event.KeyEvent.VK_ENTER)) {
      setShowValue(false);
    }
  }

  @Override
  protected void onMousePressed(MouseEvent mouseEvent, float mx, float my) {
    setShowValue(true);
  }

  @Override
  protected void onMouseReleased(MouseEvent mouseEvent, float mx, float my) {
    setShowValue(false);
  }

  @Override
  protected void onBlur() {
    setShowValue(false);
  }

}
