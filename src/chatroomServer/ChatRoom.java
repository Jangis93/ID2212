/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatroomServer;

import chatroomClient.UserInterface;
import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author Michaela
 */
public class ChatRoom implements Serializable{
    private String name;
    private String creator;
    private UserInterface ownerInterface;
    private ArrayList<String> participants;
    
    ChatRoom(String name, String owner, UserInterface client){
        this.name = name;
        this.ownerInterface = client;
        this.creator = owner;
    }
    
    public String getName(){
        return name;
    }
    
    public String getCreator(){
        return creator;
    }
    
    public UserInterface getOwnerInterface(){
        return ownerInterface;
    }    
    
    public ArrayList getParticipants() {
        return participants;
    }
}

