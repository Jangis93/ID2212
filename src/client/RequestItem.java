/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

/**
 *
 * @author mjang
 */
public class RequestItem {
    
    private String request;
    private String payload; 
    
    public RequestItem(String request, String payload){
        this.request = request;
        this.payload = payload;
    }
    
    public String getRequest(){
        return this.request;
    }
    
    public String getPayLoad(){
        return this.payload;
    }
    
    public String toString(){
        return this.request + " " + this.payload;
    }
    
}
