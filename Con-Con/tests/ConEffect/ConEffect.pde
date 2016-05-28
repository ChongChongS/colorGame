// The Nature of Code
// Daniel Shiffman
// http://natureofcode.com

// A blob skeleton
// Could be used to create blobbly characters a la Nokia Friends
// http://postspectacular.com/work/nokia/friends/start

import shiffman.box2d.*;

import org.jbox2d.collision.shapes.*;
import org.jbox2d.common.*;
import org.jbox2d.dynamics.*;
import org.jbox2d.dynamics.joints.*;
//import de.bezier.guido.*;
import controlP5.*;

// A reference to our box2d world
Box2DProcessing box2d;

// A list we'll use to track fixed objects
ArrayList<Boundary> boundaries;

// Our "blob" object
ArrayList<Skeleton> blobs;

// Just a single box this time
Box box;
// The Spring that will attach to the box from the mouse
Spring spring;

// Draw creature design or skeleton?
boolean skeleton;

void setup() {
  size(1200, 800, P3D);
  //ui
  //Interactive.make( this );
  cp5 = new ControlP5(this);
  uiPanel = new Panel();
  pss2 = new ArrayList<ParticleSystem2>();

  imgs = new PImage[5];
  imgs[0] = loadImage("corona.png");
  imgs[1] = loadImage("emitter.png");
  imgs[2] = loadImage("particle.png");
  imgs[3] = loadImage("texture.png");
  imgs[4] = loadImage("reflection.png");

  // Initialize box2d physics and create the world
  box2d = new Box2DProcessing(this);
  box2d.createWorld();

  // Add some boundaries
  boundaries = new ArrayList<Boundary>();
  boundaries.add(new Boundary(width/2, height-5, width, 10));
  boundaries.add(new Boundary(width/2, 5, width, 10));
  boundaries.add(new Boundary(width-5, height/2, 10, height));
  boundaries.add(new Boundary(5, height/2, 10, height));

  // Make a new blob
  blobs = new ArrayList<Skeleton>();
  blobs.add(new Skeleton());

  // Make the box
  box = new Box(width/2, height/2);

  // Make the spring (it doesn't really get initialized until the mouse is clicked)
  spring = new Spring();
}

void draw() {
  if(strangeMode)
    blendMode(ADD);
  else
    blendMode(BLEND);
  background(0);
  if(showPanel){
    cp5.setVisible(true);
    uiPanel.draw();
  }
  else
    cp5.setVisible(false);

  //particles
  if (pss2.size() > 0)
  {
    for (ParticleSystem2 p2 : pss2)
      p2.run();
  }

  // We must always step through time!
  box2d.step();


  // Show the blob!
  if (skeleton) {
    for (Skeleton b : blobs)
      b.displaySkeleton();
  } else {
    for (Skeleton b : blobs)
      b.displayCreature();
  }

  // Show the boundaries!
  for (Boundary wall : boundaries) {
    wall.display();
  }

  // Always alert the spring to the new mouse location
  spring.update(mouseX, mouseY);

  // Draw the box
  box.display();
  // Draw the spring (it only appears when active)
  spring.display();

  fill(255);
  text("Space bar to toggle creature/skeleton.\nClick and drag the box.\nPress s to hide uiPanel", 20, height-30);
}