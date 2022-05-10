package Records;

/**
 * This class is used for saving the different records in the table (users,package_id,status)
 */
public class Record {
    //----------------------------------------------------------------------
    // ATTRIBUTES
    //----------------------------------------------------------------------

    /*
    The username associated to a given package order
     */
    public String username;

    /*
    The package id given to the order
     */
    public int packageId;

    /*
    The status of a package
     */
    public Status status;

    //----------------------------------------------------------------------
    // CONSTRUCTOR
    //----------------------------------------------------------------------

    /**
     * A record containing information on a package, its id and the username associated to it
     * @param username
     * @param packageId
     * @param status
     */
    public Record(String username, String packageId, String status) {
        this.username = username;
        this.packageId = strToIntPackageId(packageId);
        this.status = strToStatus(status);
    }

    //----------------------------------------------------------------------
    // METHODS
    //----------------------------------------------------------------------

    /**
     * Converts a string related to a package id into an integer
     * @param packageId
     * @return
     */
    public int strToIntPackageId(String packageId){
        int number = -1;
        try{
            number = Integer.parseInt(packageId);
        }
        catch (NumberFormatException ex){
            ex.printStackTrace();
        }
        return number;
    }

    /**
     * Converts a status that is a string into a Status enum value
     * @param strStatus
     * @return
     */
    public Status strToStatus(String strStatus){
        if(strStatus.equals(Status.PKT_DESCONOCIDO.toString())){
            return Status.PKT_DESCONOCIDO;
        }
        else if(strStatus.equals(Status.PKT_DESPACHADO.toString())){
            return Status.PKT_DESPACHADO;
        }
        else if(strStatus.equals(Status.PKT_EN_CLASIFICACION.toString())){
            return Status.PKT_EN_CLASIFICACION;
        }
        else if(strStatus.equals(Status.PKT_EN_ENTREGA.toString())){
            return Status.PKT_EN_ENTREGA;
        }
        else if(strStatus.equals(Status.PKT_EN_OFICINA.toString())){
            return Status.PKT_EN_OFICINA;
        }
        else if(strStatus.equals(Status.PKT_ENTREGADO.toString())){
            return Status.PKT_ENTREGADO;
        }
        else{
            return Status.PKT_RECOGIDO;
        }
    }

    //----------------------------------------------------------------------
    // GETTERS AND SETTERS
    //----------------------------------------------------------------------


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getPackageId() {
        return packageId;
    }

    public void setPackageId(int packageId) {
        this.packageId = packageId;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
