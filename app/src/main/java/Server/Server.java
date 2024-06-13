package Server;

import Database.*;
import ModuleName.StateOfTable;
import ModuleName.Store;
import ModuleName.Table;
import org.jetbrains.annotations.NotNull;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Server {
    public final Connection connection;
    public Server(){
        ConnectionResponse response = DatabaseAPI.connect(null);
        this.connection = response.getConnection();
    }
    public void closeConnection(){
        try {
            this.connection.close();
        } catch (SQLException ignored){
        }
    }

    /**
     * Check the input of this method for invalid input, guaranteeing that the Database will be able to handle the data
     * given to it. Return appropriate ServerExitCodes for each type of logIn failure.
     * @param username The unique username that will be used to log in.
     * @param password The password that is supposed to match with the username.
     * @return Return a wrapper Object that includes a ServerExitCode. If the login was successful, a BigInteger will
     * be included that contains the private ID of the logged-in user.
     */
    public ServerObjectResponse<BigInteger> logIn(@NotNull String username, @NotNull String password){
        if (username==null){
            return new ServerObjectResponse<BigInteger>(null,ServerExitCode.NullUserName);
        }
        if (password==null){
            return new ServerObjectResponse<BigInteger>(null,ServerExitCode.NullPassword);
        }

        StatementResponse databaseResponse = DatabaseAPI.logIn(username,password,this.connection);
        boolean error=false;
        try {
            if (databaseResponse.getExitCode() == DatabaseExitCode.UnmatchedConstraint){
                return new ServerObjectResponse<BigInteger>(null,ServerExitCode.UserNameNotFound);
            } else if (databaseResponse.getExitCode() == DatabaseExitCode.UnmetCondition){
                return new ServerObjectResponse<BigInteger>(null,ServerExitCode.WrongPassword);
            } else if (databaseResponse.getExitCode() != DatabaseExitCode.Success || !databaseResponse.getStatement().getResultSet().next()){
                error=true;
                return new ServerObjectResponse<BigInteger>(null,ServerExitCode.DatabaseError);
            }
            return new ServerObjectResponse<BigInteger>
                    (databaseResponse.getStatement().getResultSet().getBigDecimal("id").toBigIntegerExact(),
                            ServerExitCode.Success);
        } catch (SQLException | ArithmeticException e){
            error=true;
            return new ServerObjectResponse<BigInteger>(null,ServerExitCode.DatabaseError);
        } finally {
            try {
                databaseResponse.getStatement().close();
            } catch (Exception ignored){
            }
            if (error){
                databaseResponse.printExitMessage();
            }
        }
    }
    public ServerResponse removeCurrentUser(@NotNull BigInteger userID){
        if (userID==null){
            return new ServerResponse(ServerExitCode.NullUserID);
        }

        StatementResponse databaseResponse = DatabaseAPI.removeUser(userID,this.connection);
        if (databaseResponse.getExitCode() == DatabaseExitCode.Success
                && databaseResponse.getExitMessage().equals("No rows were affected.")){
            return new ServerResponse(ServerExitCode.UserIDNotFound);
        } else if (databaseResponse.getExitCode().equals(DatabaseExitCode.Success)){
            return new ServerResponse(ServerExitCode.Success);
        }
        databaseResponse.printExitMessage();
        return new ServerResponse(ServerExitCode.DatabaseError);
    }
    public ServerResponse removeStore(@NotNull String storeName, @NotNull BigInteger userID){
        if (storeName==null){
            return new ServerResponse((ServerExitCode.NullStoreName));
        }
        if (userID==null){
            return new ServerResponse(ServerExitCode.NullUserID);
        }
        StatementResponse databaseResponse = DatabaseAPI.removeStore(storeName,userID,this.connection);
        if (databaseResponse.getExitCode() == DatabaseExitCode.Success
                && databaseResponse.getExitMessage().equals("No rows were affected.")){
            return new ServerResponse(ServerExitCode.StoreNotFound);
        } else if (databaseResponse.getExitCode().equals(DatabaseExitCode.Success)){
            return new ServerResponse(ServerExitCode.Success);
        }
        databaseResponse.printExitMessage();
        return new ServerResponse(ServerExitCode.DatabaseError);
    }
    public ServerResponse removeTable(@NotNull String tableName, @NotNull String storeName, @NotNull BigInteger userID){
        if (tableName==null){
            return new ServerResponse(ServerExitCode.NullTableName);
        }
        if (storeName==null){
            return new ServerResponse(ServerExitCode.NullStoreName);
        }
        if (userID==null){
            return new ServerResponse(ServerExitCode.NullUserID);
        }

        DatabaseResponse databaseResponse = DatabaseAPI.removeTable(tableName,storeName,userID,this.connection);
        if (databaseResponse.getExitCode() == DatabaseExitCode.Success
                && databaseResponse.getExitMessage().equals("No rows were affected.")){
            return new ServerResponse(ServerExitCode.TableNotFound);
        } else if (databaseResponse.getExitCode() == DatabaseExitCode.Success){
            return new ServerResponse(ServerExitCode.Success);
        }
        databaseResponse.printExitMessage();
        return new ServerResponse(ServerExitCode.DatabaseError);
    }
    public ServerResponse addUser(@NotNull String fullName, @NotNull String username, @NotNull String password, @NotNull String email){
        if (fullName==null){
            return new ServerResponse(ServerExitCode.NullFullName);
        } else if (fullName.length()>32){
            return new ServerResponse(ServerExitCode.FullNameTooLong);
        }
        if (username==null){
            return new ServerResponse(ServerExitCode.NullUserName);
        } else if (username.length()>32){
            return new ServerResponse(ServerExitCode.UserNameTooLong);
        }
        else if(username.length()<6){
            return new ServerResponse(ServerExitCode.SmallUserName);
        }
        if (password==null){
            return new ServerResponse(ServerExitCode.NullPassword);
        } else if (password.length()<8){
            return new ServerResponse(ServerExitCode.SmallPassword);
        } else if (!this.isStrongPassword(password)){
            return new ServerResponse(ServerExitCode.WeakPassword);
        }
        if (email==null){
            return new ServerResponse(ServerExitCode.NullEmail);
        } else if (!this.isValidEmail(email)){
            return new ServerResponse(ServerExitCode.InvalidEmail);
        }

        StatementResponse databaseResponse = DatabaseAPI.addUser(fullName, username, password, email,this.connection);
        if (databaseResponse.getExitCode() == DatabaseExitCode.Success){
            return new ServerResponse(ServerExitCode.Success);
        } else if (databaseResponse.getExitCode() == DatabaseExitCode.KeyConflict){
            return new ServerResponse(ServerExitCode.UserNameExists);
        } else if (databaseResponse.getExitCode() == DatabaseExitCode.UniqueFieldConflict){
            return new ServerResponse(ServerExitCode.EmailExists);
        }
        databaseResponse.printExitMessage();
        return new ServerResponse(ServerExitCode.DatabaseError);
    }
    public ServerResponse addStore(@NotNull String storeName, @NotNull BigInteger userID, int x, int y){
        if (storeName==null){
            return new ServerResponse(ServerExitCode.NullStoreName);
        } else if (storeName.length()>32){
            return new ServerResponse(ServerExitCode.StoreNameTooLong);
        }
        if (userID==null){
            return new ServerResponse(ServerExitCode.NullOwnerID);
        }
        if (x<=0){
            return new ServerResponse(ServerExitCode.InvalidXDimension);
        }
        if (y<=0){
            return new ServerResponse(ServerExitCode.InvalidYDimension);
        }

        StatementResponse databaseResponse = DatabaseAPI.addStore(storeName,userID,x,y,this.connection);
        if (databaseResponse.getExitCode() == DatabaseExitCode.Success){
            return new ServerResponse(ServerExitCode.Success);
        } else if (databaseResponse.getExitCode() == DatabaseExitCode.ForeignKeyViolation){
            return new ServerResponse(ServerExitCode.UserIDNotFound);
        } else if (databaseResponse.getExitCode() == DatabaseExitCode.UniqueFieldConflict){
            return new ServerResponse(ServerExitCode.StoreNameExists);
        }
        databaseResponse.printExitMessage();
        return new ServerResponse(ServerExitCode.DatabaseError);
    }
    public ServerResponse addTable(@NotNull String tableName, int x, int y, int people
            ,@NotNull StateOfTable state, @NotNull String storeName, @NotNull BigInteger userID){
        if (tableName==null){
            return new ServerResponse(ServerExitCode.NullTableName);
        } else if (tableName.length()>8){
            return new ServerResponse(ServerExitCode.TableNameTooLong);
        }
        if (state==null){
            return new ServerResponse(ServerExitCode.NullTableState);
        }
        if (userID==null){
            return new ServerResponse(ServerExitCode.NullUserID);
        }
        if (storeName==null){
            return new ServerResponse(ServerExitCode.NullStoreName);
        }
        if (x<0){
            return new ServerResponse(ServerExitCode.InvalidXDimension);
        }
        if (y<0){
            return new ServerResponse(ServerExitCode.InvalidYDimension);
        }
        if (people<=0){
            return new ServerResponse(ServerExitCode.InvalidPeopleNumber);
        }

        StatementResponse databaseResponse = DatabaseAPI.getStoreOwner(storeName,this.connection);
        BigInteger storeID;
        boolean error=true;
        try {
            if (databaseResponse.getExitCode() != DatabaseExitCode.Success){
                return new ServerResponse(ServerExitCode.DatabaseError);
            } else if (!databaseResponse.getStatement().getResultSet().next()){
                error=false;
                return new ServerResponse(ServerExitCode.StoreNotFound);
            }
            if (!userID.equals(databaseResponse.getStatement().getResultSet().getBigDecimal("owner").toBigIntegerExact())){
                error=false;
                return new ServerResponse(ServerExitCode.UnauthorizedID);
            }
            storeID=databaseResponse.getStatement().getResultSet().getBigDecimal("id").toBigIntegerExact();
            error=false;
        } catch (SQLException | ArithmeticException e){
            return new ServerResponse(ServerExitCode.DatabaseError);
        } finally {
            try {
                databaseResponse.getStatement().close();
            } catch (SQLException ignored){
            }
            if (error){
                databaseResponse.printExitMessage();
            }
        }

        databaseResponse = DatabaseAPI.addTable(tableName,storeID,x,y,people,state.toString(),this.connection);
        if (databaseResponse.getExitCode() == DatabaseExitCode.Success){
            return new ServerResponse(ServerExitCode.Success);
        }
        else if (databaseResponse.getExitCode() == DatabaseExitCode.ForeignKeyViolation){
            return new ServerResponse(ServerExitCode.StoreNotFound);
        } else if (databaseResponse.getExitCode() == DatabaseExitCode.UniqueFieldConflict){
            return new ServerResponse(ServerExitCode.TableNameExists);
        }
        databaseResponse.printExitMessage();
        return new ServerResponse(ServerExitCode.DatabaseError);
    }
    public ServerObjectResponse<Store> getStore(@NotNull String storeName,BigInteger userID, HashMap<String,Boolean> options){
        if (storeName==null) {
            return new ServerObjectResponse<Store>(null,ServerExitCode.NullStoreName);
        }

        StatementResponse databaseResponse = DatabaseAPI.getStore(storeName,this.connection);
        if (databaseResponse.getExitCode() != DatabaseExitCode.Success){
            databaseResponse.printExitMessage();
            return new ServerObjectResponse<Store>(null,ServerExitCode.DatabaseError);
        } else{
            try {
                ResultSet rs = databaseResponse.getStatement().getResultSet();
                if (rs.next()) {
                    BigInteger id=rs.getBigDecimal("id").toBigIntegerExact()
                            ,owner=rs.getBigDecimal("owner").toBigIntegerExact();
                    Integer X=rs.getInt("grid_x"),Y=rs.getInt("grid_y");
                    String name=rs.getString("name");
                    if (userID==null || !Objects.equals(id, userID) || options.get("id")==null || !options.get("id")){
                        id=null;
                    }
                    if (options.get("name")==null || !options.get("name")){
                        name=null;
                    }
                    if (userID==null || !Objects.equals(id, userID) || options.get("owner")==null || !options.get("owner")){
                        owner=null;
                    }
                    if (options.get("grid_x")==null || !options.get("grid_x")){
                        X=null;
                    }
                    if (options.get("grid_y")==null || !options.get("grid_y")){
                        Y=null;
                    }
                    return new ServerObjectResponse<Store>(new Store(id,name,owner,X,Y,null),ServerExitCode.Success);
                }
                else{
                    return new ServerObjectResponse<Store>(null,ServerExitCode.StoreNotFound);
                }
            } catch (SQLException | NullPointerException e) {
                databaseResponse.printExitMessage();
                return new ServerObjectResponse<Store>(null,ServerExitCode.DatabaseError);
            } finally {
                try {
                    databaseResponse.getStatement().close();
                } catch (SQLException ignored){
                }
            }
        }
    }
    public ServerArrayResponse<Store> getStores(BigInteger userID, HashMap<String,Boolean> options){
        StatementResponse databaseResponse = DatabaseAPI.getStores(this.connection);
        if (databaseResponse.getExitCode()!= DatabaseExitCode.Success){
            try {
                databaseResponse.getStatement().close();
            } catch (SQLException ignored){
            }
            databaseResponse.printExitMessage();
            return new ServerArrayResponse<Store>(null,ServerExitCode.DatabaseError);
        }
        ResultSet rs;
        try {
            rs = databaseResponse.getStatement().getResultSet();
            ArrayList<Store> stores = new ArrayList<>();
            while (rs.next()){
                BigInteger id=rs.getBigDecimal("id").toBigIntegerExact()
                        ,owner=rs.getBigDecimal("owner").toBigIntegerExact();
                String name = rs.getString("name");
                Integer grid_x=rs.getInt("grid_x")
                        ,grid_y=rs.getInt("grid_y");
                if (userID==null || !Objects.equals(id, userID) || options.get("id")==null || !options.get("id")){
                    id=null;
                }
                if (options.get("name")==null || !options.get("name")){
                    name=null;
                }
                if (userID==null || !Objects.equals(id, userID) || options.get("owner")==null || !options.get("owner")){
                    owner=null;
                }
                if (options.get("grid_x")==null || !options.get("grid_x")){
                    grid_x=null;
                }
                if (options.get("grid_y")==null || !options.get("grid_y")){
                    grid_y=null;
                }
                stores.add(new Store(id,name,owner,grid_x,grid_y,null));
            }
            return new ServerArrayResponse<Store>(stores,ServerExitCode.Success);
        } catch (SQLException e){
            databaseResponse.printExitMessage();
            return new ServerArrayResponse<Store>(null,ServerExitCode.DatabaseError);
        } finally{
            try {
                databaseResponse.getStatement().close();
            } catch (SQLException ignored){
            }
        }
    }
    public ServerArrayResponse<Table> getTables(@NotNull String storeName, BigInteger userID, HashMap<String,Boolean> options){
        if (storeName==null){
            return new ServerArrayResponse<Table>(null,ServerExitCode.NullStoreID);
        }

        StatementResponse databaseResponse = DatabaseAPI.getTables(storeName,this.connection);
        if (databaseResponse.getExitCode()!= DatabaseExitCode.Success){
            try {
                databaseResponse.getStatement().close();
            } catch (SQLException ignored){
            }
            databaseResponse.printExitMessage();
            return new ServerArrayResponse<Table>(null,ServerExitCode.DatabaseError);
        }
        ResultSet rs;
        try {
            rs = databaseResponse.getStatement().getResultSet();
            ArrayList<Table> tables = new ArrayList<>();
            while (rs.next()){
                BigInteger id=rs.getBigDecimal("id").toBigIntegerExact()
                        ,store=rs.getBigDecimal("store").toBigIntegerExact();
                String name=rs.getString("name"),state=rs.getString("state");
                Integer pos_x=rs.getInt("position_x")
                        ,pos_y=rs.getInt("position_y")
                        ,people=rs.getInt("people");
                if (userID==null || !Objects.equals(id, userID) || options.get("id")==null || !options.get("id")){
                    id=null;
                }
                if (options.get("name")==null || !options.get("name")){
                    name=null;
                }
                if (userID==null || !Objects.equals(id, userID) || options.get("store")==null || !options.get("store")){
                    store=null;
                }
                if (options.get("position_x")==null || !options.get("position_x")){
                    pos_x=null;
                }
                if (options.get("position_y")==null || !options.get("position_y")){
                    pos_y=null;
                }
                if (options.get("people")==null || !options.get("people")){
                    people=null;
                }
                if (options.get("state")==null || !options.get("state")){
                    state=null;
                }
                tables.add(new Table(id,name,store,pos_x,pos_y,people,state));
            }
            return new ServerArrayResponse<Table>(tables,ServerExitCode.Success);
        } catch (SQLException e){
            databaseResponse.printExitMessage();
            return new ServerArrayResponse<Table>(null,ServerExitCode.DatabaseError);
        } finally{
            try {
                databaseResponse.getStatement().close();
            } catch (SQLException ignored){
            }
        }
    }
    private boolean isStrongPassword(String password){
        String LOWERCASE_REGEX = ".*[a-z].*";
        String UPPERCASE_REGEX = ".*[A-Z].*";
        String DIGIT_REGEX = ".*[0-9].*";
        String SPECIAL_CHAR_REGEX = ".*[!@#$%^&*(),.?\":{}|<>].*";
        Pattern lowercasePattern = Pattern.compile(LOWERCASE_REGEX);
        Pattern uppercasePattern = Pattern.compile(UPPERCASE_REGEX);
        Pattern digitPattern = Pattern.compile(DIGIT_REGEX);
        Pattern specialCharPattern = Pattern.compile(SPECIAL_CHAR_REGEX);

        Matcher hasLowercase = lowercasePattern.matcher(password);
        Matcher hasUppercase = uppercasePattern.matcher(password);
        Matcher hasDigit = digitPattern.matcher(password);
        Matcher hasSpecialChar = specialCharPattern.matcher(password);

        return hasLowercase.matches() &&
                hasUppercase.matches() &&
                hasDigit.matches() &&
                hasSpecialChar.matches();
    }
    private boolean isValidEmail(String email){
        String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

        Pattern pattern = Pattern.compile(EMAIL_REGEX);

        Matcher matcher = pattern.matcher(email);

        return matcher.matches();
    }
}