package cli;

/**
 * 
 */

import clinterface.CLI;
import music.DJ;

/**
 * @author abrahajoav
 *
 */
public class ServerSideController {
	
    public static void main(String[] args) {
    	if (args.length == 0) {
    		new CLI(new DJ());
    	}
    	else {
    		String combinedArgs = "";
    		for(String s : args) {
    			combinedArgs += " " + s;
    		}
    		combinedArgs = combinedArgs.trim();
    		System.out.println(combinedArgs);
    		new CLI(new DJ(), combinedArgs);
    	}
    }
    
}
