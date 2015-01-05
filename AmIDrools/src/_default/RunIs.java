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
    static Vector woises = new Vector();
	
    public static void main( String args[] ) throws RemoteException, IOException
    {
        try {
        	inf = new Is();
        	inf.setTitle("Is");
        	inf.setSize(500, 500);
        	inf.setVisible(true);
        	inf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        	
            BufferedReader bf = new BufferedReader( new InputStreamReader( System.in ) );

            if (args.length >= 2)
                for (int i = 0; i < args[1].length(); ) {
                    int j = args[1].indexOf( ',', i );
                    if (j < 0) j = args[1].length();
                    Wois wois = new Wois( args[1].substring( i, j ) );
                    inf.register(wois, args[0]);
                    woises.add( wois );
                          
                    i = j + 1;
                }
            
            //Il sistema inferenziale è stato registrato, ora può iniziare a lavorare
            //while (true) {}
        } catch (Exception e) {
            //ERRORE
        } finally {
            if (inf != null) {
                //ERRORE
            }
        }
    }
}
