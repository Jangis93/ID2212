package client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import javax.swing.JFrame;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Michaela
 */
public class Client {
    private ArrayList<ClientHandler> clients; //debug purpose
    
    private ConnectedFrame cFrame;
    private final int PORT = 4444;
    
    
    public static void main(String[] args){
        Client client = new Client();
        client.setup();
        client.eventListener();
    }
    
    public Client(){
        cFrame = new ConnectedFrame();
    }
    
    
    private void setup(){
        JFrame frame = new JFrame("Nordic Olympic Games");
        frame.setContentPane(cFrame);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setVisible(true);
    }
    
    
    private void eventListener(){
        
        cFrame.startBtn.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                try{
                    Socket socket = new Socket(InetAddress.getByName(null), PORT);
                    GUIController gui = new GUIController();
                    ClientHandler handler = new ClientHandler(socket, gui);
                    handler.start();
                    //clients.add(handler);
                    cFrame.startBtn.setSelected(false);
                }catch(IOException ex){
                    System.out.println("Could not create clienthandler");
                }   
            }
        });
        
        cFrame.exitBtn.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                System.exit(0);
            }
        });
    }
}

