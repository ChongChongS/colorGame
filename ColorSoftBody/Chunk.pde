float blueprintK = 20f;
float forceScale = 0.04f;

class Chunk extends Mover
{
  //以下变量继承自超类Mover
  //PVector pos;
  //PVector speed;
  //PVector force;

  //float   mass;
  //boolean isLocked;
  ArrayList<Particle>ps;
  
  Chunk(PVector ppos){
    super();
    super.pos  = ppos.copy();
    super.mass = 0;
    ps = new ArrayList<Particle>();
  }
  
  
  void add(Particle p){
    this.ps.add(p);
  }
  
  void update(){
    for(Particle p: ps){
      p.update();
    }
  }
  
  void display(){
    pushMatrix();
    translate(pos.x,pos.y);
    
    for(Particle p: ps){
      p.display();
    }
    
    popMatrix();
    
    fill(255,0,0);
    stroke(0);
    strokeWeight(2);
    
    ellipse(pos.x,pos.y,10,10);
  }
  
  void loadShape(float[][] blueprint, float interval){
    int xx,yy;
    xx = blueprint.length;
    yy = blueprint[0].length;
    
    ps = new ArrayList<Particle>();
    
    Particle[][] pp;
    pp = new Particle[xx][];
    for(int i=0; i<xx; i++){
      pp[i] = new Particle[yy]; 
    }
    
    for(int i=0; i<xx; i++){
      for(int j=0; j<yy; j++){
        pp[i][j] = null;
        float weight;
        weight = blueprint[i][j];
        if(weight>0.05f){
          Particle p;
          p = new Particle(new PVector(j*interval,i*interval),weight*blueprintK,this);
          pp[i][j] = p;
          im.add(p);
          this.add(p);
        }  
      }
    }
    
    
    for(int i=0; i<xx; i++){
      for(int j=0; j<yy; j++){
        if(pp[i][j]==null){
          continue;
        }
        Particle xxp,xxn,yyp,yyn;
        xxp = null;
        xxn = null;
        yyp = null;
        yyn = null;
        if(i!=0){
          yyn = pp[i-1][j];
        }
        if(i!=xx-1){
          yyp = pp[i+1][j];
        }
        if(j!=0){
          yyn = pp[i][j-1];
        }
        if(j!=yy-1){
          yyp = pp[i][j+1];
        }
        
        pp[i][j].setMates(xxp,xxn,yyp,yyn);
      }
    }
    recalPos();
  }
  
  void recalPos(){
    
    PVector np = new PVector(0,0);
    float   nm = 0;
    
    for(Particle p: ps){
      np.x = (np.x*nm+p.pos.x*p.mass)/(nm+p.mass);
      np.y = (np.y*nm+p.pos.y*p.mass)/(nm+p.mass);
      nm += p.mass;
    }
    
    for(Particle p: ps){
      p.pos.x -= np.x;
      p.pos.y -= np.y;
    }
    
    this.mass = nm;
  }
  
}