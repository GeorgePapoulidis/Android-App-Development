package Database;

import java.sql.PreparedStatement;

/**
 * Extends DatabaseResponse to include data given as output from the database, for a query that has return data.
 */
public class StatementResponse extends DatabaseResponse{
    /**
     * Used for parsing all the data given as output from the corresponding database query execution.
     */
    private final PreparedStatement statement;
    /**
     * Initializes the class fields with the given parameters.
     * @param statement Parses the output of the database query.
     * @param exitMessage Contains information about the query execution at the database. It should be logged and it must be humanly readable.
     * @param databaseExitCode Encodes which state the query execution reached.
     */
    public StatementResponse(PreparedStatement statement, String exitMessage, DatabaseExitCode databaseExitCode){
        super(exitMessage,databaseExitCode);
        this.statement=statement;
    }

    /**
     * @return A PreparedStatement that can be used for parsing all data from the response of the database.
     */
    public PreparedStatement getStatement(){
        return this.statement;
    }
}
