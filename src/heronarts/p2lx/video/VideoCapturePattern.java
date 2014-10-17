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
