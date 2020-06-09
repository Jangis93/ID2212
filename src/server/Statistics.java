/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.util.ArrayList;
import java.util.Hashtable;

/**
 *
 * @author Michaela
 */
public class Statistics {
    private int numberOfParticipants;
    private int numberOfSports;
    private int numberOfCountries;
    private int numberOfWomen;
    private int numberOfMen;
    private float totalWeight;
    private float totalHeight;
    private Hashtable<String,Integer> countries;
    private Hashtable<String,Integer> sports;
    
    public Statistics(){
        this.numberOfCountries = 0;
        this.numberOfSports = 0;
        this.numberOfParticipants = 0;
        this.totalHeight = 0;
        this.totalWeight = 0;
        countries = new Hashtable<String, Integer>();
        sports = new Hashtable<String, Integer>();
    }
    
    public void addParticipant(String gender, float height, float weight, String sport, String country){
        this.numberOfParticipants += 1;
        this.totalHeight += height;
        this.totalWeight += weight;
        
        if(gender.equals("F")){
            this.numberOfWomen += 1;
        }else{
            this.numberOfMen += 1;
        }
        
        
        if(countries.containsKey(country)){
            countries.put(country, countries.get(country)+1);
        }else{
            countries.put(country, 1);
            this.numberOfCountries += 1;
        }
        
        if(sports.containsKey(sport)){
            sports.put(sport, sports.get(sport)+1);
        }else{
            sports.put(sport, 1);
            this.numberOfSports += 1;
        }
    }
    
    public float getHeight(){
        return this.totalHeight;
    }
    
    public float getWeight(){
        return this.totalWeight;
    }
    
    public int getCountMen(){
        return this.numberOfMen;
    }
    
    public int getCountWomen(){
        return this.numberOfWomen;
    }
    
    public int getCountCountries(){
        return this.numberOfCountries;
    }
    
    public int getCountSports(){
        return this.numberOfSports;
    }
    
    public int getCountParticipants(){
        return this.numberOfParticipants;
    }
    
    public Hashtable<String, Integer> getCountriesStats(){
        return this.countries;
    }
    
    public Hashtable<String, Integer> getSportsStats(){
        return this.sports;
    }
    
    public void setHeight(float height){
        this.totalHeight = height;
    }
    
    public void setWeight(float weight){
        this.totalWeight = weight;
    }
    
    public void setMen(int men){
        this.numberOfMen = men;
    }
    
    public void setWomen(int women){
        this.numberOfWomen = women;
    }
    
    public void setParticipants(int people){
        this.numberOfParticipants = people;
    }
    
    public void updateSport(String sport, int option){
        
        if(option == 0){
            // delete
            this.sports.put(sport, sports.get(sport)-1);
        }else{
            // add
            this.sports.put(sport, sports.get(sport)+1);
        }
    }
    
    public void updateCountry(String country, int option){
        
        if(option == 0){
            // delete
            this.countries.put(country, countries.get(country)-1);
        }else{
            // add
            this.countries.put(country, countries.get(country)+1);
        }
    }
    
   

}
