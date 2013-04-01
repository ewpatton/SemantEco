package edu.rpi.tw.escience.semanteco.annotator;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.csvreader.CsvReader;

//import edu.rpi.tw.data.csv.CSVHeadersForAnnotator;

/**
 * Parses the first line of a csv file and prints each field one per line.
 * If -h parameter is given, parses that line instead (in cases where the header is not on the first line).
 * 
 * This is described at:
 * 
 * https://github.com/timrdf/csv2rdf4lod-automation/wiki/Generating-enhancement-parameters
 * https://github.com/timrdf/csv2rdf4lod-automation/wiki/FAQ
 */
public class CSVHeadersForAnnotator {
   
   private static Logger logger = Logger.getLogger(CSVHeadersForAnnotator.class.getName());
   
   public final static String USAGE = 
		   "usage: CSVHeaders <file> [--comment-character char] [--header-line headerLineNumber] "+
	                                "[--delimiter delimiter]    [--number]";   
   
   /**
    * 
    * @param args
 * @return 
    */
   public static List<String> getHeaders (String[] args) {
      List<String> headerList = new ArrayList<String>();
      if (args.length < 1) {
         System.out.println(CSVHeadersForAnnotator.USAGE);
         System.exit(1);
      }
      
      int arg    = 0;
      int length = args.length;
      
      String    csvFileLoc       = args[0]; arg++; length--;
      Character commentCharacter = null; // ? FTW!
      int       headerLine       = 1;
      char      delimiter        = ',';
      boolean   number           = false;
      

      //
      // [--comment-character char]
      //
      if( length >= 2 
    		  && "--comment-character".equals(args[arg]) 
    		  && !"--header-line".equals(args[arg+1])) {
         logger.fine("trying comment-character: "+args[arg+1]);
         try {
            commentCharacter = args[arg+1].charAt(0);
         }catch(Exception e) {
            e.printStackTrace();
         }
         arg += 2; length -=2;
      }
      

      //
      // [--header-line headerLineNumber]
      //
      if( length >= 2 && "--header-line".equals(args[arg]) ) {
         //System.err.println("trying header-line: "+args[arg+1]);
         try {
            headerLine = Math.max(Integer.parseInt(args[arg+1]),1);
         }catch(Exception e) {
            headerLine = 1;
         }
         arg += 2; length -=2;
      }


      //
      // [--delimiter delimiter]
      //
      //System.err.println(length + " " + arg + " " + args[arg]);
      if( length >=2 && "--delimiter".equals(args[arg]) ) {
         try{
            //System.err.println("trying --delimiter: ."+args[arg+1]+".  "+ "\\t".equals(args[arg+1]));
            delimiter = "\\t".equals(args[arg+1]) ? '\t' : args[arg+1].charAt(0);
            //System.err.println("going with ."+ delimiter+".");
         }catch(Exception e) {
            delimiter = ',';
         }
         arg += 2; length -=2;
      }
      
      //
      // [--number]
      //
      //System.err.println(length + " " + arg + " " + args[arg]);
      if( length >=1 && "--number".equals(args[arg]) ) {
         try{
            number = true;
         }catch(Exception e) {
         }
         arg += 1; length -=1;
      }
      
      
      logger.fine(csvFileLoc + " comment " + commentCharacter + " header " + headerLine + 
    		                   " delimiter" + delimiter);
      
      
      try {
         CsvReader csvReader = new CsvReader(new BufferedReader(new InputStreamReader(new FileInputStream(csvFileLoc))),
                                             delimiter);
      
         csvReader.setSkipEmptyRecords(false);

         if( commentCharacter != null ) {
            csvReader.setComment(commentCharacter);
            csvReader.setUseComments(true);
         }
         
         for( int i = 0; i < headerLine; i++ ) {
            csvReader.readRecord();
         }

         String[] headers = csvReader.getValues();
         for( int i = 0; i < headers.length; i++ ) {
            if( number ) System.out.print(i+1+" ");
            System.out.println(headers[i]);
            headerList.add(headers[i]);
         }
      } catch (FileNotFoundException e) {
         e.printStackTrace();
      } catch (IOException e) {
         e.printStackTrace();
      }
      return headerList;
   }
   
}