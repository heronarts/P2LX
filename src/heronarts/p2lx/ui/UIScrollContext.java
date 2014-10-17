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

package heronarts.p2lx.ui;

import heronarts.lx.LXUtils;

public class UIScrollContext extends UIContext {

  private float scrollWidth;
  private float scrollHeight;

  public UIScrollContext(UI ui, float x, float y, float w, float h) {
    super(ui, x, y, w, h);
    this.scrollWidth = w;
    this.scrollHeight = h;
  }

  public UIScrollContext setScrollSize(float scrollWidth, float scrollHeight) {
    if ((this.scrollWidth != scrollWidth)
        || (this.scrollHeight != scrollHeight)) {
      this.scrollWidth = scrollWidth;
      this.scrollHeight = scrollHeight;
      rescroll();
    }
    return this;
  }

  public UIScrollContext setScrollHeight(float scrollHeight) {
    if (this.scrollHeight != scrollHeight) {
      this.scrollHeight = scrollHeight;
      rescroll();
    }
    return this;
  }

  public UIScrollContext setScrollWidth(float scrollWidth) {
    if (this.scrollWidth != scrollWidth) {
      this.scrollWidth = scrollWidth;
      rescroll();
    }
    return this;
  }

  protected void onResize() {
    super.onResize();
    rescroll();
  }

  private float minScrollX() {
    return Math.min(0, this.width - this.scrollWidth);
  }

  private float minScrollY() {
    return Math.min(0, this.height - this.scrollHeight);
  }

  private void rescroll() {
    float minScrollX = minScrollX();
    float minScrollY = minScrollY();
    if ((minScrollX > this.scrollX) || (minScrollY > this.scrollY)) {
      this.scrollX = Math.max(this.scrollX, minScrollX);
      this.scrollY = Math.max(this.scrollY, minScrollY);
      redraw();
    }
  }

  protected void onMouseWheel(float mx, float my, float delta) {
    float newScrollY = LXUtils
        .constrainf(this.scrollY - delta, minScrollY(), 0);
    if (newScrollY != this.scrollY) {
      this.scrollY = newScrollY;
      redraw();
    }
  }
}
