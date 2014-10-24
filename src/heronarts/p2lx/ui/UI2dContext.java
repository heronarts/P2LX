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

import processing.core.PConstants;
import processing.core.PGraphics;

/**
 * A UIContext is a container that owns a graphics buffer. This buffer is
 * persistent across frames and is only redrawn as necessary. It is simply
 * bitmapped onto the UI that is a part of.
 */
public class UI2dContext extends UI2dComponent implements UI2dContainer {

  /**
   * Graphics context for this container.
   */
  private final PGraphics pg;

  /**
   * Constructs a new UI2dContext
   *
   * @param ui the UI to place it in
   * @param x x-position
   * @param y y-position
   * @param w width
   * @param h height
   */
  public UI2dContext(UI ui, float x, float y, float w, float h) {
    super(x, y, w, h);
    this.pg = ui.applet.createGraphics((int) w, (int) h, PConstants.JAVA2D);
    this.pg.smooth();
  }

  @Override
  protected void onResize() {
    this.pg.setSize((int) this.width, (int) this.height);
  }

  @Override
  void draw(UI ui, PGraphics pg) {
    if (!isVisible()) {
      return;
    }
    if (this.needsRedraw || this.childNeedsRedraw) {
      this.pg.beginDraw();
      super.draw(ui, this.pg);
      this.pg.endDraw();
    }
    pg.image(this.pg, 0, 0);
  }
}
