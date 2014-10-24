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

import heronarts.p2lx.ui.UI;
import heronarts.p2lx.ui.UI2dComponent;
import processing.core.PGraphics;
import processing.core.PImage;

public class UIImage extends UI2dComponent {

  private final PImage image;

  public UIImage(PImage image) {
    this(image, 0, 0);
  }

  public UIImage(PImage image, float x, float y) {
    super(x, y, image.width, image.height);
    this.image = image;
  }

  @Override
  public void onDraw(UI ui, PGraphics pg) {
    pg.image(this.image, 0, 0, this.width, this.height);
  }

}
