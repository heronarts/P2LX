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
