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
    
    private GUIController gui;
    private ConcurrentLinkedDeque<String> requestQueue;
    
    public ClientHandler(Socket socket, GUIController gui){
        this.socket = socket;
        this.gui = gui;
        requestQueue = new ConcurrentLinkedDeque<String>(); 
        
        try{
            out = new PrintWriter(socket.getOutputStream());
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }catch(IOException e){
            System.out.println("Could not setup in/out");
        }
    }
    
    public void run(){

        //String request = "";
        String answer = "";
        String total = "";
        while(true){
            System.out.println("reading");
            try{
                if((answer = in.readLine()) != null){
                    if(answer.equals("HTTP/1.0 200 OK")){
                        writeSocket("OK");
                        
                        while((answer = in.readLine()) != null){
                            System.out.println("inside");
                            if(answer.isEmpty()){
                                System.out.println("error");
                                break;
                            }else{
                                System.out.println("answer: " + answer);
                                total += answer + "\n";
                            }       
                        }
                        System.out.println(total);
                        String t = total;
                        new Thread(new Runnable() {
                            @Override
                            public void run(){
                                String request = requestQueue.pollLast();
                                gui.serverResponse(request, t);
                            }
                        }).start();  
                    }
                }else{
                    in.close();
                    out.close();
                    socket.close();
                    // set error message that server has closed!
                }
            }catch(IOException e){
                e.printStackTrace();
            }
            
        }
    }
    

    
    /*
    Method for GUI controller to call when needing to do invoke server communication
    */
    public void serverCall(String request){
        System.out.println("servercall");
        requestQueue.addFirst(request);
        writeSocket(request);
        
        /*
        String answer = readSocket(request);
        System.out.println(answer);
        gui.serverResponse(request, answer);
        */
    }
    
    private void writeSocket(String request){
        out.write(request);
        out.write("\n");
        out.flush();
    }
    
}
