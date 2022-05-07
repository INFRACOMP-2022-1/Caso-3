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
    String csvFileName = "recordTable";

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

    //TODO: Toca probar esto
    //TODO: Metodo para cargar del csv todos los records
    public void load(){
        String line = "";
        String splitBy = ",";
        try{
            BufferedReader br = new BufferedReader(new FileReader(csvFileName +".csv"));
            while ((line = br.readLine()) != null)   //returns a Boolean value
            {
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

    //TODO: Metodo para buscar si un usuario dado existe en la tabla
    public boolean searchForUsername(String username){
        for(Record rec:recordList){
            if(rec.getUsername().toLowerCase().equals(username.toLowerCase())){
                return true;
            }
        }
        return false;
    }


    //TODO: Metodo para buscar si existe un paquete con el id dado asociado con un usuario (revisar con geovanny o harold si es asi)

    /**
     * Searches to check if a package with a certain packageId and username exists
     * @param username
     * @param packageId
     * @return
     */
    public String searchForPackage(String username,int packageId){

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

    //TODO: Documents
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

    //TODO: Search for status for given username and package

    /**
     * Gets a package status with the given id and associated username
     * @param username
     * @param packageId
     * @return
     */
    public String getPackageStatus(String username,String packageId){

        try{
            for(Record rec: recordList){
                if(rec.getUsername().toLowerCase() == username.toLowerCase()){
                    if(rec.getPackageId() == Integer.parseInt(packageId)){
                        return rec.getStatus().toString();
                    }
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return "";
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