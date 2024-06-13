package Server;

/**
 * An object that represents the response of the ServerAPI when accessing one of its endpoints.
 */
public class ServerResponse {
    /**
     * Represents either uneventful access of the API endpoint or encodes the problem that occurred.
     * The ServerExitCode is usually related to a DatabaseResponse that was handled inside the ServerAPI method.
     */
    private final ServerExitCode sec;

    /**
     * Initializes the class fields with the given parameters.
     * @param sec Encodes which state the ServerAPI request reached.
     */
    public ServerResponse(ServerExitCode sec){
        this.sec=sec;
    }
    /**
     * @return The encoded result of the ServerAPI endpoint trigger.
     */
    public ServerExitCode getExitCode(){
        return this.sec;
    }
}
