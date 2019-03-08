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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
                    if((str = in.readLine()).equals("OK")){
                        System.out.println(str);
                        getRequest();
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
                System.out.println(result);
                out.write(result);
                out.write("\n");
                out.flush();
            }
            out.write("\n");
            out.flush();
        }catch(SQLException e){
            e.printStackTrace();
        }
    }
}
