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

import heronarts.lx.model.LXModel;
import heronarts.lx.model.LXPoint;
import heronarts.p2lx.P2LX;
import heronarts.p2lx.ui.UI;
import heronarts.p2lx.ui.UI3dComponent;
import processing.core.PConstants;
import processing.core.PGraphics;

/**
 * Draws a cloud of points in the layer
 */
public class UIPointCloud extends UI3dComponent {

  protected final P2LX lx;

  protected final LXModel model;

  /**
   * Weight of points
   */
  protected float pointWeight = 1;

  /**
   * Point cloud for everything in the LX instance
   *
   * @param lx
   */
  public UIPointCloud(P2LX lx) {
    this(lx, lx.model);
  }

  /**
   * Point cloud for points in the specified model
   *
   * @param lx
   * @param model
   */
  public UIPointCloud(P2LX lx, LXModel model) {
    this.lx = lx;
    this.model = model;
  }

  /**
   * Sets the weight of points
   *
   * @param pointWeight Point weight
   * @return this
   */
  public UIPointCloud setPointWeight(float pointWeight) {
    this.pointWeight = pointWeight;
    return this;
  }

  @Override
  protected void onDraw(UI ui, PGraphics pg) {
    int[] colors = this.lx.getColors();
    pg.strokeWeight(this.pointWeight);
    pg.beginShape(PConstants.POINTS);
    for (LXPoint p : this.model.points) {
      pg.stroke(colors[p.index]);
      pg.vertex(p.x, p.y, p.z);
    }
    pg.endShape();
  }
}
