package Server;

public class ServerResponse {
    private final ServerExitCode sec;
    public ServerResponse(ServerExitCode sec){
        this.sec=sec;
    }
    public ServerExitCode getExitCode(){
        return this.sec;
    }
}
