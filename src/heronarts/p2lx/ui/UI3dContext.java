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

import heronarts.lx.LXLoopTask;
import heronarts.lx.LXUtils;
import heronarts.lx.modulator.DampedParameter;
import heronarts.lx.parameter.LXParameter;
import heronarts.lx.parameter.LXParameterListener;
import heronarts.lx.parameter.MutableParameter;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PVector;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

/**
 * This is a layer that contains a 3d scene with a camera. Mouse movements
 * control the camera, and the scene can contain components.
 */
public class UI3dContext extends UIObject implements UITabFocus, LXLoopTask {

  private final PVector center = new PVector(0, 0, 0);

  private final PVector eye = new PVector(0, 0, 0);

  /**
   * Angle of the eye position about the vertical Z-axis
   */
  public final MutableParameter theta = new MutableParameter("Theta", 0);

  /**
   * Angle of the eye position off the X-Y plane
   */
  public final MutableParameter phi = new MutableParameter("Phi", 0);

  /**
   * Radius of the eye positon from center of the scene
   */
  public final MutableParameter radius = new MutableParameter("Radius", 120);

  /**
   * Max velocity used to damp changes to radius (zoom)
   */
  public final MutableParameter zoomVelocity = new MutableParameter("ZVel", 2400);

  /**
   * Max velocity used to damp changes to rotation (theta/phi)
   */
  public final MutableParameter rotateVelocity = new MutableParameter("RVel", 4*Math.PI);

  private final DampedParameter thetaDamped = new DampedParameter(theta, this.rotateVelocity);

  private final DampedParameter phiDamped = new DampedParameter(phi, this.rotateVelocity);

  private final DampedParameter radiusDamped = new DampedParameter(radius, this.zoomVelocity);

  // Radius bounds
  private float minRadius = 0, maxRadius = Float.MAX_VALUE;

  private static final float MAX_PHI = PConstants.HALF_PI * .9f;

  public UI3dContext(UI ui) {
    setUI(ui);
    this.thetaDamped.start();
    this.radiusDamped.start();
    this.phiDamped.start();
    computeEye();
    this.radius.addListener(new LXParameterListener() {
      public void onParameterChanged(LXParameter p) {
        float value = radius.getValuef();
        if (value < minRadius || value > maxRadius) {
          radius.setValue(LXUtils.constrainf(value, minRadius, maxRadius));
        }
      }
    });
    this.phi.addListener(new LXParameterListener() {
      public void onParameterChanged(LXParameter p) {
        float value = phi.getValuef();
        if (value < -MAX_PHI || value > MAX_PHI) {
          phi.setValue(LXUtils.constrainf(value, -MAX_PHI, MAX_PHI));
        }
      }
    });
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
    this.radius.setValue(radius);
    return this;
  }

  /**
   * Sets the camera's maximum zoom speed
   *
   * @param zoomVelocity Max units/per second radius may change by
   * @return this
   */
  public UI3dContext setZoomVelocity(float zoomVelocity) {
    this.zoomVelocity.setValue(zoomVelocity);
    return this;
  }

  /**
   * Sets the camera's maximum rotation speed
   *
   * @param rotateVelocity Max radians/per second viewing angle may change by
   * @return this
   */
  public UI3dContext setRotateVelocity(float rotateVelocity) {
    this.rotateVelocity.setValue(rotateVelocity);
    return this;
  }

  /**
   * Set the theta angle of viewing
   *
   * @param theta Angle about the y axis
   * @return this
   */
  public UI3dContext setTheta(float theta) {
    this.theta.setValue(theta);
    return this;
  }

  /**
   * Set the phi angle of viewing
   *
   * @param phi Angle about the y axis
   * @return this
   */
  public UI3dContext setPhi(float phi) {
    this.phi.setValue(phi);
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
    setRadius(LXUtils.constrainf(this.radius.getValuef(), minRadius, maxRadius));
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
    return this;
  }

  /**
   * Gets the center position of the scene
   *
   * @return center of scene
   */
  public PVector getCenter() {
    return this.center;
  }

  /**
   * Gets the latest computed eye position
   *
   * @return eye position
   */
  public PVector getEye() {
    return this.eye;
  }

  private void computeEye() {
    float rv = this.radiusDamped.getValuef();
    float tv = this.thetaDamped.getValuef();
    float pv = this.phiDamped.getValuef();

    float sintheta = (float) Math.sin(tv);
    float costheta = (float) Math.cos(tv);
    float sinphi = (float) Math.sin(pv);
    float cosphi = (float) Math.cos(pv);

    this.eye.x = this.center.x + rv * cosphi * sintheta;
    this.eye.z = this.center.z - rv * cosphi * costheta;
    this.eye.y = this.center.y + rv * sinphi;
  }

  @Override
  public final void draw(UI ui, PGraphics pg) {
    if (!isVisible()) {
      return;
    }

    // Compute the eye position
    computeEye();

    // Set the camera view
    this.ui.applet.camera(
      this.eye.x, this.eye.y, this.eye.z,
      this.center.x, this.center.y, this.center.z,
      0, -1, 0
    );

    // Draw all the components in the scene
    this.beforeDraw(ui, pg);
    for (UIObject child : this.children) {
      child.draw(ui, pg);
    }
    this.afterDraw(ui, pg);

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
  protected void beforeDraw(UI ui, PGraphics pg) {
  }

  /**
   * Subclasses may override, useful to turn off lighting, etc.
   */
  protected void afterDraw(UI ui, PGraphics pg) {
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
      this.radius.incrementValue(dy);
    } else if (mouseEvent.isMetaDown()) {
      this.center.x -= dx;
      this.center.y += dy;
    } else {
      this.theta.incrementValue(-dx * .003);
      this.phi.incrementValue(dy * .003);
    }
  }

  @Override
  protected void onMouseWheel(MouseEvent mouseEvent, float mx, float my, float delta) {
    this.radius.incrementValue(delta);
  }

  @Override
  protected void onKeyPressed(KeyEvent keyEvent, char keyChar, int keyCode) {
    float amount = keyEvent.isShiftDown() ? .2f : .02f;
    if (keyCode == java.awt.event.KeyEvent.VK_LEFT) {
      this.theta.incrementValue(amount);
    } else if (keyCode == java.awt.event.KeyEvent.VK_RIGHT) {
      this.theta.incrementValue(-amount);
    } else if (keyCode == java.awt.event.KeyEvent.VK_UP) {
      this.phi.incrementValue(-amount);
    } else if (keyCode == java.awt.event.KeyEvent.VK_DOWN) {
      this.phi.incrementValue(amount);
    }
  }

  @Override
  public void loop(double deltaMs) {
    this.thetaDamped.loop(deltaMs);
    this.phiDamped.loop(deltaMs);
    this.radiusDamped.loop(deltaMs);
  }
}
