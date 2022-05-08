package Records;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Contains all the records(username, packageId, status) and adds a series of methods to make easier to add and look up packages
 */
public class RecordList {
    //----------------------------------------------------------------------
    // ATTRIBUTES
    //----------------------------------------------------------------------

    /*
    A list of records.
     */
    ArrayList<Record> recordList ;

    /*
    The file path of the csv with all the information
     */
    String csvFileName = "src/src-concurrent-server-client/src/Records/recordTable";

    //----------------------------------------------------------------------
    // CONSTRUCTOR
    //----------------------------------------------------------------------

    /**
     * Constructor for a record list
     */
    public RecordList() {
        this.csvFileName = csvFileName;
        this.recordList = new ArrayList<Record>();
    }

    //----------------------------------------------------------------------
    // METHODS
    //----------------------------------------------------------------------

    /**
     * Loads the csv containing the existing records.
     * NOTE: The CSV need to be separated by , (some systems use ';' )
     */
    public void load(){
        String line = "";
        String splitBy = ",";
        try{
            BufferedReader br = new BufferedReader(new FileReader(csvFileName +".csv"));
            while ((line = br.readLine()) != null){
                String[] splitedLine = line.split(splitBy);    // use comma as separator
                String username = splitedLine[0];
                String packageId = splitedLine[1];
                String status = splitedLine[2];

                Record newRecord = new Record(username,packageId,status);
                recordList.add(newRecord);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks if a given username exists in the record list table
     * @param username the username to be searched
     * @return True if the searched username exists
     */
    public boolean searchForUsername(String username){
        for(Record rec:recordList){
            if(rec.getUsername().equalsIgnoreCase(username)){
                return true;
            }
        }
        return false;
    }

    /**
     * Searches if a package with the given id exists on the record list
     * @param packageId integer containing a possible package id
     * @return true if a package id with that number exists, false the contrary
     */
    public boolean searchForPackageId(int packageId){
        for(Record rec: recordList){
            if(rec.getPackageId() == packageId){
                return true;
            }
        }
        return false;
    }

    /**
     * Searches to check if a package with a certain packageId and username exists
     * @param username the username associated with the package
     * @param packageId the package id associated with a package
     * @return String containing the status of the package
     */
    public String searchForPackage(String username,int packageId) {
        try{
            for(Record rec: recordList){
                if(rec.getUsername().toLowerCase() == username.toLowerCase()){
                    if(rec.getPackageId() == packageId){
                        return rec.getStatus().toString();
                    }
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return "FAILURE";//IT SHOULD NEVER REACH THIS BECAUSE IT WAS ALREADY CHECKED THAT THE PACKAGE EXISTS
    }

    /**
     * Checks if a package with a give username and package id exists in the recordList
     * @param username the username associated with the package
     * @param packageId the package id associated with the package
     * @return True if there exists a package associated with that username and package id, False the contrary
     */
    public boolean checkIfPackageExists(String username,int packageId){

        try{
            for(Record rec: recordList){
                if(rec.getUsername().toLowerCase() == username.toLowerCase()){
                    if(rec.getPackageId() == packageId){
                        return true;
                    }
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return false;
    }

    //----------------------------------------------------------------------
    // GETTERS AND SETTERS
    //----------------------------------------------------------------------

    public ArrayList<Record> getRecordList() {
        return recordList;
    }

    public void setRecordList(ArrayList<Record> recordList) {
        this.recordList = recordList;
    }

    public String getCsvFileName() {
        return csvFileName;
    }

    public void setCsvFileName(String csvFileName) {
        this.csvFileName = csvFileName;
    }
}
