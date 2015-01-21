package _default;

import java.awt.Toolkit;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.RemoteException;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JFrame;

import ami_drools.Is;
import ami_drools.Wois;


public class RunIs {

	static Is inf = null;
	
    public static void main( String args[] ) throws RemoteException, IOException
    { 
        try {
        	//inf = new Is(args[0].toString());
        	inf = new Is(args[0], args[1]);
        	inf.setTitle("Is " + args[0]);
        	inf.setSize(500, 600);
        	inf.setVisible(true);
        	inf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        	inf.resizeLogoUnibs("images/Logo_unibs.gif",150,150);
        	inf.resizeButton();
        } catch (Exception e) {
            //ERRORE
        } finally {
            if (inf != null) {
                //ERRORE
            }
        }
    }
}
