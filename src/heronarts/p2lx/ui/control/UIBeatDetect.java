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

package heronarts.p2lx.ui.control;

import processing.core.PApplet;
import processing.core.PGraphics;
import heronarts.lx.LXColor;
import heronarts.lx.audio.FrequencyGate;
import heronarts.p2lx.ui.UI;
import heronarts.p2lx.ui.UIWindow;
import heronarts.p2lx.ui.component.UIKnob;

public class UIBeatDetect extends UIWindow {

  private final static int WIDTH = 140;
  private final static int HEIGHT = 188;

  private final FrequencyGate gate;

  private final float eqX, eqWidth, eqHeight, eqTop, eqBottom, masterX, masterWidth, kickX, kickWidth, piece;

  private final int eqBands;

  private float bandX = 0, bandWidth = 0;

  public UIBeatDetect(UI ui, FrequencyGate gate, float x, float y) {
    super(ui, "BEAT DETECT", x, y, WIDTH, HEIGHT);
    this.gate = gate;

    float yp = this.height - 96;
    new UIKnob(4, yp).setParameter(gate.minBand).addToContainer(this);
    new UIKnob(4 + 1*34, yp).setParameter(gate.avgBands).addToContainer(this);
    new UIKnob(4 + 2*34, yp).setParameter(gate.floor).addToContainer(this);
    new UIKnob(4 + 3*34, yp).setParameter(gate.threshold).addToContainer(this);
    new UIKnob(4, yp + 48).setParameter(gate.eq.gain).addToContainer(this);
    new UIKnob(4 + 1*34, yp + 48).setParameter(gate.eq.slope).addToContainer(this);
    new UIKnob(4 + 2*34, yp + 48).setParameter(gate.eq.attack).addToContainer(this);
    new UIKnob(4 + 3*34, yp + 48).setParameter(gate.release).addToContainer(this);

    this.eqBands = gate.eq.numBands / 2;
    this.masterX = 6;
    this.masterWidth = this.kickWidth = 12;
    this.kickX = this.width - this.masterWidth - 6;

    this.eqX = this.masterX + this.masterWidth + 4;
    this.eqWidth = this.kickX - this.eqX - 4;
    this.eqHeight = 62;
    this.eqTop = UIWindow.TITLE_LABEL_HEIGHT;
    this.eqBottom = this.eqTop + this.eqHeight;

    this.piece = (this.eqWidth-2) / this.eqBands;

  }

  @Override
  public void onDraw(UI ui, PGraphics pg) {
    super.onDraw(ui, pg);

    int highlight = ui.getHighlightColor();

    pg.stroke(0xff999999);
    pg.fill(0xff000000);
    pg.rect(this.eqX, this.eqTop, this.eqWidth, this.eqHeight);
    pg.rect(this.masterX, this.eqTop, this.masterWidth, this.eqHeight);
    pg.fill(LXColor.hsb(LXColor.h(highlight), LXColor.s(highlight), 100*this.gate.getValuef()));
    pg.rect(this.kickX, this.eqTop, this.kickWidth, this.eqHeight);

    pg.noStroke();

    // Band range
    pg.noStroke();
    pg.fill(0xff444444);
    this.bandX = this.eqX + 1 + (this.piece * this.gate.minBand.getValuei());
    this.bandWidth = 1 + this.piece * PApplet.min(this.gate.avgBands.getValuei(), this.eqBands - this.gate.minBand.getValuei());
    pg.rect(this.bandX, this.eqTop + 1, this.bandWidth, this.eqHeight- 1);
    if ((this.gate.minBand.getValuei() + this.gate.avgBands.getValuei()) <= this.eqBands) {
      pg.fill(0xff393939);
      pg.rect(this.bandX + this.bandWidth - this.piece, this.eqTop + 3, this.piece-1, this.eqHeight-5);
    }

    // Eq bands
    for (int i = 0; i < this.eqBands; ++i) {
      if (i >= this.gate.minBand.getValuei() && (i < (this.gate.minBand.getValuei() + this.gate.avgBands.getValuei()))) {
        pg.fill(highlight);
      } else {
        pg.fill(ui.getSelectionColor());
      }
      pg.rect(this.eqX + 2 + this.piece*i, this.eqBottom, this.piece-1, -(this.eqHeight-1)*this.gate.eq.getBandf(i));
    }
    pg.fill(highlight);
    pg.rect(this.masterX + 1, this.eqBottom, this.masterWidth-1, -(this.eqHeight-1)*this.gate.getLevelf());

    float threshY = (this.eqBottom-1) - (this.eqHeight-2) * this.gate.threshold.getValuef();
    float floorY = (this.eqBottom-1) - (this.eqHeight-2) * this.gate.floor.getValuef() * this.gate.threshold.getValuef();
    pg.stroke(0xff6666ff);
    pg.line(this.masterX+1, floorY, this.masterX + this.masterWidth - 1, floorY);
    pg.line(this.eqX+1, floorY, this.eqX + this.eqWidth - 1, floorY);

    pg.stroke(0xffff6666);
    pg.line(this.masterX, threshY, this.masterX + this.masterWidth - 1, threshY);
    pg.line(this.eqX, threshY, this.eqX + this.eqWidth - 1, threshY);

    redraw();
  }

  private boolean bandDragging = false;
  private boolean bandDraggingRange = false;
  private float bandDelta = 0;

  @Override
  public void onMousePressed(float mx, float my) {
    super.onMousePressed(mx, my);
    this.bandDragging = false;
    if ((mx >= this.bandX) && (mx < (this.bandX + this.bandWidth)) &&
        (my >= this.eqTop) && (my <= this.eqBottom)) {
      this.bandDragging = true;
      this.bandDraggingRange = (mx > (this.bandX + this.piece * (this.gate.avgBands.getValuei() -1 ))) && (mx < (this.eqX + this.eqWidth - this.piece));
      this.bandDelta = 0;
    }
  }

  @Override
  public void onMouseDragged(float mx, float my, float dx, float dy) {
    super.onMouseDragged(mx, my, dx, dy);
    if (this.bandDragging) {
      if (mx >= this.eqX && mx <= (this.eqX + this.eqWidth)) {
        this.bandDelta += dx;
        int steps = (int) (this.bandDelta / this.piece);
        if (steps != 0) {
          if (this.bandDraggingRange) {
            int newVal = PApplet.constrain(this.gate.avgBands.getValuei() + steps, 1, this.eqBands);
            this.gate.avgBands.setValue(newVal);
          } else {
            int newVal = PApplet.constrain(this.gate.minBand.getValuei() + steps, 0, this.eqBands-1);
            this.gate.minBand.setValue(newVal);
          }
          this.bandDelta = this.bandDelta % (steps * this.piece);
        }
      }
    }
  }
}
