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
import heronarts.lx.color.LXColor;
import heronarts.lx.parameter.DiscreteParameter;
import heronarts.lx.parameter.LXParameter;
import heronarts.lx.parameter.LXParameterListener;
import heronarts.p2lx.ui.UI;
import heronarts.p2lx.ui.UIFocus;
import heronarts.p2lx.ui.UI2dTextComponent;

import java.util.ArrayList;
import java.util.List;

import processing.core.PConstants;
import processing.core.PGraphics;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

/**
 * UI for a list of state items
 */
public class UIItemList extends UI2dTextComponent implements UIFocus {

  public static interface Item {

    public boolean isSelected();

    public boolean isPending();

    public String getLabel();

    public void onMousePressed();

    public void onMouseReleased();
  }

  public static abstract class AbstractItem implements Item {
    public boolean isPending() {
      return false;
    }

    public void onMousePressed() {
    }

    public void onMouseReleased() {
    }
  }

  private List<Item> items = new ArrayList<Item>();

  public final DiscreteParameter focusIndex = new DiscreteParameter("FOCUS", 1);
  public final DiscreteParameter scrollOffset = new DiscreteParameter("OFFSET",
      1);

  private int itemHeight = 20;
  private int numVisibleItems = 0;

  private boolean hasScroll;
  private float scrollYStart;
  private float scrollYHeight;

  public UIItemList() {
    this(0, 0, 0, 0);
  }

  public UIItemList(float x, float y, float w, float h) {
    super(x, y, w, h);
    this.focusIndex.addListener(new LXParameterListener() {
      public void onParameterChanged(LXParameter parameter) {
        onFocusIndexChanged();
      }
    });
    this.scrollOffset.addListener(new LXParameterListener() {
      public void onParameterChanged(LXParameter parameter) {
        onScrollOffsetChanged();
      }
    });
  }

  public int getFocusIndex() {
    return this.focusIndex.getValuei();
  }

  public UIItemList setFocusIndex(int focusIndex) {
    this.focusIndex.setValue(focusIndex);
    return this;
  }

  private void onFocusIndexChanged() {
    int fi = this.focusIndex.getValuei();
    int so = this.scrollOffset.getValuei();
    if (fi < so) {
      setScrollOffset(fi);
    } else if (fi >= (so + this.numVisibleItems)) {
      setScrollOffset(fi - this.numVisibleItems + 1);
    }
    redraw();
  }

  public UIItemList select() {
    Item selectedItem = this.items.get(getFocusIndex());
    selectedItem.onMousePressed();
    selectedItem.onMouseReleased();
    redraw();
    return this;
  }

  @Override
  protected void onDraw(UI ui, PGraphics pg) {
    int yp = 0;
    boolean even = true;
    int so = getScrollOffset();
    int fi = getFocusIndex();
    pg.strokeWeight(1);
    for (int i = 0; i < this.numVisibleItems; ++i) {
      if (i + so >= this.items.size()) {
        break;
      }
      int itemIndex = i + so;
      Item item = this.items.get(itemIndex);
      int itemColor;
      int labelColor;
      if (item.isSelected()) {
        labelColor = ui.WHITE;
        itemColor = ui.theme.getPrimaryColor();
      } else if (item.isPending()) {
        labelColor = 0xfff0f0f0;
        itemColor = ui.theme.getSecondaryColor();
      } else {
        labelColor = ui.BLACK;
        itemColor = ui.theme.getControlDisabledColor();
      }
      float factor = even ? .92f : 1.08f;
      itemColor = LXColor.scaleBrightness(itemColor, factor);

      pg.noStroke();
      pg.fill(itemColor);
      pg.rect(0, yp, this.width, this.itemHeight);
      pg.fill(labelColor);
      pg.textFont(hasFont() ? getFont() : ui.theme.getControlFont());
      pg.textAlign(PConstants.LEFT, PConstants.TOP);
      pg.text(item.getLabel(), 6, yp + 4);

      if (itemIndex == fi) {
        pg.stroke(item.isSelected() ? ui.theme.getControlTextColor() : ui.theme.getFocusColor());
        pg.noFill();
        pg.rect(0, yp, this.width - 1, this.itemHeight - 1);
      }

      yp += this.itemHeight;
      even = !even;
    }
    if (this.hasScroll) {
      pg.noStroke();
      pg.fill(0x26ffffff);
      pg.rect(this.width - 12, 0, 12, this.height);
      pg.fill(ui.theme.getControlBackgroundColor());
      pg.rect(this.width - 11, this.scrollYStart+1, 10, this.scrollYHeight-2);
    }

  }

  private boolean scrolling = false;
  private Item pressedItem = null;

  @Override
  protected void onKeyPressed(KeyEvent keyEvent, char keyChar, int keyCode) {
    int index = getFocusIndex();
    if (keyCode == java.awt.event.KeyEvent.VK_UP) {
      index = Math.max(0, index - 1);
    } else if (keyCode == java.awt.event.KeyEvent.VK_DOWN) {
      index = Math.min(index + 1, this.items.size() - 1);
    } else if ((keyChar == ' ')
        || (keyCode == java.awt.event.KeyEvent.VK_ENTER)) {
      select();
    }
    setFocusIndex(index);
  }

  @Override
  protected void onMousePressed(MouseEvent mouseEvent, float mx, float my) {
    this.pressedItem = null;
    if (this.hasScroll && mx >= this.width - 12) {
      if ((my >= this.scrollYStart)
          && (my < (this.scrollYStart + this.scrollYHeight))) {
        this.scrolling = true;
        this.dAccum = 0;
      }
    } else {
      int index = (int) my / this.itemHeight;
      if (getScrollOffset() + index < this.items.size()) {
        setFocusIndex(getScrollOffset() + index);
        this.pressedItem = this.items.get(getFocusIndex());
        this.pressedItem.onMousePressed();
        redraw();
      }
    }
  }

  @Override
  protected void onMouseReleased(MouseEvent mouseEvent, float mx, float my) {
    this.scrolling = false;
    if (this.pressedItem != null) {
      this.pressedItem.onMouseReleased();
      redraw();
    }
  }

  private float dAccum = 0;

  @Override
  protected void onMouseDragged(MouseEvent mouseEvent, float mx, float my, float dx, float dy) {
    if (this.scrolling) {
      this.dAccum += dy;
      float scrollOne = this.height / this.items.size();
      int offset = (int) (this.dAccum / scrollOne);
      if (offset != 0) {
        this.dAccum -= offset * scrollOne;
        moveScrollOffset(offset);
      }
    }
  }

  private float wAccum = 0;

  @Override
  protected void onMouseWheel(MouseEvent mouseEvent, float mx, float my, float delta) {
    if (!hasFocus()) {
      // TODO(mcslee): focus
    }
    this.wAccum += delta;
    int offset = (int) (this.wAccum / 5);
    if (offset != 0) {
      this.wAccum -= offset * 5;
      moveScrollOffset(offset);
    }
  }

  public int getScrollOffset() {
    return this.scrollOffset.getValuei();
  }

  public UIItemList setScrollOffset(int offset) {
    this.scrollOffset.setValue(LXUtils.constrain(offset, 0, this.items.size()
        - this.numVisibleItems));
    return this;
  }

  private void moveScrollOffset(int delta) {
    setScrollOffset(getScrollOffset() + delta);
  }

  private void onScrollOffsetChanged() {
    int so = this.scrollOffset.getValuei();
    this.scrollYStart = Math.round(so * this.height / this.items.size());
    this.scrollYHeight = Math.round(this.numVisibleItems * this.height
        / this.items.size());
    int fi = this.focusIndex.getValuei();
    if ((fi < so) || (fi > so + this.numVisibleItems - 1)) {
      setFocusIndex(so);
    }
    redraw();
  }

  public UIItemList setItems(List<Item> items) {
    this.items = items;
    this.numVisibleItems = (int) (this.height / this.itemHeight);
    this.hasScroll = this.items.size() > this.numVisibleItems;
    this.focusIndex.setRange(0, this.items.size());
    this.scrollOffset.setRange(0,
        Math.max(0, this.items.size() - this.numVisibleItems) + 1);
    onScrollOffsetChanged();
    redraw();
    return this;
  }
}
