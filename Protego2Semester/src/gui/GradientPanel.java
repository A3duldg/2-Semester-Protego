package gui;

import java.awt.*;
import javax.swing.*;

public class GradientPanel extends JPanel {
    @Override //We override the painting method of a normal panel to get our custom effect
    protected void paintComponent(Graphics g) { //paintComponent is called whenever the panel needs to be drawn (opening, resizing, etc)
        super.paintComponent(g); // Calls the normal JPanel painting method
        Graphics2D g2d = (Graphics2D) g; // // we implement Graphics2D which gives more options in terms of paint & layout

        
     // We need to know how big the panel is, so our gradient fills it all
        int width = getWidth(); 
        int height = getHeight();
        

        // The type of colors we want in our gradient
        Color color1 = new Color(44, 44, 44);   // Charcoal Grey
        Color color2 = new Color(75, 75, 75);  // Gun Metal
        
        
        // We call the gradient paint method to smoothly create a gradient between our colors
        // 0,0 is the top left corner and 0,height is the bottom left corner 
        GradientPaint gp = new GradientPaint(0, 0, color1, 0, height, color2);
        
        // we tell it to use this paint
        g2d.setPaint(gp);
        // and to paint it all
        g2d.fillRect(0, 0, width, height);
    }
}
