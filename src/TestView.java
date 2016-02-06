import java.applet.Applet;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

import java.applet.AudioClip;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import com.leapmotion.leap.*;
import com.leapmotion.leap.Frame;
import com.leapmotion.leap.Vector;

import javax.swing.*;

public class TestView extends Listener{
	//this attribute is used to identify the quantity of squares
	int size = -1;
	int type = -1;
	
	JFrame GUIframe = new JFrame();
	DrawPanel panel = new DrawPanel();
	JPanel rootPanel = new JPanel();
	ArrayList<ImageIcon> images = new ArrayList<ImageIcon>(); 
	
	Calendar now = Calendar.getInstance();
	int year = now.get(Calendar.YEAR);
	int month = (now.get(Calendar.MONTH) + 1);
	int day = now.get(Calendar.DAY_OF_MONTH);
	
	int matchCounter = 0;
	//Now, the gametable is varying based on users' choice
	GameModel g;
	String sname =" ";//student's name
	boolean evenCheck = true;
	int velocity = 10;//后续可改，提高游戏难度
	int[] matchBox = {-5,-1};// the two tapped imageId
	int imageId = -1;//the id of the image which is tapped, showList上，即gridLayout上的被点击的图片编号
	
	int corCount = 0;
	long endMili;
	long startMili;
	Vector normalizedPoint;
	Vector indexPosition;
	Vector normalizedIndex = new Vector(0,0,0);
	
	ArrayList<String> imageList = new ArrayList<String>();
	SourceDataLine sourceDataLine = null;
	
	public void setSize(int n){
		size = n;
	}
	public void setType(int m){
		this.type = m;
	}
	
	public void initialize(){
		matchBox[0] =-5;
		matchBox[1] =-1;
		matchCounter = 0;	
		//setup the view
//		this.setSize(20);//for test正常应该由其他类set
//		this.setType(1);//for test正常应该由其他类set
		rootPanel.removeAll();
		GUIframe.getContentPane().removeAll();
		startMili=System.currentTimeMillis();
		g = new GameModel(size);
		GUIframe.setSize(700,700);
		GUIframe.setVisible(true);	
		rootPanel.setBackground(Color.white);
		GUIframe.getContentPane().add(rootPanel);
		GUIframe.setGlassPane(panel);
		GUIframe.getGlassPane().setVisible(true);
		if(size == 20){
			rootPanel.setLayout(new GridLayout(4,5,10,10));
		}
		else if(size ==12){
			rootPanel.setLayout(new GridLayout(3,4,10,10));
		}
		else if(size ==6){
			rootPanel.setLayout(new GridLayout(2,3,10,10));
		}
		else{System.out.println("GAME TABLE SIZE　ERROR!");}
		for(int i=0; i<g.getListSize(); i++){
			String imagePath = g.getShowList().get(i);
			JLabel label = new JLabel(new ImageIcon(imagePath));
			rootPanel.add(label);			
		}
		panel.setOpaque(false);
	}

	public void onInit(Controller controller) {
        System.out.println("Initialized");
    }
	
	public void onConnect(Controller controller) {
        System.out.println("Connected");
        //Gesture configuration
        controller.enableGesture(Gesture.Type.TYPE_SCREEN_TAP);
        controller.enableGesture(Gesture.Type.TYPE_KEY_TAP);
        controller.config().setFloat("Gesture.KeyTap.MinDownVelocity", 40.0f);
        controller.config().setFloat("Gesture.KeyTap.HistorySeconds", .10f);
        controller.config().setFloat("Gesture.KeyTap.MinDistance", 1.2f);
        controller.config().setFloat("Gesture.ScreenTap.MinDownVelocity", 25.0f);
        controller.config().setFloat("Gesture.ScreenTap.HistorySeconds", .5f);
        controller.config().setFloat("Gesture.ScreenTap.MinDistance", 1.0f);
        controller.config().save();
    }
	
	public void onFrame(Controller controller) {
//  set interactionBox
        Frame frame = controller.frame();
        InteractionBox interactionBox = frame.interactionBox();
        float z = interactionBox.depth();
        float x = interactionBox.width();
        float y = interactionBox.height();
        
        Vector point = new Vector(x,y,z);
        normalizedPoint = interactionBox.normalizePoint(point);
        
        FingerList indexFingerList = frame.fingers().fingerType(Finger.Type.TYPE_INDEX);
        Finger indexFinger = indexFingerList.get(0);
        indexPosition = indexFinger.stabilizedTipPosition();
        normalizedIndex = interactionBox.normalizePoint(indexPosition);

		panel.setXY(normalizedIndex.getX(), normalizedIndex.getY());
		panel.repaint();
		
		//gesture recognition and proceeding
		 GestureList gestures = frame.gestures();
	        for (int i = 0; i < gestures.count(); i++) {
	            Gesture gesture = gestures.get(i);

	            switch (gesture.type()) {
	                case TYPE_SCREEN_TAP:
	                	System.out.println(evenCheck);
                		mainProcess(g.getListSize());
	                	
	                    break;
	                case TYPE_KEY_TAP:
	                		System.out.println(evenCheck);
	                		mainProcess(g.getListSize());	   
	                    break;
	                default:
	                    System.out.println("Unknown gesture type.");
	                    break;
	            }
	        }
	        
	        if(evenCheck){
				try {
					if(!isBoxMatch()){
						for(int i=0;i<velocity;i++){  
							   for(int j = 0;j<velocity*1000;j++){  
							      System.out.println("running outer");  
							   }  
							}  
						flipOffView(matchBox[0]);
						flipOffView(matchBox[1]);
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				matchBox[0] = -5;
				matchBox[1] = -1;
			}
		}

	public void flipOnView(int imageId){
		rootPanel.setBackground(Color.white);
		System.out.println(this.type);
		AudioInputStream audioInputStream = null;
		GUIframe.getContentPane().remove(rootPanel);  
		rootPanel = new JPanel();  	
		rootPanel.setBackground(Color.white);
		g.flipOnShowList(imageId,this.type);
		for(int i=0; i<g.getListSize(); i++){
			String imagePath = g.getShowList().get(i);
			JLabel label = new JLabel(new ImageIcon(imagePath));
			rootPanel.add(label);			
		}
		GUIframe.getContentPane().add(rootPanel);
		if(this.size == 20){rootPanel.setLayout(new GridLayout(4,5,10,10));}
		if(this.size == 12){rootPanel.setLayout(new GridLayout(3,4,10,10));}
		if(this.size == 6){rootPanel.setLayout(new GridLayout(2,3,10,10));}
		GUIframe.setVisible(true);
		
		if(type == 3||type == 2){
			try {
				audioInputStream = AudioSystem.getAudioInputStream(new File(g.getSoundList().get(imageId)));
			} catch (UnsupportedAudioFileException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			AudioFormat audioFormat = audioInputStream.getFormat();
			 // 设置数据输入
			  DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class,
			    audioFormat, AudioSystem.NOT_SPECIFIED);
			  SourceDataLine sourceDataLine = null;
			try {
				sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
			} catch (LineUnavailableException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			  try {
				sourceDataLine.open(audioFormat);
			} catch (LineUnavailableException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			  sourceDataLine.start();

			  /*
			   * 从输入流中读取数据发送到混音器
			   */
			  int count;
			  byte tempBuffer[] = new byte[1024];
			  try {
				while ((count = audioInputStream.read(tempBuffer, 0, tempBuffer.length)) != -1) {
				   if (count > 0) {
				    sourceDataLine.write(tempBuffer, 0, count);
				   }
				  }
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			  // 清空数据缓冲,并关闭输入
			  sourceDataLine.drain();
			  sourceDataLine.close();
		}
	}
	public void flipOffView(int imageId){
		rootPanel.setBackground(Color.white);
		GUIframe.getContentPane().remove(rootPanel);  
		rootPanel = new JPanel();  		
		rootPanel.setForeground(Color.white);
		g.flipOffShowList(imageId);
		for(int i=0; i<g.getListSize(); i++){
			String imagePath = g.getShowList().get(i);
			JLabel label = new JLabel(new ImageIcon(imagePath));
			rootPanel.add(label);			
		}
		GUIframe.getContentPane().add(rootPanel);
		rootPanel.setBackground(Color.white);
		if(this.size == 20){rootPanel.setLayout(new GridLayout(4,5,10,10));}
		if(this.size == 12){rootPanel.setLayout(new GridLayout(3,4,10,10));}
		if(this.size == 6){rootPanel.setLayout(new GridLayout(2,3,10,10));}
		GUIframe.setVisible(true);
		for(int i=0;i<velocity;i++){  
			   for(int j = 0;j<velocity;j++){  
				   System.out.println("running inner");  
			   }  
			}
	}
	
	public void putInBox(int imageId){
		if(!evenCheck){
			matchBox[0]=imageId;
		}
		else if(evenCheck){
			matchBox[1]=imageId;
		}
		System.out.println("matchBox:"+ matchBox[0]+", "+matchBox[1]);
	}
	public boolean isBoxMatch() throws IOException{
		if(g.getRealListNum().get(matchBox[0])==g.getRealListNum().get(matchBox[1])){
			matchCounter++;
			System.out.println("Matched: "+matchCounter);
			if((size == 20&&matchCounter==10)||(size == 12&&matchCounter==6)||(size == 6&&matchCounter==3)){
				endMili=System.currentTimeMillis();
				long duration = (endMili - startMili)/1000;//total time used for this round
				//写入记录
				if(!this.sname.equals("")){
					System.out.println("我有名字！"+this.sname);
				String filename = "./bin/Records/"+sname+".dat";
				FileWriter fw = new FileWriter(filename);   
				System.out.println(day+"."+month+"."+year);//test 日期输出
				String date = day+"."+month+"."+year;
				String diff = null;
				String typ = null;
				switch(size){
				case 20:
					diff = "Hard";					 
					break;
				
				case 12:
					diff = "Normal";
					break;
				case 6:
					diff = "Easy";
					break;
				}
				switch(type){
				case 1:
					typ = "Image";					 
					break;
				
				case 2:
					typ = "Sound";
					break;
				case 3:
					typ = "Image+Sound";
					break;
				}
				String durStr = duration+"s";
		        fw.write(date+"\t",0,date.length()+1); 
		        fw.write(durStr+"\t",0,durStr.length()+1);    
		        fw.write(diff+"\t",0,diff.length()+1);    
		        fw.write(typ+"\n",0,typ.length()+1);    
		        fw.flush();    
		          
		        OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(filename));    
		        osw.write(date+"\t",0,date.length()+1); 
		        osw.write(durStr+"\t",0,durStr.length()+1);    
		        osw.write(diff+"\t",0,diff.length()+1);    
		        osw.write(typ+"\n",0,typ.length()+1);  
		        osw.flush();    
		          
		        PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(filename)),true);    
		        pw.print(date+"\t");    
		        pw.print(durStr+"\t"); 
		        pw.print(diff+"\t"); 
		        pw.print(typ+"\t"); 
		        
		        fw.close();    
		        osw.close();    
		        pw.close();    
				}
				
			//You win 界面：
				GUIframe.getContentPane().remove(rootPanel);  
				GUIframe.getContentPane().remove(panel);
				rootPanel = new JPanel(); 
				JLabel label1 = new JLabel(new ImageIcon("./bin/bj.gif"));
				JLabel winLabel = new JLabel("YOU WIN! ",JLabel.CENTER);
				JLabel durLabel = new JLabel("Total time: "+duration+"s",JLabel.CENTER);
				durLabel.setFont(new java.awt.Font("Dialog",  1, 32)); 
				winLabel.setFont(new java.awt.Font("Dialog",  1,  48)); 
				winLabel.setForeground(Color.red);
				rootPanel.add(label1);
				rootPanel.add(winLabel);
				rootPanel.add(durLabel);
				rootPanel.setLayout(new GridLayout(3,1,10,10));	
				rootPanel.setBackground(Color.white);
				GUIframe.getContentPane().add(rootPanel);
				GUIframe.setVisible(true);
			//掌声：	
				AudioInputStream audioInputStream = null;
				try {
					audioInputStream = AudioSystem.getAudioInputStream(new File("./bin/win.wav"));
				} catch (UnsupportedAudioFileException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				AudioFormat audioFormat = audioInputStream.getFormat();
				 // 设置数据输入
				  DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class,
				    audioFormat, AudioSystem.NOT_SPECIFIED);
				  
				try {
					sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
				} catch (LineUnavailableException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				  try {
					sourceDataLine.open(audioFormat);
				} catch (LineUnavailableException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				  sourceDataLine.start();
				  System.out.println("keyiting");
				  long st = System.currentTimeMillis();
				  
				  

				  /*
				   * 从输入流中读取数据发送到混音器
				   */
				  int count;
				  byte tempBuffer[] = new byte[1024];
				  try {
					while (((count = audioInputStream.read(tempBuffer, 0, tempBuffer.length)) != -1)&&!(System.currentTimeMillis()-st>=7000&&System.currentTimeMillis()-st<=8000)) {
					   if (count > 0) {
					    sourceDataLine.write(tempBuffer, 0, count);
					   }
					  }
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				  // 清空数据缓冲,并关闭输入
				  sourceDataLine.drain();
				  sourceDataLine.close();

			}
			return true;
		}
		else{
			return false;
		}
	}
	public void mainProcess(int size){
//		if(corCount == size/2){System.out.println("YOU WIN!");}
		if(size == 20){
			if((normalizedIndex.getX()*700)<=132&&(normalizedIndex.getX()*700>=0)&&(700-normalizedIndex.getY()*700)>=0&&(700-normalizedIndex.getY()*700)<=167){
				imageId = 0;
				if(g.getShowList().get(imageId)=="./bin/5x4/back.jpg")
				{	
					evenCheck = !evenCheck;
					flipOnView(imageId);
					putInBox(imageId);
				}
        	}
        	if((normalizedIndex.getX()*700)<=274&&(normalizedIndex.getX()*700>=142)&&(700-normalizedIndex.getY()*700)>=0&&(700-normalizedIndex.getY()*700)<=167){
        		imageId = 1;
        		if(g.getShowList().get(imageId)=="./bin/5x4/back.jpg")
				{	
					evenCheck = !evenCheck;
        			flipOnView(imageId);
        			putInBox(imageId);
				}
        	}
        	if((normalizedIndex.getX()*700)<=416&&(normalizedIndex.getX()*700>=284)&&(700-normalizedIndex.getY()*700)>=0&&(700-normalizedIndex.getY()*700)<=167){
        		imageId = 2;
        		if(g.getShowList().get(imageId)=="./bin/5x4/back.jpg")
				{	
					evenCheck = !evenCheck;
        			flipOnView(imageId);
        			putInBox(imageId);
				}
        	}
        	if((normalizedIndex.getX()*700)<=558&&(normalizedIndex.getX()*700>=426)&&(700-normalizedIndex.getY()*700)>=0&&(700-normalizedIndex.getY()*700)<=167){
        		imageId = 3;
        		if(g.getShowList().get(imageId)=="./bin/5x4/back.jpg")
				{	
					evenCheck = !evenCheck;
        			flipOnView(imageId);
        			putInBox(imageId);
				}
			}
        	if((normalizedIndex.getX()*700)<=700&&(normalizedIndex.getX()*700>=568)&&(700-normalizedIndex.getY()*700)>=0&&(700-normalizedIndex.getY()*700)<=167){
        		imageId = 4;
        		if(g.getShowList().get(imageId)=="./bin/5x4/back.jpg")
				{	
					evenCheck = !evenCheck;
					flipOnView(imageId);
					putInBox(imageId);	
				}
        	}
        	if((normalizedIndex.getX()*700)<=132&&(normalizedIndex.getX()*700>=0)&&(700-normalizedIndex.getY()*700)>=178&&(700-normalizedIndex.getY()*700)<=345){
        		imageId = 5;
        		if(g.getShowList().get(imageId)=="./bin/5x4/back.jpg")
				{	
					evenCheck = !evenCheck;
					flipOnView(imageId);
					putInBox(imageId);
				}				
        	}
        	if((normalizedIndex.getX()*700)<=274&&(normalizedIndex.getX()*700>=142)&&(700-normalizedIndex.getY()*700)>=178&&(700-normalizedIndex.getY()*700)<=345){
        		imageId = 6;
        		if(g.getShowList().get(imageId)=="./bin/5x4/back.jpg")
				{	
					evenCheck = !evenCheck;
					flipOnView(imageId);
					putInBox(imageId);
				}			
        	}
        	if((normalizedIndex.getX()*700)<=416&&(normalizedIndex.getX()*700>=284)&&(700-normalizedIndex.getY()*700)>=178&&(700-normalizedIndex.getY()*700)<=345){
        		imageId = 7;
        		if(g.getShowList().get(imageId)=="./bin/5x4/back.jpg")
				{	
					evenCheck = !evenCheck;
					flipOnView(imageId);
					putInBox(imageId);
				}			
        	}
        	if((normalizedIndex.getX()*700)<=558&&(normalizedIndex.getX()*700>=426)&&(700-normalizedIndex.getY()*700)>=178&&(700-normalizedIndex.getY()*700)<=345){
        		imageId = 8;
        		if(g.getShowList().get(imageId)=="./bin/5x4/back.jpg")
				{	
					evenCheck = !evenCheck;
					flipOnView(imageId);
					putInBox(imageId);
				}	
				
        	}
        	if((normalizedIndex.getX()*700)<=700&&(normalizedIndex.getX()*700>=568)&&(700-normalizedIndex.getY()*700)>=178&&(700-normalizedIndex.getY()*700)<=345){
        		imageId = 9;
        		if(g.getShowList().get(imageId)=="./bin/5x4/back.jpg")
				{	
					evenCheck = !evenCheck;
					flipOnView(imageId);
					putInBox(imageId);
				}	
				
        	}
        	if((normalizedIndex.getX()*700)<=132&&(normalizedIndex.getX()*700>=0)&&(700-normalizedIndex.getY()*700)>=355&&(700-normalizedIndex.getY()*700)<=522){
        		imageId = 10;
        		if(g.getShowList().get(imageId)=="./bin/5x4/back.jpg")
				{	
					evenCheck = !evenCheck;
					flipOnView(imageId);
					putInBox(imageId);
				}	
				
        	}
        	if((normalizedIndex.getX()*700)<=274&&(normalizedIndex.getX()*700>=142)&&(700-normalizedIndex.getY()*700)>=355&&(700-normalizedIndex.getY()*700)<=522){
        		imageId = 11;
        		if(g.getShowList().get(imageId)=="./bin/5x4/back.jpg")
				{	
					evenCheck = !evenCheck;
					flipOnView(imageId);
					putInBox(imageId);
				}	
				
        	}
        	if((normalizedIndex.getX()*700)<=416&&(normalizedIndex.getX()*700>=284)&&(700-normalizedIndex.getY()*700)>=355&&(700-normalizedIndex.getY()*700)<=522){
        		imageId = 12;
        		if(g.getShowList().get(imageId)=="./bin/5x4/back.jpg")
				{	
					evenCheck = !evenCheck;
					flipOnView(imageId);
					putInBox(imageId);
				}	
					
        	}
        	if((normalizedIndex.getX()*700)<=558&&(normalizedIndex.getX()*700>=426)&&(700-normalizedIndex.getY()*700)>=355&&(700-normalizedIndex.getY()*700)<=522){
        		imageId = 13;
        		if(g.getShowList().get(imageId)=="./bin/5x4/back.jpg")
				{	
					evenCheck = !evenCheck;
					flipOnView(imageId);
					putInBox(imageId);
				}	
					
        	}
        	if((normalizedIndex.getX()*700)<=700&&(normalizedIndex.getX()*700>=568)&&(700-normalizedIndex.getY()*700)>=355&&(700-normalizedIndex.getY()*700)<=522){
        		imageId = 14;
        		if(g.getShowList().get(imageId)=="./bin/5x4/back.jpg")
				{	
					evenCheck = !evenCheck;
					flipOnView(imageId);
					putInBox(imageId);
				}	
					
        	}
        	if((normalizedIndex.getX()*700)<=132&&(normalizedIndex.getX()*700>=0)&&(700-normalizedIndex.getY()*700)>=533&&(700-normalizedIndex.getY()*700)<=700){
        		imageId = 15;
        		if(g.getShowList().get(imageId)=="./bin/5x4/back.jpg")
				{	
					evenCheck = !evenCheck;
					flipOnView(imageId);
					putInBox(imageId);
				}	
				
        	}
        	if((normalizedIndex.getX()*700)<=274&&(normalizedIndex.getX()*700>=142)&&(700-normalizedIndex.getY()*700)>=533&&(700-normalizedIndex.getY()*700)<=700){
        		imageId = 16;
        		if(g.getShowList().get(imageId)=="./bin/5x4/back.jpg")
				{	
					evenCheck = !evenCheck;
					flipOnView(imageId);
					putInBox(imageId);
				}	
					
        	}
        	if((normalizedIndex.getX()*700)<=416&&(normalizedIndex.getX()*700>=284)&&(700-normalizedIndex.getY()*700)>=533&&(700-normalizedIndex.getY()*700)<=700){
        		imageId = 17;
        		if(g.getShowList().get(imageId)=="./bin/5x4/back.jpg")
				{	
					evenCheck = !evenCheck;
					flipOnView(imageId);
					putInBox(imageId);
				}	
				
        	}
        	if((normalizedIndex.getX()*700)<=558&&(normalizedIndex.getX()*700>=426)&&(700-normalizedIndex.getY()*700)>=533&&(700-normalizedIndex.getY()*700)<=700){
        		imageId = 18;
        		if(g.getShowList().get(imageId)=="./bin/5x4/back.jpg")
				{	
					evenCheck = !evenCheck;
					flipOnView(imageId);
					putInBox(imageId);
				}	
				
        	}
        	if((normalizedIndex.getX()*700)<=700&&(normalizedIndex.getX()*700>=568)&&(700-normalizedIndex.getY()*700)>=533&&(700-normalizedIndex.getY()*700)<=700){
        		imageId = 19;
        		if(g.getShowList().get(imageId)=="./bin/5x4/back.jpg")
				{	
					evenCheck = !evenCheck;
					flipOnView(imageId);
					putInBox(imageId);
				}				
        	}
		}
		if(size == 12){
			//continue to work
			if((normalizedIndex.getX()*700)>=0&&(normalizedIndex.getX()*700<=167)&&(700-normalizedIndex.getY()*700)>=0&&(700-normalizedIndex.getY()*700)<=226){
				imageId = 0;
				if(g.getShowList().get(imageId)=="./bin/4x3/back.jpg")
				{	
					evenCheck = !evenCheck;
					flipOnView(imageId);
					putInBox(imageId);
				}
        	}
        	if((normalizedIndex.getX()*700)>=178&&(normalizedIndex.getX()*700<=345)&&(700-normalizedIndex.getY()*700)>=0&&(700-normalizedIndex.getY()*700)<=226){
        		imageId = 1;
        		if(g.getShowList().get(imageId)=="./bin/4x3/back.jpg")
				{	
					evenCheck = !evenCheck;
        			flipOnView(imageId);
        			putInBox(imageId);
				}
        	}
        	if((normalizedIndex.getX()*700)>=356&&(normalizedIndex.getX()*700<=523)&&(700-normalizedIndex.getY()*700)>=0&&(700-normalizedIndex.getY()*700)<=226){
        		imageId = 2;
        		if(g.getShowList().get(imageId)=="./bin/4x3/back.jpg")
				{	
					evenCheck = !evenCheck;
        			flipOnView(imageId);
        			putInBox(imageId);
				}
        	}
        	if((normalizedIndex.getX()*700)>=534&&(normalizedIndex.getX()*700<=700)&&(700-normalizedIndex.getY()*700)>=0&&(700-normalizedIndex.getY()*700)<=226){
        		imageId = 3;
        		if(g.getShowList().get(imageId)=="./bin/4x3/back.jpg")
				{	
					evenCheck = !evenCheck;
        			flipOnView(imageId);
        			putInBox(imageId);
				}
			}
        	if((normalizedIndex.getX()*700)>=0&&(normalizedIndex.getX()*700<=167)&&(700-normalizedIndex.getY()*700)>=237&&(700-normalizedIndex.getY()*700)<=463){
        		imageId = 4;
        		if(g.getShowList().get(imageId)=="./bin/4x3/back.jpg")
				{	
					evenCheck = !evenCheck;
					flipOnView(imageId);
					putInBox(imageId);	
				}
        	}
        	if((normalizedIndex.getX()*700)>=178&&(normalizedIndex.getX()*700<=345)&&(700-normalizedIndex.getY()*700)>=237&&(700-normalizedIndex.getY()*700)<=463){
        		imageId = 5;
        		if(g.getShowList().get(imageId)=="./bin/4x3/back.jpg")
				{	
					evenCheck = !evenCheck;
					flipOnView(imageId);
					putInBox(imageId);
				}				
        	}
        	if((normalizedIndex.getX()*700)>=356&&(normalizedIndex.getX()*700<=523)&&(700-normalizedIndex.getY()*700)>=237&&(700-normalizedIndex.getY()*700)<=463){
        		imageId = 6;
        		if(g.getShowList().get(imageId)=="./bin/4x3/back.jpg")
				{	
					evenCheck = !evenCheck;
					flipOnView(imageId);
					putInBox(imageId);
				}			
        	}
        	if((normalizedIndex.getX()*700)>=534&&(normalizedIndex.getX()*700<=700)&&(700-normalizedIndex.getY()*700)>=237&&(700-normalizedIndex.getY()*700)<=463){
        		imageId = 7;
        		if(g.getShowList().get(imageId)=="./bin/4x3/back.jpg")
				{	
					evenCheck = !evenCheck;
					flipOnView(imageId);
					putInBox(imageId);
				}			
        	}
        	if((normalizedIndex.getX()*700)>=0&&(normalizedIndex.getX()*700<=167)&&(700-normalizedIndex.getY()*700)>=474&&(700-normalizedIndex.getY()*700)<=700){
        		imageId = 8;
        		if(g.getShowList().get(imageId)=="./bin/4x3/back.jpg")
				{	
					evenCheck = !evenCheck;
					flipOnView(imageId);
					putInBox(imageId);
				}	
				
        	}
        	if((normalizedIndex.getX()*700)>=178&&(normalizedIndex.getX()*700<=345)&&(700-normalizedIndex.getY()*700)>=474&&(700-normalizedIndex.getY()*700)<=700){
        		imageId = 9;
        		if(g.getShowList().get(imageId)=="./bin/4x3/back.jpg")
				{	
					evenCheck = !evenCheck;
					flipOnView(imageId);
					putInBox(imageId);
				}	
				
        	}
        	if((normalizedIndex.getX()*700)>=356&&(normalizedIndex.getX()*700<=523)&&(700-normalizedIndex.getY()*700)>=474&&(700-normalizedIndex.getY()*700)<=700){
        		imageId = 10;
        		if(g.getShowList().get(imageId)=="./bin/4x3/back.jpg")
				{	
					evenCheck = !evenCheck;
					flipOnView(imageId);
					putInBox(imageId);
				}	
				
        	}
        	if((normalizedIndex.getX()*700)>=534&&(normalizedIndex.getX()*700<=700)&&(700-normalizedIndex.getY()*700)>=474&&(700-normalizedIndex.getY()*700)<=700){
        		imageId = 11;
        		if(g.getShowList().get(imageId)=="./bin/4x3/back.jpg")
				{	
					evenCheck = !evenCheck;
					flipOnView(imageId);
					putInBox(imageId);
				}	
				
        	}
		}
		if(size == 6){
			//continue to work
			if((normalizedIndex.getX()*700)>=0&&(normalizedIndex.getX()*700<=226)&&(700-normalizedIndex.getY()*700)>=0&&(700-normalizedIndex.getY()*700)<=345){
				imageId = 0;
				if(g.getShowList().get(imageId)=="./bin/3x2/back.jpg")
				{	
					evenCheck = !evenCheck;
					flipOnView(imageId);
					putInBox(imageId);
				}
        	}
        	if((normalizedIndex.getX()*700)>=237&&(normalizedIndex.getX()*700<=463)&&(700-normalizedIndex.getY()*700)>=0&&(700-normalizedIndex.getY()*700)<=345){
        		imageId = 1;
        		if(g.getShowList().get(imageId)=="./bin/3x2/back.jpg")
				{	
					evenCheck = !evenCheck;
        			flipOnView(imageId);
        			putInBox(imageId);
				}
        	}
        	if((normalizedIndex.getX()*700)>=474&&(normalizedIndex.getX()*700<=700)&&(700-normalizedIndex.getY()*700)>=0&&(700-normalizedIndex.getY()*700)<=345){
        		imageId = 2;
        		if(g.getShowList().get(imageId)=="./bin/3x2/back.jpg")
				{	
					evenCheck = !evenCheck;
        			flipOnView(imageId);
        			putInBox(imageId);
				}
        	}
        	if((normalizedIndex.getX()*700)>=0&&(normalizedIndex.getX()*700<=226)&&(700-normalizedIndex.getY()*700)>=356&&(700-normalizedIndex.getY()*700)<=700){
        		imageId = 3;
        		if(g.getShowList().get(imageId)=="./bin/3x2/back.jpg")
				{	
					evenCheck = !evenCheck;
        			flipOnView(imageId);
        			putInBox(imageId);
				}
			}
        	if((normalizedIndex.getX()*700)>=237&&(normalizedIndex.getX()*700<=463)&&(700-normalizedIndex.getY()*700)>=356&&(700-normalizedIndex.getY()*700)<=700){
        		imageId = 4;
        		if(g.getShowList().get(imageId)=="./bin/3x2/back.jpg")
				{	
					evenCheck = !evenCheck;
					flipOnView(imageId);
					putInBox(imageId);	
				}
        	}
        	if((normalizedIndex.getX()*700)>=474&&(normalizedIndex.getX()*700<=700)&&(700-normalizedIndex.getY()*700)>=356&&(700-normalizedIndex.getY()*700)<=700){
        		imageId = 5;
        		if(g.getShowList().get(imageId)=="./bin/3x2/back.jpg")
				{	
					evenCheck = !evenCheck;
					flipOnView(imageId);
					putInBox(imageId);
				}				
        	}
		}
	}

}

