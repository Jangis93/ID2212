/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatroomServer;

import chatroomClient.UserInterface;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.Naming;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author Michaela
 */
public class RGCenter extends UnicastRemoteObject implements RGCenterInterface {
    private ArrayList<ChatRoomInterface> chatRooms;
    private ArrayList<UserInterface> users;
    
    RGCenter() throws RemoteException, MalformedURLException {
        super();
        try{
            LocateRegistry.getRegistry(8888).list();
        }catch(RemoteException e){
            LocateRegistry.createRegistry(8888);
        }
        Naming.rebind("rmi://localhost:8888/rgcenter", this);
        chatRooms = new ArrayList();
        users = new ArrayList();
        System.out.println("RGCenter up!");
    }
    
    public static void main(String[] args){
        System.out.println("RGCenter starting!");
        try {
            new RGCenter();
        } catch (RemoteException ex) {
            Logger.getLogger(RGCenter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MalformedURLException ex) {
            Logger.getLogger(RGCenter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public ArrayList getRooms(){
        return chatRooms;
    }
    
    public void registerUser(UserInterface user) {
        System.out.println("Register new user");
        if(!users.contains(user)){
            users.add(user);
            System.out.println("Client is now connected!");
            System.out.print(users.toString());
            updateUser(user);
        } else{
            System.out.println("Client is already connected!");
        }
    }
    
    public void unregisterUser(UserInterface user) {
        users.remove(user);
    }
    
    public void registerRoom(ChatRoomInterface room) {
        chatRooms.add(room);
    }
    
    public void unregisterRoom(ChatRoomInterface room) {
        chatRooms.remove(room);
    }
    
    private void updateUser(UserInterface user) {
        ArrayList<ChatRoom> rooms = convert(chatRooms);
        try{
           user.updateUser(rooms);  // gör om till items!
        }catch(RemoteException re){
            System.err.println("couldn't get client"); // log error
        }  
    }
    
    private ArrayList convert(ArrayList rooms) {
        ArrayList<ChatRoom> items = new ArrayList();
        return items;
    }
    
    
}
