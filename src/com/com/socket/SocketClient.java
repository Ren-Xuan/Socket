package com.com.socket;


import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;





public class SocketClient {

	private JFrame frame;
	private JTextArea area;
	private JLabel label;
	private JTextField field;
	private JButton button;
	private JButton exitButton;
	private Socket socket;
	private String userName;
	private BufferedReader bufferedReader;
	private PrintWriter printWriter;

	
	
	public SocketClient() {
		String title = createConnection();
		frame=new JFrame(title);
		area=new JTextArea(20,45);
		label=new JLabel();
		field=new JTextField(30);
		button=new JButton("发送");
		exitButton = new JButton("退出");
		init();
		addEventHandler();
	}



	private String createConnection() {
		String hostName = "";
		String port = null;
		do {
			//初始化服务器ip和端口
			hostName=JOptionPane.showInputDialog(frame,"请输入服务器地址：(localhost)");
			port=JOptionPane.showInputDialog(frame,"请输入端口号：");
			//初始化昵称
			userName = JOptionPane.showInputDialog(frame,"请输入昵称：");
			try {
				//初始化Socket
				socket=new Socket(hostName, Integer.parseInt(port));
				
				//初始化socket输入输出流
				printWriter=new PrintWriter(socket.getOutputStream());
				bufferedReader=new BufferedReader(new InputStreamReader(socket.getInputStream()));
			}catch (IOException e) {
				JOptionPane.showMessageDialog(frame, "连接参数不正确，请重新输入");
			}
		}while(socket==null);
		
		String title =hostName+":"+port+"的"+userName;
		return title;
	}


	
	private void init() {
		field.setFont(new Font("", Font.BOLD, 20));
		area.setFont(new Font("", Font.BOLD, 24));
		JScrollPane jScrollPane=new JScrollPane(area);
		JPanel panel=new JPanel();
		panel.add(label);
		panel.add(field);
		panel.add(button);
		panel.add(exitButton);
		frame.add(jScrollPane,BorderLayout.CENTER);
		frame.add(panel, BorderLayout.SOUTH);
	}

	private void addEventHandler() {
		//发送消息按钮
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(field.getText().trim().equals("")) {
					JOptionPane.showMessageDialog(frame, "不能发送空内容");
					return;
				}
				//发送给服务器消息
				printWriter.println(userName+":"+field.getText());
				printWriter.flush();
				field.setText("");
			}
		});
		//退出聊天
		exitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				/**
				 * 弹出对话框询问是否确定退出
				 * 服务器发送退出请求：userName+“：%GOODBYE%”
				 * 等待200毫秒，再关闭socket，退出程序
				 */
				int op=JOptionPane.showConfirmDialog(frame, "确定离开聊天室吗？"," 确认退出",
						JOptionPane.YES_NO_OPTION);
				if(op==JOptionPane.YES_OPTION) {
					
					printWriter.println(userName+":%GGODBYE%");
					printWriter.flush();
					try {
						Thread.sleep(200);
					}catch (Exception e) {
						e.printStackTrace();
					}finally {
						try {
							socket.close();
						}catch (IOException e) {}
						System.exit(0);
					}
				}	
			}
		});
		
	
		//右上角结束会话监听器，然后就退出聊天
		frame.addWindowFocusListener(new WindowAdapter() {
			public void windowClosing(WindowEvent arg0) {
				/**
				 * 弹出对话框询问是否确定退出
				 * 服务器发送退出请求：userName+“：%GOODBYE%”
				 * 等待200毫秒，再关闭socket，退出程序
				 */
				int op=JOptionPane.showConfirmDialog(frame, "确定离开聊天室吗？"," 确认退出",
						JOptionPane.YES_NO_OPTION);
				if(op==JOptionPane.YES_OPTION) {
					printWriter.println(userName+":%GGODBYE%");
					printWriter.flush();
					try {
						Thread.sleep(200);
					}catch (Exception e) {
						e.printStackTrace();
					}finally {
						try {
							socket.close();
						}catch (IOException e) {}
						System.exit(0);
					}
				}	
			}
		});
	}
	
	
	
	public void showMe() {
		frame.pack();
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		new Thread() {
			public void run() {
				while(true) {
						try {
							String str=bufferedReader.readLine();
							area.append(str+"\n");
						}catch (IOException e) {
							e.printStackTrace();
						}
				}
			}
		}.start();
	}

	public static void main(String[] args) {
		new SocketClient().showMe();
	}
}
