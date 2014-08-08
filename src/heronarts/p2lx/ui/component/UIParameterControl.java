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

  protected UIParameterControl(float x, float y, float w, float h) {
    super(x, y, w, h);
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

  public UIParameterControl setParameter(
      LXListenableNormalizedParameter parameter) {
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
      float amount = keyEvent.isShiftDown() ? .05f : .01f;
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
