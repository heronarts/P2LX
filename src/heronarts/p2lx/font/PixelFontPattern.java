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

package heronarts.p2lx.font;

import heronarts.p2lx.P2LX;
import heronarts.p2lx.P2LXPattern;
import heronarts.lx.LXColor;
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
    this.addModulator(this.hMod.trigger());
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
