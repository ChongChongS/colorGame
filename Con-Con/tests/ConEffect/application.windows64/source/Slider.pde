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
    void mouseDragged ( float mx, float my )
    {
        valueX = mx - height/2;
        
        if ( valueX < x ) valueX = x;
        if ( valueX > x+width-height ) valueX = x+width-height;
        
        value = map( valueX, x, x+width-height, minValue, maxValue);
    }

    void draw () 
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