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
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import chatroomClient.ChatRegPage;
import chatroomClient.User;
import chatroomServer.RGCenterInterface;
import java.net.MalformedURLException;

import java.rmi.RemoteException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    public ChatRegPage cRegPage;
    
    public boolean connected;
    private boolean chatRegistated;
    private final int PORT = 4444;
    
    private ClientHandler handler;
    private String chosenRecord;
    
    private User user;
    private RGCenterInterface rgCenter;
        
    GUIController() throws NotBoundException, MalformedURLException, RemoteException {
        mPage = new MainPage();
        pPage = new PartPage();
        sPage = new StatsPage();
        aPage = new AddPage();
        cRegPage = new ChatRegPage();
        
        data = new ArrayList();
        updateList = new ConcurrentLinkedDeque<String>(); 
        
        rgCenter = (RGCenterInterface) Naming.lookup("rmi://localhost:8888/rgcenter");
        
        chatRegistated = false;
        user = new User(this);
    }
    
    @Override
    public void run(){
        int[] frameSize = new int[]{750, 700};
        startNewFrame(mPage, "Nordic Olympic Games", frameSize);
        eventListener();
    }
    
    
    public void serverResponse(String request, String answer){
        if(request.equals("GET RECORDS")){
            fillRecords(answer);
        }else if(request.equals("GET STATS")){
            setStats(answer);
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
        }else if(request.equals("POST ADD")){
            String newRecord = updateList.pollLast();
            data.add(newRecord);
            
            DefaultTableModel model = (DefaultTableModel) mPage.record_list.getModel();
            model.addRow(processData(newRecord));
            mPage.record_list.setModel(model);
            mPage.msgField.setText("You have added a new record!" + "\n" + "Record:" + "\n" + newRecord);
        }else if(request.equals("UPDATE")){
            // notification from changes that other clients has done
            String[] tokens = answer.split("\n");
            if(tokens[0].equals("DELETE")){
                int index = getRecord(tokens[1]);
                String record = data.get(index);
                data.remove(index);
                DefaultTableModel model = (DefaultTableModel) mPage.record_list.getModel();
                model.removeRow(index);
                mPage.record_list.setModel(model);
                mPage.msgField.setText("An update from the server has occured: " + "\n" + 
                        "Deleted record: " + "\n" + record);
            }else if(tokens[0].equals("UPDATE")){
                int index = getRecord(tokens[1].split(" ")[0]);
                String record = data.get(index);
                data.set(index, tokens[2]);
                DefaultTableModel model = (DefaultTableModel) mPage.record_list.getModel();
                model.removeRow(index);
                model.insertRow(index, processData(tokens[2]));
                mPage.record_list.setModel(model);
                mPage.msgField.setText("An update from the server has occured: " + "\n" + 
                        "Old record: " + "\n" + tokens[1] + "\n" + "Updated record: " + "\n" + tokens[2]);
            }else if(tokens[0].equals("ADD")){
                System.out.println(Arrays.toString(tokens));

                data.add(tokens[1]);
                DefaultTableModel model = (DefaultTableModel) mPage.record_list.getModel();
                model.addRow(processData(tokens[1]));
                mPage.record_list.setModel(model);
                mPage.msgField.setText("An update from the server has occured: " + "\n" + "New record: " + "\n" + answer);

            }      
            
        }
    }
    
    private int getRecord(String ID){
        
        for(int i = 0; i < data.size(); i++){
            System.out.println(data.get(i));
            String[] tokens = data.get(i).split(" ");
            if(tokens[0].equals(ID)){
                return i;
            }
        }
        return -1;
    }
    
    private int checkInputNewRecord(){
        String errorMsg = "";
        int error = 0;
                
        String pattern = "([a-zA-Z]+-?)*";
        Pattern r = Pattern.compile(pattern);
        String name = aPage.nameField.getText();
        String surname = aPage.surnameField.getText();
        Matcher m1 = r.matcher(name);
        Matcher m2 = r.matcher(surname);

        if(name.split(" ").length > 1 | surname.split(" ").length > 1){
            error = 1;
        }else if(!m1.matches() | !m2.matches()){
            error = 2;
        }

        String height = aPage.heightField.getText();
        String weight = aPage.weightField.getText();
        try{
            Float.parseFloat(height);
            Float.parseFloat(weight);
        }catch(NumberFormatException n){
            error = 3;
        }

        String country = aPage.countryBox.getSelectedItem().toString();
        String day = aPage.dayBox.getSelectedItem().toString();
        String month = aPage.monthBox.getSelectedItem().toString();
        String year = aPage.yearBox.getSelectedItem().toString();

        if(day.equals("Day") | month.equals("Month") | year.equals("Year")){
            error = 4;
        }
        
        return error;
    }
    
    private void eventListener(){
        
        aPage.addBtn.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                String errorMsg = "";
                String newRecord = "";
                if(!connected){
                    mPage.msgField.setText("You are not connected to the server!");
                }else{
                    int errorCode = checkInputNewRecord();
                    if(errorCode == 0){
                        errorMsg = "Everything looks good!";
                        newRecord = aPage.IDfield.getText() + " " + aPage.nameField.getText() + " " + aPage.surnameField.getText() + 
                                " " + aPage.genderBox.getSelectedItem().toString() + " " + 
                                aPage.yearBox.getSelectedItem().toString()  + "/" + aPage.monthBox.getSelectedItem().toString() + "/" + aPage.dayBox.getSelectedItem().toString() + 
                                " " + aPage.heightField.getText() + " " + aPage.weightField.getText() + " " + 
                                aPage.countryBox.getSelectedItem().toString() + " " + aPage.sportBox.getSelectedItem().toString();
                        System.out.println(newRecord);
                        if(connected){
                            String payLoad = newRecord;
                            updateList.addFirst(payLoad);
                            new Thread(new Runnable() {
                            @Override
                            public void run(){
                                handler.serverCall("POST ADD", payLoad);
                            }
                            }).start();
                        }else{
                            mPage.msgField.setText("You are not connected to the server!");
                        }
                    }else if(errorCode == 1){
                        errorMsg = "Please don't use spaces in the names. Add a - if there are several. ";
                    }else if(errorCode == 2){
                        errorMsg = "Illegal characters have been found in the name values. ";
                    }else if(errorCode == 3){
                        errorMsg = "You have wrongly given either the height or weight.";
                    }else{
                        errorMsg = "You have failed in providing a correct date. Please try again. ";
                    }
                    aPage.msgField.setText(errorMsg);
                }
            }
        });
        
        mPage.addBtn.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                int[] frameSize = new int[]{400, 500};
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
                    String request = "GET RECORDS";
                    if(connected){
                        new Thread(new Runnable() {
                        @Override
                        public void run(){
                            handler.serverCall(request, "");
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
        
        mPage.statsBtn.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                int[] frameSize = new int[]{750, 700};
                startNewFrame(sPage, "Statistics", frameSize);
                String request = "GET STATS";
                if(connected){
                    new Thread(new Runnable() {
                    @Override
                    public void run(){
                        handler.serverCall(request, "");
                    }
                    }).start();
                }else{
                    mPage.msgField.setText("You could not retrieve the statistics!");
                }
                
                mPage.statsBtn.setSelected(false);
            }
        });
        
        mPage.chatBtn.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                int[] frameSize = new int[]{400, 500};
                startNewFrame(cRegPage, "Put in user information", frameSize);
                mPage.chatBtn.setSelected(false);            
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
                    int newRow = row + 1;
                    if(row == data.size()-1){
                        newRow = row - 1;
                    }
                    DefaultTableModel model = (DefaultTableModel) pPage.recordTable.getModel();
                    model.removeRow(0);
                    Object[] ob = processData(data.get(newRow));
                    model.insertRow(0, ob);
                    pPage.recordTable.setModel(model);

                    // setup the server connection in the communication thread
                    String recordID = data.get(row).split(" ")[0];
                    String request = "DELETE";
                    String payLoad = recordID;
                    new Thread(new Runnable() {
                    @Override
                    public void run(){
                        handler.serverCall(request, payLoad);
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
                
                if(newRecord[0] == "false"){
                    pPage.textField.setText("Your input was not correct" + "\n" + "Remember height, weight, ID is numbers " + "\n" 
                                                + "And no numbers in the rest of the columns");
                }else if(newRecord[1].trim().equals(record)){
                    pPage.textField.setText("You have not updated the record." + "\n" + "Don't forget to press enter after editing" );
                }else{
                    if(connected){
                        String request = "POST";
                        String payLoad = record.split(" ")[0] + " " + newRecord[1];
                        String updatedRecord = row + " " + newRecord[1];
                        updateList.addFirst(updatedRecord);
                        new Thread(new Runnable() {
                        @Override
                        public void run(){
                            handler.serverCall(request, payLoad);
                        }
                        }).start(); 
                    }else{
                        pPage.textField.setText("You are not connected to the server!");
                    }
                    
                }
                pPage.updtBtn.setSelected(false);
            }
        });
        
        cRegPage.regBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                String name = cRegPage.nameField.getText();
                if(!chatRegistated){
                    if(name.length() > 0){
                        user.addName(name);

                        chatRegistated = true;
                        cRegPage.regBtn.setText("Unregister");
                        cRegPage.regBtn.setSelected(false);
                        try {
                            rgCenter.registerUser(user);
                            // TODO: register in registrationCenter!
                        } catch (RemoteException ex) {
                            Logger.getLogger(GUIController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }else {
                    chatRegistated = false;
                    cRegPage.regBtn.setText("Register");
                    cRegPage.regBtn.setSelected(false);
                    cRegPage.nameField.setText("");
                    
                    // TODO: unregister in registrationCenter!
                }
 
            }
        });
        
        cRegPage.regRoomBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                String chatRoom = cRegPage.roomTitleField.getText();
                if(chatRegistated){
                    
                }else {
                    // should not be able to create a room with remote interface
                    // notify user that it has not registered
                    System.out.println("user has not registated");
                }
            }
        });
    }   
    
    private void setStats(String stats){
        String[] tokens = stats.split("\n");
        
        Float avHeight = Float.parseFloat(tokens[5]) / Integer.parseInt(tokens[0]);
        Float avWeight = Float.parseFloat(tokens[6]) / Integer.parseInt(tokens[0]);
        
        sPage.partField.setText(tokens[0]);
        sPage.countryField.setText(tokens[1]);
        sPage.sportsField.setText(tokens[2]);
        sPage.menField.setText(tokens[3]);
        sPage.womenField.setText(tokens[4]);
        sPage.heightField.setText(String.valueOf(avHeight));
        sPage.weightField.setText(String.valueOf(avWeight));
        
        DefaultTableModel countryTable = new DefaultTableModel(){
            @Override
            public boolean isCellEditable(int row, int column){
                return false;
            }
        };
        countryTable.addColumn("COUNTRY");
        countryTable.addColumn("ATHLETES");

        int j = 0;
        for(int i = 7; i < 7 + 2 * Integer.parseInt(tokens[1]); i += 2){
            
            Object[] row = {tokens[i], tokens[i+1]};

            countryTable.insertRow(j, row);
            j++;
        }
        sPage.countryTable.setModel(countryTable);
        
        DefaultTableModel sportTable = new DefaultTableModel(){
            @Override
            public boolean isCellEditable(int row, int column){
                return false;
            }
        };
        sportTable.addColumn("SPORT");
        sportTable.addColumn("PRACTIONERS");
        int num;
        String sport;
        j = 0;
        for(int i = 7+2*Integer.parseInt(tokens[1]); i < 7+2*Integer.parseInt(tokens[1])+ 2*Integer.parseInt(tokens[2]) +1; i += 2){
            try{
                num = Integer.parseInt(tokens[i+1]);
                sport = tokens[i];
            }catch(NumberFormatException e){
                sport = tokens[i] + " " + tokens[i+1];
                i++;
                num = Integer.parseInt(tokens[i+1]);
            }
            Object[] row = {sport, num};
            System.out.println(sport + " " + num);

            sportTable.insertRow(j, row);
            j++;
        }
        sPage.sportTable.setModel(sportTable);
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
