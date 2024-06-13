package Database;

import java.sql.PreparedStatement;

public class StatementResponse extends DatabaseResponse{
    private final PreparedStatement statement;
    public StatementResponse(PreparedStatement statement, String errorMessage, DatabaseExitCode exitCode){
        super(errorMessage,exitCode);
        this.statement=statement;
    }

    public PreparedStatement getStatement(){
        return this.statement;
    }
}
