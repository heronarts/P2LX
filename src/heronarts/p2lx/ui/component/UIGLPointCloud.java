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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import processing.core.PGraphics;
import processing.opengl.PGL;
import processing.opengl.PJOGL;
import processing.opengl.PShader;
import heronarts.lx.model.LXModel;
import heronarts.lx.model.LXPoint;
import heronarts.p2lx.P2LX;
import heronarts.p2lx.ui.UI;

/**
 * Same as a UIPointCloud, except this version uses GLSL to draw
 * the points with a vertex shader.
 */
public class UIGLPointCloud extends UIPointCloud {

  private final PShader shader;
  private final FloatBuffer vertexData;
  private int vertexBufferObjectName;


  /**
   * Point cloud for everything in the LX instance
   *
   * @param lx
   */
  public UIGLPointCloud(P2LX lx) {
    this(lx, lx.model);
  }

  /**
   * Point cloud for points in the specified model
   *
   * @param lx
   * @param model
   */
  public UIGLPointCloud(P2LX lx, LXModel model) {
    super(lx, model);

    // Load shader
    this.shader = lx.applet.loadShader("frag.glsl", "vert.glsl");

    // Create a buffer for vertex data
    this.vertexData = ByteBuffer
      .allocateDirect(model.size * 7 * Float.SIZE/8)
      .order(ByteOrder.nativeOrder())
      .asFloatBuffer();

    // Put all the points into the buffer
    this.vertexData.rewind();
    for (LXPoint point : model.points) {
      // Each point has 7 floats, XYZRGBA
      this.vertexData.put(point.x);
      this.vertexData.put(point.y);
      this.vertexData.put(point.z);
      this.vertexData.put(0f);
      this.vertexData.put(0f);
      this.vertexData.put(0f);
      this.vertexData.put(1f);
    }
    this.vertexData.position(0);

    // Generate a buffer binding
    IntBuffer resultBuffer = ByteBuffer
      .allocateDirect(1 * Integer.SIZE/8)
      .order(ByteOrder.nativeOrder())
      .asIntBuffer();

    PGL pgl = this.lx.applet.beginPGL();
    pgl.genBuffers(1, resultBuffer); // Generates a buffer, places its id in resultBuffer[0]
    this.vertexBufferObjectName = resultBuffer.get(0); // Grab our buffer name
    this.lx.applet.endPGL();
  }

  @Override
  protected void onDraw(UI ui, PGraphics pg) {
    int[] colors = this.lx.getColors();

    // Put our new colors in the vertex data
    int i = 0;
    for (LXPoint p : this.model.points) {
      int c = colors[p.index];
      this.vertexData.put(7*i + 3, (0xff & (c >> 16)) / 255f); // R
      this.vertexData.put(7*i + 4, (0xff & (c >> 8)) / 255f); // G
      this.vertexData.put(7*i + 5, (0xff & (c)) / 255f); // B
      ++i;
    }

    PGL pgl = this.lx.applet.beginPGL();

    // Bind to our vertex buffer object, place the new color data
    pgl.bindBuffer(PGL.ARRAY_BUFFER, this.vertexBufferObjectName);
    pgl.bufferData(PGL.ARRAY_BUFFER, this.model.size * 7 * Float.SIZE/8, this.vertexData, PGL.DYNAMIC_DRAW);

    this.shader.bind();
    int vertexLocation = pgl.getAttribLocation(this.shader.glProgram, "vertex");
    int colorLocation = pgl.getAttribLocation(this.shader.glProgram, "color");
    pgl.enableVertexAttribArray(vertexLocation);
    pgl.enableVertexAttribArray(colorLocation);
    pgl.vertexAttribPointer(vertexLocation, 3, PGL.FLOAT, false, 7 * Float.SIZE/8, 0);
    pgl.vertexAttribPointer(colorLocation, 4, PGL.FLOAT, false, 7 * Float.SIZE/8, 3 * Float.SIZE/8);
    javax.media.opengl.GL2 gl2 = (javax.media.opengl.GL2) ((PJOGL)pgl).gl;
    gl2.glPointSize(this.pointWeight);
    pgl.drawArrays(PGL.POINTS, 0, this.model.size);
    pgl.disableVertexAttribArray(vertexLocation);
    pgl.disableVertexAttribArray(colorLocation);
    this.shader.unbind();

    pgl.bindBuffer(PGL.ARRAY_BUFFER, 0);
    this.lx.applet.endPGL();
  }

}
