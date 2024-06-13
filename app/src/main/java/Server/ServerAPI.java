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

/**
 * This is an API used by clients, to gain restricted access to the database.
 * Any checks of user input must be done in this class.
 * Any handling and obfuscating of data from the database must be done in this class.
 * The return objects of ServerAPI must be static and not have a live connection to the database.
 * This Object must be terminated using the .close() method before being destroyed.
 */
public class ServerAPI {
    /**
     * A live connection to the database, used in order to avoid creating new connections for each request.
     * It must be implicitly closed when the ServerAPI object is about to be destroyed.
     */
    public final Connection connection;
    public ServerAPI(){
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
     * given to it.
     * Return appropriate ServerExitCodes for each type of logIn failure.
     * @param username The unique username that will be used to log in.
     * @param password The password that is supposed to match with the username.
     * @return The ID of the logged-in user, or null if the log in failed and an encoded result of the trigger of the API endpoint.
     */
    public ServerObjectResponse<BigInteger> logIn(@NotNull String username, @NotNull String password){
        //Check for invalid user input.
        if (username==null){
            return new ServerObjectResponse<>(null,ServerExitCode.NullUserName);
        }
        if (password==null){
            return new ServerObjectResponse<>(null,ServerExitCode.NullPassword);
        }

        //Trigger the appropriate database API endpoint and handle its response.
        StatementResponse databaseResponse = DatabaseAPI.logIn(username,password,this.connection);
        boolean error=false;
        try {
            if (databaseResponse.getExitCode() == DatabaseExitCode.UnmatchedConstraint){
                return new ServerObjectResponse<>(null,ServerExitCode.UserNameNotFound);
            } else if (databaseResponse.getExitCode() == DatabaseExitCode.UnmetCondition){
                return new ServerObjectResponse<>(null,ServerExitCode.WrongPassword);
            } else if (databaseResponse.getExitCode() != DatabaseExitCode.Success || !databaseResponse.getStatement().getResultSet().next()){
                error=true;
                return new ServerObjectResponse<>(null,ServerExitCode.DatabaseError);
            }
            return new ServerObjectResponse<>
                    (databaseResponse.getStatement().getResultSet().getBigDecimal("id").toBigIntegerExact(),
                            ServerExitCode.Success);
        } catch (SQLException | ArithmeticException e){
            error=true;
            return new ServerObjectResponse<>(null,ServerExitCode.DatabaseError);
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

    /**
     * Removes from the database the user entry corresponding to the given user ID.
     * It is used for deleting ones own personal account.
     * @param userID The ID of the user requesting for their account to be removed.
     * @return An encoded result of attempting to remove a user.
     */
    public ServerResponse removeCurrentUser(@NotNull BigInteger userID){
        //Check for invalid user input.
        if (userID==null){
            return new ServerResponse(ServerExitCode.NullUserID);
        }

        //Trigger the appropriate database API endpoint and handle its response.
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

    /**
     * Remove from the database the entry corresponding to the Store, matching the given name.
     * The removal can be successfully executed only by the owner of the Store or by an Admin.
     * @param storeName The unique name of the Store marked for removal.
     * @param userID The ID of the user requesting the Store removal.
     * @return An encoded result of attempting to remove a Store.
     */
    public ServerResponse removeStore(@NotNull String storeName, @NotNull BigInteger userID){
        //Check for invalid user input.
        if (storeName==null){
            return new ServerResponse((ServerExitCode.NullStoreName));
        }
        if (userID==null){
            return new ServerResponse(ServerExitCode.NullUserID);
        }

        //Trigger the appropriate database API endpoint and handle its response.
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

    /**
     * Remove from the database the entry corresponding to the Table, matching the store name given.
     * The removal can be successfully executed only by the owner of the Store or by an Admin.
     * @param tableName The unique name of the Table marked for removal
     * @param storeName The unique name of the Store to which the Table for removal belongs.
     * @param userID The ID of the user requesting the Table removal.
     * @return An encoded result of attempting to remove a Table from a Store.
     */
    public ServerResponse removeTable(@NotNull String tableName, @NotNull String storeName, @NotNull BigInteger userID){
        //Check for invalid user input.
        if (tableName==null){
            return new ServerResponse(ServerExitCode.NullTableName);
        }
        if (storeName==null){
            return new ServerResponse(ServerExitCode.NullStoreName);
        }
        if (userID==null){
            return new ServerResponse(ServerExitCode.NullUserID);
        }

        //Trigger the appropriate database API endpoint and handle its response.
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

    /**
     * Adds a new user to the database, using the given input.
     * @param fullName The first and last name of the new user. Can be up to 32 characters long.
     * @param username The unique username of the new user. Must be 6-32 characters long.
     * @param password The password of the new user, used for logging in. Must pass the "isStrongPassword(String)" check. Must be at least 8 characters long.
     * @param email The unique email of the user used for communication. Must pass the "isValidEmail(String)" check.
     * @return An encoded result of attempting to add a new User.
     */
    public ServerResponse addUser(@NotNull String fullName, @NotNull String username, @NotNull String password, @NotNull String email){
        //Check for invalid user input.
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

        //Trigger the appropriate database API endpoint and handle its response.
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

    /**
     * Adds a new Store to the database, using the given input.
     * @param storeName The unique name of the Store. Can be up to 32 characters long.
     * @param userID The ID of the user requesting the Store addition.
     * @param x The horizontal length of the grid used to display the store in the GUI. Must be positive.
     * @param y The vertical length of the grid used to display the store in the GUI. Must be positive.
     * @return An encoded result of attempting to add a new Store.
     */
    public ServerResponse addStore(@NotNull String storeName, @NotNull BigInteger userID, int x, int y){
        //Check for invalid user input.
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

        //Trigger the appropriate database API endpoint and handle its response.
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

    /**
     * Add a new Table, using the given input, to the database.
     * The new Table must be owned by an existing Store, and the Store must be owned by the user requesting the addition.
     * @param tableName The name of the table to be added. Can be up to 8 characters long.
     * @param x The horizontal position on the Store grid at which the new Table is placed. Must be positive.
     * @param y The vertical position on the Store grid at which the new Table is placed. Must be positive.
     * @param people The number of people that can sit on the new Table. Must be positive.
     * @param state The state of occupation of the new Table.
     * @param storeName The unique name of the Store to which the new Table will belong.
     * @param userID The ID of the user requesting the addition of a new Table to the specified Store.
     * @return An encoded result of attempting to add a new Table.
     */
    public ServerResponse addTable(@NotNull String tableName, int x, int y, int people
            ,@NotNull StateOfTable state, @NotNull String storeName, @NotNull BigInteger userID){
        //Check for invalid user input.
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

        //Check if the user owns the Store, to which they attempt to add a new Table.
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

        //Trigger the appropriate database API endpoint and handle its response.
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

    /**
     * Get a Store from the database.
     * The data from the database is used to populate a Store object, which is then returned.
     * The data which will populate the Store object are the ones requested by 'options', minus those that the User
     * doesn't have access to.
     * @param storeName The unique name of the Store.
     * @param userID The ID of the user attempting to retrieve the Store data.
     * @param options Requested fields of the Store.
     * @return An object Store and an encoded result of the attempt to
     * get a Store from the database.
     */
    public ServerObjectResponse<Store> getStore(@NotNull String storeName,BigInteger userID, HashMap<String,Boolean> options){
        //Check for invalid user input.
        if (storeName==null) {
            return new ServerObjectResponse<>(null,ServerExitCode.NullStoreName);
        }

        //Trigger the appropriate database API endpoint and handle its response.
        StatementResponse databaseResponse = DatabaseAPI.getStore(storeName,this.connection);
        if (databaseResponse.getExitCode() != DatabaseExitCode.Success){
            databaseResponse.printExitMessage();
            return new ServerObjectResponse<>(null,ServerExitCode.DatabaseError);
        } else{
            try {
                //Get the results and obfuscate any sensitive data and any data that wasn't explicitly requested inside 'options'.
                ResultSet rs = databaseResponse.getStatement().getResultSet();
                if (rs.next()) {
                    BigInteger id=rs.getBigDecimal("id").toBigIntegerExact()
                            ,owner=rs.getBigDecimal("owner").toBigIntegerExact();
                    Integer X=rs.getInt("grid_x"),Y=rs.getInt("grid_y");
                    String name=rs.getString("name");
                    if (userID==null || !Objects.equals(id, userID) || Boolean.FALSE.equals(options.get("id"))){
                        id=null;
                    }
                    if (Boolean.FALSE.equals(options.get("name"))){
                        name=null;
                    }
                    if (userID==null || !Objects.equals(id, userID) || Boolean.FALSE.equals(options.get("owner"))){
                        owner=null;
                    }
                    if (Boolean.FALSE.equals(options.get("grid_x"))){
                        X=null;
                    }
                    if (Boolean.FALSE.equals(options.get("grid_y"))){
                        Y=null;
                    }
                    return new ServerObjectResponse<>(new Store(id,name,owner,X,Y,null),ServerExitCode.Success);
                }
                else{
                    return new ServerObjectResponse<>(null,ServerExitCode.StoreNotFound);
                }
            } catch (SQLException | NullPointerException e) {
                databaseResponse.printExitMessage();
                return new ServerObjectResponse<>(null,ServerExitCode.DatabaseError);
            } finally {
                try {
                    databaseResponse.getStatement().close();
                } catch (SQLException ignored){
                }
            }
        }
    }

    /**
     * Get all Stores from the database.
     * Used for getting surface level information about all existing stores, like their name.
     * Detailed information about a Store should be requested using its unique name in a separate request.
     * This API endpoint is computationally expensive. Avoid when possible.
     * The data from the database is used to populate an ArrayList of Store objects, which is then returned.
     * The data which will populate the Store objects are the ones requested by 'options', minus those that the User
     * doesn't have access to.
     * @param userID The ID of the user attempting to retrieve the Store data.
     * @param options Requested fields of the Store.
     * @return An ArrayList of objects Store and an encoded result of the attempt to
     * get all Stores from the database.
     */
    public ServerArrayResponse<Store> getStores(BigInteger userID, HashMap<String,Boolean> options){
        //Trigger the appropriate database API endpoint and handle its response.
        StatementResponse databaseResponse = DatabaseAPI.getStores(this.connection);
        if (databaseResponse.getExitCode()!= DatabaseExitCode.Success){
            try {
                databaseResponse.getStatement().close();
            } catch (SQLException ignored){
            }
            databaseResponse.printExitMessage();
            return new ServerArrayResponse<>(null,ServerExitCode.DatabaseError);
        }
        //Get the results and obfuscate any sensitive data and any data that wasn't explicitly requested inside 'options'.
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
                if (userID==null || !Objects.equals(id, userID) || Boolean.FALSE.equals(options.get("id"))){
                    id=null;
                }
                if (Boolean.FALSE.equals(options.get("name"))){
                    name=null;
                }
                if (userID==null || !Objects.equals(id, userID) || Boolean.FALSE.equals(options.get("owner"))){
                    owner=null;
                }
                if (Boolean.FALSE.equals(options.get("grid_x"))){
                    grid_x=null;
                }
                if (Boolean.FALSE.equals(options.get("grid_y"))){
                    grid_y=null;
                }
                stores.add(new Store(id,name,owner,grid_x,grid_y,null));
            }
            return new ServerArrayResponse<>(stores,ServerExitCode.Success);
        } catch (SQLException e){
            databaseResponse.printExitMessage();
            return new ServerArrayResponse<>(null,ServerExitCode.DatabaseError);
        } finally{
            try {
                databaseResponse.getStatement().close();
            } catch (SQLException ignored){
            }
        }
    }

    /**
     * The data from the database is used to populate an ArrayList of Table objects, which is then returned.
     * The data which will populate the Table objects are the ones requested by 'options', minus those that the User
     * doesn't have access to.
     * @param storeName The unique name of the Store which owns the requested Tables.
     * @param userID The ID of the user attempting to retrieve the Store data.
     * @param options Requested fields of the Store.
     * @return An ArrayList of objects Table and an encoded result of the attempt to
     * get all Stores from the database.
     */
    public ServerArrayResponse<Table> getTables(@NotNull String storeName, BigInteger userID, HashMap<String,Boolean> options){
        //Check for invalid user input.
        if (storeName==null){
            return new ServerArrayResponse<>(null,ServerExitCode.NullStoreID);
        }

        //Trigger the appropriate database API endpoint and handle its response.
        StatementResponse databaseResponse = DatabaseAPI.getTables(storeName,this.connection);
        if (databaseResponse.getExitCode()!= DatabaseExitCode.Success){
            try {
                databaseResponse.getStatement().close();
            } catch (SQLException ignored){
            }
            databaseResponse.printExitMessage();
            return new ServerArrayResponse<>(null,ServerExitCode.DatabaseError);
        }
        //Get the results and obfuscate any sensitive data and any data that wasn't explicitly requested inside 'options'.
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
                if (userID==null || !Objects.equals(id, userID) || Boolean.FALSE.equals(options.get("id"))){
                    id=null;
                }
                if (Boolean.FALSE.equals(options.get("name"))){
                    name=null;
                }
                if (userID==null || !Objects.equals(id, userID) || Boolean.FALSE.equals(options.get("store"))){
                    store=null;
                }
                if (Boolean.FALSE.equals(options.get("position_x"))){
                    pos_x=null;
                }
                if (Boolean.FALSE.equals(options.get("position_y"))){
                    pos_y=null;
                }
                if (Boolean.FALSE.equals(options.get("people"))){
                    people=null;
                }
                if (Boolean.FALSE.equals(options.get("state"))){
                    state=null;
                }
                tables.add(new Table(id,name,store,pos_x,pos_y,people,state));
            }
            return new ServerArrayResponse<>(tables,ServerExitCode.Success);
        } catch (SQLException e){
            databaseResponse.printExitMessage();
            return new ServerArrayResponse<>(null,ServerExitCode.DatabaseError);
        } finally{
            try {
                databaseResponse.getStatement().close();
            } catch (SQLException ignored){
            }
        }
    }

    /**
     * Check if the given password matches certain criteria to characterize it as strong.
     * @param password The password to be checked.
     * @return True if the password matches all criteria, and false otherwise.
     */
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

    /**
     * Check if the given String corresponds to a valid email address.
     * @param email The String to be checked.
     * @return True if the given String corresponds to a valid email address, and false otherwise.
     */
    private boolean isValidEmail(String email){
        String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

        Pattern pattern = Pattern.compile(EMAIL_REGEX);

        Matcher matcher = pattern.matcher(email);

        return matcher.matches();
    }
}