//ui
ControlP5 cp5;
Panel uiPanel;
float particleSpeed = 0.2;
boolean isup  = false;
boolean isleft  = false;
boolean isright  = false;
boolean showParticles = true;
boolean showParticles2 = true;
boolean mouseReleased = false;
ArrayList<ParticleSystem2> pss2;
boolean colorOrFirework = true;
PImage[] imgs;
float rRadiusScale = 1.0;
float rRadiusSpeed = 2.0;
float rLifeSpeed = 8.0;
boolean showPanel = true;

void keyPressed() {
  if (key == ' ') {
    skeleton = !skeleton;
  }
  if (key == 's') {
    showPanel = !showPanel;
  }
}


// When the mouse is released we're done with the spring
void mouseReleased() {
  mouseReleased = true;
  spring.destroy();
}

// When the mouse is pressed we. . .
void mousePressed() {
  mouseReleased = false;

  // Check to see if the mouse was clicked on the box
  if (box.contains(mouseX, mouseY)) {
    // And if so, bind the mouse location to the box with a spring
    spring.bind(mouseX, mouseY, box);
  }
}

void addParticle2() {
  Vec2 center = new Vec2(0, 0);
  for (Body b : blobs.get(0).bodies) {
    // We look at each body and get its screen position
    Vec2 pos = box2d.getBodyPixelCoord(b);
    center.addLocal(pos);
  }
  center.mulLocal(1.0/blobs.get(0).bodies.size());
  
  color newCol = color(random(255),random(255),random(255));
  pss2.add(new ParticleSystem2(center.x, center.y, int(blobs.get(0).bodyRadius + 150), int(random(80, 120)), newCol));
}