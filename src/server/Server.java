/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author Michaela
 */
public class Server {
    private final int PORT = 4444;
    private ServerSocket socket; 
    
    static final String JDBC_DRIVER = "jdbc:derby://localhost:1527/Members";
    static protected Connection connection;
    
    
    
    public static void main(String[] args){
        Server server = new Server();
        server.listen();
    }
    
    public Server(){
        
        try{
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
            connection = DriverManager.getConnection(JDBC_DRIVER, "michaela", "michaela");
            socket = new ServerSocket(PORT, 0, InetAddress.getByName(null));
            System.out.println("Created server");
        }catch(IOException e){
            e.printStackTrace();
        }catch(ClassNotFoundException  ce){
            ce.printStackTrace();
        }catch(SQLException se){
            se.printStackTrace();
        }
    }
    
    private void listen(){
        
        while(true){
            System.out.println("running");
            try{
                if(socket != null){
                    Socket client = socket.accept();
                    
                }
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }
}
