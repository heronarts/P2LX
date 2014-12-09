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

import javax.media.opengl.GL2;

import heronarts.lx.model.LXModel;
import heronarts.lx.model.LXPoint;
import heronarts.p2lx.P2LX;
import heronarts.p2lx.ui.UI;
import heronarts.p2lx.ui.UI3dComponent;
import processing.core.PGraphics;
import processing.opengl.PGL;
import processing.opengl.PJOGL;

/**
 * Draws a cloud of points in the layer
 */
public class UIPointCloud extends UI3dComponent {

  protected final P2LX lx;

  protected final LXModel model;

  /**
   * Weight of points
   */
  protected float pointSize = 2;

  private float[] pointSizeAttenuation = null;

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
  public UIPointCloud setPointSize(float pointSize) {
    this.pointSize = pointSize;
    return this;
  }

  /**
   * Sets point size attenuation, fn = 1/sqrt(constant + linear*d + quadratic*d^2)
   *
   * @param a Constant factor
   * @param b Linear factor
   * @param c Quadratic factor
   * @return this
   */
  public UIPointCloud setPointSizeAttenuation(float a, float b, float c) {
    this.pointSizeAttenuation = new float[] { a, b, c };
    return this;
  }

  @Override
  protected void onDraw(UI ui, PGraphics pg) {

    PGL pgl = this.lx.applet.beginPGL();
    GL2 gl2 = (javax.media.opengl.GL2) ((PJOGL)pgl).gl;
    if (this.pointSizeAttenuation != null) {
      gl2.glEnable(GL2.GL_VERTEX_PROGRAM_POINT_SIZE);
      gl2.glPointParameterf(GL2.GL_POINT_SIZE_MIN, 1.f);
      gl2.glPointParameterf(GL2.GL_POINT_SIZE_MAX, this.pointSize);
      gl2.glPointParameterfv(GL2.GL_POINT_DISTANCE_ATTENUATION, this.pointSizeAttenuation, 0);
    } else {
      gl2.glDisable(GL2.GL_VERTEX_PROGRAM_POINT_SIZE);
    }
    gl2.glPointSize(this.pointSize);
    gl2.glEnable(GL2.GL_POINT_SMOOTH);
    gl2.glDisable(GL2.GL_POINT_SPRITE);

    int[] colors = this.lx.getColors();
    gl2.glBegin(GL2.GL_POINTS);
    for (LXPoint p : this.model.points) {
      int c = colors[p.index];
      gl2.glColor3ub(
        (byte) (0xff & (c >>> 16)),
        (byte) (0xff & (c >>> 8)),
        (byte) (0xff & c)
      );
      gl2.glVertex3f(p.x, p.y, p.z);
    }
    gl2.glEnd();

    this.lx.applet.endPGL();
  }
}
