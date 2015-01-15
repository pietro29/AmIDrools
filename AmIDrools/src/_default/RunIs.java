package _default;

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
        	inf = new Is(args[0].toString());
        	inf.setTitle("Is " + args[0].toString());
        	inf.setSize(500, 500);
        	inf.setVisible(true);
        	inf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        } catch (Exception e) {
            //ERRORE
        } finally {
            if (inf != null) {
                //ERRORE
            }
        }
    }
}
