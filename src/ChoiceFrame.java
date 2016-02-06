import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import com.leapmotion.leap.Controller;
public class ChoiceFrame extends JFrame implements MouseListener{
	JFrame frame = new JFrame("Music Puzzle -Leap Motion");
	JPanel panel = new JPanel();
	JPanel panel1 = new JPanel();
	Choice chc1 = new Choice();
	Choice chc2 = new Choice();
	JTextField jt = new JTextField();
	JLabel label = new JLabel("If you want to record, please enter the student's name below, if no, leave it empty");
	//JLabel label1 = new JLabel(".");
	JButton startButton = new JButton("Start the Game!");
	JLabel backMain;
	JLabel backRecord;
	
	JPanel mainPanel = new JPanel();
	JButton recButton = new JButton("View Records");
	JButton gameButton = new JButton("Start a Game");
	JButton viewButton = new JButton("View this Record");
	JTextField nameField = new JTextField();
	TestView listener;
    
	String filePath;
	public ChoiceFrame(TestView t){
		this.setMainFrame(t);
	}
	
	public void setMainFrame(TestView t){
		mainPanel.removeAll();
		frame.getContentPane().removeAll();
		JPanel buttonPanel = new JPanel();
		buttonPanel.setBackground(Color.white);
		JLabel welLabel = new JLabel(new ImageIcon("./bin/main.jpg"));
		buttonPanel.add(recButton);
		buttonPanel.add(gameButton);
		recButton.setSize(10,20);
		gameButton.setSize(10,20);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(500,250);
		mainPanel.setBackground(Color.white);
		mainPanel.setLayout(new BorderLayout());
		mainPanel.add(welLabel,BorderLayout.CENTER);
		mainPanel.add(buttonPanel,BorderLayout.SOUTH);
		frame.getContentPane().add(mainPanel);
		frame.setVisible(true);	

		recButton.addMouseListener(this);
		gameButton.addMouseListener(this);
		this.setListener(t);
	}
	public void setGameFrame(){
		panel.removeAll();
		frame.getContentPane().removeAll();
		startButton.setSize(10,20);
		frame.getContentPane().remove(mainPanel);
		panel.setBackground(Color.white);
		panel1.setBackground(Color.white);
		backMain = new JLabel("Go Back");
		backMain.setText("<html><u>＜－Go Back</u></html>");
		backMain.setFont(new Font("Dialog",1,12));
		backMain.setForeground(Color.blue);
		backMain.addMouseListener(this);
		chc1.removeAll();
		chc2.removeAll();
		chc1.add("Please select Difficulty:");
		chc1.add("Hard");
		chc1.add("Normal");
		chc1.add("Easy");
		chc2.add("Please select Type:");
		chc2.add("Image Only");
		chc2.add("Sound Only");
		chc2.add("Image + Sound");
		panel.add(backMain);
		panel.add(chc1);
		panel.add(chc2);
		panel.add(label);
	//	panel.add(label1);
		panel.add(jt);
		panel1.add(startButton);
		panel.add(panel1);
		frame.getContentPane().add(panel);
		panel.setLayout(new GridLayout(6,1,10,10));
		frame.getContentPane().add(panel);
		frame.setVisible(true);

		startButton.addMouseListener(this);
		
	}
	public void setRecFrame(){
		frame.getContentPane().removeAll();
		frame.setSize(500,250);
		nameField.setText("");
		JPanel recPanel = new JPanel();
		JPanel labelPanel = new JPanel();
		JPanel textPanel = new JPanel();
		JPanel buttonPanel = new JPanel();
		backMain = new JLabel("Go Back");
		backMain.setText("<html><u>＜－Go Back</u></html>");
		backMain.setFont(new Font("Dialog",1,12));
		backMain.setForeground(Color.blue);
		backMain.addMouseListener(this);
		JLabel n2 = new JLabel();
		JLabel n3 = new JLabel();
		JLabel n4 = new JLabel();
		recPanel.setBackground(Color.white);
		labelPanel.setBackground(Color.white);
		buttonPanel.setBackground(Color.white);
		textPanel.setBackground(Color.white);
		buttonPanel.add(viewButton);
		viewButton.setSize(10,20);
		JLabel nameLabel = new JLabel("Enter a name:",JLabel.CENTER);
		
		labelPanel.add(backMain);
		labelPanel.add(n2);
		labelPanel.add(nameLabel);
		textPanel.add(n3);
		textPanel.add(nameField);
		textPanel.add(n4);
		labelPanel.add(textPanel);
		labelPanel.setLayout(new GridLayout(4,1));
		textPanel.setLayout(new GridLayout(1,3));
		
		recPanel.add(labelPanel);
		recPanel.add(buttonPanel);
		recPanel.setLayout(new GridLayout(2,1,10,10));
		viewButton.addMouseListener(this);
		frame.getContentPane().remove(mainPanel);
		frame.getContentPane().add(recPanel);
		frame.setVisible(true);
	}

	public void setListener(TestView t){
		listener = t;
	}
	
	public void showBankPanel(){
		
	}
	public String getName(){
		return jt.getText();
	}
	
	public void showRecord(){
		if(nameField.getText().equals("")){
			System.out.println("RIGHT!");
			nameField.setText("Please enter a name!");
			nameField.setForeground(Color.gray);
			System.out.println("Nothing entered in the view Record frame.");
		}else{ 
			if(!(new File("./bin/records/"+nameField.getText()+".dat").exists())){
			//
			nameField.setText("No record, try another.");
			nameField.setForeground(Color.gray);
		}
		else{
			this.geneTable();
		}
		}
	}
	
	public void geneTable(){
		Vector<String> entries = new Vector<String>();
		Vector<Vector<String>> tokenVec = new Vector<Vector<String>>();
		Vector<String> tempVec = new Vector<String>();
		int row = 0;
		int col = 0;
		String[] column = {"Date", "Time used", "Difficulty","Type"};
		
		String tempStr = null;
		try {
//            String encoding="GBK";
            filePath = "./bin/Records/"+nameField.getText()+".dat";
            File file=new File(filePath);
            if(file.isFile()&&file.exists()){ //判断文件是否存在
            	System.out.println("I exist!");
                InputStreamReader read = new InputStreamReader(
                new FileInputStream(file));//考虑到编码格式
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt = null;
                while((lineTxt = bufferedReader.readLine()) != null){
                    tempStr = lineTxt;
                    entries.add(tempStr);
                }
                read.close();
                //将文件中条目输入到二维Vector中
               
                for(int i = 0;i<entries.size();i++){
                	StringTokenizer st = new StringTokenizer(entries.get(i));
                	tempVec.clear();
                	while(st.hasMoreTokens()){
                		tempVec.add(st.nextToken());
                		 System.out.println(tempVec.toString());//test
                	}
                	tokenVec.add(tempVec);
                }
                row = tokenVec.size();
                System.out.println("ROW: "+row);//test
                col = tokenVec.get(0).size();
                String entry[][] = new String[row][col];
                System.out.println(tokenVec.size()+"          "+tokenVec.get(0).size());//test
                for(int i = 0;i<tokenVec.size();i++){
                	for(int j = 0;j<tokenVec.get(i).size();j++){
                		entry[i][j] = tokenVec.get(i).get(j);
                	}
                }       
                System.out.println(entry[0][0].toString());//test
                JTable table = new JTable(entry,column);
                table.add(new JScrollBar());
        		setTableFrame(table);
    }
            else{
        System.out.println("There is no record for this student, try another.");
            }
    } catch (Exception e) {
        System.out.println("读取文件内容出错");
        e.printStackTrace();
    }
 
}
	public void setTableFrame(JTable table){
	frame.setSize(400,600);
	frame.getContentPane().removeAll();
	backRecord = new JLabel("Go Back");
	backRecord.setText("<html><u>＜－Go Back</u></html>");
	backRecord.setFont(new Font("Dialog",1,12));
	backRecord.setForeground(Color.blue);
	backRecord.addMouseListener(this);
	JLabel nameLabel = new JLabel(nameField.getText()+"'s Record",JLabel.CENTER);
	nameLabel.setFont(new Font("Dialog", 1, 24));
	nameLabel.setForeground(Color.blue);
	JScrollPane scroll = new JScrollPane(table);
	scroll.setBackground(Color.white);
	JPanel lpanel = new JPanel();
	
	lpanel.setLayout(new BorderLayout());
	lpanel.add(backRecord,BorderLayout.WEST);
	lpanel.add(nameLabel,BorderLayout.CENTER);
	frame.add(lpanel,BorderLayout.NORTH);
	frame.add(scroll);
	frame.setVisible(true);
}
	@Override
	public void mouseClicked(MouseEvent event) {
		// TODO Auto-generated method stub
		if(jt.getText()!=""){
			listener.sname = this.getName();
			System.out.println(listener.sname);
		}
		if(event.getSource() == gameButton){
			this.setGameFrame();
		}
			if(event.getSource()==recButton){
				this.setRecFrame();
			}
			if(event.getSource()==viewButton){
				//读取文件，出列表
				this.showRecord();
			}
			if(event.getSource()==backMain){
				System.out.println("Clicked me");
				this.setMainFrame(listener);
			}
			if(event.getSource()==backRecord){
				this.setRecFrame();
			}
		if(event.getSource() == startButton){
			if(chc1.getSelectedItem() == "Hard"&&chc2.getSelectedItem() == "Image Only"){
				listener.setSize(20);
				listener.setType(1);
				listener.initialize();
			}
			else if(chc1.getSelectedItem() == "Normal"&&chc2.getSelectedItem() == "Image Only"){
				listener.setSize(12);
				listener.setType(1);
				listener.initialize();
			}
			else if(chc1.getSelectedItem() == "Easy"&&chc2.getSelectedItem() == "Image Only"){
				listener.setSize(6);
				listener.setType(1);
				listener.initialize();
			}
			else if(chc1.getSelectedItem() == "Hard"&&chc2.getSelectedItem() == "Sound Only"){
				listener.setSize(20);
				listener.setType(2);
				listener.initialize();
			}
			else if(chc1.getSelectedItem() == "Normal"&&chc2.getSelectedItem() == "Sound Only"){
				listener.setSize(12);
				listener.setType(2);
				listener.initialize();
			}
			else if(chc1.getSelectedItem() == "Easy"&&chc2.getSelectedItem() == "Sound Only"){
				listener.setSize(6);
				listener.setType(2);
				listener.initialize();
			}
			else if(chc1.getSelectedItem() == "Hard"&&chc2.getSelectedItem() == "Image + Sound"){
				listener.setSize(20);
				listener.setType(3);
				listener.initialize();
			}
			else if(chc1.getSelectedItem() == "Normal"&&chc2.getSelectedItem() == "Image + Sound"){
				listener.setSize(12);
				listener.setType(3);
				listener.initialize();
			}
			else if(chc1.getSelectedItem() == "Easy"&&chc2.getSelectedItem() == "Image + Sound"){
				listener.setSize(6);
				listener.setType(3);
				listener.initialize();
			}else{
				startButton.setText("Please choose type and difficulty");
			}
			//other options
		}
	}
	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
