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
 * A component in a CameraLayer. Draws itself and may draw children.
 */
public abstract class UI3dComponent extends UIObject {

  @Override
  public boolean contains(float x, float y) {
    return false;
  }

  /**
   * Adds a child to this component
   *
   * @param child
   * @return this
   */
  public final UI3dComponent addChild(UI3dComponent child) {
    this.children.add(child);
    return this;
  }

  /**
   * Removes a child from this component
   *
   * @param child
   * @return this
   */
  public final UI3dComponent removeChild(UI3dComponent child) {
    this.children.remove(child);
    return this;
  }
}
