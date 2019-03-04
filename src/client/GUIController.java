/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

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
        pPage = new PartPage();
        sPage = new StatsPage();
        connected = true;
    }
    
    private void inputListener(){
        
        
    }
    
}
