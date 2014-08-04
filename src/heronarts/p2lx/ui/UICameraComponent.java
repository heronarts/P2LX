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

package heronarts.p2lx.ui;

import java.util.ArrayList;
import java.util.List;

/**
 * A component in a CameraLayer. Draws itself and may draw children.
 */
public abstract class UICameraComponent {

  private final List<UICameraComponent> children = new ArrayList<UICameraComponent>();

  boolean visible = true;

  final void draw(UI ui) {
    onDraw(ui);
    for (UICameraComponent child : this.children) {
      child.draw(ui);
    }
  }

  public boolean isVisible() {
    return this.visible;
  }

  public UICameraComponent setVisible(boolean visible) {
    this.visible = visible;
    return this;
  }

  /**
   * Adds a child to this component
   * 
   * @param child
   * @return this
   */
  public final UICameraComponent addChild(UICameraComponent child) {
    this.children.add(child);
    return this;
  }

  /**
   * Removes a child from this component
   * 
   * @param child
   * @return this
   */
  public final UICameraComponent removeChild(UICameraComponent child) {
    this.children.remove(child);
    return this;
  }

  /**
   * Draws this component. Subclasses should implement. Parents are drawn before
   * their children.
   * 
   * @param ui UI context
   */
  protected abstract void onDraw(UI ui);
}
