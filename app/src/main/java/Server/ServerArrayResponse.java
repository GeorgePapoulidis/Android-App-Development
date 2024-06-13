package Server;

import java.util.ArrayList;

/**
 * Extends ServerResponse to store an ArrayList of generic Objects.
 * It is used when the ServerAPI endpoint must also return multiple objects of the same type as a response.
 */
public class ServerArrayResponse<T> extends ServerResponse {
    /**
     * The ServerAPI return ArrayList.
     * The type of objects stored in the ArrayList can be of any type, but each ArrayList can store only one type of object.
     * The type handling must be done be the client.
     */
    private final ArrayList<T> data;
    /**
     * Initializes the class fields with the given parameters.
     * @param data The ArrayList of generic Objects returned by the ServerAPI.
     * @param sec Encodes which state the ServerAPI request reached.
     */
    public ServerArrayResponse(ArrayList<T> data, ServerExitCode sec){
        super(sec);
        this.data=data;
    }
    /**
     * Return a copy of the ArrayList of generic Objects returned by the ServerAPI.
     * @return The generic Object ArrayList returned by the ServerAPI.
     */
    public ArrayList<T> getData(){
        if (this.data==null){
            return null;
        }
        return new ArrayList<>(this.data);
    }
}
