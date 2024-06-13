package ModuleName;


import Server.Server;
import Server.ServerArrayResponse;
import Server.ServerObjectResponse;
import Server.ServerResponse;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;


public class Platform {

    HashSet<Store> stores=new HashSet<Store>();
    HashSet<Owner> owners=new HashSet<Owner>();
    static Server myServer=new Server();
    public static String addStore(String name,BigInteger userID,Integer x, Integer y){
       if (name==null){
           return "NullStoreName";
       }

       if(userID==null){
           return "NullOwnerID";
       }

       if (x<0){
           return "InvalidXDimension";
       }

       if(y<0){
           return "InvalidYDimension";
       }

       ServerResponse zing=myServer.addStore(name,userID,x,y);
       return String.valueOf(zing.getExitCode());

    }

    //koita tin methodo auti kalutera. AddTable alla den uparxei tableName. Einai logiko auto? oxi. pos egine?
    //Ekana ego lathosm, kai me akolouthises tufla, xoris na skefteis. Dont do that

    public String addTable(String tableName,Integer x,Integer y,Integer people,
        StateOfTable state,String storeName, BigInteger userID){
        if(tableName==null){
            return "NullTableName";
        } else if (tableName.length()>8) {
            return "TableNameTooLong";
        }
        if (state==null){
            return "NullTableState";
        }
        if (userID==null){
            return "NullUserID";
        }
        if (storeName==null){
            return "NullStoreName";
        }

        if (x<0){
            return "InvalidXDimension";
        }
        if (y<0){
            return "InvalidYDimension";
        }
        if (people<=0){
            return "InvalidPeopleNumber";
        }

        ServerResponse zing=myServer.addTable(storeName,x,y,people,state,storeName,userID);
        return String.valueOf(zing.getExitCode());
    }




    public static String addUser(String fullName,String username,String password,String email){
        if(fullName==null){
            return "NullFullName";
        } else if (fullName.length()>32) {
            return "FullNameTooLong";
        }

        if(username==null){
            return "NullUserName";
        } else if (username.length()>32) {
            return "UserNameTooLong";
        }

        if (password==null){
            return "NullPassword";
        } else if (password.length()<8) {
            return "SmallPassword";
        }

        if(email==null){
            return "NullEmail";
        }

        ServerResponse zing=myServer.addUser(fullName, username, password, email);
        return String.valueOf(zing.getExitCode());
    }

    public static String searchStore(BigInteger userID,HashMap<String,Boolean> options){
        if(userID==null){
            return "NullUserID";
        }

        ServerArrayResponse<Store> zing=myServer.getStores(userID, options);
        return String.valueOf(zing.getExitCode());
    }

    public static String logIn(String username,String password){
        if (username==null){
            return "NullUserName";
        }
        if (password==null){
            return "NullPassword";
        }


        ServerObjectResponse<BigInteger> zing=myServer.logIn(username,password);
        return String.valueOf(zing.getExitCode());

        /**
         * Ελεγχος στο gui για το τι ServerExitCode εχουμε και αναλογο poppaki.
         */
    }

    public String removeCurrentUser(BigInteger userID){
        if(userID==null){
            return "NullUserID";
        }
        ServerResponse zing=myServer.removeCurrentUser(userID);
        return String.valueOf(zing.getExitCode());
        /**
         * Ελεγχος στο gui για το τι ServerExitCode εχουμε και αναλογο poppaki.
         */
    }

    public String removeStore(String storeName,BigInteger userID){
        //Ebala na xreiazetai storeName anti gia storeID
        if(userID==null){
            return "NullUserID";
        }
        if(storeName==null){
            return "NullStoreName";
        }

        ServerResponse zing=myServer.removeStore(storeName,userID);
        return String.valueOf(zing.getExitCode());
    }

    public String removeTable(String tableName,String storeName, BigInteger userID){
        //Edo prosthesa ego to storeName
        //Ebala na xreiazetai tableName anti gia tableID
        if(storeName==null){
            return "NullStoreName";
        }
        if(userID==null){
            return "NullUserID";
        }
        if(tableName==null){
            return "NullTableID";
        }
        ServerResponse zing=myServer.removeTable(tableName,storeName,userID);
        return String.valueOf(zing.getExitCode());
    }

    public String getStore(String storeName, BigInteger user, HashMap<String,Boolean> options){
        //Ebala na xreiazetai storeName anti gia storeID
        if (storeName==null) {
            return "NullStoreID";
        }
        if(user==null){
            return "NullUserID";
        }
        ServerObjectResponse<Store> zing=myServer.getStore(storeName,user,options);
        return String.valueOf(zing.getExitCode());
    }

    public String getTables(String storeName,BigInteger userID,HashMap<String,Boolean> options){
        if (storeName==null){
            return "NullStoreID";
        }
        if(userID==null){
            return "NullUserID";
        }
        ServerArrayResponse<Table> zing=myServer.getTables(storeName, userID, options);
        return String.valueOf(zing.getExitCode());
    }

    /*
    public String changeTableState(BigInteger storeID,StateOfTable newState){
        if (storeID==null){
            return "NullTableID";
        }
        if (newState==null){
            return "NullTableState";
        }

        ServerResponse zing=myServer.changeTableState(storeID, newState);
        return String.valueOf(zing.getExitCode());
    }

     */
/*
    public String changeStoreName(BigInteger storeID,String newName){
        if (storeID==null){
            return "NullTableID";
        }
        if (newName==null){
            return "NullStoreName";
        }

        ServerResponse zing=myServer.changeStoreName(storeID, newName);
        return String.valueOf(zing.getExitCode());
    }

 */

    public void printStoresData(){
        for(Store zing:this.stores){
            zing.printStore();
            System.out.println();
        }
    }

    public void printOwnerData(){
        for(Owner zing:this.owners){
            zing.printOwner();
            //System.out.println();
        }
    }

    public HashSet<Store> getStores() {
        return this.stores;
    }

    public void setStores(HashSet<Store> stores) {
        this.stores = stores;
    }

    public HashSet<Owner> getUsers() {
        return this.owners;
    }

    public void setUsers(HashSet<Owner> owners) {
        this.owners = owners;
    }
}
