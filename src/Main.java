import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.leapmotion.leap.Controller;


public class Main extends JFrame{
//	JFrame frame = new JFrame("Image Matching Game -Leap Motion");
//	JPanel panel = new JPanel();
//	JButton button54 = new JButton("5X4");
//	JButton button43 = new JButton("4X3");
 //   static Controller controller;
 //   static TestView listener;
    
//	public Main(){
//		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
////		frame.setSize(700,700);
//		frame.setVisible(true);	
//		panel.add(button54);
//		panel.add(button43);
//		frame.getContentPane().add(panel);
//		panel.setLayout(new GridLayout(1,2,10,10));
//		button54.addActionListener(this);
//	}
	public static void main(String[] args){
		// TODO Auto-generated method stub
 //       ChoiceFrame cframe = new ChoiceFrame();
        // Have the sample listener receive events from the controller
//		Main main = new Main();
		Controller controller = new Controller();
	    TestView listener = new TestView();
		controller.addListener(listener);
		ChoiceFrame choiceFrame = new ChoiceFrame(listener);

        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Remove the sample listener when done
        controller.removeListener(listener);

        // Keep this process running until Enter is pressed
        System.out.println("Press Enter to quit...");
        
	}



//	@Override
//	public void actionPerformed(ActionEvent event) {
		// TODO Auto-generated method stub
//		if(event.getSource() == button54){
//			//set Size 20 and jump to Type selection panel
//			listener.setSize(20);
//			listener.initialize();
//		}
//		if(event.getSource() == button43){
//			//set Size 12 and jump to Type selection panel
//		}
//	}
}
