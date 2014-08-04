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

package heronarts.p2lx.video;

import heronarts.lx.transition.IrisTransition;
import heronarts.p2lx.P2LX;
import heronarts.p2lx.P2LXPattern;
import processing.video.Capture;

public class VideoCapturePattern extends P2LXPattern {

  private Capture capture;

  public VideoCapturePattern(P2LX lx) {
    super(lx);
    this.capture = null;
    setTransition(new IrisTransition(lx));
  }

  @Override
  public void onActive() {
    this.capture = new Capture(this.applet, this.lx.width, this.lx.height);
  }

  @Override
  public void onInactive() {
    this.capture.dispose();
    this.capture = null;
  }

  @Override
  public void run(double deltaMs) {
    if (this.capture.available()) {
      this.capture.read();
    }
    this.capture.loadPixels();
    for (int i = 0; i < this.colors.length; ++i) {
      this.colors[i] = this.capture.pixels[i];
    }
  }

}
