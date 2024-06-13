package Database;

import java.sql.Connection;

public class ConnectionResponse extends DatabaseResponse{
    private final Connection connection;

    public ConnectionResponse(Connection connection, String errorMessage, DatabaseExitCode exitCode){
        super(errorMessage,exitCode);
        this.connection=connection;
    }
    public Connection getConnection(){
        return this.connection;
    }
}
