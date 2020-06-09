/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatroomServer;

import chatroomClient.UserInterface;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 *
 * @author Michaela
 */
public interface RGCenterInterface extends Remote{
    
    public ArrayList getRooms() throws RemoteException;
    public void registerUser(UserInterface user) throws RemoteException;
    public void unregisterUser(UserInterface user) throws RemoteException;
    public void registerRoom(ChatRoomInterface room) throws RemoteException;
    public void unregisterRoom(ChatRoomInterface room) throws RemoteException;
    
}
