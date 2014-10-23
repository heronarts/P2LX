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

import processing.event.KeyEvent;
import heronarts.lx.LXUtils;
import heronarts.lx.parameter.DiscreteParameter;
import heronarts.lx.parameter.LXListenableNormalizedParameter;
import heronarts.lx.parameter.LXParameter;
import heronarts.lx.parameter.LXParameterListener;
import heronarts.p2lx.ui.UIObject;

public abstract class UIParameterControl extends UIObject implements
    LXParameterListener {

  protected LXListenableNormalizedParameter parameter = null;

  protected boolean enabled = true;

  protected UIParameterControl(float x, float y, float w, float h) {
    super(x, y, w, h);
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

  public LXParameter getParameter() {
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

  @Override
  protected void onKeyPressed(KeyEvent keyEvent, char keyChar, int keyCode) {
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

}
