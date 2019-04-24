/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringTokenizer;

/**
 *
 * @author Michaela
 */
public class ConnectionHandler extends Thread implements Serializable {
    private Server connectionPoint;
    private Socket client;
    private Connection connection;
    private BufferedReader in;
    private PrintWriter out;
    private final int ID;
    static final String OK = "HTTP/1.0 200 OK";
    static final String NO_CONTENT = "HTTP/1.0 204 NO CONTENT";
    static final String CREATED = "HTTP/1.0 201 CREATED";
    static final String UNPROCESSABLE = "HTTP/1.0 422 UNPROCESSABLE ENTITY";
    static final String NOT_FOUND = "HTTP/1.0 404 NOT FOUND";
    static final String UNAVAILABLE = "HTTP/1.0 503 SERVICE UNAVAILABLE";
    static final String UPDATE = "HTTP/1.0 UPDATE";
    
    
    public ConnectionHandler(Server server, Socket client, Connection connection, int ID){
        this.connectionPoint = server;
        this.client = client;
        this.connection = connection;
        this.ID = ID;
    }
    
    public void run(){
        String str;
        try{
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            out = new PrintWriter(client.getOutputStream());
            
            while(true){
                str = in.readLine();
                StringTokenizer st = new StringTokenizer(str);
                String method = st.nextToken();
                String resource = st.nextToken();
                System.out.println(ID + " " + str);
                if(method.equals("GET")){
                    out.write(OK);
                    out.write("\n");
                    out.flush();
                    if((str = in.readLine()).equals("HTTP/1.0 100 CONTINUE")){
                        if(resource.equals("RECORDS")){
                            getRequest();
                        }else if(resource.equals("STATS")){
                            getStats();
                        }
                    }
                }else if(method.equals("DELETE")){
                    String record = resource;
                    out.write(OK);
                    out.write("\n");
                    out.flush();
                    if((str = in.readLine()).equals("HTTP/1.0 100 CONTINUE")){
                        //System.out.println(record);
                        String oldRecord = getRecord(record);
                        deleteRequest(record);
                        connectionPoint.notifyUpdate(ID, "DELETE", record, record);
                        //connectionPoint.updateStats(record, oldRecord, "");
                    }
                }else if(method.equals("POST")){
                    String record = str;
                    out.write(OK);
                    out.write("\n");
                    out.flush();
                    if((str = in.readLine()).equals("HTTP/1.0 100 CONTINUE")){
                        //System.out.println(record);
                        String[] tokens = record.split(" ");
                        String oldRecord = getRecord(tokens[1]);
                        String newRecord = record.substring(5+tokens[1].length()+1);
                        updateRequest(record);
                        connectionPoint.notifyUpdate(ID, "UPDATE", oldRecord, newRecord);
                        //connectionPoint.updateStats(record, oldRecord, newRecord);
                    }
                }
            }
        }catch(IOException e){
            e.printStackTrace();
        }
                
    }
    
    private String getRecord(String ID){
        String query = "SELECT * FROM PARTICIPANTS WHERE ID=" + ID;
        String result = "";
        try{
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            if(resultSet.next()){
                result = resultSet.getInt("ID") + " " + resultSet.getString("NAME") + " " + resultSet.getString("GENDER") + " " + resultSet.getString("BIRTHDAY") + " "
                            + resultSet.getFloat("HEIGHT") + " " + resultSet.getFloat("WEIGHT") + " " + resultSet.getString("COUNTRY") + " " + resultSet.getString("SPORT");
            }
        }catch(SQLException e){
            e.printStackTrace();       
        }
        return result;
    }
    
    public void notify(String request, String oldRecord, String newRecord){
        //System.out.println(ID + " Notifying!");
        out.write(UPDATE);
        out.write("\n");
        out.flush();
        out.write(request);
        out.write("\n");
        out.flush();
        out.write(oldRecord);
        out.write("\n");
        out.flush();
        out.write(newRecord);
        out.write("\n");
        out.flush();
        out.write("\n");
        out.flush();
    }
    
    private void getStats(){
        
    }
    
    /*
    Sends all information that exists in database to client
    */
    private void getRequest(){
        String query = "SELECT * FROM PARTICIPANTS";
        try{
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            String result = "";
            while(resultSet.next()){
                result = resultSet.getInt("ID") + " " + resultSet.getString("NAME") + " " + resultSet.getString("GENDER") + " " + resultSet.getString("BIRTHDAY") + " "
                            + resultSet.getFloat("HEIGHT") + " " + resultSet.getFloat("WEIGHT") + " " + resultSet.getString("COUNTRY") + " " + resultSet.getString("SPORT");
                out.write(result);
                out.write("\n");
                out.flush();
            }
            out.write("\n");
            out.flush();
        }catch(SQLException e){
            e.printStackTrace();
            out.write(UNAVAILABLE);
            out.write("\n");
            out.flush();            
        }

    }
    
    
    private void deleteRequest(String record){
        String ID = record;
        String query = "DELETE FROM PARTICIPANTS WHERE ID=" + ID;
        try{
           Statement statement = connection.createStatement();
           statement.executeUpdate(query);
           connection.commit();
           statement.close();
        }catch(SQLException s){
            out.write(NOT_FOUND);
            out.write("\n");
            out.flush();
        }
        out.write(NO_CONTENT);
        out.write("\n");
        out.flush();
        
    }
    
    private void updateRequest(String input){
        String[] tokens = input.split(" ");
        String oldID = tokens[1];
        String sport = "";
        
        String query = "UPDATE PARTICIPANTS SET ID = ?, NAME = ?, GENDER=?, BIRTHDAY=?, HEIGHT=?, WEIGHT=?, SPORT=?, COUNTRY=? " + 
                            "WHERE ID = " + oldID;
        
        try{          
            PreparedStatement pState = connection.prepareStatement(query); 
            pState.setInt(1, Integer.parseInt(tokens[2]));
            pState.setString(2, tokens[3] + " " + tokens[4]);
            pState.setString(3, tokens[5]);
            pState.setString(4, tokens[6]);
            pState.setFloat(5, Float.parseFloat(tokens[7]));
            pState.setFloat(6, Float.parseFloat(tokens[8]));

            if(tokens.length - 10 > 1 ){
                sport = tokens[10] + " " +  tokens[11];
            }else{
                sport = tokens[10];
            }
            pState.setString(7, sport);
            pState.setString(8 , tokens[9]);
            pState.executeUpdate();
            out.write(CREATED);
            out.write("\n");
            out.flush();
            pState.close();
        }catch(SQLException s){
            System.out.println("could not update record");
            out.write(UNPROCESSABLE);
            out.write("\n");
            out.flush();
        }
        
    }
}
