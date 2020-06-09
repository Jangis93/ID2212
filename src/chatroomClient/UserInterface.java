/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatroomClient;

import chatroomServer.ChatRoom;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 *
 * @author Michaela
 */
public interface UserInterface extends Remote{
    
    // public void notifyUser() throws RemoteException;
    // public void chatMessage(String message) throws RemoteException; // sender info, room info
    
    
    public void updateUser(ArrayList<ChatRoom> rooms) throws RemoteException;
    
}
