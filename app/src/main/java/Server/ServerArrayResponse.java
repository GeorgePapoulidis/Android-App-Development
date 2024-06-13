package Server;

import java.util.ArrayList;

public class ServerArrayResponse<T> extends ServerResponse {
    private final ArrayList<T> data;
    public ServerArrayResponse(ArrayList<T> data, ServerExitCode sec){
        super(sec);
        this.data=data;
    }
    public ArrayList<T> getData(){
        if (this.data==null){
            return null;
        }
        return new ArrayList<>(this.data);
    }
}
