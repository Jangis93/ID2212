/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.util.ArrayList;

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
    private ArrayList<Country> countryStats;
    private ArrayList<Sport> sportStats;
    
    public Statistics(){
        this.numberOfCountries = 0;
        this.numberOfSports = 0;
        this.numberOfParticipants = 0;
        this.totalHeight = 0;
        this.totalWeight = 0;
        this.countryStats = new ArrayList();
        this.sportStats = new ArrayList();
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
        
        boolean countryFound = false;
        for(int i = 0; i < countryStats.size(); i++){
            if(countryStats.get(i).getName().equals(country)){
                Country c = countryStats.get(i);
                c.addMember();
                countryStats.set(i, c);
                countryFound = true;
                break;
            }
        }
        if(!countryFound){
            Country newCountry = new Country(country);
            countryStats.add(newCountry);
            this.numberOfCountries += 1;
        }
        
        boolean sportFound = false;
        for(int i = 0; i < sportStats.size(); i++){
            if(sportStats.get(i).getName().equals(sport)){
                Sport s = sportStats.get(i);
                s.addParticipant();
                sportStats.set(i, s);
                break;
            }
        }
        if(!sportFound){
            Sport newSport = new Sport(sport);
            sportStats.add(newSport);
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
    
    public void updateSport(String sport, int option){
        
        for(int i = 0; i < sportStats.size(); i++){
            if(sportStats.get(i).getName().equals(sport)){
                Sport s = sportStats.get(i);
                if(option == 0){
                    s.deleteParticipant();
                }else if(option == 1){
                    s.addParticipant();
                }
                sportStats.set(i, s);
                break;
            }
        }
    }
    
    public void updtateCountry(String country, int option){
        
        for(int i = 0; i < countryStats.size(); i++){
            if(countryStats.get(i).getName().equals(country)){
                Country s = countryStats.get(i);
                if(option == 0){
                    s.deleteMember();
                }else if(option == 1){
                    s.addMember();
                }
                countryStats.set(i, s);
                break;
            }
        }
    }

}
