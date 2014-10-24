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

package heronarts.p2lx.ui;

import heronarts.lx.LXUtils;

import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PVector;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

/**
 * This is a layer that contains a 3d scene with a camera. Mouse movements
 * control the camera, and the scene can contain components.
 */
public class UI3dContext extends UIObject implements UITabFocus {

  private final PVector center = new PVector(0, 0, 0);

  private final PVector eye = new PVector(0, 0, 0);

  // Polar eye position
  private float theta = 0, phi = 0, radius = 120;

  // Radius bounds
  private float minRadius = 0, maxRadius = Float.MAX_VALUE;

  public UI3dContext(UI ui) {
    setUI(ui);
    computeEye();
  }

  /**
   * Adds a component to the layer
   *
   * @param component
   * @return this
   */
  public final UI3dContext addComponent(UI3dComponent component) {
    this.children.add(component);
    return this;
  }

  /**
   * Removes a component from the layer
   *
   * @param component
   * @return this
   */
  public final UI3dContext removeComponent(UI3dComponent component) {
    this.children.remove(component);
    return this;
  }

  /**
   * Set radius of the camera
   *
   * @param radius radius
   * @return this
   */
  public UI3dContext setRadius(float radius) {
    this.radius = radius;
    computeEye();
    return this;
  }

  /**
   * Set the theta angle of viewing
   *
   * @param theta Angle about the y axis
   * @return this
   */
  public UI3dContext setTheta(float theta) {
    this.theta = theta;
    computeEye();
    return this;
  }

  /**
   * Set the phi angle of viewing
   *
   * @param phi Angle about the y axis
   * @return this
   */
  public UI3dContext setPhi(float phi) {
    this.phi = phi;
    computeEye();
    return this;
  }

  /**
   * Sets bounds on the radius
   *
   * @param minRadius
   * @param maxRadius
   * @return this
   */
  public UI3dContext setRadiusBounds(float minRadius, float maxRadius) {
    this.minRadius = minRadius;
    this.maxRadius = maxRadius;
    setRadius(LXUtils.constrainf(this.radius, minRadius, maxRadius));
    return this;
  }

  /**
   * Set minimum radius
   *
   * @param minRadius
   * @return this
   */
  public UI3dContext setMinRadius(float minRadius) {
    return setRadiusBounds(minRadius, this.maxRadius);
  }

  /**
   * Set maximum radius
   *
   * @param maxRadius
   * @return this
   */
  public UI3dContext setMaxRadius(float maxRadius) {
    return setRadiusBounds(this.minRadius, maxRadius);
  }

  /**
   * Sets the center of the scene
   *
   * @param x
   * @param y
   * @param z
   * @return this
   */
  public UI3dContext setCenter(float x, float y, float z) {
    this.center.x = x;
    this.center.y = y;
    this.center.z = z;
    computeEye();
    return this;
  }

  public PVector getCenter() {
    return this.center;
  }

  public PVector getEye() {
    return this.eye;
  }

  private void computeEye() {
    float maxPhi = PConstants.HALF_PI * .9f;
    this.phi = LXUtils.constrainf(this.phi, -maxPhi, maxPhi);
    this.radius = LXUtils.constrainf(this.radius, this.minRadius, this.maxRadius);
    float sintheta = (float) Math.sin(this.theta);
    float costheta = (float) Math.cos(this.theta);
    float sinphi = (float) Math.sin(this.phi);
    float cosphi = (float) Math.cos(this.phi);
    this.eye.x = this.center.x + this.radius * cosphi * sintheta;
    this.eye.z = this.center.z - this.radius * cosphi * costheta;
    this.eye.y = this.center.y + this.radius * sinphi;
  }

  @Override
  public final void draw(UI ui, PGraphics pg) {
    if (!isVisible()) {
      return;
    }

    // Set the camera view
    this.ui.applet.camera(
      this.eye.x, this.eye.y, this.eye.z,
      this.center.x, this.center.y, this.center.z,
      0, -1, 0
    );

    // Draw all the components in the scene
    this.beforeDraw();
    for (UIObject child : this.children) {
      child.draw(ui, pg);
    }
    this.afterDraw();

    // Reset the camera
    this.ui.applet.camera();

    if (hasFocus()) {
      pg.strokeWeight(1);
      pg.stroke(ui.theme.getFocusColor());
      int focusInset = 2;
      int focusDash = 10;
      // Top left
      pg.line(focusInset, focusInset, focusInset + focusDash, focusInset);
      pg.line(focusInset, focusInset, focusInset, focusInset + focusDash);
      // Top right
      pg.line(ui.applet.width - focusInset, focusInset, ui.applet.width - focusInset - focusDash, focusInset);
      pg.line(ui.applet.width - focusInset, focusInset, ui.applet.width - focusInset, focusInset + focusDash);
      // Bottom left
      pg.line(focusInset, ui.applet.height - focusInset, focusInset + focusDash, ui.applet.height - focusInset);
      pg.line(focusInset, ui.applet.height - focusInset, focusInset, ui.applet.height - focusInset - focusDash);
      // Bottom right
      pg.line(ui.applet.width - focusInset, ui.applet.height - focusInset, ui.applet.width - focusInset - focusDash, ui.applet.height - focusInset);
      pg.line(ui.applet.width - focusInset, ui.applet.height - focusInset, ui.applet.width - focusInset, ui.applet.height - focusInset - focusDash);
    }
  }

  /**
   * Subclasses may override, useful to turn on lighting, etc.
   */
  protected void beforeDraw() {
  }

  /**
   * Subclasses may override, useful to turn off lighting, etc.
   */
  protected void afterDraw() {
  }

  @Override
  protected void onMousePressed(MouseEvent mouseEvent, float mx, float my) {
    if (mouseEvent.getCount() > 1) {
      focus();
    }
  }

  @Override
  protected void onMouseDragged(MouseEvent mouseEvent, float mx, float my, float dx, float dy) {
    if (mouseEvent.isShiftDown()) {
      this.radius += dy;
    } else if (mouseEvent.isMetaDown()) {
      this.center.x -= dx;
      this.center.y += dy;
    } else {
      this.theta -= dx * .003;
      this.phi += dy * .003;
    }
    computeEye();
  }

  @Override
  protected void onMouseWheel(MouseEvent mouseEvent, float mx, float my, float delta) {
    setRadius(this.radius + delta);
  }

  @Override
  protected void onKeyPressed(KeyEvent keyEvent, char keyChar, int keyCode) {
    float amount = keyEvent.isShiftDown() ? .2f : .02f;
    if (keyCode == java.awt.event.KeyEvent.VK_LEFT) {
      this.theta += amount;
      computeEye();
    } else if (keyCode == java.awt.event.KeyEvent.VK_RIGHT) {
      this.theta -= amount;
      computeEye();
    } else if (keyCode == java.awt.event.KeyEvent.VK_UP) {
      this.phi -= amount;
      computeEye();
    } else if (keyCode == java.awt.event.KeyEvent.VK_DOWN) {
      this.phi += amount;
      computeEye();
    }
  }
}
