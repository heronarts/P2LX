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

package heronarts.p2lx.ui.component;

import heronarts.p2lx.ui.UI;
import heronarts.p2lx.ui.UIObject;
import processing.core.PGraphics;
import processing.core.PImage;

public class UIImage extends UIObject {

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
