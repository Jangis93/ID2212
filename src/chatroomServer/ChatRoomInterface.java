/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatroomServer;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author Michaela
 */
public interface ChatRoomInterface extends Remote{
    
    public void createRoom() throws RemoteException;
    public void destroyRoom()throws RemoteException;
    
}
