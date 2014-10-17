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

package heronarts.p2lx.font;

import heronarts.p2lx.P2LX;
import heronarts.p2lx.P2LXPattern;
import heronarts.lx.color.LXColor;
import heronarts.lx.modulator.SawLFO;
import processing.core.PApplet;
import processing.core.PImage;

/**
 * Example pattern to render a text string using PixelFont.
 */
public class PixelFontPattern extends P2LXPattern {

  final private SawLFO hMod = new SawLFO(0, 360, 10000);
  final private SawLFO pMod = new SawLFO(0, 0, 10000);
  final private PImage image;

  public PixelFontPattern(P2LX lx) {
    this(lx, "The quick brown fox jumped over the lazy dog.");
  }

  public PixelFontPattern(P2LX lx, String s) {
    super(lx);
    this.image = (new PixelFont(lx)).drawString(s);
    this.addModulator(this.hMod).trigger();
    this.addModulator(
        this.pMod.setRange(-lx.width, this.image.width, this.image.width * 250))
        .trigger();
  }

  @Override
  public void run(double deltaMs) {
    for (int i = 0; i < this.colors.length; ++i) {
      double col = this.lx.column(i) + this.pMod.getValue();
      int floor = (int) Math.floor(col);
      int ceil = (int) Math.ceil(col);
      float b1 = this.applet
          .brightness(this.image.get(floor, this.lx.row(i)));
      float b2 = this.applet
          .brightness(this.image.get(ceil, this.lx.row(i)));

      this.colors[i] = LXColor.hsb(this.hMod.getValue(), 100.,
          PApplet.lerp(b1, b2, (float) (col - floor)));
    }
  }
}
