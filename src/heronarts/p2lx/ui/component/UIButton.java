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

import heronarts.lx.parameter.BooleanParameter;
import heronarts.lx.parameter.LXParameter;
import heronarts.lx.parameter.LXParameterListener;
import heronarts.p2lx.ui.UI;
import heronarts.p2lx.ui.UIFocus;
import heronarts.p2lx.ui.UIObject;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.event.KeyEvent;

public class UIButton extends UIObject implements UIFocus {

  protected boolean active = false;
  protected boolean isMomentary = false;

  protected int inactiveColor = 0xff222222;
  protected int activeColor = UI.get().getHighlightColor();
  protected int labelColor = 0xff999999;

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
    setBorderColor(0xff666666);
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
      pg.fill(this.active ? 0xffffffff : this.labelColor);
      pg.textFont(ui.getItemFont());
      pg.textAlign(PConstants.CENTER);
      pg.text(label, this.width / 2, this.height - 5);
    }
  }

  @Override
  protected void onMousePressed(float mx, float my) {
    setActive(this.isMomentary ? true : !this.active);
  }

  @Override
  protected void onMouseReleased(float mx, float my) {
    if (this.isMomentary) {
      setActive(false);
    }
  }

  @Override
  public void onKeyPressed(KeyEvent keyEvent, char keyChar, int keyCode) {
    if ((keyChar == ' ') || (keyCode == java.awt.event.KeyEvent.VK_ENTER)) {
      setActive(this.isMomentary ? true : !this.active);
    }
  }

  @Override
  public void onKeyReleased(KeyEvent keyEvent, char keyChar, int keyCode) {
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

  public UIButton setLabelColor(int labelColor) {
    if (this.labelColor != labelColor) {
      this.labelColor = labelColor;
      redraw();
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
