
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;


public class sudoGUI {
	JFrame frame = new JFrame();
	Panel btnPanel;
	Panel numPanel;
	Panel txtPanel;
	JTextField[][] box = new JTextField[10][10];
	//弄十的原因是为了好记忆，0,0不用，只用1-9的list  
    private ArrayList<Integer>[][] availableNum=new ArrayList[10][10];  
    private int[][] correctNum=new int[10][10];  
    private Scanner scan=new Scanner(System.in);  
    private int countNum=0;  
    private BufferedReader br;
	Refresh fresh;
	JTextArea area = new JTextArea(4,50);
	
    class Refresh extends Thread{
    	boolean flag;
        public Refresh(){
        	flag = true;
        }
     
        @Override
        public void run() {
        	while(flag){
        		int[] nextInsert=new int[2];  
                int count=0;
                int times = 0;
                while(!judgeMatrixFull()){  
                    nextInsert=judgeIfOnlyOne();  
                    if(nextInsert[0]==-1){  
                        insertIfCan();  
                        count++;
        	        	System.out.println("count: " + count);
        	        	if(count>=10){
        	        		area.append("\nThe procedure gets stuck.");
        	        		flag = false;
        	        		break;
        	        	}
                        continue;
                    }  
                    int value=availableNum[nextInsert[0]][nextInsert[1]].get(0);  
                    insert(nextInsert[0],nextInsert[1],value);
                    times++;
                    System.out.println(times + ":" + judgeMatrixFull());
                    
//                   	 每次修改显示
                    //	TODO!!!刷新
//                    System.out.println(correctNum[1][1]);
            	
            	
    	        	for(int i=1;i<10;i++){
    	            	for(int j=1;j<10;j++){
    	            		if(correctNum[i][j]!=-1){
    	            			
    	            			box[i][j].setText("                   " + String.valueOf(correctNum[i][j]));
    	            			System.out.print(correctNum[i][j] + " ");
    	            		}
    	            		else{
    	            			String str = "";
    	            			for(int k=0;k<availableNum[i][j].size();k++){
    	            				str += availableNum[i][j].get(k) + " ";
    	            			}
    	            			int numlen = (40-(availableNum[i][j].size()*2-1))/2;
    	            			String empStr = "";
    	            			for (int i2=1;i2<numlen;i2++){
    	            				empStr+=" ";
    	            			}
    	            			box[i][j].setText(empStr+String.valueOf(str));
    	            			System.out.print(str + " ");
    	            		}
    	            	}
    	    			System.out.println();
    	            }
    	            try {
    					Thread.sleep(100);
    				} catch (InterruptedException e) {
    					// TODO Auto-generated catch block
    					e.printStackTrace();
    				}
    	        }

        		area.append("\nThe procedure succeeds.");
        		flag = false;
        	}
        	
        	
        }
    }
    
	//先输入已知位置的数字  
    public void inputNumKnown(){  
    	int row = 0;
    	int col;
    	int value;
        try {
        	
			br = new BufferedReader(new FileReader("num.txt"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
        String line;
        try {
        	int lineNum = 0;
        	//	读新的一行
			while ((line = br.readLine()) != null) {
				lineNum++;
				if(lineNum>9){break;}
				row++;
				String[] ss = line.split("");
				int a = 9;
				if (a>ss.length){
					a = ss.length;
				}
				for(int j=0;j<a;j++){
					if(!ss[j].equals("_")){
						col = j+1;
						value= Integer.parseInt(ss[j]);
//						System.out.println(ss[j]);
			            insert(row,col,value);  
			            delete(row,col,value);  
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
    }  
	
	private void generatebtnPanel() {
		area.append("Ready.");
		btnPanel = new Panel();
		btnPanel.setLayout(new FlowLayout());
		JButton load = new JButton("LOAD");
		JButton run = new JButton("RUN");
		JButton interrupt = new JButton("INTERRUPT");
		JButton clear = new JButton("CLEAR");
		JButton quit = new JButton("QUIT");
        
		FlowLayout fl=new FlowLayout();
		fl.setAlignment(FlowLayout.LEFT);
		btnPanel.setLayout(fl); 
		
		btnPanel.add(load);
		btnPanel.add(run);
		btnPanel.add(interrupt);
		btnPanel.add(clear);
		btnPanel.add(quit);
		
		//	监听 LOAD 按钮
		load.addActionListener(new ActionListener(){ 
	    	@Override
            public void actionPerformed(ActionEvent arg0) {
	    		area.append("\noading num.txt");
	    		fresh = new Refresh();
	    		fresh.start();
	    		fresh.suspend();
	            inputNumKnown();
	            for(int i=1;i<10;i++){
	            	for(int j=1;j<10;j++){
	            		if(correctNum[i][j]!=-1){
	            			box[i][j].setText("                   " + String.valueOf(correctNum[i][j]));
	            		}
//	            		System.out.print(availableNum[i][j] + " ");
	            	}
//	            	System.out.println();
	            }
	            
	    	}
	    });
		

		//	监听 RUN 按钮
		run.addActionListener(new ActionListener(){ 
	    	@Override
            public void actionPerformed(ActionEvent arg0) {
	    		area.append("\nWorking...");
	    		fresh.resume();
	    	}
	    });
		


		//	监听 INTERRUPT 按钮
		interrupt.addActionListener(new ActionListener(){ 
	    	@Override
            public void actionPerformed(ActionEvent arg0) {

        		area.append("\nThe user interrupts the solving procedure.");
        		fresh.suspend();
	                
	    	}
	    });
		
//		监听 CLEAR 按钮
		clear.addActionListener(new ActionListener(){ 
	    	@Override
            public void actionPerformed(ActionEvent arg0) {
	    	    availableNum=new ArrayList[10][10];  
	    	    correctNum=new int[10][10];  
	    	    countNum=0;  
	    	    fresh.flag=false;
	    	    for(int i=0;i<10;i++){  
	                for(int j=0;j<10;j++){  
	                    availableNum[i][j]=new ArrayList<>();
	                }  
	            }  
	      
	            for(int row=1;row<10;row++){  
	                for(int col=1;col<10;col++){  
	                    for(int i=1;i<10;i++)  
	                        availableNum[row][col].add(new Integer(i));  
	                }  
	            }  
	      
	            //先都初始化为-1，即此时没有填充数字  
	            for(int i=0;i<10;i++)  
	                for(int j=0;j<10;j++)  
	                    correctNum[i][j]=-1;  
	    	    
	    	    for(int i=1;i<10;i++){
	            	for(int j=1;j<10;j++){
	            		box[i][j].setText("");
	            		box[i][j].setBackground(Color.white);
	            	}
	            }
	    	}
	    });
		
		
//		监听 QUIT 按钮
		quit.addActionListener(new ActionListener(){ 
	    	@Override
            public void actionPerformed(ActionEvent arg0) {
	    	    System.exit(0);
	    	}
	    });
	}
	
	private void generatenumPanel() {
		numPanel = new Panel();
		numPanel.setLayout(new FlowLayout());
		JTextField area11 = new JTextField(10);
		JTextField area12 = new JTextField(10);
		JTextField area13 = new JTextField(10);
		JTextField area14 = new JTextField(10);
		JTextField area15 = new JTextField(10);
		JTextField area16 = new JTextField(10);
		JTextField area17 = new JTextField(10);
		JTextField area18 = new JTextField(10);
		JTextField area19 = new JTextField(10);
		JTextField area21 = new JTextField(10);
		JTextField area22 = new JTextField(10);
		JTextField area23 = new JTextField(10);
		JTextField area24 = new JTextField(10);
		JTextField area25 = new JTextField(10);
		JTextField area26 = new JTextField(10);
		JTextField area27 = new JTextField(10);
		JTextField area28 = new JTextField(10);
		JTextField area29 = new JTextField(10);
		JTextField area31 = new JTextField(10);
		JTextField area32 = new JTextField(10);
		JTextField area33 = new JTextField(10);
		JTextField area34 = new JTextField(10);
		JTextField area35 = new JTextField(10);
		JTextField area36 = new JTextField(10);
		JTextField area37 = new JTextField(10);
		JTextField area38 = new JTextField(10);
		JTextField area39 = new JTextField(10);
		JTextField area41 = new JTextField(10);
		JTextField area42 = new JTextField(10);
		JTextField area43 = new JTextField(10);
		JTextField area44 = new JTextField(10);
		JTextField area45 = new JTextField(10);
		JTextField area46 = new JTextField(10);
		JTextField area47 = new JTextField(10);
		JTextField area48 = new JTextField(10);
		JTextField area49 = new JTextField(10);
		JTextField area51 = new JTextField(10);
		JTextField area52 = new JTextField(10);
		JTextField area53 = new JTextField(10);
		JTextField area54 = new JTextField(10);
		JTextField area55 = new JTextField(10);
		JTextField area56 = new JTextField(10);
		JTextField area57 = new JTextField(10);
		JTextField area58 = new JTextField(10);
		JTextField area59 = new JTextField(10);
		JTextField area61 = new JTextField(10);
		JTextField area62 = new JTextField(10);
		JTextField area63 = new JTextField(10);
		JTextField area64 = new JTextField(10);
		JTextField area65 = new JTextField(10);
		JTextField area66 = new JTextField(10);
		JTextField area67 = new JTextField(10);
		JTextField area68 = new JTextField(10);
		JTextField area69 = new JTextField(10);
		JTextField area71 = new JTextField(10);
		JTextField area72 = new JTextField(10);
		JTextField area73 = new JTextField(10);
		JTextField area74 = new JTextField(10);
		JTextField area75 = new JTextField(10);
		JTextField area76 = new JTextField(10);
		JTextField area77 = new JTextField(10);
		JTextField area78 = new JTextField(10);
		JTextField area79 = new JTextField(10);
		JTextField area81 = new JTextField(10);
		JTextField area82 = new JTextField(10);
		JTextField area83 = new JTextField(10);
		JTextField area84 = new JTextField(10);
		JTextField area85 = new JTextField(10);
		JTextField area86 = new JTextField(10);
		JTextField area87 = new JTextField(10);
		JTextField area88 = new JTextField(10);
		JTextField area89 = new JTextField(10);
		JTextField area91 = new JTextField(10);
		JTextField area92 = new JTextField(10);
		JTextField area93 = new JTextField(10);
		JTextField area94 = new JTextField(10);
		JTextField area95 = new JTextField(10);
		JTextField area96 = new JTextField(10);
		JTextField area97 = new JTextField(10);
		JTextField area98 = new JTextField(10);
		JTextField area99 = new JTextField(10);
        
		GridLayout gl=new GridLayout(9,9);
		numPanel.setLayout(gl); 
		
		numPanel.add(area11);
		numPanel.add(area12);
		numPanel.add(area13);
		numPanel.add(area14);
		numPanel.add(area15);
		numPanel.add(area16);
		numPanel.add(area17);
		numPanel.add(area18);
		numPanel.add(area19);
		numPanel.add(area21);
		numPanel.add(area22);
		numPanel.add(area23);
		numPanel.add(area24);
		numPanel.add(area25);
		numPanel.add(area26);
		numPanel.add(area27);
		numPanel.add(area28);
		numPanel.add(area29);
		numPanel.add(area31);
		numPanel.add(area32);
		numPanel.add(area33);
		numPanel.add(area34);
		numPanel.add(area35);
		numPanel.add(area36);
		numPanel.add(area37);
		numPanel.add(area38);
		numPanel.add(area39);
		numPanel.add(area41);
		numPanel.add(area42);
		numPanel.add(area43);
		numPanel.add(area44);
		numPanel.add(area45);
		numPanel.add(area46);
		numPanel.add(area47);
		numPanel.add(area48);
		numPanel.add(area49);
		numPanel.add(area51);
		numPanel.add(area52);
		numPanel.add(area53);
		numPanel.add(area54);
		numPanel.add(area55);
		numPanel.add(area56);
		numPanel.add(area57);
		numPanel.add(area58);
		numPanel.add(area59);
		numPanel.add(area61);
		numPanel.add(area62);
		numPanel.add(area63);
		numPanel.add(area64);
		numPanel.add(area65);
		numPanel.add(area66);
		numPanel.add(area67);
		numPanel.add(area68);
		numPanel.add(area69);
		numPanel.add(area71);
		numPanel.add(area72);
		numPanel.add(area73);
		numPanel.add(area74);
		numPanel.add(area75);
		numPanel.add(area76);
		numPanel.add(area77);
		numPanel.add(area78);
		numPanel.add(area79);
		numPanel.add(area81);
		numPanel.add(area82);
		numPanel.add(area83);
		numPanel.add(area84);
		numPanel.add(area85);
		numPanel.add(area86);
		numPanel.add(area87);
		numPanel.add(area88);
		numPanel.add(area89);
		numPanel.add(area91);
		numPanel.add(area92);
		numPanel.add(area93);
		numPanel.add(area94);
		numPanel.add(area95);
		numPanel.add(area96);
		numPanel.add(area97);
		numPanel.add(area98);
		numPanel.add(area99);
		box[1][1] = area11;
		box[1][2] = area12;
		box[1][3] = area13;
		box[1][4] = area14;
		box[1][5] = area15;
		box[1][6] = area16;
		box[1][7] = area17;
		box[1][8] = area18;
		box[1][9] = area19;
		box[2][1] = area21;
		box[2][2] = area22;
		box[2][3] = area23;
		box[2][4] = area24;
		box[2][5] = area25;
		box[2][6] = area26;
		box[2][7] = area27;
		box[2][8] = area28;
		box[2][9] = area29;
		box[3][1] = area31;
		box[3][2] = area32;
		box[3][3] = area33;
		box[3][4] = area34;
		box[3][5] = area35;
		box[3][6] = area36;
		box[3][7] = area37;
		box[3][8] = area38;
		box[3][9] = area39;
		box[4][1] = area41;
		box[4][2] = area42;
		box[4][3] = area43;
		box[4][4] = area44;
		box[4][5] = area45;
		box[4][6] = area46;
		box[4][7] = area47;
		box[4][8] = area48;
		box[4][9] = area49;
		box[5][1] = area51;
		box[5][2] = area52;
		box[5][3] = area53;
		box[5][4] = area54;
		box[5][5] = area55;
		box[5][6] = area56;
		box[5][7] = area57;
		box[5][8] = area58;
		box[5][9] = area59;
		box[6][1] = area61;
		box[6][2] = area62;
		box[6][3] = area63;
		box[6][4] = area64;
		box[6][5] = area65;
		box[6][6] = area66;
		box[6][7] = area67;
		box[6][8] = area68;
		box[6][9] = area69;
		box[7][1] = area71;
		box[7][2] = area72;
		box[7][3] = area73;
		box[7][4] = area74;
		box[7][5] = area75;
		box[7][6] = area76;
		box[7][7] = area77;
		box[7][8] = area78;
		box[7][9] = area79;
		box[8][1] = area81;
		box[8][2] = area82;
		box[8][3] = area83;
		box[8][4] = area84;
		box[8][5] = area85;
		box[8][6] = area86;
		box[8][7] = area87;
		box[8][8] = area88;
		box[8][9] = area89;
		box[9][1] = area91;
		box[9][2] = area92;
		box[9][3] = area93;
		box[9][4] = area94;
		box[9][5] = area95;
		box[9][6] = area96;
		box[9][7] = area97;
		box[9][8] = area98;
		box[9][9] = area99;
	}
	
	private void generatetxtPanel() {
		txtPanel = new Panel();
		txtPanel.setLayout(new GridLayout(1,1));
		area.setBackground(Color.GREEN);
		txtPanel.add(new JScrollPane(area));
	}
	
	
	public sudoGUI(String panelName) {
		
        for(int i=0;i<10;i++){  
            for(int j=0;j<10;j++){  
                availableNum[i][j]=new ArrayList<>();  
            }  
        }  
  
        for(int row=1;row<10;row++){  
            for(int col=1;col<10;col++){  
                for(int i=1;i<10;i++)  
                    availableNum[row][col].add(new Integer(i));  
            }  
        }  
  
        //先都初始化为-1，即此时没有填充数字  
        for(int i=0;i<10;i++)  
            for(int j=0;j<10;j++)  
                correctNum[i][j]=-1;  
		
		frame = new JFrame(panelName);
		
		generatebtnPanel();
		generatenumPanel();
		generatetxtPanel();


        frame.setLayout(new BorderLayout());
        frame.add(BorderLayout.NORTH, btnPanel);
        frame.add(BorderLayout.CENTER, numPanel);
        frame.add(BorderLayout.SOUTH, txtPanel);
        
        
        frame.setSize(1200, 400);
        frame.setLocation(300,300);
        frame.setVisible(true);
     }
	
	
	

    //在对应数独位置插入正确值  
    public void insert(int row,int col,int value){  
        correctNum[row][col]=value;

		box[row][col].setBackground(Color.YELLOW);
        availableNum[row][col]=null;  
        delete(row,col,value);  
  
    }  
    //每插入一个数值，就删除相应的行列和小框框3X3数独里对应的ArrayList里可能的该值  
    public void delete(int row,int col,int value){  
        //delte row  
        for(int i=1;i<10;i++){  
            if (availableNum[row][i]!=null)  
                availableNum[row][i].remove(new Integer(value));  
        }  
  
        //delete column  
        for(int i=1;i<10;i++){  
            if (availableNum[i][col]!=null)  
                availableNum[i][col].remove(new Integer(value));  
        }  
  
        //delete box num  
        int[] itsCenter=judgeCenterPos(row,col);  
        for(int temp1=itsCenter[0]-1;temp1<=itsCenter[0]+1;temp1++)  
            for(int temp2=itsCenter[1]-1;temp2<=itsCenter[1]+1;temp2++)  
                if(availableNum[temp1][temp2]!=null){  
                    availableNum[temp1][temp2].remove(new Integer(value));  
                }  
  
    }  
    //判断插入的值时处于哪个小框框数独里  
    public int[] judgeCenterPos(int row,int col){  
        int[] itsCenter=new int[2];  
        for(int centerRow=2;centerRow<9;centerRow+=3)  
            for(int centerCol=2;centerCol<9;centerCol+=3){  
                if( Math.abs(row-centerRow)<=1 &&   
                    Math.abs(col-centerCol)<=1 ){  
                    itsCenter[0]=centerRow;  
                    itsCenter[1]=centerCol;  
                    return itsCenter;  
                }  
  
            }  
        return itsCenter;  
  
    }  
  
    //判断空格里所能填的数字是不是只能有一个,当返回-1时通过检测报错  
    public int[] judgeIfOnlyOne(){  
  
        for(int row=1;row<10;row++)  
            for(int col=1;col<10;col++){  
                if(availableNum[row][col]!=null)  
                    if(availableNum[row][col].size()==1)  
                        return new int[]{row,col};  
            }  
  
        return new int[]{-1,-1};  
  
    }  
  
    // 判断为唯一，但是空格里还有多于1个的数时，我们直接将哪个正确的值填入  
    public void insertIfCan(){  
  
        for(int row=1;row<=7;row+=3){  
            for(int col=1;col<=7;col+=3){  
                for(int z=1;z<10;z++){  
                    int count=0;  
                    Integer temp=new Integer(z);  
                    int itemp=0,jtemp=0;  
                    outer:  
                    for(int i=row;i<row+3;i++){  
                        for(int j=col;j<col+3;j++){  
                            if(availableNum[i][j]!=null){  
                                if(availableNum[i][j].contains(temp)){  
                                    count++;  
                                    itemp=i;  
                                    jtemp=j;  
                                    if (count>1)  
                                        break outer;  
                                }  
                            }  
                        }  
                    }  
                    if(count==1 && itemp!=0){  
                        insert(itemp,jtemp,z);  
                    }  
                }  
                  
            }  
        }  
    }  
  
    //判断数独的矩阵是否填满，没有则继续  
    public boolean judgeMatrixFull(){  
        for(int i=1;i<10;i++)  
            for(int j=1;j<10;j++)  
                if(correctNum[i][j]==-1)  
                    return false;  
        return true;  
    }  
  
    public static void main(String[] args){
    	sudoGUI sudo = new sudoGUI("SUDOKU");
    }  
}
