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
import heronarts.p2lx.ui.UIContext;
import processing.core.PImage;

public class UIImageContext extends UIContext {
  public UIImageContext(UI ui, PImage image) {
    this(ui, image, 0, 0);
  }

  public UIImageContext(UI ui, PImage image, float x, float y) {
    super(ui, x, y, image.width, image.height);
    new UIImage(image).addToContainer(this);
  }
}
