/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
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
    
    public ClientHandler(Socket socket, GUIController gui){
        this.socket = socket;
        this.gui = gui;
        
        try{
            out = new PrintWriter(socket.getOutputStream());
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }catch(IOException e){
            System.out.println("Could not setup in/out");
        }
    }
    
    public void run(){
        
        boolean running = true;
        while(running){
            if(gui.connected){
                
                /*
                try{
                    // read from the server output
                }catch(){
                    
                }
                */
            }else{
                // close the connection
                try{
                  socket.close();
                    running = false;  
                }catch(IOException ex){
                    System.out.println("Connection closed because client disconnected"); //debug purpose
                }         
            }
        }
    }
}
