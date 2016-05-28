import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import shiffman.box2d.*; 
import org.jbox2d.collision.shapes.*; 
import org.jbox2d.common.*; 
import org.jbox2d.dynamics.*; 
import org.jbox2d.dynamics.joints.*; 
import controlP5.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class ConEffect extends PApplet {

// The Nature of Code
// Daniel Shiffman
// http://natureofcode.com

// A blob skeleton
// Could be used to create blobbly characters a la Nokia Friends
// http://postspectacular.com/work/nokia/friends/start







//import de.bezier.guido.*;


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

public void setup() {
  
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

public void draw() {
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
// The Nature of Code
// Daniel Shiffman
// http://natureofcode.com

// A blob skeleton
// Could be used to create blobbly characters a la Nokia Friends
// http://postspectacular.com/work/nokia/friends/start

class Skeleton {

  // A list to keep track of all the bodies and joints
  ArrayList<Body> bodies;
  ArrayList<Joint> joints;

  float bodyRadius;  // The radius of each body that makes up the skeleton
  float radius;      // The radius of the entire blob
  float totalPoints; // How many points make up the blob

  ParticleSystem ps;

  // We should modify this constructor to receive arguments
  // So that we can make many different types of blobs
  Skeleton() {

    // Create the empty ArrayLists
    bodies = new ArrayList<Body>();
    joints = new ArrayList<Joint>();

    // Where and how big is the blob
    Vec2 center = new Vec2(width/2, height/2);
    radius = 100;
    totalPoints = 32;
    bodyRadius = 10;

    // Initialize all the points in a circle
    for (int i = 0; i < totalPoints; i++) {
      // Look polar to cartesian coordinate transformation!
      float theta = PApplet.map(i, 0, totalPoints, 0, TWO_PI);
      float x = center.x + radius * sin(theta);
      float y = center.y + radius * cos(theta);

      // Make each individual body
      BodyDef bd = new BodyDef();
      bd.type = BodyType.DYNAMIC;

      bd.fixedRotation = true; // no rotation!
      bd.position.set(box2d.coordPixelsToWorld(x, y));
      Body body = box2d.createBody(bd);

      // The body is a circle
      CircleShape cs = new CircleShape();
      cs.m_radius = box2d.scalarPixelsToWorld(bodyRadius);

      // Define a fixture
      FixtureDef fd = new FixtureDef();
      fd.shape = cs;
      fd.density = 1;
      fd.friction = 0.5f;
      fd.restitution = 0.3f;

      // Finalize the body
      body.createFixture(fd);

      // Store our own copy for later rendering
      bodies.add(body);
    }

    // Now connect the outline of the shape all with joints
    for (int i = 0; i < bodies.size(); i++) {
      DistanceJointDef djd = new DistanceJointDef();
      Body a = bodies.get(i);
      int next = i+1;
      if (i == bodies.size()-1) {
        next = 0;
      }
      Body b = bodies.get(next);
      // Connection between previous particle and this one
      djd.bodyA = a;
      djd.bodyB = b;
      // Equilibrium length is distance between these bodies
      Vec2 apos = a.getWorldCenter();
      Vec2 bpos = b.getWorldCenter();
      float d = dist(apos.x, apos.y, bpos.x, bpos.y);
      djd.length = d;
      // These properties affect how springy the joint is 
      djd.frequencyHz = 10;
      djd.dampingRatio = 0.9f;

      // Make the joint.  
      DistanceJoint dj = (DistanceJoint) box2d.world.createJoint(djd);
      joints.add(dj);
    }


    // Make some joints that cross the center of the blob between bodies
    for (int i = 0; i < bodies.size(); i++) {
      for (int j = i+2; j < bodies.size(); j+=4) { 
        DistanceJointDef djd = new DistanceJointDef();
        Body a = bodies.get(i);
        Body b = bodies.get(j);
        // Connection between two bides
        djd.bodyA = a;
        djd.bodyB = b;
        // Equilibrium length is distance between these bodies
        Vec2 apos = a.getWorldCenter();
        Vec2 bpos = b.getWorldCenter();
        float d = dist(apos.x, apos.y, bpos.x, bpos.y);

        djd.length = d;
        // These properties affect how springy the joint is 
        djd.frequencyHz = 3;
        djd.dampingRatio = 0.1f;

        // Make the joint.  
        DistanceJoint dj = (DistanceJoint) box2d.world.createJoint(djd);
        joints.add(dj);
      }
    }

    ps = new ParticleSystem(imgs, new PVector(center.x, center.y));
  }


  // Draw the skeleton as circles for bodies and lines for joints
  public void displaySkeleton() {
    // Draw the outline
    stroke(0);
    strokeWeight(1);
    for (Joint j : joints) {
      Body a = j.getBodyA();
      Body b = j.getBodyB();
      Vec2 posa = box2d.getBodyPixelCoord(a);
      Vec2 posb = box2d.getBodyPixelCoord(b);
      line(posa.x, posa.y, posb.x, posb.y);
    }

    // Draw the individual circles
    for (Body b : bodies) {
      // We look at each body and get its screen position
      Vec2 pos = box2d.getBodyPixelCoord(b);
      // Get its angle of rotation
      float a = b.getAngle();
      pushMatrix();
      translate(pos.x, pos.y);
      rotate(a);
      fill(175);
      stroke(0);
      strokeWeight(1);
      ellipse(0, 0, bodyRadius*2, bodyRadius*2);
      popMatrix();
    }
  }


  // Draw it as a creature
  public void displayCreature() {

    // Let's compute the center!
    Vec2 center = new Vec2(0, 0);

    if (showParticles) {
      for (Body b : bodies) {
        // We look at each body and get its screen position
        Vec2 pos = box2d.getBodyPixelCoord(b);
        center.addLocal(pos);
      }
      center.mulLocal(1.0f/bodies.size());
      if (isup)
        ps.applyForce(new PVector(0, -particleSpeed));
      if (isleft)
        ps.applyForce(new PVector(-particleSpeed, 0));
      if (isright) {
        ps.applyForce(new PVector(particleSpeed, 0));
      }
      for (int i = 0; i < 5; i++) {
        ps.addParticle(center.x, center.y);
      }
      ps.run();
    }

    // Make a curvy polygon 
    center = new Vec2(0, 0);
    beginShape();
    stroke(175);
    strokeWeight(bodyRadius*2);
    fill(175);
    for (Body b : bodies) {
      // We look at each body and get its screen position
      Vec2 pos = box2d.getBodyPixelCoord(b);
      curveVertex(pos.x, pos.y);
      center.addLocal(pos);
    }
    endShape(CLOSE);
    // Center is average of all points
    center.mulLocal(1.0f/bodies.size());

    // Find angle between center and side body
    Vec2 pos = box2d.getBodyPixelCoord(bodies.get(0));
    float dx = pos.x - center.x;
    float dy = pos.y - center.y;
    float angle = atan2(dy, dx)-PI/2;

    // Draw eyes and mouth relative to center
    pushMatrix();
    strokeWeight(1);
    stroke(0);
    translate(center.x, center.y);
    rotate(angle);
    fill(0);
    ellipse(-25, -50, 16, 16);
    ellipse(25, -50, 16, 16);
    line(-50, 50, 50, 50);
    popMatrix();

    //noStroke();
    //PImage img = loadImage("concon.jpg");
    //beginShape();
    //texture(img);
    //for (Body b: bodies) {
    // // We look at each body and get its screen position
    // Vec2 pos = box2d.getBodyPixelCoord(b);
    // curveVertex(pos.x, pos.y);
    //}
    //endShape(CLOSE);
  }

  public Body getFirstBody() {
    return bodies.get(0);
  }
}
// The Nature of Code
// Daniel Shiffman
// http://natureofcode.com

// A fixed boundary class

class Boundary {

  // A boundary is a simple rectangle with x,y,width,and height
  float x;
  float y;
  float w;
  float h;
  
  // But we also have to make a body for box2d to know about it
  Body b;

  Boundary(float x_,float y_, float w_, float h_) {
    x = x_;
    y = y_;
    w = w_;
    h = h_;

    // Define the polygon
    PolygonShape sd = new PolygonShape();
    // Figure out the box2d coordinates
    float box2dW = box2d.scalarPixelsToWorld(w/2);
    float box2dH = box2d.scalarPixelsToWorld(h/2);
    // We're just a box
    sd.setAsBox(box2dW, box2dH);


    // Create the body
    BodyDef bd = new BodyDef();
    bd.type = BodyType.STATIC;
    bd.position.set(box2d.coordPixelsToWorld(x,y));
    b = box2d.createBody(bd);
    
    // Attached the shape to the body using a Fixture
    b.createFixture(sd,1);
  }

  // Draw the boundary, if it were at an angle we'd have to do something fancier
  public void display() {
    fill(0);
    stroke(0);
    rectMode(CENTER);
    rect(x,y,w,h);
  }

}
// The Nature of Code
// Daniel Shiffman
// http://natureofcode.com

// A rectangular box

class Box {

  // We need to keep track of a Body and a width and height
  Body body;
  float w;
  float h;

  // Constructor
  Box(float x_, float y_) {
    float x = x_;
    float y = y_;
    w = 50;
    h = 50;
    // Add the box to the box2d world
    makeBody(new Vec2(x, y), w, h);
    body.setUserData(this);
  }

  // This function removes the particle from the box2d world
  public void killBody() {
    box2d.destroyBody(body);
  }

  public boolean contains(float x, float y) {
    Vec2 worldPoint = box2d.coordPixelsToWorld(x, y);
    Fixture f = body.getFixtureList();
    boolean inside = f.testPoint(worldPoint);
    return inside;
  }

  // Drawing the box
  public void display() {
    // We look at each body and get its screen position
    Vec2 pos = box2d.getBodyPixelCoord(body);
    // Get its angle of rotation
    float a = body.getAngle();

    rectMode(PConstants.CENTER);
    pushMatrix();
    translate(pos.x, pos.y);
    rotate(-a);
    fill(50);
    stroke(0);
    rect(0, 0, w, h);
    popMatrix();
  }

  // This function adds the rectangle to the box2d world
  public void makeBody(Vec2 center, float w_, float h_) {
    // Define and create the body
    BodyDef bd = new BodyDef();
    bd.type = BodyType.DYNAMIC;
    bd.position.set(box2d.coordPixelsToWorld(center));
    body = box2d.createBody(bd);

    // Define a polygon (this is what we use for a rectangle)
    PolygonShape sd = new PolygonShape();
    float box2dW = box2d.scalarPixelsToWorld(w_/2);
    float box2dH = box2d.scalarPixelsToWorld(h_/2);
    sd.setAsBox(box2dW, box2dH);

    // Define a fixture
    FixtureDef fd = new FixtureDef();
    fd.shape = sd;
    // Parameters that affect physics
    fd.density = 1;
    fd.friction = 0.3f;
    fd.restitution = 0.5f;

    body.createFixture(fd);
    //body.setMassFromShapes();

    // Give it some initial random velocity
    body.setLinearVelocity(new Vec2(random(-5, 5), random(2, 5)));
    body.setAngularVelocity(random(-5, 5));
  }
}
//ui
ControlP5 cp5;
Panel uiPanel;
float particleSpeed = 0.2f;
boolean isup  = false;
boolean isleft  = false;
boolean isright  = false;
boolean showParticles = true;
boolean showParticles2 = true;
boolean mouseReleased = false;
ArrayList<ParticleSystem2> pss2;
boolean colorOrFirework = true;
PImage[] imgs;
float rRadiusScale = 1.0f;
float rRadiusSpeed = 2.0f;
float rLifeSpeed = 8.0f;
boolean showPanel = true;
boolean strangeMode = true;

public void keyPressed() {
  if (key == ' ') {
    skeleton = !skeleton;
  }
  if (key == 's') {
    showPanel = !showPanel;
  }
}


// When the mouse is released we're done with the spring
public void mouseReleased() {
  mouseReleased = true;
  spring.destroy();
}

// When the mouse is pressed we. . .
public void mousePressed() {
  mouseReleased = false;

  // Check to see if the mouse was clicked on the box
  if (box.contains(mouseX, mouseY)) {
    // And if so, bind the mouse location to the box with a spring
    spring.bind(mouseX, mouseY, box);
  }
}

public void addParticle2() {
  Vec2 center = new Vec2(0, 0);
  for (Body b : blobs.get(0).bodies) {
    // We look at each body and get its screen position
    Vec2 pos = box2d.getBodyPixelCoord(b);
    center.addLocal(pos);
  }
  center.mulLocal(1.0f/blobs.get(0).bodies.size());
  
  int newCol = color(random(255),random(255),random(255));
  pss2.add(new ParticleSystem2(center.x, center.y, PApplet.parseInt(blobs.get(0).bodyRadius + 150), PApplet.parseInt(random(80, 120)), newCol));
}
class Panel {
  PVector position;
  PVector size;
  int bgCol;

  Panel() {
    position = new PVector(0, 0);
    size     = new PVector(600, 890);
    bgCol    = color(255, 50);

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
      .setValue(0.2f)
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

    cp5.addToggle("colorOrFirework")
      .setPosition(10, 250)
      .setSize(20, 20)
      .setValue(true)
      ;

    cp5.addTextlabel("label7")
      .setText("color or firework ?")
      .setPosition(40, 250)
      .setFont(createFont(" ", 15))
      ;

    cp5.addSlider("rRadiusScale")
      .setPosition(10, 290)
      .setSize(100, 20)
      .setRange(0, 5)
      .setValue(1)
      ;
    cp5.getController("rRadiusScale").getCaptionLabel().align(ControlP5.LEFT, ControlP5.BOTTOM_OUTSIDE).setPaddingX(0);

    cp5.addTextlabel("label8")
      .setText("control each circle radius")
      .setPosition(120, 290)
      .setFont(createFont(" ", 15))
      ;

    cp5.addSlider("rRadiusSpeed")
      .setPosition(10, 330)
      .setSize(100, 20)
      .setRange(0, 10)
      .setValue(2)
      ;
    cp5.getController("rRadiusSpeed").getCaptionLabel().align(ControlP5.LEFT, ControlP5.BOTTOM_OUTSIDE).setPaddingX(0);

    cp5.addTextlabel("label9")
      .setText("control the big circle radius")
      .setPosition(120, 330)
      .setFont(createFont(" ", 15))
      ;

    cp5.addSlider("rLifeSpeed")
      .setPosition(10, 370)
      .setSize(100, 20)
      .setRange(0, 20)
      .setValue(8)
      ;
    cp5.getController("rLifeSpeed").getCaptionLabel().align(ControlP5.LEFT, ControlP5.BOTTOM_OUTSIDE).setPaddingX(0);

    cp5.addTextlabel("label10")
      .setText("control circle life speed")
      .setPosition(120, 370)
      .setFont(createFont(" ", 15))
      ;

    cp5.addToggle("strangeMode")
      .setPosition(10, 410)
      .setSize(20, 20)
      .setValue(true)
      ;

    cp5.addTextlabel("label11")
      .setText("you wanna play strange mode?")
      .setPosition(40, 410)
      .setFont(createFont(" ", 15))
      ;
  }

  public void draw() {
    noStroke();
    fill(bgCol);
    rect(position.x, position.y, size.x, size.y);
    stroke(255, 200);
    strokeWeight(1);
    line(10, 206, 300, 206);
    line(10, 405, 300, 405);
  }
}
class Particle {
  PVector loc;
  PVector vel;
  PVector acc;
  float lifespan;

  PImage img;

  // Another constructor (the one we are using here)
  Particle(float x, float y, PImage img_) {
    // Boring example with constant acceleration
    acc = new PVector(0, 0);
    vel = PVector.random2D();
    loc = new PVector(x, y);
    lifespan = 255;
    img = img_;
  }

  public void run() {
    update();
    render();
  }

  public void applyForce(PVector f) {
    acc.add(f);
  }

  // Method to update location
  public void update() {
    vel.add(acc);
    loc.add(vel);
    acc.mult(0);
    lifespan -= 2.0f;
  }

  // Method to display
  public void render() {
    imageMode(CENTER);
    tint(lifespan);
    image(img, loc.x, loc.y, 32, 32);
  }

  // Is the particle still useful?
  public boolean isDead() {
    if (lifespan <= 0.0f) {
      return true;
    } else {
      return false;
    }
  }
}

class Particle2 {
  PVector loc;
  float lifespan;
  int R;
  int col;
  float r, cx, cy;
  PImage img;

  Particle2(int mCol, int mR) {
    col = mCol;
    R = mR;
    lifespan = 255.0f;
    r = random(5, 50);
    cx = 0;
    cy = 0;
    img = null;

    float ran = random(100);
    float temX = random(width/2 - mR, width/2 + mR);
    float temY = sqrt(pow(R, 2) - pow(abs(width/2 - temX), 2));
    if (ran < 50) {
      loc = new PVector(temX, height/2 - temY);
    } else
      loc = new PVector(temX, height/2 + temY);
  }

  Particle2(float x, float y, int mCol, int mR) {
    col = mCol;
    R = mR;
    lifespan = 255.0f;
    r = random(3, 20);
    cx = x;
    cy = y;
    int index = PApplet.parseInt(random(0,5));
    img = imgs[index];

    float ran = random(100);
    float temX = random(cx - mR, cx + mR);
    float temY = sqrt(pow(R, 2) - pow(abs(cx - temX), 2));
    if (ran < 50) {
      loc = new PVector(temX, cy - temY);
    } else
      loc = new PVector(temX, cy + temY);
  }

  public void run() {
    update();
    display();
  }

  // Method to update location
  public void update() {
    PVector f = new PVector(width/2, height/2).sub(loc);
    loc.add(f.normalize().mult(-rRadiusSpeed));
    if (r > 3)
      r -= 0.2f;
    lifespan -= rLifeSpeed;
  }

  // Method to display
  public void display() {
    if (colorOrFirework) {
      fill(col, lifespan);
      noStroke();
      ellipse(loc.x, loc.y, r * rRadiusScale, r * rRadiusScale);
    }
    else
    {
      imageMode(CENTER);
      tint(lifespan);
      image(img, loc.x, loc.y, r * 3 * rRadiusScale, r * 3 * rRadiusScale);
    }
  }

  // Is the particle still useful?
  public boolean isDead() {
    if (lifespan <= 0.0f) {
      return true;
    } else {
      return false;
    }
  }
}
class ParticleSystem {

  ArrayList<Particle> particles;    // An arraylist for all the particles

  PImage[] textures;

  ParticleSystem(PImage[] imgs, PVector v) {
    textures = imgs;
    particles = new ArrayList();              // Initialize the arraylist
  }

  public void run() {
    for (int i = particles.size()-1; i >= 0; i--) {
      Particle p = particles.get(i);
      p.run();
      if (p.isDead()) {
        particles.remove(i);
      }
    }
  }

  public void addParticle(float x, float y) {
    int r = PApplet.parseInt(random(textures.length));
    particles.add(new Particle(x,y,textures[r]));
  }
  
  
  public void applyForce(PVector f) {
    for (Particle p : particles) {
      p.applyForce(f);
    }
  }

  public void addParticle(Particle p) {
    particles.add(p);
  }

  // A method to test if the particle system still has particles
  public boolean dead() {
    if (particles.isEmpty()) {
      return true;
    } 
    else {
      return false;
    }
  }
}


class ParticleSystem2 {

  ArrayList<Particle2> particles2;
  int R;
  float locX,locY;

  ParticleSystem2(int R, int num, int col) {
    locX = 0;
    locY = 0;
    particles2 = new ArrayList<Particle2>();        
    for (int i = 0; i < num; i++) {
      particles2.add(new Particle2(col, R));
    }
  }
  
  ParticleSystem2(float cx,float cy,int R, int num, int col) {
    locX = cx;
    locY = cy;
    
    particles2 = new ArrayList<Particle2>();        
    for (int i = 0; i < num; i++) {
      particles2.add(new Particle2(cx,cy,col, R));
    }
  }

  public void run() {
    for (int i = particles2.size()-1; i >= 0; i--) {
      Particle2 p = particles2.get(i);
      p.run();
      if (p.isDead()) {
        particles2.remove(i);
      }
    }
  }

  // A method to test if the particle system still has particles
  public boolean dead() {
    if (particles2.isEmpty()) {
      return true;
    } else {
      return false;
    }
  }
  
}
public class Slider
{
    float x, y, width, height;
    float valueX = 0, value,minValue,maxValue;
    String name;
    
    Slider (String nn,float minV,float maxV, float xx, float yy, float ww, float hh,float stPos) 
    {
        name = nn;
        minValue = minV;
        maxValue = maxV;
        
        x = xx; 
        y = yy; 
        width = ww; 
        height = hh;
        
        valueX = x + (width - height) * (stPos - minV)/(maxV - minV);
        value = map( valueX, x, x+width-height, minValue, maxValue);
    
        // register it
        //Interactive.add( this );
    }
    
    // called from manager
    public void mouseDragged ( float mx, float my )
    {
        valueX = mx - height/2;
        
        if ( valueX < x ) valueX = x;
        if ( valueX > x+width-height ) valueX = x+width-height;
        
        value = map( valueX, x, x+width-height, minValue, maxValue);
    }

    public void draw () 
    {
        noStroke();
        
        fill(255);
        textSize(15);
        text(name,x - name.length()*10,y + height*3/4);
        text(value,x + width + 5,y + height*3/4);
        
        fill( 100 );
        rect(x, y, width, height);
        
        fill( 120 );
        rect( valueX, y, height, height );
    }
}
// The Nature of Code
// Daniel Shiffman
// http://natureofcode.com

// Class to describe the spring joint (displayed as a line)

class Spring {

  // This is the box2d object we need to create
  MouseJoint mouseJoint;

  Spring() {
    // At first it doesn't exist
    mouseJoint = null;
  }

  // If it exists we set its target to the mouse location 
  public void update(float x, float y) {
    if (mouseJoint != null) {
      // Always convert to world coordinates!
      Vec2 mouseWorld = box2d.coordPixelsToWorld(x,y);
      mouseJoint.setTarget(mouseWorld);
    }
  }

  public void display() {
    if (mouseJoint != null) {
      // We can get the two anchor points
      Vec2 v1 = new Vec2(0,0);
      mouseJoint.getAnchorA(v1);
      Vec2 v2 = new Vec2(0,0);
      mouseJoint.getAnchorB(v2);
      // Convert them to screen coordinates
      v1 = box2d.coordWorldToPixels(v1);
      v2 = box2d.coordWorldToPixels(v2);
      // And just draw a line
      //coordinate
      stroke(color(255,25));
      strokeWeight(1);
      line(v2.x,v2.y,v2.x,0);
      line(v2.x,v2.y,v2.x,height);
      line(0,v2.y,v2.x,v2.y);
      line(v2.x,v2.y,width,v2.y);
      //direction line
      stroke(color(255,0,0));
      strokeWeight(2);
      line(v1.x,v1.y,v2.x,v2.y);
      noFill();
      ellipse(v1.x,v1.y,10,10);
      //angle
      float angle = atan2(v1.x - v2.x, v1.y - v2.y) - PI/2;
      arc(v2.x,v2.y, 80, 80, -angle, 0, PIE);
      fill(255,200);
      text(degrees(angle),v2.x + 40,v2.y - 40);
    }
  }


  // This is the key function where
  // we attach the spring to an x,y location
  // and the Box object's location
  public void bind(float x, float y, Box box) {
    // Define the joint
    MouseJointDef md = new MouseJointDef();
    // Body A is just a fake ground body for simplicity (there isn't anything at the mouse)
    md.bodyA = box2d.getGroundBody();
    // Body 2 is the box's boxy
    md.bodyB = box.body;
    // Get the mouse location in world coordinates
    Vec2 mp = box2d.coordPixelsToWorld(x,y);
    // And that's the target
    md.target.set(mp);
    // Some stuff about how strong and bouncy the spring should be
    md.maxForce = 1000.0f * box.body.m_mass;
    md.frequencyHz = 5.0f;
    md.dampingRatio = 0.9f;

    // Make the joint!
    mouseJoint = (MouseJoint) box2d.world.createJoint(md);
  }

  public void destroy() {
    // We can get rid of the joint when the mouse is released
    if (mouseJoint != null) {
      box2d.world.destroyJoint(mouseJoint);
      mouseJoint = null;
      if(showParticles2)
        addParticle2();
    }
  }

}
  public void settings() {  size(1200, 800, P3D); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "ConEffect" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
