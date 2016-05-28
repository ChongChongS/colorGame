class Panel {
  PVector position;
  PVector size;
  color bgCol;

  Panel() {
    position = new PVector(0, 0);
    size     = new PVector(300, 100);
    bgCol    = color(255, 25);

    cp5.addToggle("showParticles")
      .setPosition(10, 10)
      .setSize(20, 20)
      .setValue(true)
      ;

    cp5.addTextlabel("label1")
      .setText("show moving particles?")
      .setPosition(40, 10)
      .setFont(createFont(" ", 15))
      ;

    cp5.addToggle("isup")
      .setPosition(10, 50)
      .setSize(20, 20)
      ;

    cp5.addTextlabel("label2")
      .setText("add up direction speed?")
      .setPosition(40, 50)
      .setFont(createFont(" ", 15))
      ;

    cp5.addToggle("isleft")
      .setPosition(10, 90)
      .setSize(20, 20)
      ;

    cp5.addTextlabel("label3")
      .setText("add left direction speed?")
      .setPosition(40, 90)
      .setFont(createFont(" ", 15))
      ;

    cp5.addToggle("isright")
      .setPosition(10, 130)
      .setSize(20, 20)
      ;

    cp5.addTextlabel("label4")
      .setText("add right direction speed?")
      .setPosition(40, 130)
      .setFont(createFont(" ", 15))
      ;

    //particleSpeed
    cp5.addSlider("particleSpeed")
      .setPosition(10, 170)
      .setSize(100, 20)
      .setRange(0, 1)
      .setValue(0.2)
      ;
    cp5.getController("particleSpeed").getCaptionLabel().align(ControlP5.LEFT, ControlP5.BOTTOM_OUTSIDE).setPaddingX(0);

    cp5.addTextlabel("label5")
      .setText("change particle speed")
      .setPosition(120, 170)
      .setFont(createFont(" ", 15))
      ;

    cp5.addToggle("showParticles2")
      .setPosition(10, 210)
      .setSize(20, 20)
      .setValue(true)
      ;

    cp5.addTextlabel("label6")
      .setText("show released particles?")
      .setPosition(40, 210)
      .setFont(createFont(" ", 15))
      ;
  }

  void draw() {
    fill(bgCol);
    rect(position.x, position.y, size.x, size.y);
  }
}