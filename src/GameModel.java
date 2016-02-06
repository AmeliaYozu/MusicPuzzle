import java.util.*;


public class GameModel {
	final int MUSIC = 1;
	final int VALENCIA = 2;
	ArrayList<Integer> que1 = new ArrayList<Integer>();
	ArrayList<Integer> que2 = new ArrayList<Integer>();
	//gameSet 是随机的que
	ArrayList<Integer> gameSet1 = new ArrayList<Integer>();
	ArrayList<Integer> gameSet2 = new ArrayList<Integer>();
	ArrayList<String> showList = new ArrayList<String>();
	ArrayList<String> realList = new ArrayList<String>();
	ArrayList<String> soundList = new ArrayList<String>();
	ArrayList<Integer> realListNum = new ArrayList<Integer>();
	int size = -1;
	int type = 1;
	String backPath = "";

    private Random random = new Random();   
    //数组大小   
    private static final int SIZE = 10;   
    //要重排序的数组   
    private int[] positions = new int[SIZE];   
    
	public GameModel(int n){
		gameSet1.clear();
		gameSet2.clear();
		showList.clear();
		realList.clear();
		soundList.clear();
		realListNum.clear();
		
		this.setSize(n);
		this.setup();
		this.initRealList();
		this.initShowList(n); 
		this.initSoundList();
	}
	
	public void setBackPath(int size){
		if(size == 10){
			backPath = "./bin/5x4/back.jpg";
		}
		if(size == 6){
			backPath = "./bin/4x3/back.jpg";
		}
		if(size == 3){
			backPath = "./bin/3x2/back.jpg";
		}
	}
	public void setSize(int n){
		size = (n/2);
	}
	public void setType(int m){
		type = m;
	}
	public void setup(){
		for(int i = 0; i<size;i++){
			que1.add(i);
			que2.add(i);
		}
		for(int i = 0;i<size;i++){
			int n = (new Random()).nextInt(que1.size());
			int m = (new Random()).nextInt(que2.size());
			gameSet1.add(que1.get(n));
			que1.remove(n);
			gameSet2.add(que2.get(m));
			que2.remove(m);
		}
		System.out.println(gameSet1.toString());
		System.out.println(gameSet2.toString());
	}
	public void initShowList(int n){
		//setup the init images as all cards are the backs
		this.setBackPath(size);
		for(int i = 0; i<(2*size);i++){
			showList.add(backPath);
		}
		if(showList.size()!=2*size){
			System.out.println("ERROR: showList size error occurs!");
		}
	}
	public void initRealList(){
		//setup the init imageList behind the backs
		if(size == 10){
			for(int i = 0;i<gameSet1.size();i++){
				realListNum.add(gameSet1.get(i));
			}
			for(int i = 0;i<gameSet2.size();i++){
				realListNum.add(gameSet2.get(i));
			}
			this.changePosition();
			for(int i = 0; i<size*2;i++){
				realList.add("./bin/5x4/music/"+realListNum.get(i)+".jpg");
			}			
		}
		if(size == 6){
			for(int i = 0;i<gameSet1.size();i++){
				realListNum.add(gameSet1.get(i));
			}
			for(int i = 0;i<gameSet2.size();i++){
				realListNum.add(gameSet2.get(i));
			}
			this.changePosition();
			for(int i = 0; i<size*2;i++){
				realList.add("./bin/4x3/music/"+realListNum.get(i)+".jpg");
			}	
		}
		if(size == 3){
			for(int i = 0;i<gameSet1.size();i++){
				realListNum.add(gameSet1.get(i));
			}
			for(int i = 0;i<gameSet2.size();i++){
				realListNum.add(gameSet2.get(i));
			}
			this.changePosition();
			for(int i = 0; i<size*2;i++){
				realList.add("./bin/3x2/music/"+realListNum.get(i)+".jpg");
			}	
		}
		if(realList.size()!=size*2){
			System.out.println("ERROR: realList size error occurs!"+realList.size());
		}
	}
	
	public void initSoundList(){
		//setup the init imageList behind the backs
		if(size == 10){
			for(int i = 0; i<size*2;i++){
			soundList.add("./bin/5x4/music/"+realListNum.get(i)+".wav");
			}
		}
		if(size == 6){
			for(int i = 0; i<size*2;i++){
				soundList.add("./bin/4x3/music/"+realListNum.get(i)+".wav");
			}
		}
		if(size == 3){
			for(int i = 0; i<size*2;i++){
				soundList.add("./bin/3x2/music/"+realListNum.get(i)+".wav");
			}
		}
		if(soundList.size()!=size*2){
			System.out.println("ERROR: soundList size error occurs!"+soundList.size());
		}
	}
	public void flipOnShowList(int imageId, int type){
		showList.remove(imageId);
		if(type == 2){
			showList.add(imageId, "./bin/0.gif");
		}
		else{
		showList.add(imageId, realList.get(imageId));
		}
	}
	public void flipOffShowList(int imageId){
		showList.remove(imageId);
		if(size == 10){
		showList.add(imageId, "./bin/5x4/back.jpg");
		}
		if(size == 6){
			showList.add(imageId, "./bin/4x3/back.jpg");
		}
		if(size == 3){
			showList.add(imageId, "./bin/3x2/back.jpg");
		}
		else{
			System.out.println("Error in flipOffShowList METHOD!");
		}
	}
	
	   public void changePosition() {   
	        for(int index=size*2-1; index>=0; index--) {   
	            //从0到index处之间随机取一个值，跟index处的元素交换   
	            exchange(random.nextInt(index+1), index);   
	        }   
	    }   
	       
	    //交换位置   
	    private void exchange(int p1, int p2) {   
	        int temp = realListNum.get(p1);   
	        realListNum.set(p1, realListNum.get(p2));  
	        realListNum.set(p2,temp);  //更好位置 
	    }    
	    
	public int getListSize(){
		return showList.size();
	}
	public ArrayList<String> getShowList(){
		return showList;
	}
	public ArrayList<String> getRealList(){
		return realList;
	}
	public ArrayList<String> getSoundList(){
		return soundList;
	}
	public ArrayList<Integer> getRealListNum(){
		return realListNum;
	}
}
