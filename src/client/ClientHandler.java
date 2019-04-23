/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Michaela
 */
public class ClientHandler extends Thread{
    
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private boolean running;
    
    private GUIController gui;
    private ConcurrentLinkedDeque<String> requestQueue;
    
    public ClientHandler(Socket socket, GUIController gui){
        this.socket = socket;
        this.gui = gui;
        requestQueue = new ConcurrentLinkedDeque<String>(); 
        running = true;
        
        try{
            out = new PrintWriter(socket.getOutputStream());
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }catch(IOException e){
            System.out.println("Could not setup in/out");
        }
    }
    
    public void killThread(){
        try{
            out.close();
            in.close();
            socket.close(); 
        }catch(IOException e){
            e.printStackTrace();
        }
        running = false;
    }
    
    public boolean getStatus(){
        return running;
    }
    
    public void run(){

        String answer = "";
        String total = "";
        while(running){
            System.out.println("reading");
            total = "";
            try{
                if((answer = in.readLine()) != null){
                    if(answer.equals("HTTP/1.0 200 OK")){
                        writeSocket("HTTP/1.0 100 CONTINUE");
                        if(requestQueue.getLast().equals("GET")){
                            while((answer = in.readLine()) != null){
                                if(answer.isEmpty()){
                                    break;
                                }else{
                                    total += answer + "\n";
                                }
                            }
                            String t = total;
                            new Thread(new Runnable() {
                                @Override
                                public void run(){
                                    String request = requestQueue.pollLast();
                                    gui.serverResponse(request, t);
                                }
                            }).start();
                        }else if(requestQueue.getLast().equals("DELETE")){
                            if((answer = in.readLine()) != null & answer.equals("HTTP/1.0 204 NO CONTENT")){
                                new Thread(new Runnable() {
                                @Override
                                public void run(){
                                    String t = " ";
                                    String request = requestQueue.pollLast();
                                    gui.serverResponse(request, t);
                                }
                                }).start();
                            }  
                        }else if(requestQueue.getLast().equals("POST")){
                            if((answer = in.readLine()) != null & answer.equals("HTTP/1.0 201 CREATED")){
                                new Thread(new Runnable() {
                                @Override
                                public void run(){
                                    String t = " ";
                                    String request = requestQueue.pollLast();
                                    gui.serverResponse(request, t);
                                }
                                }).start();
                            }
                        }
                    }else{
                        in.close();
                        out.close();
                        socket.close();
                    }
                }
            }catch(IOException e){
                
            }

        }
    }
    
    /*
    Method for GUI controller to call when needing to do invoke server communication
    */
    public void serverCall(String request){
        requestQueue.addFirst(request.split(" ")[0]);
        out.write(request);
        out.write("\n");
        out.flush();
    }
    
    private void writeSocket(String request){
        out.write(request);
        out.write("\n");
        out.flush();
    }
    
}
