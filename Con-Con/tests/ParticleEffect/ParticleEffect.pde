import de.bezier.guido.*;

//ui
//Slider (String nn,float minV,float maxV, float xx, float yy, float ww, float hh,float stPos) 
Slider r;
ArrayList<Slider> ss;
ArrayList<Circle> cs;

void setup(){
  size(1000,800);
  Interactive.make(this);
  
  //Sliders
  r = new Slider("r",          0,100,100,10, 100,20,50);
  //ss.add(new Slider("r",          0,100,100,10, 100,20,50));
  //ss.add(new Slider("R",        100,150,100,40, 100,20,125));
  //ss.add(new Slider("randomR",    0, 50,100,70, 100,20,20));
  //ss.add(new Slider("speed",      0,0.5,100,110,100,20,0.1));
  //ss.add(new Slider("totalPoints",0,200,100,140,100,20,100));
}

void draw ()
{
    background(0);
    fill(255,25);
    rect(0,0,300,height);//ui panel

}