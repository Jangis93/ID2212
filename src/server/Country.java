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
public class Country {
    private String name;
    private int members;
    
    public Country(String name){
        this.name = name;
        this.members = 1;
    }
    
    public String getName(){
        return this.name;
    }
    
    public int getMembers(){
        return this.members;
    }
    
    public void addMember(){
        this.members += 1;
    }
    
    public void deleteMember(){
        this.members -= 1;
    }
}
