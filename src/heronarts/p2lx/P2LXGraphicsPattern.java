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
