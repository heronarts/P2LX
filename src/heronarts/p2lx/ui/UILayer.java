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

import processing.event.KeyEvent;


public interface UILayer {

  public void draw();

  public boolean mousePressed(float mx, float my);

  public boolean mouseReleased(float mx, float my);

  public boolean mouseClicked(float mx, float my);

  public boolean mouseDragged(float mx, float my);

  public boolean mouseWheel(float mx, float my, float delta);

  public boolean keyPressed(KeyEvent keyEvent, char keyChar, int keyCode);

  public boolean keyReleased(KeyEvent keyEvent, char keyChar, int keyCode);

  public boolean keyTyped(KeyEvent keyEvent, char keyChar, int keyCode);
}
