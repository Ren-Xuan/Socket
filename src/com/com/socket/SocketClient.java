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
		button=new JButton("����");
		exitButton = new JButton("�˳�");
		init();
		addEventHandler();
	}



	private String createConnection() {
		String hostName = "";
		String port = null;
		do {
			//��ʼ��������ip�Ͷ˿�
			hostName=JOptionPane.showInputDialog(frame,"�������������ַ��(localhost)");
			port=JOptionPane.showInputDialog(frame,"������˿ںţ�");
			//��ʼ���ǳ�
			userName = JOptionPane.showInputDialog(frame,"�������ǳƣ�");
			try {
				//��ʼ��Socket
				socket=new Socket(hostName, Integer.parseInt(port));
				
				//��ʼ��socket���������
				printWriter=new PrintWriter(socket.getOutputStream());
				bufferedReader=new BufferedReader(new InputStreamReader(socket.getInputStream()));
			}catch (IOException e) {
				JOptionPane.showMessageDialog(frame, "���Ӳ�������ȷ������������");
			}
		}while(socket==null);
		
		String title =hostName+":"+port+"��"+userName;
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
		//������Ϣ��ť
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(field.getText().trim().equals("")) {
					JOptionPane.showMessageDialog(frame, "���ܷ��Ϳ�����");
					return;
				}
				//���͸���������Ϣ
				printWriter.println(userName+":"+field.getText());
				printWriter.flush();
				field.setText("");
			}
		});
		//�˳�����
		exitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				/**
				 * �����Ի���ѯ���Ƿ�ȷ���˳�
				 * �����������˳�����userName+����%GOODBYE%��
				 * �ȴ�200���룬�ٹر�socket���˳�����
				 */
				int op=JOptionPane.showConfirmDialog(frame, "ȷ���뿪��������"," ȷ���˳�",
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
		
	
		//���Ͻǽ����Ự��������Ȼ����˳�����
		frame.addWindowFocusListener(new WindowAdapter() {
			public void windowClosing(WindowEvent arg0) {
				/**
				 * �����Ի���ѯ���Ƿ�ȷ���˳�
				 * �����������˳�����userName+����%GOODBYE%��
				 * �ȴ�200���룬�ٹر�socket���˳�����
				 */
				int op=JOptionPane.showConfirmDialog(frame, "ȷ���뿪��������"," ȷ���˳�",
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
