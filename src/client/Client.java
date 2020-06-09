package client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    
    public static void main(String[] args){
        Client client = new Client();
        client.setup();
        client.eventListener();
    }
    
    public Client(){
        cFrame = new ConnectedFrame();
    } 
    
    private void setup(){
        JFrame frame = new JFrame("Welcome!");
        frame.setContentPane(cFrame);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setVisible(true);
    }
    
    private void eventListener(){
        
        cFrame.startBtn.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                try {
                    GUIController gui = new GUIController();
                    gui.start();
                    cFrame.startBtn.setSelected(false);
                } catch (NotBoundException ex) {
                    Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                } catch (MalformedURLException ex) {
                    Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                } catch (RemoteException ex) {
                    Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
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
