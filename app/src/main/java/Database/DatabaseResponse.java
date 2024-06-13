package Database;


/**
 * An object that represents the outcome of the attempt to execute a query to the database.
 */
public abstract class DatabaseResponse {
    /**
     * Humanly readable text to allow logging and debugging of the outcome of the database query.
     */
    protected final String exitMessage;
    /**
     * Represents either success of the database query or encodes the problem that occurred.
     */
    protected final DatabaseExitCode exitCode;

    /**
     * Initializes the class fields with the given parameters.
     * @param exitMessage Contains information about the query execution at the database. It should be logged and it must be humanly readable.
     * @param databaseExitCode Encodes which state the query execution reached.
     */
    public DatabaseResponse(String exitMessage, DatabaseExitCode databaseExitCode) {
        this.exitMessage = exitMessage;
        this.exitCode = databaseExitCode;
    }

    /**
     * @return A textual representation of the result of the corresponding database query execution.
     */
    public String getExitMessage(){
        return this.exitMessage;
    }

    /**
     * Stores to the specified logging location, the outcome of the corresponding database query.
     */
    public void printExitMessage(){
        if (exitCode==DatabaseExitCode.Success){
            System.out.println(this.exitMessage);
        } else {
            System.err.println(this.exitMessage);
        }
    }

    /**
     * @return An encoded representation of the result of the corresponding database query execution.
     */
    public DatabaseExitCode getExitCode(){
        return this.exitCode;
    }
}
