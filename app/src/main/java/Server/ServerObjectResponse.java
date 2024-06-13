package Server;

import ModuleName.Store;
import ModuleName.Table;

public class ServerObjectResponse<T> extends ServerResponse{
    private final T data;
    public ServerObjectResponse(T data, ServerExitCode sec){
        super(sec);
        this.data=data;
    }
    public T getData(){
        if (data instanceof Table){
            return (T) new Table((Table) data);
        } else if (data instanceof Store){
            return (T) new Store((Store) data);
        } else{
            return data;
        }
    }
}
