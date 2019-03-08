/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Michaela
 */
public class GUIController extends Thread {
    ArrayList<String> data;
    private MainPage mPage;
    private PartPage pPage;
    private StatsPage sPage;
    
    public boolean connected;
    private final int PORT = 4444;
    
    private ClientHandler handler;
        
    GUIController() {
        mPage = new MainPage();
        pPage = new PartPage();
        sPage = new StatsPage();
        connected = true;
        data = new ArrayList();       
    }
    
    @Override
    public void run(){
        int[] frameSize = new int[]{650, 600};
        startNewFrame(mPage, "Nordic Olympic Games", frameSize);
        eventListener();
    }
    
    private void eventListener(){
 
        mPage.cntBtn.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                String request = "GET";
                try{
                    Socket socket = new Socket(InetAddress.getByName(null), PORT);
                    handler = new ClientHandler(socket, GUIController.this);
                    handler.start();
                }catch(IOException es){
                    es.printStackTrace();
                }
                
                new Thread(new Runnable() {
                    @Override
                    public void run(){
                        handler.serverCall(request);
                    }
                }).start();
                mPage.cntBtn.setSelected(false);
            }
        });
    }
    
    public void serverResponse(String request, String answer){
        if(request.equals("GET")){
            fillRecords(answer);
        }
    }
    
    private void fillRecords(String answer){
        //try{
            DefaultTableModel recordTable = new DefaultTableModel(){
                @Override
                public boolean isCellEditable(int row, int column){
                    return false;
                }
            };
            recordTable.addColumn("ID");
            recordTable.addColumn("NAME");
            recordTable.addColumn("GENDER");
            recordTable.addColumn("BIRTHDAY");
            recordTable.addColumn("HEIGHT");
            recordTable.addColumn("WEIGHT");
            recordTable.addColumn("SPORT");
            recordTable.addColumn("COUNTRY");
            
            //System.out.println(answer);
            String[] tokens = answer.split("\n");
            for(int i = 0; i < tokens.length; i++){
                data.add(tokens[i]);
                Object[] row = processData(tokens[i]);
                recordTable.insertRow(i, row);
            }
            mPage.record_list.setModel(recordTable);
        //}
    }
    
    private Object[] processData(String output){
        String[] tokens = output.split(" ");
        String sport = " ";
        //System.out.println(Arrays.toString(tokens));
        if(tokens.length < 7 || tokens.length == 7){
            System.out.println(Arrays.toString(tokens));
        }
        if(tokens.length - 9  == 1){
            sport = tokens[8] + " " + tokens[9];
        }else{
            sport = tokens[8];
        }
        Object[] ob = {tokens[0], tokens[1] + " " + tokens[2], tokens[3], tokens[4], tokens[5], tokens[6], sport, tokens[7] };
        return ob;
    }
    

    private void startNewFrame(JPanel ob, String title, int[] frameSize){
        JFrame frame = new JFrame(title);
        frame.setContentPane(ob);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(frameSize[0], frameSize[1]);
        frame.setVisible(true);
    }
        
}
