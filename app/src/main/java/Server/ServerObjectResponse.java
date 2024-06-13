package Server;

import ModuleName.Store;
import ModuleName.Table;

/**
 * Extends ServerResponse to store a generic Object.
 * It is used when the ServerAPI endpoint must also return an object as a response.
 */
public class ServerObjectResponse<T> extends ServerResponse{
    /**
     * The ServerAPI return object.
     * It can be of any type. The type handling must be done be the client.
     */
    private final T data;

    /**
     * Initializes the class fields with the given parameters.
     * @param data The generic Object returned by the ServerAPI.
     * @param sec Encodes which state the ServerAPI request reached.
     */
    public ServerObjectResponse(T data, ServerExitCode sec){
        super(sec);
        this.data=data;
    }
    /**
     * @return The generic Object returned by the ServerAPI.
     */
    public T getData(){
        return data;
    }
}
