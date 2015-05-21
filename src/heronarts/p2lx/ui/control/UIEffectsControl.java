/**
 * Copyright 2015- Mark C. Slee, Heron Arts LLC
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
 * @author L8on <lwallace@gmail.com>
 */

package heronarts.p2lx.ui.control;

import heronarts.lx.LX;
import heronarts.lx.LXChannel;
import heronarts.lx.effect.LXEffect;
import heronarts.lx.parameter.LXListenableNormalizedParameter;
import heronarts.lx.parameter.LXParameter;
import heronarts.p2lx.ui.UI;
import heronarts.p2lx.ui.UIWindow;
import heronarts.p2lx.ui.component.UIItemList;
import heronarts.p2lx.ui.component.UIKnob;

import java.util.ArrayList;
import java.util.List;

/**
 * UIWindow implementation to control a list of effects.
 * It ultimately operates on a static List of LXEffects,
 * but has convenient constructors that accept an
 * LX or LXChannel object as the source of effects.
 */
public class UIEffectsControl extends UIWindow {
  private final static String DEFAULT_TITLE = "EFFECT";
  private final static int DEFAULT_NUM_KNOBS = 4;
  private final static int KNOBS_PER_ROW = 4;
  private final static int KNOB_ROW_HEIGHT = 48;
  private final static int BASE_HEIGHT = 174;
  public final static int WIDTH = 140;

  private final UIKnob[] knobs;
  private LXEffect selectedEffect;

  public UIEffectsControl(UI ui, LX lx, float x, float y) {
    this(ui, lx, DEFAULT_TITLE, x, y);
  }

  public UIEffectsControl(UI ui, LX lx, int numKnobs, float x, float y) {
    this(ui, lx, DEFAULT_TITLE, numKnobs,  x, y);
  }

  public UIEffectsControl(UI ui, LX lx, String label, float x, float y) {
    this(ui, lx, label, DEFAULT_NUM_KNOBS, x, y);
  }

  public UIEffectsControl(UI ui, LX lx, String label, int numKnobs, float x, float y) {
    this(ui, lx.getEffects(), label, numKnobs, x, y);
  }

  public UIEffectsControl(UI ui, LXChannel channel, float x, float y) {
    this(ui, channel, DEFAULT_TITLE, x, y);
  }

  public UIEffectsControl(UI ui, LXChannel channel, String label, float x, float y) {
    this(ui, channel.getEffects(), label, DEFAULT_NUM_KNOBS, x, y);
  }

  public UIEffectsControl(UI ui, LXChannel channel, int numKnobs, float x, float y) {
    this(ui, channel.getEffects(), DEFAULT_TITLE, numKnobs, x, y);
  }

  public UIEffectsControl(UI ui, List<LXEffect> effects, String label, int numKnobs, float x, float y) {
    super(ui, label, x, y, WIDTH, BASE_HEIGHT + KNOB_ROW_HEIGHT * (numKnobs / KNOBS_PER_ROW));

    int yp = TITLE_LABEL_HEIGHT;

    List<UIItemList.Item> items = new ArrayList<UIItemList.Item>();
    for (LXEffect eff : effects) {
      items.add(new EffectScrollItem(eff));
    }
    final UIItemList effectList =
      new UIItemList(1, yp, this.width - 2, 140)
      .setItems(items);

    effectList
      .setBackgroundColor(ui.theme.getWindowBackgroundColor())
      .addToContainer(this);
    yp += effectList.getHeight() + 10;

    this.knobs = new UIKnob[numKnobs];
    for (int ki = 0; ki < knobs.length; ++ki) {
      knobs[ki] = new UIKnob(5 + 34 * (ki % KNOBS_PER_ROW), yp
          + (ki / KNOBS_PER_ROW) * KNOB_ROW_HEIGHT);
      knobs[ki].addToContainer(this);
    }

    if (effects.size() > 0) {
      selectEffect(effects.get(0));
    }
  }

  private void selectEffect(LXEffect effect) {
    this.selectedEffect = effect;

    int pi = 0;
    for (LXParameter parameter : effect.getParameters()) {
      if (pi >= this.knobs.length) {
        break;
      }
      if (parameter instanceof LXListenableNormalizedParameter) {
        this.knobs[pi++].setParameter((LXListenableNormalizedParameter) parameter);
      }
    }

    while (pi < this.knobs.length) {
      this.knobs[pi++].setParameter(null);
    }
  }

  private class EffectScrollItem extends UIItemList.AbstractItem {

    private final LXEffect effect;
    private final String label;

    EffectScrollItem(LXEffect effect) {
      this.effect = effect;
      this.label = UI.uiClassName(effect, "Effect");
    }

    public String getLabel() {
      return this.label;
    }

    public boolean isSelected() {
      return selectedEffect == this.effect;
    }

    @Override
    public boolean isPending() {
      return this.effect.isEnabled();
    }

    @Override
    public void onMousePressed() {
      if (!this.isSelected()) {
        selectEffect(this.effect);
      }

      if (this.effect.isMomentary()) {
        this.effect.enable();
      } else {
        this.effect.toggle();
      }
    }

    @Override
    public void onMouseReleased() {
      if (this.effect.isMomentary()) {
        this.effect.disable();
      }
    }
  }
}
