package records;

/**
 * This class is used for saving the different records in the table (users,package_id,status)
 */
public class Record {
    //----------------------------------------------------------------------
    // CONSTANTS
    //----------------------------------------------------------------------
    enum Status {
        PKT_EN_OFICINA,
        PKT_RECOGIDO,
        PKT_EN_CLASIFICACION,
        PKT_DESPACHADO,
        PKT_EN_ENTREGA,
        PKT_ENTREGADO,
        PKT_DESCONOCIDO
    }

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

    public Record(String username, int packageId, Status status) {
        this.username = username;
        this.packageId = packageId;
        this.status = status;
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
