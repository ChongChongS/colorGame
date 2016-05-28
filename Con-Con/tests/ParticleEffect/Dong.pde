class Dong {
  float x, y;
  float r0, r1,maxr,speed;
  float R,randomR;

  Dong(float mR,float rR,float mr,float s) {
    R = mR;
    randomR = rR;
    maxr = mr;
    speed = s;
    
    float f= random(-PI, PI);
    x = cos(f) * random(R - randomR, R + randomR);
    y = sin(f) * random(R - randomR, R + randomR);
    r0 = random(maxr/20, maxr/5);
  }

  void display() {
    r1 += (r0 - r1) * speed;
    ellipse(x, y, r1, r1);
  }

  void update() {
    r1 = maxr;
  }
}