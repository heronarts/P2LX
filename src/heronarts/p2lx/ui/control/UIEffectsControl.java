package heronarts.p2lx.ui.control;

import heronarts.lx.LX;
import heronarts.lx.LXChannel;
import heronarts.lx.effect.LXEffect;
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

  private final List<LXEffect> effects;// = new ArrayList<LXEffect>();
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

    this.effects = effects;
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

    final UIKnob[] knobs = new UIKnob[numKnobs];
    for (int ki = 0; ki < knobs.length; ++ki) {
      knobs[ki] = new UIKnob(5 + 34 * (ki % KNOBS_PER_ROW), yp
          + (ki / KNOBS_PER_ROW) * KNOB_ROW_HEIGHT);
      knobs[ki].addToContainer(this);
    }
  }

  private void selectEffect(LXEffect effect) {
    this.selectedEffect = effect;
  }

  private class EffectScrollItem extends UIItemList.AbstractItem {

    private LXEffect effect;

    private String label;

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
      return effect.isEnabled();
    }

    @Override
    public void onMousePressed() {
      if (!this.isSelected()) {
        selectEffect(effect);
      }

      if (effect.isMomentary()) {
        effect.enable();
      } else {
        effect.toggle();
      }
    }

    public void onMouseReleased() {
      if (effect.isMomentary()) {
        effect.disable();
      }
    }
  }
}
