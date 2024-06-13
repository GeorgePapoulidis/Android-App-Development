package Database;

public abstract class DatabaseResponse {
    protected final String exitMessage;
    protected final DatabaseExitCode exitCode;
    public DatabaseResponse(String exitMessage, DatabaseExitCode databaseExitCode) {
        this.exitMessage = exitMessage;
        this.exitCode = databaseExitCode;
    }
    public String getExitMessage(){
        return this.exitMessage;
    }
    public void printExitMessage(){
        if (exitCode==DatabaseExitCode.Success){
            System.out.println(this.exitMessage);
        } else {
            System.err.println(this.exitMessage);
        }
    }
    public DatabaseExitCode getExitCode(){
        return this.exitCode;
    }
}
