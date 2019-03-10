/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ConcurrentLinkedDeque;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Michaela
 */
public class GUIController extends Thread {
    private ArrayList<String> data;
    private ConcurrentLinkedDeque<String> updateList;
    private MainPage mPage;
    private PartPage pPage;
    private StatsPage sPage;
    private AddPage aPage;
    
    public boolean connected;
    private final int PORT = 4444;
    
    private ClientHandler handler;
    private String chosenRecord;
        
    GUIController() {
        mPage = new MainPage();
        pPage = new PartPage();
        sPage = new StatsPage();
        aPage = new AddPage();
        
        data = new ArrayList();
        updateList = new ConcurrentLinkedDeque<String>(); 
    }
    
    @Override
    public void run(){
        int[] frameSize = new int[]{750, 700};
        startNewFrame(mPage, "Nordic Olympic Games", frameSize);
        eventListener();
    }
    
    
    public void serverResponse(String request, String answer){
        if(request.equals("GET")){
            fillRecords(answer);
        }else if(request.equals("DELETE")){
            int index = Integer.parseInt(updateList.pollLast().split(" ")[0]); 
            data.remove(index);
            DefaultTableModel model = (DefaultTableModel) mPage.record_list.getModel();
            model.removeRow(index);
            mPage.record_list.setModel(model);
            pPage.textField.setText("The record has successfuly been deleted.");
        }else if(request.equals("POST")){ 
            String record = updateList.pollLast();
            int index = Integer.parseInt(record.substring(0,1));
            String updatedRecord = record.substring(2);
            data.set(index, updatedRecord);
            
            DefaultTableModel model = (DefaultTableModel) mPage.record_list.getModel();
            model.removeRow(index);
            model.insertRow(index, processData(updatedRecord));
            mPage.record_list.setModel(model);
            pPage.textField.setText("The record was succesfully updated!");
        }
    }
    
    private void eventListener(){
        
        mPage.addBtn.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                int[] frameSize = new int[]{750, 300};
                startNewFrame(aPage, "Add Participant", frameSize);
                mPage.addBtn.setSelected(false);
            }
        });
        
        mPage.cntBtn.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                if(mPage.cntBtn.getText().equals("Connect")){
                    try{
                        Socket socket = new Socket(InetAddress.getByName(null), PORT);
                        handler = new ClientHandler(socket, GUIController.this);
                        handler.start();
                        mPage.cntBtn.setText("Update Records");
                        mPage.msgField.setText("You are now connected with server");
                        connected = true;
                    }catch(IOException es){
                         es.printStackTrace();
                    }
                }else{
                    String request = "GET";
                    if(connected){
                        new Thread(new Runnable() {
                        @Override
                        public void run(){
                            handler.serverCall(request);
                        }
                        }).start();
                    }else{
                        mPage.msgField.setText("You are not connected to the server!");
                    }
                }
                mPage.cntBtn.setSelected(false);
            }
        });
        
        mPage.discBtn.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){

                if(handler == null){
                    mPage.msgField.setText("You have not connected to the server yet!");
                }else if(handler.getStatus()){
                    handler.killThread();
                    mPage.cntBtn.setText("Connect");
                    mPage.msgField.setText("you are now disconnected from the server.");
                    connected = false;
                }
                mPage.discBtn.setSelected(false);
            }
        });
        
        mPage.record_list.addMouseListener(new MouseAdapter(){
            public void mousePressed(MouseEvent mouseEvent){
                Point point = mouseEvent.getPoint();
                int row = mPage.record_list.rowAtPoint(point);
                if(mouseEvent.getClickCount() == 2 ){
                    System.out.println(row);
                    
                    int[] frameSize = new int[]{750, 700};
                    startNewFrame(pPage, "Edit Participant", frameSize);
                    
                    DefaultTableModel recordTable = new DefaultTableModel(){
                        @Override
                        public boolean isCellEditable(int row, int column){
                            return true;
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
                    
                    
                    String tokens = data.get(row);
                    Object[] processRow = processData(tokens);
                    recordTable.insertRow(0, processRow);
                    pPage.setRow(row);
                    pPage.recordTable.setModel(recordTable);
                }
            }
        });
        
        pPage.nxtBtn.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                int row = pPage.getRow();
                int newRow = row + 1;
                if(row == (data.size() - 1)){
                    newRow = 0;
                }
                String record = data.get(newRow);
                pPage.setRow(newRow);
                DefaultTableModel model = (DefaultTableModel) pPage.recordTable.getModel();
                model.removeRow(0);

                Object[] ob = processData(data.get(newRow));
                model.insertRow(0, ob);
                pPage.recordTable.setModel(model);
                pPage.nxtBtn.setSelected(false);
            }
        });
        
        pPage.prevBtn.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                int row = pPage.getRow();
                int newRow = row - 1;
                if(row == 0){
                    newRow = data.size() - 1;
                }
                String record = data.get(newRow);
                pPage.setRow(newRow);
                DefaultTableModel model = (DefaultTableModel) pPage.recordTable.getModel();
                model.removeRow(0);

                Object[] ob = processData(data.get(newRow));
                model.insertRow(0, ob);
                pPage.recordTable.setModel(model);
                pPage.prevBtn.setSelected(false);
            }
        });
        
        pPage.delBtn.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                int row = pPage.getRow();
                
                if(connected){
                    // store request to do when server has sucessfully handle the request
                    String updateRequest = row + " " + -1;
                    updateList.addFirst(updateRequest);

                    // Adjust the GUI to visualize the deletion
                    DefaultTableModel model = (DefaultTableModel) pPage.recordTable.getModel();
                    model.removeRow(0);
                    Object[] ob = processData(data.get(row + 1));
                    model.insertRow(0, ob);
                    pPage.recordTable.setModel(model);

                    // setup the server connection in the communication thread
                    String recordID = data.get(row).split(" ")[0];
                    String request = "DELETE " + recordID;
                    new Thread(new Runnable() {
                    @Override
                    public void run(){
                        handler.serverCall(request);
                    }
                    }).start();
                }else{
                    pPage.textField.setText("You are not connected to the server!");
                }
                
                pPage.delBtn.setSelected(false);
            }
        });
        
        pPage.updtBtn.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                int row = pPage.getRow();
                String record = data.get(row);
                String[] newRecord = getEditedRecord(row);
                String n = newRecord[1];
                
                if(newRecord[0] == "false"){
                    pPage.textField.setText("Your input was not correct" + "\n" + "Remember height, weight, ID is numbers " + "\n" 
                                                + "And no numbers in the rest of the columns");
                }else if(newRecord[1].trim().equals(record)){
                    pPage.textField.setText("You have not updated the record." + "\n" + "Don't forget to press enter after editing" );
                }else{
                    if(connected){
                        String request = "POST " + record.split(" ")[0] + " " + newRecord[1];
                        String updatedRecord = row + " " + newRecord[1];
                        updateList.addFirst(updatedRecord);
                        new Thread(new Runnable() {
                        @Override
                        public void run(){
                            handler.serverCall(request);
                        }
                        }).start(); 
                    }else{
                        pPage.textField.setText("You are not connected to the server!");
                    }
                    
                }
                pPage.updtBtn.setSelected(false);
            }
        });

    }
    
    private String addRecord(){
        return "";
    }
    
    
    private String[] getEditedRecord(int row){
        
        String value = "";
        String editedRecord = "";
        boolean valid = true;
        for(int i = 0; i < pPage.recordTable.getColumnCount(); i++){
            value = pPage.recordTable.getValueAt(0, i).toString();
            switch(i){
                case 0: 
                    try{
                        Integer.parseInt(value);
                    }catch(NumberFormatException n){
                        n.printStackTrace();
                        valid = false;                        
                    }
                    break;
                case 1:
                    if(value.matches(".*\\d+.*")){
                        valid = false;
                    }
                    break;
                case 2:
                    if(value.matches(".*\\d+.*") || value.length() > 1){
                        valid = false;
                    }
                    break;
                case 3:
                    if(value.matches("a-zA-Z")){
                        valid = false;
                    }
                    break;
                case 4:
                    try{
                        Float.parseFloat(value);
                    }catch(NumberFormatException e){
                        valid = false;
                    }
                    break;
                case 5:
                    try{
                        Float.parseFloat(value);
                    }catch(NumberFormatException e){
                        valid = false;
                    }
                    break;
                case 6:
                    if(value.matches(".*\\d+.*")){
                        valid = false;
                    }
                    break;
                case 7:
                    if(value.matches(".*\\d+.*")){
                        valid = false;
                    }
                    break;
            }
            editedRecord += value + " ";
        }
        String[] returnValues = {String.valueOf(valid), editedRecord};
        return returnValues;
    }
    
    private void fillRecords(String answer){
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
            recordTable.addColumn("COUNTRY");
            recordTable.addColumn("SPORT");
            
            String[] tokens = answer.split("\n");
            for(int i = 0; i < tokens.length; i++){
                data.add(tokens[i]);
                Object[] row = processData(tokens[i]);
                
                recordTable.insertRow(i, row);
            }
            mPage.record_list.setModel(recordTable);
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
        Object[] ob = {tokens[0], tokens[1] + " " + tokens[2], tokens[3], tokens[4], tokens[5], tokens[6], tokens[7], sport};
        return ob;
    }

    private void startNewFrame(JPanel ob, String title, int[] frameSize){
        JFrame frame = new JFrame(title);
        frame.setContentPane(ob);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(frameSize[0], frameSize[1]);
        frame.setVisible(true);
    }
        
}
