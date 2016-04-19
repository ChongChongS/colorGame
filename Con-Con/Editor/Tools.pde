/*
tool1：填色工具
 tool2：颜色橡皮工具
 tool3：终点工具
 tools：有活菌工具
 */
class Tools extends Toggle
{
  int id;
  Tools(PVector p, PVector s, String t, int i)
  {
    super(p, s, t);
    this.id = i;
  }

  void display()
  {
    super.display();
    if (choosed)
    {
      switch(id)
      {
        //tool1:fill color tool
      case 1:
        fill(color(ep.r.getValue(), ep.g.getValue(), ep.b.getValue(), ep.a.getValue()));
        rect(mouseX - 5, mouseY - 5, 10, 10);
        break;
        //tool2:cancle color tool
      case 2:
        fill(color(230));
        stroke(0);
        ellipse(mouseX, mouseY, 10, 10);
        break;
        //tool3:destination
      case 3:
        fill(color(255, 0, 0));
        stroke(0);
        rect(mouseX, mouseY - 15, cellLength/3, 15);
        line(mouseX, mouseY - 15, mouseX, mouseY + 15);
        break;
        //tool4:hasLiveBacteria
      case 4:
        noFill();
        stroke(0);
        strokeWeight(2);
        ellipse(mouseX, mouseY, 45, 45);
        ellipse(mouseX, mouseY - 10, 8, 8);
        ellipse(mouseX - 10, mouseY + 10, 8, 8);
        ellipse(mouseX + 12, mouseY + 8, 8, 8);
        break;
      }
    }
  }

  void clickEvent()
  {
    if (choosed)
    {
      switch(id)
      {
      case 1:
        println("tool1:Fill Color");
        for (Cell c : cells)
        {
          if (c.isInside(mouseX, mouseY))
          {
            c.col = color(ep.r.getValue(), ep.g.getValue(), ep.b.getValue(), ep.a.getValue());
            c.isPoisonous = true;
          }
        }
        break;
      case 2:
        println("tool2:Cancle Color");
        for (Cell c : cells)
        {
          if (c.isInside(mouseX, mouseY))
          {
            c.col = color(0);
            c.isPoisonous = false;
          }
        }
        break;
      case 3:
        println("tool3:Set or Cancle Destination");
        for (Cell c : cells)
        {
          if (c.isInside(mouseX, mouseY))
          {
            c.isDestination = !c.isDestination;
          }
        }
        break;
      case 4:
        println("tool4:Set or Cancle LiveBacteria");
        for (Cell c : cells)
        {
          if (c.isInside(mouseX, mouseY))
          {
            c.hasViableBacteria = !c.hasViableBacteria;
          }
        }
        break;
      }
    }
  }
}