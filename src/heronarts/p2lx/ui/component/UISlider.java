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

import heronarts.lx.LXUtils;
import heronarts.p2lx.ui.UI;
import heronarts.p2lx.ui.UIFocus;
import processing.core.PGraphics;

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
    this.direction = direction;
    setBackgroundColor(0xff333333);
    setBorderColor(0xff666666);
  }

  @Override
  protected void onDraw(UI ui, PGraphics pg) {
    pg.noStroke();
    pg.fill(0xff222222);
    switch (this.direction) {
    case HORIZONTAL:
      pg.rect(4, this.height / 2 - 2, this.width - 8, 4);
      pg.fill(0xff666666);
      pg.stroke(0xff222222);
      pg.rect((int) (4 + getNormalized() * (this.width - 8 - HANDLE_WIDTH)), 4,
          HANDLE_WIDTH, this.height - 8);
      break;
    case VERTICAL:
      pg.rect(this.width / 2 - 2, 4, 4, this.height - 8);
      pg.fill(0xff666666);
      pg.stroke(0xff222222);
      pg.rect(4, (int) (4 + (1 - getNormalized())
          * (this.height - 8 - HANDLE_WIDTH)), this.width - 8, HANDLE_WIDTH);
      break;
    }
  }

  private boolean editing = false;
  private long lastClick = 0;
  private float doubleClickMode = 0;
  private float doubleClickP = 0;

  @Override
  protected void onMousePressed(float mx, float my) {
    long now = System.currentTimeMillis();
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
      if ((now - this.lastClick) < DOUBLE_CLICK_THRESHOLD
          && Math.abs(mp - this.doubleClickP) < 3) {
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
    this.lastClick = now;
  }

  @Override
  protected void onMouseReleased(float mx, float my) {
    this.editing = false;
  }

  @Override
  protected void onMouseDragged(float mx, float my, float dx, float dy) {
    if (this.editing) {
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
