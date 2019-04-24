/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

/**
 *
 * @author Michaela
 */
public class Sport {
    private String name;
    private int participants;
    
    public Sport(String name){
        this.name = name;
        this.participants = 1;
    }
    
    public String getName(){
        return this.name;
    }
    
    public int getParticipants(){
        return this.participants;
    }
    
    public void addParticipant(){
        this.participants += 1;
    }
    
    public void deleteParticipant(){
        this.participants -= 1;
    }
    
}
