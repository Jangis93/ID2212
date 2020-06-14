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
    //private ConcurrentLinkedDeque<String> requestQueue;
    private ConcurrentLinkedDeque<RequestItem> requestQueue;
    
    private Logger logger = Logger.getLogger(ClientHandler.class.getName());
    
    public ClientHandler(Socket socket, GUIController gui){
        this.socket = socket;
        this.gui = gui;
        requestQueue = new ConcurrentLinkedDeque<>(); 
        running = true;
        
        try{
            out = new PrintWriter(socket.getOutputStream());
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }catch(IOException e){
            logger.log(Level.WARNING, "Could not setup communication links for server communication");
            // TODO: retry or inform user of the problem
        }
    }
    
    public void killThread(){
        try{
            out.close();
            in.close();
            socket.close(); 
        }catch(IOException e){
            logger.log(Level.INFO, "Tried to close socket when exception was thrown: ", e);
        }
        running = false;
    }
    
    public boolean getStatus(){
        return running;
    }
    
    /*
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
                        if(requestQueue.getLast().equals("GET RECORDS") | requestQueue.getLast().equals("GET STATS")){
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
                        }else if(requestQueue.getLast().equals("POST") | requestQueue.getLast().equals("POST ADD")){
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
                    }else if(answer.equals("HTTP/1.0 UPDATE")){
                        System.err.println(answer);
                        while((answer = in.readLine()) != null){
                            if(answer.isEmpty()){
                                    break;
                            }else{
                                total += answer + "\n";
                            }   
                        }
                        String t = total;
                        String r = "UPDATE";
                        new Thread(new Runnable() {
                            @Override
                            public void run(){
                                gui.serverResponse(r, t);
                            }
                        }).start();
                    }

                }
            }catch(IOException e){
                
            }

        }
    }
    */
    
    public void run(){
        String request = "";
        String payLoad = "";
        String serverRespons = "";
        String intermediateAnswer = "";
        
        while(running){
            
            // check the queue
            if(!requestQueue.isEmpty()){
                RequestItem newRequest = requestQueue.getLast();
                
                logger.log(Level.INFO, "Request read from request queue: ", newRequest.getRequest());
                
                if(request.split(" ")[0].equals("GET")){
                    request = newRequest.getRequest();
                }else{
                    request = newRequest.toString();
                }
                writeSocket(request);
            }
                
            try {
                if(in.ready() && (intermediateAnswer = in.readLine()) != null){
                    logger.log(Level.INFO, "Server respons read: ", intermediateAnswer);

                    if(intermediateAnswer.equals("HTTP/1.0 200 OK")){
                        writeSocket("HTTP/1.0 100 CONTINUE");

                        while((intermediateAnswer = in.readLine()) != null){
                            if(intermediateAnswer.isEmpty()){
                                break;
                            }else if(intermediateAnswer.equals("HTTP/1.0 204 NO CONTENT") 
                                    || intermediateAnswer.equals("HTTP/1.0 201 CREATED") ){
                                serverRespons = " ";
                                // maybe break
                            }
                            else{
                                serverRespons += intermediateAnswer + "\n";
                            }
                        }

                        clientCall(requestQueue.pollLast().getRequest(), serverRespons);


                    }else if(intermediateAnswer.equals("HTTP/1.0 UPDATE")){
                        while((intermediateAnswer = in.readLine()) != null){
                            if(intermediateAnswer.isEmpty()){
                                    break;
                            }else{
                                serverRespons += intermediateAnswer + "\n";
                            }   
                        }

                        clientCall("UPDATE", serverRespons);
                    }
                }

            } catch(IOException e) {
                logger.log(Level.WARNING, "Exception occured when reading respons from server: ", e);
            }
            
        }
    }
    
    /*
    Method for GUI controller to call when needing to do invoke server communication
    */
    public void serverCall(String request, String payLoad){
        requestQueue.addFirst(new RequestItem(request, payLoad));
    }
    
    private void clientCall(String request, String payLoad){
        gui.serverResponse(request, payLoad);
    }
    
    private void writeSocket(String request){
        out.println(request);
        out.flush();
    }
    
}
