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
import java.util.Arrays;
import java.util.StringTokenizer;

/**
 *
 * @author Michaela
 */
public class ConnectionHandler extends Thread implements Serializable {
    private Server ConnectionPoint;
    private Socket client;
    private Connection connection;
    private BufferedReader in;
    private PrintWriter out;
    static final String OK = "HTTP/1.0 200 OK";
    static final String NO_CONTENT = "HTTP/1.0 204 NO CONTENT";
    static final String CREATED = "HTTP/1.0 201 CREATED";
    static final String UNPROCESSABLE = "HTTP/1.0 422 UNPROCESSABLE ENTITY";
    static final String NOT_FOUND = "HTTP/1.0 404 NOT FOUND";
    static final String UNAVAILABLE = "HTTP/1.0 503 SERVICE UNAVAILABLE";
    
    public ConnectionHandler(Server server, Socket client, Connection connection){
        this.ConnectionPoint = server;
        this.client = client;
        this.connection = connection;
    }
    
    public void run(){
        String str;
        try{
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            out = new PrintWriter(client.getOutputStream());
            
            while(true){
                str = in.readLine();
                System.out.println(str);
                if(str.equals("GET")){
                    out.write(OK);
                    out.write("\n");
                    out.flush();
                    if((str = in.readLine()).equals("HTTP/1.0 100 CONTINUE")){
                        System.out.println(str);
                        getRequest();
                    }
                }else if(str.split(" ")[0].equals("DELETE")){
                    String record = str.split(" ")[1];
                    out.write(OK);
                    out.write("\n");
                    out.flush();
                    if((str = in.readLine()).equals("HTTP/1.0 100 CONTINUE")){
                        System.out.println(record);
                        deleteRequest(record);
                    }
                }else if(str.split(" ")[0].equals("POST")){
                    String record = str;
                    out.write(OK);
                    out.write("\n");
                    out.flush();
                    if((str = in.readLine()).equals("HTTP/1.0 100 CONTINUE")){
                        System.out.println(record);
                        updateRequest(record);
                    }
                }
            }
        }catch(IOException e){
            e.printStackTrace();
        }
                
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
        String ID = record.split(" ")[0];
        String query = "DELETE FROM PARTICIPANTS WHERE ID=" + ID;
        try{
           Statement statement = connection.createStatement();
           statement.executeUpdate(query);
           statement.close();
        }catch(SQLException s){
            System.out.println("Could not remove record");
            out.write(NOT_FOUND);
            out.write("\n");
            out.flush();
        }
        System.out.println("removed record");
        out.write(NO_CONTENT);
        out.write("\n");
        out.flush();
    }
    
    private void updateRequest(String input){
        String[] tokens = input.split(" ");
        String oldID = tokens[1];
        System.out.println("tokens: " + Arrays.toString(tokens));
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
        }catch(SQLException s){
            System.out.println("could not update record");
            out.write(UNPROCESSABLE);
            out.write("\n");
            out.flush();
        }
        
    }
}
