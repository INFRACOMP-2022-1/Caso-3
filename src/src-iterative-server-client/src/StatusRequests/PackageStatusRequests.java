package StatusRequests;

public class PackageStatusRequests {
    //----------------------------------------------------------------------
    // ATTRIBUTES
    //----------------------------------------------------------------------

    /*
    The username attached to the package that is being searched
     */
    public String username;

    /*
    The package id of the searched package
     */
    public int packageId;

    //----------------------------------------------------------------------
    // CONSTRUCTOR
    //----------------------------------------------------------------------

    /**
     * Creates a package status request. It contains the username attached to a package and the package id of the searched package.
     * @param username the username attached to the searched package
     * @param packageId the package id of the searched package
     */
    public PackageStatusRequests(String username, int packageId){
        this.username = username;
        this.packageId = packageId;
    }

    //----------------------------------------------------------------------
    // METHODS
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


}
