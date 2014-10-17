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

/**
 * This is a UIObject that may contain other UIObjects. Mouse and drawing events
 * are automatically delegated appropriately. The onDraw method of the container
 * itself is invoked before its children, meaning that children are drawn on top
 * of underlying elements.
 */
public class UIContainer extends UIObject {

  /**
   * Constructs an empty UIContainer with no size.
   */
  public UIContainer() {
  }

  /**
   * Constructs an empty container with a size.
   *
   * @param x x-position
   * @param y y-position
   * @param w width
   * @param h height
   */
  public UIContainer(float x, float y, float w, float h) {
    super(x, y, w, h);
  }

  /**
   * Constructs a container with a set of children.
   *
   * @param children Child objects
   */
  public UIContainer(UIObject[] children) {
    for (UIObject child : children) {
      child.addToContainer(this);
    }
  }

  /**
   * Gets a child of this container
   *
   * @param index index of child
   * @return object
   */
  public UIObject getChild(int index) {
    return this.children.get(index);
  }

}
