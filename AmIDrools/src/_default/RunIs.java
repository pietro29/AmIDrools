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

import ami_drools.Is;
import ami_drools.Wois;


public class RunIs {

	static Is inf = null;
    static Vector woises = new Vector();
	
    public static void main( String args[] ) throws RemoteException, IOException
    {
        try {
        	inf = new Is();
            BufferedReader bf = new BufferedReader( new InputStreamReader( System.in ) );

            if (args.length >= 2)
                for (int i = 0; i < args[1].length(); ) {
                    int j = args[1].indexOf( ',', i );
                    if (j < 0) j = args[1].length();
                    Wois wois = new Wois( args[1].substring( i, j ) );
                    inf.register(wois, args[0]);
                    woises.add( wois );
                    
                    
                  //Importo le regole relative a quella determinata wois
                    String name = "//" + args[1].substring( i, j ) + "/FileServer";
                    File file = new File(args[1].substring( i, j ));
                    byte[] filedata = wois.downloadFile(name);
                    BufferedOutputStream output = new
                            BufferedOutputStream(new FileOutputStream(file.getName()));
                          output.write(filedata,0,filedata.length);
                          output.flush();
                          output.close();
                    //
                          
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
