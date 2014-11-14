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

import heronarts.lx.parameter.BooleanParameter;
import heronarts.lx.parameter.LXParameter;
import heronarts.lx.parameter.LXParameterListener;
import heronarts.p2lx.ui.UI;
import heronarts.p2lx.ui.UIFocus;
import heronarts.p2lx.ui.UI2dTextComponent;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

public class UIButton extends UI2dTextComponent implements UIFocus {

  protected boolean active = false;
  protected boolean isMomentary = false;

  protected int inactiveColor = UI.get().theme.getControlBackgroundColor();
  protected int activeColor = UI.get().theme.getPrimaryColor();

  private String activeLabel = "";
  private String inactiveLabel = "";

  private BooleanParameter parameter = null;

  private final LXParameterListener parameterListener = new LXParameterListener() {
    public void onParameterChanged(LXParameter p) {
      setActive(parameter.isOn());
    }
  };

  public UIButton() {
    this(0, 0, 0, 0);
  }

  public UIButton(float x, float y, float w, float h) {
    super(x, y, w, h);
    setBorderColor(UI.get().theme.getControlBorderColor());
    setFontColor(UI.get().theme.getControlTextColor());
    setBackgroundColor(this.inactiveColor);
  }

  public BooleanParameter getParameter() {
    return this.parameter;
  }

  public UIButton setParameter(BooleanParameter parameter) {
    if (this.parameter != null) {
      this.parameter.removeListener(this.parameterListener);
    }
    this.parameter = parameter;
    if (parameter != null) {
      parameter.addListener(this.parameterListener);
      setActive(parameter.isOn());
    }
    return this;
  }

  public UIButton setMomentary(boolean momentary) {
    this.isMomentary = momentary;
    return this;
  }

  @Override
  protected void onDraw(UI ui, PGraphics pg) {
    String label = this.active ? this.activeLabel : this.inactiveLabel;
    if ((label != null) && (label.length() > 0)) {
      pg.fill(this.active ? 0xffffffff : getFontColor());
      pg.textFont(hasFont() ? getFont() : ui.theme.getControlFont());
      pg.textAlign(PConstants.CENTER);
      pg.text(label, this.width / 2, (int) (this.height * .75));
    }
  }

  @Override
  protected void onMousePressed(MouseEvent mouseEvent, float mx, float my) {
    setActive(this.isMomentary ? true : !this.active);
  }

  @Override
  protected void onMouseReleased(MouseEvent mouseEvent, float mx, float my) {
    if (this.isMomentary) {
      setActive(false);
    }
  }

  @Override
  protected void onKeyPressed(KeyEvent keyEvent, char keyChar, int keyCode) {
    if ((keyChar == ' ') || (keyCode == java.awt.event.KeyEvent.VK_ENTER)) {
      setActive(this.isMomentary ? true : !this.active);
    }
  }

  @Override
  protected void onKeyReleased(KeyEvent keyEvent, char keyChar, int keyCode) {
    if ((keyChar == ' ') || (keyCode == java.awt.event.KeyEvent.VK_ENTER)) {
      if (this.isMomentary) {
        setActive(false);
      }
    }
  }

  public boolean isActive() {
    return this.active;
  }

  public UIButton setActive(boolean active) {
    if (this.active != active) {
      if (this.parameter != null) {
        this.parameter.setValue(active);
      }
      setBackgroundColor(active ? this.activeColor : this.inactiveColor);
      onToggle(this.active = active);
      redraw();
    }
    return this;
  }

  public UIButton toggle() {
    return setActive(!this.active);
  }

  /**
   * Subclasses may override this to handle changes to the button's state
   *
   * @param active Whether button is active
   */
  protected void onToggle(boolean active) {
  }

  public UIButton setActiveColor(int activeColor) {
    if (this.activeColor != activeColor) {
      this.activeColor = activeColor;
      if (this.active) {
        setBackgroundColor(activeColor);
      }
    }
    return this;
  }

  public UIButton setInactiveColor(int inactiveColor) {
    if (this.inactiveColor != inactiveColor) {
      this.inactiveColor = inactiveColor;
      if (!this.active) {
        setBackgroundColor(inactiveColor);
      }
    }
    return this;
  }

  public UIButton setLabel(String label) {
    setActiveLabel(label);
    setInactiveLabel(label);
    return this;
  }

  public UIButton setActiveLabel(String activeLabel) {
    if (!this.activeLabel.equals(activeLabel)) {
      this.activeLabel = activeLabel;
      if (this.active) {
        redraw();
      }
    }
    return this;
  }

  public UIButton setInactiveLabel(String inactiveLabel) {
    if (!this.inactiveLabel.equals(inactiveLabel)) {
      this.inactiveLabel = inactiveLabel;
      if (!this.active) {
        redraw();
      }
    }
    return this;
  }
}
