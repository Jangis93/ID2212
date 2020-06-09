/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatroomClient;

import chatroomServer.ChatRoom;
import client.GUIController;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Michaela
 */
public class User extends UnicastRemoteObject implements UserInterface{
    private String name;
    private GUIController gui;
    
    public User(GUIController gui) throws RemoteException{
        this.gui = gui;
    }
    
    public void addRoom(String title){
        // create rooms with chatroom interface an
    }
    
    public void notifyUser(){
        
    }
    
    public void updateUser(ArrayList<ChatRoom> roomList) {
        // update chatrooms in list
                
        Object[] rooms = roomList.toArray();
        DefaultTableModel tbmark = (DefaultTableModel) gui.cRegPage.roomTable.getModel();
        tbmark.setRowCount(0);
        for(int i = 0; i < roomList.size(); i++){
            tbmark.addRow(new Object[]{
                roomList.get(i).getName(), roomList.get(i).getCreator(), roomList.get(i).getParticipants().size()
            });
        }
    }
    
    public void addName(String name) {
        this.name = name;
    }
}
