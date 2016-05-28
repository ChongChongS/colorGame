class Circle {
  float x, y;
  float r0, r1,maxr,speed;
  float R,randomR;

  Circle(float R,float rR,float mr,float s) {
    float f = random(-PI, PI);
    x = cos(f) * random(R - rR, R + rR);
    y = sin(f) * random(R - rR, R + rR);
    
    r0 = random(maxr/20, maxr/5);
    maxr = mr;
    speed = s;
  }

  void display() {
    r1 += (r0 - r1) * speed;
    ellipse(x, y, r1, r1);
  }

  void update() {
    r1 = maxr;
  }
}