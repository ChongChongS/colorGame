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

  void run() {
    update();
    render();
  }

  void applyForce(PVector f) {
    acc.add(f);
  }

  // Method to update location
  void update() {
    vel.add(acc);
    loc.add(vel);
    acc.mult(0);
    lifespan -= 2.0;
  }

  // Method to display
  void render() {
    imageMode(CENTER);
    tint(lifespan);
    image(img, loc.x, loc.y, 32, 32);
  }

  // Is the particle still useful?
  boolean isDead() {
    if (lifespan <= 0.0) {
      return true;
    } 
    else {
      return false;
    }
  }
}

class Particle2{
   PVector loc;
  float lifespan;
  int R;
  color col;
  float r,cx,cy;

  Particle2(color mCol, int mR) {
    col = mCol;
    R = mR;
    lifespan = 255.0;
    r = random(5, 50);
    cx = 0;
    cy = 0;
    
    float ran = random(100);
    float temX = random(width/2 - mR,width/2 + mR);
    float temY = sqrt(pow(R,2) - pow(abs(width/2 - temX),2));
    if(ran < 50){
      loc = new PVector(temX,height/2 - temY);
    }
    else
      loc = new PVector(temX,height/2 + temY);
  }
  
  Particle2(float x,float y,color mCol, int mR) {
    col = mCol;
    R = mR;
    lifespan = 255.0;
    r = random(3, 20);
    cx = x;
    cy = y;
    
    float ran = random(100);
    float temX = random(cx - mR,cx + mR);
    float temY = sqrt(pow(R,2) - pow(abs(cx - temX),2));
    if(ran < 50){
      loc = new PVector(temX,cy - temY);
    }
    else
      loc = new PVector(temX,cy + temY);
  }
  
  void run() {
    update();
    display();
  }

  // Method to update location
  void update() {
    PVector f = new PVector(width/2,height/2).sub(loc);
    loc.add(f.normalize().mult(-0.8));
    if(r > 3)
      r -= 0.2;
    lifespan -= 10.0;
  }

  // Method to display
  void display() {
    fill(col, lifespan);
    noStroke();
    ellipse(loc.x, loc.y, r, r);
  }

  // Is the particle still useful?
  boolean isDead() {
    if (lifespan <= 0.0) {
      return true;
    } else {
      return false;
    }
  }
}