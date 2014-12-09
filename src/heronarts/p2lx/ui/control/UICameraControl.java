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

package heronarts.p2lx.ui.control;

import heronarts.p2lx.ui.UI;
import heronarts.p2lx.ui.UI3dContext;
import heronarts.p2lx.ui.UIWindow;
import heronarts.p2lx.ui.component.UIKnob;

public class UICameraControl extends UIWindow {

  public final static int WIDTH = 140;
  public final static int HEIGHT = 72;

  public UICameraControl(UI ui, UI3dContext context, float x, float y) {
    super(ui, "CAMERA", x, y, WIDTH, HEIGHT);

    float xp = 5;
    float yp = UIWindow.TITLE_LABEL_HEIGHT;
    new UIKnob(xp, yp).setParameter(context.perspective).addToContainer(this);
    xp += 34;
    new UIKnob(xp, yp).setParameter(context.depth).addToContainer(this);
  }

}
