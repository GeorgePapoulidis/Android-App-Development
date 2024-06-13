package Database;

import java.sql.Connection;

/**
 * Extends DatabaseResponse to include a Connection object as part of the response from the database.
 */
public class ConnectionResponse extends DatabaseResponse{
    /**
     * A live connection to the database. It can be null when a connection failed to be established.
     */
    private final Connection connection;

    /**
     * Initializes the class fields with the given parameters.
     * @param connection The live connection to the database. Can be null when no connection was established.
     * @param exitMessage Contains information about the query execution at the database. It should be logged and it must be humanly readable.
     * @param databaseExitCode Encodes which state the query execution reached.
     */
    public ConnectionResponse(Connection connection, String exitMessage, DatabaseExitCode databaseExitCode){
        super(exitMessage,databaseExitCode);
        this.connection=connection;
    }

    /**
     * @return The currently save connection to the database.
     */
    public Connection getConnection(){
        return this.connection;
    }
}
