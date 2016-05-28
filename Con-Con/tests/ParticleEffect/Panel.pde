class Panel{
  PVector position;
  PVector size;
  color bgCol;

  Panel() {
    position = new PVector(0, 0);
    size     = new PVector(300, height);
    bgCol    = color(255, 25);

    //slider 控制每个圆的半径
    cp5.addSlider("circleRadius")
      .setPosition(50, 10)
      .setSize(100, 15)
      .setRange(0, 100)
      .setValue(50)
      ;
    //slider 控制整个圆的半径
    cp5.addSlider("bodyRadius")
      .setPosition(50, 35)
      .setSize(100, 15)
      .setRange(100, 200)
      .setValue(150)
      ;
    //slider 控制大圆宽度的随机范围
    cp5.addSlider("randomRadius")
      .setPosition(50, 60)
      .setSize(100, 15)
      .setRange(0, 50)
      .setValue(20)
      ;
    //slider 控制每个圆变化的速度
    cp5.addSlider("speed")
      .setPosition(50, 85)
      .setSize(100, 15)
      .setRange(0, 0.5)
      .setValue(0.1)
      ;
    //slider 控制圆圈总共的个数
    cp5.addSlider("totalPoints")
      .setPosition(50, 110)
      .setSize(100, 15)
      .setRange(0, 200)
      .setValue(100)
      ;
    //button 生成粒子系统
    cp5.addButton("Generate")
      .setPosition(50, 135)
      .setSize(100, 15)
      ;
    //矩阵控制动画
  }

  void draw() {
    fill(bgCol);
    rect(position.x, position.y, size.x, size.y);
  }
}