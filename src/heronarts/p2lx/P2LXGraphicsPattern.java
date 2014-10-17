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

package heronarts.p2lx;

import processing.core.PConstants;
import processing.core.PGraphics;

public abstract class P2LXGraphicsPattern extends P2LXPattern {

  private final PGraphics pg;

  protected P2LXGraphicsPattern(P2LX lx) {
    super(lx);
    this.pg = this.applet.createGraphics(lx.width, lx.height, PConstants.P2D);
  }

  @Override
  final protected void run(double deltaMs) {
    this.pg.beginDraw();
    this.run(deltaMs, this.pg);
    this.pg.endDraw();
    this.pg.loadPixels();
    for (int i = 0; i < this.lx.total; ++i) {
      this.colors[i] = this.pg.pixels[i];
    }
  }

  abstract protected void run(double deltaMs, PGraphics pg);

}
