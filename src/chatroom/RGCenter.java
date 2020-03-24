/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatroom;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.Naming;
import java.net.MalformedURLException;
import java.util.ArrayList;
/**
 *
 * @author Michaela
 */
public class RGCenter extends UnicastRemoteObject implements RGCenterInterface {
    private ArrayList<ChatRoomInterface> chatRooms;
    
    RGCenter() throws RemoteException, MalformedURLException {
        super();
        try{
            LocateRegistry.getRegistry(8888).list();
        }catch(RemoteException e){
            LocateRegistry.createRegistry(8888);
        }
        Naming.rebind("rmi://localhost:8888/rgcenter", this);
        chatRooms = new ArrayList();
    }
    
    
}
