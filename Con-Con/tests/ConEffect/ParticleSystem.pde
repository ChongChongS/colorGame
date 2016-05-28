class ParticleSystem {

  ArrayList<Particle> particles;    // An arraylist for all the particles

  PImage[] textures;

  ParticleSystem(PImage[] imgs, PVector v) {
    textures = imgs;
    particles = new ArrayList();              // Initialize the arraylist
  }

  void run() {
    for (int i = particles.size()-1; i >= 0; i--) {
      Particle p = particles.get(i);
      p.run();
      if (p.isDead()) {
        particles.remove(i);
      }
    }
  }

  void addParticle(float x, float y) {
    int r = int(random(textures.length));
    particles.add(new Particle(x,y,textures[r]));
  }
  
  
  void applyForce(PVector f) {
    for (Particle p : particles) {
      p.applyForce(f);
    }
  }

  void addParticle(Particle p) {
    particles.add(p);
  }

  // A method to test if the particle system still has particles
  boolean dead() {
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

  ParticleSystem2(int R, int num, color col) {
    locX = 0;
    locY = 0;
    particles2 = new ArrayList<Particle2>();        
    for (int i = 0; i < num; i++) {
      particles2.add(new Particle2(col, R));
    }
  }
  
  ParticleSystem2(float cx,float cy,int R, int num, color col) {
    locX = cx;
    locY = cy;
    
    particles2 = new ArrayList<Particle2>();        
    for (int i = 0; i < num; i++) {
      particles2.add(new Particle2(cx,cy,col, R));
    }
  }

  void run() {
    for (int i = particles2.size()-1; i >= 0; i--) {
      Particle2 p = particles2.get(i);
      p.run();
      if (p.isDead()) {
        particles2.remove(i);
      }
    }
  }

  // A method to test if the particle system still has particles
  boolean dead() {
    if (particles2.isEmpty()) {
      return true;
    } else {
      return false;
    }
  }
  
}