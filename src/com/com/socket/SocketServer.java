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
			//初始化一个ServerSocket类并设置端口号，这里默认设置为8080
			serverSocket=new ServerSocket(8080);
			//客户端的hashmap
			allSocket=new HashSet<Socket>();
		}catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	
	private void startService() throws IOException {
		//不断循环接受新客户端的请求
		while(true) {
			System.out.println("服务器成功启动，等待客户端相应.....");
			//接受新客户端建立的请求
			Socket s=serverSocket.accept();
			System.out.println(new Date()+" ip:"+s.getLocalAddress()+" "+"登入了聊天室！");
			allSocket.add(s);
			//启动一个客户端的子线程
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
			*  得到s的输入流，并包装成BufferedReader
			*  循环不停的从BufferedReader中读取数据。
			*  每读到一行数据就将这一行数据转发给所有在线的客户端。
			*  循环遍历allSockets,得到每一个socket，
			*  然后得到该socket的输出流，并包装，再向输出流中写数据。
			*/
	
			BufferedReader br=null;
			try {
				br=new BufferedReader(new InputStreamReader(socket.getInputStream()));
				//不断监听当前socket的消息
				while(true) {
					String str=br.readLine();
					if(str.split(":").equals("%GOODBYE%")) {
						allSocket.remove(socket);
						sendMessageToAllClient(str.split(":")[0]+",离开聊天室！");
						System.out.println(str.split(":")[0]+",离开聊天室！");
						socket.close();
						break;
					}
					sendMessageToAllClient(str);
				}
			}catch (IOException e) {
				e.printStackTrace();
			}
		}//run end!
		//把当前收到的所有消息分发给所以所有客户的以显示
		public void sendMessageToAllClient(String str)throws IOException{
			Date date=new Date();
			SimpleDateFormat sf=new SimpleDateFormat("hh点mm分ss秒");
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