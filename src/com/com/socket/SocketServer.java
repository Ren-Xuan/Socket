package com.com.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;



public class SocketServer {
	private ServerSocket serverSocket;
	private HashSet<Socket> allSocket;

	


	public SocketServer() {
		try {
			//��ʼ��һ��ServerSocket�ಢ���ö˿ںţ�����Ĭ������Ϊ8080
			serverSocket=new ServerSocket(8080);
			//�ͻ��˵�hashmap
			allSocket=new HashSet<Socket>();
		}catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	
	private void startService() throws IOException {
		//����ѭ�������¿ͻ��˵�����
		while(true) {
			System.out.println("�������ɹ��������ȴ��ͻ�����Ӧ.....");
			//�����¿ͻ��˽���������
			Socket s=serverSocket.accept();
			System.out.println(new Date()+" ip:"+s.getLocalAddress()+" "+"�����������ң�");
			allSocket.add(s);
			//����һ���ͻ��˵����߳�
			new ChatRoomServerThread(s).start();
		}
	}

	class ChatRoomServerThread extends Thread{
		private Socket socket;
	
		public ChatRoomServerThread(Socket s) {
			this.socket=s;
		}
	

		public void run(){
			/**
			*  �õ�s��������������װ��BufferedReader
			*  ѭ����ͣ�Ĵ�BufferedReader�ж�ȡ���ݡ�
			*  ÿ����һ�����ݾͽ���һ������ת�����������ߵĿͻ��ˡ�
			*  ѭ������allSockets,�õ�ÿһ��socket��
			*  Ȼ��õ���socket�������������װ�������������д���ݡ�
			*/
	
			BufferedReader br=null;
			try {
				br=new BufferedReader(new InputStreamReader(socket.getInputStream()));
				//���ϼ�����ǰsocket����Ϣ
				while(true) {
					String str=br.readLine();
					if(str.split(":").equals("%GOODBYE%")) {
						allSocket.remove(socket);
						sendMessageToAllClient(str.split(":")[0]+",�뿪�����ң�");
						System.out.println(str.split(":")[0]+",�뿪�����ң�");
						socket.close();
						break;
					}
					sendMessageToAllClient(str);
				}
			}catch (IOException e) {
				e.printStackTrace();
			}
		}//run end!
		//�ѵ�ǰ�յ���������Ϣ�ַ����������пͻ�������ʾ
		public void sendMessageToAllClient(String str)throws IOException{
			Date date=new Date();
			SimpleDateFormat sf=new SimpleDateFormat("hh��mm��ss��");
			String dateStr = sf.format(date);

			for(Socket temp:allSocket) {
				PrintWriter pWriter=new PrintWriter(temp.getOutputStream());
				pWriter.println(str+"\t["+dateStr+"]");
				pWriter.flush();
			}
		}
	}//Thread class end!

	public static void main(String[] args){
		
		try{
			new SocketServer().startService();
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
}