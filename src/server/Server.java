/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import static server.ConnectionHandler.UNAVAILABLE;

/**
 *
 * @author Michaela
 */
public class Server {
    private String tsvPath = "C:/Users/Michaela/Documents/NetBeansProjects/NOG/ID2212/participants.tsv";
    private final int PORT = 4444;
    private ServerSocket socket; 
    
    static final String JDBC_DRIVER = "jdbc:derby://localhost:1527/Members";
    static protected Connection connection;
    private ArrayList<ConnectionHandler> handlers;
    private Statistics stats;
    
    public static void main(String[] args){
        Server server = new Server();
        if(!server.isEmpty()){
            try{
                server.fillDatabase();
            }catch(Exception e){
                e.printStackTrace();
            }
        }else{
            server.setUpStats();
        }
        server.listen();
    }
    
    public Server(){
        
        try{
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
            connection = DriverManager.getConnection(JDBC_DRIVER, "michaela", "michaela");
            socket = new ServerSocket(PORT, 0, InetAddress.getByName(null));
            handlers = new ArrayList<>();
            stats = new Statistics();
        }catch(IOException e){
            e.printStackTrace();
        }catch(ClassNotFoundException  ce){
            ce.printStackTrace();
        }catch(SQLException se){
            se.printStackTrace();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void notifyUpdate(int ID, String request, String oldRecord, String newRecord){
        
        for(int i = 0; i < handlers.size(); i++){
            if(i != ID){
                ConnectionHandler h = handlers.get(i);
                h.notify(request, oldRecord, newRecord);
            } 
        }  
    }
    
    public synchronized String handleStats(String request, String oldRecord, String newRecord){
        String result = "";
        
        Hashtable<String, Integer> countries = stats.getCountriesStats();
        Hashtable<String, Integer> sports = stats.getSportsStats();
        System.out.println(oldRecord);
        if(request.equals("DELETE")){
            String[] tokens = oldRecord.split(" ");
            if(tokens[3].equals("F")){
                stats.setWomen(stats.getCountWomen() - 1);
            }else{
                stats.setMen(stats.getCountMen() - 1);
            }
            
            stats.setParticipants(stats.getCountParticipants() - 1);
            stats.setHeight(stats.getHeight() - Float.parseFloat(tokens[5]));
            stats.setWeight(stats.getWeight() - Float.parseFloat(tokens[6]));
            
            stats.updateCountry(tokens[7], 0);
            if(tokens.length > 9){
                stats.updateSport(tokens[8] + " " + tokens[9], 0);
            }else{
                stats.updateSport(tokens[8], 0);
            }            
            return result;
        }
            
        result = stats.getCountParticipants() + " " + stats.getCountCountries() + " " +  stats.getCountSports() + 
                " " + stats.getCountMen() + " " + stats.getCountWomen() + " " + stats.getHeight() + " " + stats.getWeight();
        
        for(String key : countries.keySet()){
            result += " " + key + " " + countries.get(key);
        }
        
        for(String key : sports.keySet()){
            result += " " + key + " " + sports.get(key);
        }
        
        return result;
    }
    
    
    private void listen(){
        
        while(true){
            try{
                Socket client = socket.accept();
                ConnectionHandler handler = new ConnectionHandler(this, client, connection, handlers.size());
                handlers.add(handler);
                handler.start();
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }
    
    private void setUpStats(){
        String query = "SELECT * FROM PARTICIPANTS";
        float height = 0f, weight = 0f;
        String country = " ", sport = " ", gender = " ";
        try{
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            String result = "";
            while(resultSet.next()){
                gender = resultSet.getString("GENDER");
                height = resultSet.getFloat("HEIGHT");
                weight = resultSet.getFloat("WEIGHT");
                country = resultSet.getString("COUNTRY"); 
                sport = resultSet.getString("SPORT");
                stats.addParticipant(gender, height, weight, sport, country);
            }

        }catch(SQLException e){
            e.printStackTrace();           
        }
    }
    
    private void fillDatabase() throws Exception{
        BufferedReader TSVFile = new BufferedReader(new FileReader(tsvPath));
        String dataRow;
        String query = "INSERT INTO PARTICIPANTS (ID, NAME, GENDER, BIRTHDAY, HEIGHT, WEIGHT, SPORT, COUNTRY)"
                            + "VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
        
        float height = 0f, weight = 0f;
        String name = " ", country = " ", birthday = " ", sport = " ", gender = " ";
        int ID = 0;
        
        PreparedStatement pState = null;
        pState = connection.prepareStatement(query);
       
        while((dataRow = TSVFile.readLine()) != null){
            String[] tokens = dataRow.split(" ");
            for(int i = 0; i < tokens.length; i++){
                switch(i){
                    case 0: ID = Integer.parseInt(tokens[0]);
                        break;
                    case 1:
                        String tmp = tokens[1].substring(0, tokens[1].length()-1);
                        name = tmp + " " + tokens[2];
                        break;
                    case 2: gender = tokens[3];
                        break;
                    case 3: country = tokens[4];
                        break;
                    case 4: birthday = tokens[5];
                        break;
                    case 5: height = Float.parseFloat(tokens[6]);
                        break;
                    case 6: weight = Float.parseFloat(tokens[7]);
                        break;                         
                }

                if(tokens.length - 8 > 1){
                    sport = tokens[8] + " " +  tokens[9];
                }else{
                    sport = tokens[8];
                }
            }
            
            pState.setInt(1, ID);
            pState.setString(2, name);
            pState.setString(3, gender);
            pState.setString(4, birthday);
            pState.setFloat(5, height);
            pState.setFloat(6, weight);
            pState.setString(7, sport);
            pState.setString(8, country);
            pState.executeUpdate();
            stats.addParticipant(gender, height, weight, sport, country);
                    
        }
        pState.close();
        
    }
        
    private boolean isEmpty(){
        String query = "SELECT * FROM PARTICIPANTS";
        try{
            Statement st = connection.createStatement();
            ResultSet resultSet = st.executeQuery(query);
            
            if(resultSet.next()){
                resultSet.close();
                return true;
            }else{
                resultSet.close();
                return false;
            }
        }catch(SQLException s){
            s.printStackTrace();
        }
        return false;
    }
    
}
