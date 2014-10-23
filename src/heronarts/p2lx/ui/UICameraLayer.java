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

import java.util.ArrayList;
import java.util.List;

import processing.core.PConstants;
import processing.core.PVector;
import processing.event.KeyEvent;

/**
 * This is a layer that contains a 3d scene with a camera. Mouse movements
 * control the camera, and the scene can contain components.
 */
public class UICameraLayer implements UILayer, UIFocus {

  private final UI ui;

  private final List<UICameraComponent> components = new ArrayList<UICameraComponent>();

  private boolean visible = true;

  private final PVector center = new PVector();

  private final PVector eye = new PVector();

  // Center of the scene
  private float cx = 0, cy = 0, cz = 0;

  // Polar eye position
  private float theta = 0, phi = 0, radius = 120;

  // Computed eye position
  private float ex = 0, ey = 0, ez = 0;

  // Mouse tracking
  private float px = 0, py = 0;

  // Radius bounds
  private float minRadius = 0, maxRadius = Float.MAX_VALUE;

  public UICameraLayer(UI ui) {
    this.ui = ui;
    computeEye();
  }

  /**
   * Adds a component to the layer
   *
   * @param component
   * @return this
   */
  public final UICameraLayer addComponent(UICameraComponent component) {
    this.components.add(component);
    return this;
  }

  /**
   * Removes a component from the layer
   *
   * @param component
   * @return this
   */
  public final UICameraLayer removeComponent(UICameraComponent component) {
    this.components.remove(component);
    return this;
  }

  /**
   * Set radius of the camera
   *
   * @param radius radius
   * @return this
   */
  public UICameraLayer setRadius(float radius) {
    this.radius = LXUtils.constrainf(radius, this.minRadius, this.maxRadius);
    computeEye();
    return this;
  }

  /**
   * Set the theta angle of viewing
   *
   * @param theta Angle about the y axis
   * @return this
   */
  public UICameraLayer setTheta(float theta) {
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
  public UICameraLayer setPhi(float phi) {
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
  public UICameraLayer setRadiusBounds(float minRadius, float maxRadius) {
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
  public UICameraLayer setMinRadius(float minRadius) {
    return setRadiusBounds(minRadius, this.maxRadius);
  }

  /**
   * Set maximum radius
   *
   * @param maxRadius
   * @return this
   */
  public UICameraLayer setMaxRadius(float maxRadius) {
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
  public UICameraLayer setCenter(float x, float y, float z) {
    this.cx = x;
    this.cy = y;
    this.cz = z;
    computeEye();
    return this;
  }

  public PVector getCenter() {
    this.center.set(this.cx, this.cy, this.cz);
    return this.center;
  }

  public PVector getEye() {
    this.eye.set(this.ex, this.ey, this.ez);
    return this.eye;
  }

  private void computeEye() {
    float maxPhi = PConstants.HALF_PI * .9f;
    this.phi = LXUtils.constrainf(this.phi, -maxPhi, maxPhi);
    float sintheta = (float) Math.sin(this.theta);
    float costheta = (float) Math.cos(this.theta);
    float sinphi = (float) Math.sin(this.phi);
    float cosphi = (float) Math.cos(this.phi);
    this.ex = this.cx + this.radius * cosphi * sintheta;
    this.ez = this.cz - this.radius * cosphi * costheta;
    this.ey = this.cy + this.radius * sinphi;
  }

  public boolean isVisible() {
    return this.visible;
  }

  public UICameraLayer setVisible(boolean visible) {
    this.visible = visible;
    return this;
  }

  public final void draw() {
    if (!this.visible) {
      return;
    }

    // Set the camera view
    this.ui.applet.camera(this.ex, this.ey, this.ez, this.cx, this.cy, this.cz,
        0, -1, 0);

    // Draw all the components in the scene
    this.beforeDraw();
    for (UICameraComponent component : this.components) {
      if (component.isVisible()) {
        component.draw(this.ui);
      }
    }
    this.afterDraw();

    // Reset the camera
    this.ui.applet.camera();
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

  private long lastMousePress = 0;

  public final boolean mousePressed(float mx, float my) {
    long now = System.currentTimeMillis();
    if (now - this.lastMousePress < UIObject.DOUBLE_CLICK_THRESHOLD) {
      this.ui.willFocus(this, null);
    }
    this.lastMousePress = now;
    this.px = mx;
    this.py = my;
    return true;
  }

  public final boolean mouseReleased(float mx, float my) {
    return true;
  }

  public final boolean mouseClicked(float mx, float my) {
    return false;
  }

  public final boolean mouseDragged(float mx, float my) {
    float dx = mx - this.px;
    float dy = my - this.py;
    this.px = mx;
    this.py = my;

    this.theta -= dx * .003;
    this.phi += dy * .003;

    computeEye();

    return true;
  }

  public final boolean mouseWheel(float mx, float my, float delta) {
    setRadius(this.radius + delta);
    return true;
  }

  public final boolean keyPressed(KeyEvent keyEvent, char keyChar, int keyCode) {
    float amount = keyEvent.isShiftDown() ? .2f : .02f;
    if (keyCode == java.awt.event.KeyEvent.VK_LEFT) {
      this.theta += amount;
      computeEye();
      return true;
    } else if (keyCode == java.awt.event.KeyEvent.VK_RIGHT) {
      this.theta -= amount;
      computeEye();
      return true;
    } else if (keyCode == java.awt.event.KeyEvent.VK_UP) {
      this.phi -= amount;
      computeEye();
      return true;
    } else if (keyCode == java.awt.event.KeyEvent.VK_DOWN) {
      this.phi += amount;
      computeEye();
      return true;
    }

    return false;
  }

  public final boolean keyReleased(KeyEvent keyEvent, char keyChar,
      int keyCode) {
    return false;
  }

  public final boolean keyTyped(KeyEvent keyEvent, char keyChar, int keyCode) {
    return false;
  }

}
