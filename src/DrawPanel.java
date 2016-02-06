import java.awt.*;

import javax.swing.*;

public class DrawPanel extends JPanel{
	float x;
	float y;
	
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		
			g.setColor(Color.black);
			g.fillOval((int)(x*700),(int)(700-y*700), 20, 20);
	}

	public void setXY(float f, float g){
		x = f;
		y = g;
	}
}
