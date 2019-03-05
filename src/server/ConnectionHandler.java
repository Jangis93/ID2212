/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.Socket;
import java.util.StringTokenizer;

/**
 *
 * @author Michaela
 */
public class ConnectionHandler extends Thread implements Serializable {
    private Server ConnectionPoint;
    private Socket client;
    
    public ConnectionHandler(Server server, Socket client){
        System.out.println("ConnectionHandler created");
        this.ConnectionPoint = server;
        this.client = client;
    }
    
    public void run(){
        System.out.println("running communication");
        try{
            
            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            PrintWriter out = new PrintWriter(client.getOutputStream());
            String str = in.readLine();
            System.out.println(str);   // for log
        }catch(IOException e){
        e.printStackTrace();
        }
                
    }
}
