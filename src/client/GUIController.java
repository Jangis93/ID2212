/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author Michaela
 */
public class GUIController {
    private MainPage mPage;
    private PartPage pPage;
    private StatsPage sPage;
    
    public boolean connected;
    
    GUIController(){
        mPage = new MainPage();
        startNewFrame(mPage, "Nordic Olympic Games");
        pPage = new PartPage();
        sPage = new StatsPage();
        connected = true;
    }
    

    
    private void inputListener(){
        
        
    }
    
    private void startNewFrame(JPanel ob, String title){
        JFrame frame = new JFrame(title);
        frame.setContentPane(ob);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(650, 600);
        frame.setVisible(true);
    }
    
    
    public MainPage getMpage(){
        return this.mPage;
    }
    
    public PartPage getPpage(){
        return this.pPage;
    }
        
    public StatsPage getSpage(){
        return this.sPage;
    }
    
}
