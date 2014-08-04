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

import processing.core.PApplet;
import heronarts.lx.pattern.LXPattern;

public abstract class P2LXPattern extends LXPattern {

  protected final PApplet applet;

  protected P2LXPattern(P2LX lx) {
    super(lx);
    this.applet = lx.applet;
  }
}
