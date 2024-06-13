package Database;

import org.jetbrains.annotations.NotNull;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Properties;

/**
 * An API Class that allows execution of queries to the database.
 * Every method must always return an object of type DatabaseResponse or one of its children.
 */
public class DatabaseAPI {
    /**
     * Specifies the amount of time in seconds after which, we assume the database did not respond in time.
     */
    private static final int VALIDATION_TIMEOUT=10;
    public DatabaseAPI(){
    }

    /**
     * Accept a Connection object.
     * If the Connection object does not have a live connection to the database, replace it with a Connection object that does.
     * Connection is replaced with null if the creation of a live Connection fails.
     * @param connection This parameter will be populated with a live connection to the database.
     * @return A live Connection to the database or null, the encoded and the humanly readable result of the Connection creation.
     */
    public static ConnectionResponse connect(Connection connection) {
        //Check if the Connection provided is already live.
        if (connection != null){
            try {
                if (connection.isValid(VALIDATION_TIMEOUT)) {
                    return new ConnectionResponse(connection,"", DatabaseExitCode.Success);
                }
            } catch (Exception ignored) {
            }
        }

        //Attempt to open a new Connection to the database.
        String dbURL = "jdbc:postgresql://vanillaminecraft.ddns.net:5432/server_database";
        Properties connectionProperties = DatabaseAPI.getConnectionProperties();
        Connection conn;
        try {
            conn = DriverManager.getConnection(dbURL,connectionProperties);
        } catch (SQLTimeoutException t) {
            String exitMessage="Error occurred at method DatabaseAPI.connect():\n";
            exitMessage = exitMessage.concat("Connecting to database: " +
                    "Timeout of" + VALIDATION_TIMEOUT + " seconds exceeded.");
            return new ConnectionResponse(null,exitMessage, DatabaseExitCode.ConnectingTimeOut);
        } catch (SQLException e) {
            String exitMessage = "Error occurred at method DatabaseAPI.connect():\n";
            exitMessage = exitMessage.concat("Connecting to database: " +
                    "Inaccessible database or null URL.");
            return new ConnectionResponse(null,exitMessage, DatabaseExitCode.ConnectingFailed);
        }

        //Check if the Connection object created actually has a live connection to the database.
        try {
            if (conn.isValid(VALIDATION_TIMEOUT)) {
                return new ConnectionResponse(conn,"", DatabaseExitCode.Success);
            } else {
                String exitMessage = "Error occurred at method DatabaseAPI.connect():\n";
                exitMessage = exitMessage.concat("Verifying database connection: " +
                        "Timeout of" + VALIDATION_TIMEOUT + " seconds exceeded.");
                return new ConnectionResponse(null,exitMessage, DatabaseExitCode.ConnectionValidationTimeOut);
            }
        } catch (SQLException e) {
            String exitMessage = "Error occurred at method DatabaseAPI.connect():\n";
            exitMessage = exitMessage.concat("Verifying database connection: " +
                    "Invalid timeout for connection validation.");
            return new ConnectionResponse(null,exitMessage, DatabaseExitCode.InvalidAPIParameters);
        }
    }

    /**
     * An object creation method to clean up the code.
     * @return A Properties object used to define connection parameters to the database.
     */
    private static Properties getConnectionProperties() {
        Properties connectionProperties = new Properties();
        connectionProperties.put("user","george");
        connectionProperties.put("password","111111");
        connectionProperties.put("options","-c idle_session_timeout=300s");
        connectionProperties.put("loginTimeout",VALIDATION_TIMEOUT);
        connectionProperties.put("socketTimeout",VALIDATION_TIMEOUT*1000);
        connectionProperties.put("connectTimeout","20");
        connectionProperties.put("autosave","always");
        return connectionProperties;
    }

    /**
     * This method is used by all endpoints of the API.
     * It is responsible for executing the given query, identifying errors, returning any data returned from the database
     * and the state of the execution of the query.
     * @param preparedStatementBatches Contains PreparedStatement values for multiple batches.
     * @param query Contains the SQL query for execution.
     * @param callerMethod Stores the method and parameter types of the method that started executeQuery.
     * @param goalOfMethod Stores an explanation of what query is aiming to achieve.
     * @param returnResults Signifies if a PreparedStatement should be returned.
     * @param allowScroll Signifies if the PreparedStatement should be able to be parsed multiple times.
     * @param connection A connection to the database used for the query execution.
     * @return Contains data from the database, the encoded and the humanly readable result of the database query execution.
     */
    private static StatementResponse executeQuery(@NotNull ArrayList<ArrayList<Object>> preparedStatementBatches,
                                                  @NotNull String query, String callerMethod,
                                                  String goalOfMethod, boolean returnResults, boolean allowScroll, Connection connection){
        //Check if the Connection to database is live.
        ConnectionResponse connectionResponse = DatabaseAPI.connect(connection);
        if (connectionResponse.getExitCode()!= DatabaseExitCode.Success){
            return new StatementResponse(null,connectionResponse.getExitMessage(),connectionResponse.getExitCode());
        }
        if (callerMethod==null){
            callerMethod="*unknown caller method*";
        }
        if (goalOfMethod==null){
            goalOfMethod="*unknown goal of method*";
        }

        //Create PreparedStatement using the allowScroll parameter given.
        PreparedStatement statement;
        try {
            if(allowScroll){
                statement = connection.prepareStatement(query,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
            } else {
                statement = connection.prepareStatement(query);
            }
        } catch (SQLException e) {
            String exitMessage = "Error occurred at method DatabaseAPI."+callerMethod+":\n";
            exitMessage = exitMessage.concat("Preparing statement to "+goalOfMethod+": "+
                    "Failed to create prepared statement. Invalid URL or inaccessible database.");
            return new StatementResponse(null,exitMessage, DatabaseExitCode.PreparedStatementCreationFailed);
        }

        //Create batches to the PreparedStatement. The values will be contained in each element of preparedStatementBatches.
        try {
            for (ArrayList<Object> tableRow : preparedStatementBatches) {
                for (int j = 0; j < tableRow.size(); j++) {
                    Object obj = tableRow.get(j);

                    if(obj instanceof Integer){
                        statement.setInt(j+1,(int) obj);
                    } else if (obj instanceof BigInteger) {
                        statement.setObject(j+1,obj,Types.BIGINT);
                    }else if (obj instanceof String){
                        statement.setString(j+1,(String) obj);
                    }else {
                        String exitMessage = "Error occurred at method DatabaseAPI." + callerMethod + ":\n";
                        exitMessage = exitMessage.concat("Preparing statement to " + goalOfMethod + ": " +
                                "Parameter provided has unrecognized type.");
                        try {
                            statement.close();
                        } catch (Exception ignored) {
                        }
                        return new StatementResponse(null, exitMessage, DatabaseExitCode.InvalidParameterType);
                    }

                }
                statement.addBatch();
            }
        } catch (SQLException e) {
            String exitMessage = "Error occurred at method DatabaseAPI." + callerMethod + ":\n";
            exitMessage = exitMessage.concat("Preparing statement to " + goalOfMethod + ": " +
                    "Query and parameters provided do not match or PreparedStatement object has been closed.");
            try{
                statement.close();
            } catch (Exception ignored){
            }
            return new StatementResponse(null,exitMessage, DatabaseExitCode.ErrorSettingQueryParameters);
        }

        //Execute query according to returnResults value.
        //Close the PreparedStatement according to returnResults value.
        try{
            if (returnResults){
                statement.executeQuery();
                return new StatementResponse(statement,"", DatabaseExitCode.Success);
            }
            else {
                int rowsAffected = statement.executeUpdate();
                if (rowsAffected==0){
                    return new StatementResponse(null, "No rows were affected.", DatabaseExitCode.Success);
                } else{
                    return new StatementResponse(null, "Success", DatabaseExitCode.Success);
                }
            }
        } catch (SQLTimeoutException e){
            String exitMessage="Error occurred at method DatabaseAPI." + callerMethod + ":\n";
            exitMessage = exitMessage.concat("Executing Query to " + goalOfMethod + ": " +
                    "Timeout for executing query has been reached.");
            return new StatementResponse(null,exitMessage, DatabaseExitCode.QueryExecutionTimeOut);
        } catch (SQLException e) {
            String exitMessage = "Error occurred at method DatabaseAPI." + callerMethod + ":\n";
            exitMessage = exitMessage.concat("Executing Query to " + goalOfMethod + ": " +
                    "Query execution failed.");
            return new StatementResponse(null,exitMessage, getConstraintExitCode(e));
        } finally {
            if (!returnResults){
                try {
                    statement.close();
                } catch (Exception ignored) {
                }
            }
        }
    }

    /**
     * Decodes the database error using the exception that occurred.
     * An exception can be thrown by the database for different reasons, at the same state of execution.
     * It is needed to discern exactly what caused the issue to be able to log it appropriately.
     * @param e The exception that was thrown during the query execution.
     * @return Encoded representation of the specific database error that occurred.
     */
    private static DatabaseExitCode getConstraintExitCode(SQLException e) {
        DatabaseExitCode dec= DatabaseExitCode.QueryExecutionFailed;
        if (e.getSQLState().equals("23505")){
            dec = DatabaseExitCode.KeyConflict;
            String error= e.getMessage();
            if (error != null && (error.contains("user_email_key") || error.contains("user_username_key")
                    || error.contains("unique_name_per_store") || error.contains("store_name_key"))){
                dec=DatabaseExitCode.UniqueFieldConflict;
            }
        }
        if (e.getSQLState().equals("23503")){
            dec=DatabaseExitCode.ForeignKeyViolation;
        }
        return dec;
    }

    /**
     * Registers a new user to the platform and stores its data in the database.
     * @param fullName Contains the first and last name of the user. Can be up to 32 characters long.
     * @param username Unique field used for logging into the users account. Can be up to 32 characters long.
     * @param password A safe password used for logging into the user's account.
     * @param email A unique contact email for the user.
     * @param connection A connection to the database used for the query execution.
     * @return The encoded and the humanly readable result of the addition of the new user. No database data is returned.
     */
    public static StatementResponse addUser(@NotNull String fullName, @NotNull String username, @NotNull String password, @NotNull String email, Connection connection){
        //Hash the given password for safe storing in the database.

        //Create a hashing algorithm.
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 128);
        SecretKeyFactory factory;
        try {
            factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        } catch (NoSuchAlgorithmException e) {
            String exitMessage = "Error occurred at method DatabaseAPI.addUser():\n";
            exitMessage = exitMessage.concat("Hashing password: "+
                    "Hashing algorithm provided could not be found.");
            return new StatementResponse(null,exitMessage, DatabaseExitCode.InvalidHashingAlgorithm);
        }

        //Hash the password provided.
        byte[] hashedPassword;
        try {
            hashedPassword = factory.generateSecret(spec).getEncoded();
        } catch (InvalidKeySpecException ex) {
            String exitMessage = "Error occurred at method DatabaseAPI.addUser():\n";
            exitMessage = exitMessage.concat("Hashing password: "+
                    "KeySpec for password was invalid.");
            return new StatementResponse(null,exitMessage, DatabaseExitCode.InvalidKeySpec);
        }

        //Add PreparedStatement batch values.
        ArrayList<ArrayList<Object>> input = new ArrayList<>();
        ArrayList<Object> user = new ArrayList<>();
        user.add(fullName);
        user.add(username);
        user.add(Base64.getEncoder().encodeToString(salt));
        user.add(Base64.getEncoder().encodeToString(hashedPassword));
        user.add(email);
        input.add(user);

        String query = "INSERT INTO android_schema.user (full_name, username, password_salt, password_hashed, email)" +
                " VALUES(?,?,?,?,?)";

        return DatabaseAPI.executeQuery(input, query, "addUser(String,String,String,String,Connection",
                "add new User", false, false, connection);
    }

    /**
     * Add a new Store to the database, using the given parameters.
     * @param name The unique name of the store.
     * @param ownerID The ID of the owner of the store. Can be up to 32 characters long.
     * @param x The horizontal length of the grid used to display the store in the GUI. Must be positive.
     * @param y The vertical length of the grid used to display the store in the GUI. Must be positive.
     * @param connection A connection to the database used for the query execution.
     * @return The encoded and the humanly readable result of the Store addition. No database data is returned.
     */
    public static StatementResponse addStore(@NotNull String name, @NotNull  BigInteger ownerID, int x, int y, Connection connection){
        //Add PreparedStatement batch values.
        ArrayList<ArrayList<Object>> input = new ArrayList<>();
        ArrayList<Object> store = new ArrayList<>();
        store.add(name);
        store.add(ownerID);
        store.add(x);
        store.add(y);
        input.add(store);

        String query = "INSERT INTO android_schema.store (name, owner, grid_x, grid_y)" +
                " VALUES(?,?,?,?)";
        return DatabaseAPI.executeQuery(input, query, "addStore(String,BigInteger,int,int,Connection)",
                "add new Store", false, false, connection);
    }

    /**
     * Add a new Table to the database, using the given parameters.
     * The Table stored will belong to the Store with ID storeID.
     * The Store with ID storeID must exist for the addition of the new Table to succeed.
     * @param tableName The name of the table to be added. Can be up to 8 characters long.
     * @param storeID The ID of the store, to which the new Table belongs to.
     * @param x The horizontal position on the Store grid at which the new Table is placed. Must be positive.
     * @param y The vertical position on the Store grid at which the new Table is placed. Must be positive.
     * @param people The number of people that can sit on the new Table. Must be positive.
     * @param state The state of occupation of the new Table.
     * @param connection A connection to the database used for the query execution.
     * @return The encoded and the humanly readable result of the Table addition. No database data is returned.
     */
    public static StatementResponse addTable(@NotNull String tableName, @NotNull BigInteger storeID, int x, int y, int people,
                                             @NotNull String state, Connection connection){
        //Add PreparedStatement batch values.
        ArrayList<ArrayList<Object>> input = new ArrayList<>();
        ArrayList<Object> table = new ArrayList<>();
        table.add(tableName);
        table.add(storeID);
        table.add(x);
        table.add(y);
        table.add(people);
        table.add(state);
        input.add(table);

        String query = "INSERT INTO android_schema.table (name,store,position_x,position_y,people,state)" +
                " VALUES(?,?,?,?,?,?)";
        return DatabaseAPI.executeQuery(input, query,
                "addTable(String,BigInteger,int,int,int,String,Connection)",
                "add new Table for specified Store", false, false, connection);
    }

    /**
     * Finds the ID of the first user with the given username and password, that was found in the database.
     * The results of the database query will be accessible through the ResultSet of the PreparedStatement returned in the StatementResponse.
     * @param username The unique field of the user attempting to log in.
     * @param password The password of the user attempting to log in.
     * @param connection A connection to the database used for the query execution.
     * @return Returns the ID of the first user found matching the given credentials, the encoded and the humanly
     * readable result of logging in.
     */
    public static StatementResponse logIn(@NotNull String username, @NotNull String password, Connection connection){
        //Add PreparedStatement batch values.
        ArrayList<ArrayList<Object>> input = new ArrayList<>();
        ArrayList<Object> user = new ArrayList<>();
        user.add(username);
        input.add(user);

        //Get the user ID, salt, and password of the first user with a matching username.
        String query = "SELECT android_schema.user.ID,android_schema.user.password_salt,android_schema.user.password_hashed " +
                "FROM android_schema.user " +
                "WHERE android_schema.user.username = ? " +
                "LIMIT 1;";
        StatementResponse statementResponse = DatabaseAPI.executeQuery(input, query, "logIn(String,String,Connection)",
                "get information of user with specified Username", true, true, connection);

        //Retrieve the ID, salt and password from the database response.
        byte[] salt;
        byte[] databaseHashedPassword;
        boolean error=true;
        try {
            if (statementResponse.getStatement().getResultSet().next()){
                statementResponse.getStatement().getResultSet().getBigDecimal("ID").toBigIntegerExact();
                salt = Base64.getDecoder().decode(statementResponse.getStatement().getResultSet().getString("password_salt"));
                databaseHashedPassword = Base64.getDecoder().decode(statementResponse.getStatement().getResultSet().getString("password_hashed"));
                error=false;
            }
            else{
                String exitMessage = "Response for method DatabaseAPI.logIn(String,String,Connection):\n";
                exitMessage = exitMessage.concat("Executing Query to find account with specified Username: " +
                        "Username not found.");
                return new StatementResponse(null,exitMessage, DatabaseExitCode.UnmatchedConstraint);
            }
        } catch (IllegalArgumentException i){
            String exitMessage = "Error occurred at method DatabaseAPI.logIn(String,String,Connection):\n";
            exitMessage = exitMessage.concat("Retrieving data from initial query: "+
                    "Hashed password was incorrectly stored.");
            return new StatementResponse(null,exitMessage, DatabaseExitCode.ResultRetrievalCastingFailed);
        }
        catch (ArithmeticException a){
            String exitMessage = "Error occurred at method DatabaseAPI.logIn(String,String,Connection):\n";
            exitMessage = exitMessage.concat("Retrieving data from initial query: "+
                    "ID contained non zero fractional part.");
            return new StatementResponse(null,exitMessage, DatabaseExitCode.ResultRetrievalCastingFailed);
        }
        catch (SQLException e) {
            String exitMessage = "Error occurred at method DatabaseAPI.logIn(String,String,Connection):\n";
            exitMessage = exitMessage.concat("Retrieving data from initial query: "+
                    "Retrieval of user entry failed.");
            return new StatementResponse(null,exitMessage, DatabaseExitCode.ResultRetrievalFailed);
        } finally {
            if (error){
                try{
                    statementResponse.getStatement().close();
                } catch (SQLException ignore){
                }
            }
        }

        //Create the hashing algorithm using the salt found.
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 128);
        SecretKeyFactory factory;
        try {
            factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        } catch (NoSuchAlgorithmException e) {
            String exitMessage = "Error occurred at method DatabaseAPI.logIn(String,String,Connection):\n";
            exitMessage = exitMessage.concat("Hashing password: "+
                    "Hashing algorithm provided could not be found.");
            return new StatementResponse(null,exitMessage, DatabaseExitCode.InvalidHashingAlgorithm);
        }

        //Hash the given password using the salt found in the database.
        byte[] hashedPassword;
        try {
            hashedPassword = factory.generateSecret(spec).getEncoded();
        } catch (InvalidKeySpecException ex) {
            String exitMessage = "Error occurred at method DatabaseAPI.logIn(String,String,Connection):\n";
            exitMessage = exitMessage.concat("Hashing password: "+
                    "KeySpec for password was invalid.");
            return new StatementResponse(null,exitMessage, DatabaseExitCode.InvalidKeySpec);
        }

        //Check if the stored hashed password matches the hashed password created using the user input password and
        //the salt found in the database.
        error=true;
        try {
            if (Arrays.equals(hashedPassword, databaseHashedPassword)){
                statementResponse.getStatement().getResultSet().beforeFirst();
                error=false;
                return new StatementResponse(statementResponse.getStatement(),"", DatabaseExitCode.Success);
            }
            else{
                String exitMessage = "Response for method DatabaseAPI.logIn(String,String,Connection):\n";
                exitMessage = exitMessage.concat("Verifying password: "+
                        "Username and Password do not match.");
                return new StatementResponse(null,exitMessage, DatabaseExitCode.UnmetCondition);
            }
        } catch (SQLException e) {
            String exitMessage = "Error occurred at method DatabaseAPI.logIn(String,String,Connection):\n";
            exitMessage = exitMessage.concat("Reverting read ResultSet: "+
                    "Database error, close ResultSet or ResultSet that can not be scrolled backwards.");
            return new StatementResponse(null,exitMessage, DatabaseExitCode.ErrorRollingResultSetBackwards);
        } finally {
            if (error){
                try {
                    statementResponse.getStatement().close();
                } catch (SQLException ignored){
                }
            }
        }
    }

    /**
     * Get all the data stored in the database that correspond to the Store with the given name.
     * The results contain the data of the first Store found that matches the given name.
     * The results of the database query will be accessible through the ResultSet of the PreparedStatement returned in the StatementResponse.
     * @param storeName The unique name of the Store.
     * @param connection A connection to the database used for the query execution.
     * @return Returns all fields in the database that represent the Store,
     * the encoded and the humanly readable result of getting the Store's data.
     */
    public static StatementResponse getStore(@NotNull String storeName, Connection connection){
        //Add PreparedStatement batch values.
        ArrayList<ArrayList<Object>> input = new ArrayList<>();
        ArrayList<Object> store = new ArrayList<>();
        store.add(storeName);
        input.add(store);

        String query = "SELECT * " +
                "FROM android_schema.store " +
                "WHERE android_schema.store.name = ? "+
                "LIMIT 1";
        return DatabaseAPI.executeQuery(input,query,"getStore(BigInteger,Connection)",
                "get Store information", true,false,connection);
    }

    /**
     * Get all data stored in the database for every Store.
     * This method is bandwidth expensive and should be used with caution.
     * The results of the database query will be accessible through the ResultSet of the PreparedStatement returned in the StatementResponse.
     * @param connection A connection to the database used for the query execution.
     * @return Returns all fields for every entry representing a Store in the database,
     * the encoded and the humanly readable result of getting all data for all Stores.
     */
    public static StatementResponse getStores(Connection connection){
        //Add PreparedStatement batch values.
        ArrayList<ArrayList<Object>> input = new ArrayList<>();
        ArrayList<Object> store = new ArrayList<>();
        input.add(store);

        String query = "SELECT * " +
                "FROM android_schema.store ";
        return DatabaseAPI.executeQuery(input,query,"getStores(Connection)",
                "get every Store's information", true,false,connection);
    }

    /**
     * Get all Table data, for the Tables that belong to the Store with the name given.
     * The results of the database query will be accessible through the ResultSet of the PreparedStatement returned in the StatementResponse.
     * @param storeName The unique name of the Store to which the Tables belong.
     * @param connection A connection to the database used for the query execution.
     * @return Returns all fields of every database entry representing a Table,
     * the encoded and the humanly readable result of getting data for Tables.
     */
    public static StatementResponse getTables(@NotNull String storeName, Connection connection){
        //Add PreparedStatement batch values.
        ArrayList<ArrayList<Object>> input = new ArrayList<>();
        ArrayList<Object> store = new ArrayList<>();
        store.add(storeName);
        input.add(store);

        String query = "SELECT t.* " +
                "FROM android_schema.table t " +
                "JOIN android_schema.store s ON t.store = s.id " +
                "WHERE s.name = ? ";
        return DatabaseAPI.executeQuery(input,query,"getTables(BigInteger,Connection)",
                "get Tables for specified Store", true,false,connection);
    }

    /**
     * Get the ID of the user who owns the Store with the given name.
     * The results of the database query will be accessible through the ResultSet of the PreparedStatement returned in the StatementResponse.
     * @param storeName The unique name of the Store.
     * @param connection A connection to the database used for the query execution.
     * @return Returns the ID of the user, the encoded and the humanly
     * readable result of getting the ID of the Store owner.
     */
    public static StatementResponse getStoreOwner(@NotNull String storeName, Connection connection){
        //Add PreparedStatement batch values.
        ArrayList<ArrayList<Object>> input = new ArrayList<>();
        ArrayList<Object> store = new ArrayList<>();
        store.add(storeName);
        input.add(store);

        String query = "SELECT android_schema.store.owner, android_schema.store.id" +
                " FROM android_schema.store" +
                " WHERE android_schema.store.name = ? ";
        return DatabaseAPI.executeQuery(input,query,"getTableOwner(BigInteger,Connection)",
                "get OwnerID of Table", true,false,connection);
    }

    /**
     * Remove the user from the database that has a matching with the ID provided.
     * @param userID The ID of the user for removal.
     * @param connection A connection to the database used for the query execution.
     * @return The encoded and the humanly readable result of the user removal. No database data is returned.
     */
    public static StatementResponse removeUser(@NotNull BigInteger userID, Connection connection){
        //Add PreparedStatement batch values.
        ArrayList<ArrayList<Object>> input = new ArrayList<>();
        ArrayList<Object> user = new ArrayList<>();
        user.add(userID);
        input.add(user);

        String query = "DELETE FROM android_schema.user"+
                " WHERE android_schema.user.id = ?";
        return DatabaseAPI.executeQuery(input, query, "removeUser(BigInteger,Connection)",
                "remove user and their data from database",false,false,connection);
    }

    /**
     * Remove the Store from the database that has a matching name with the name provided.
     * Allow removal of Stores only to the owner of the Store or Admins.
     * @param storeName The unique name of the Store for removal.
     * @param userID The ID of the user requesting the removal.
     * @param connection A connection to the database used for the query execution.
     * @return The encoded and the humanly readable result of the Store removal. No database data is returned.
     */
    public static StatementResponse removeStore(@NotNull String storeName, @NotNull BigInteger userID, Connection connection){
        //Add PreparedStatement batch values.
        ArrayList<ArrayList<Object>> input = new ArrayList<>();
        ArrayList<Object> user = new ArrayList<>();
        user.add(storeName);
        user.add(userID);
        input.add(user);

        String query = "DELETE FROM android_schema.store"+
                " WHERE android_schema.store.name = ? AND android_schema.store.owner = ?";
        return DatabaseAPI.executeQuery(input, query, "removeStore(String,BigInteger,Connection)",
                "remove Store and its data from database",false,false,connection);
    }

    /**
     * Remove the Table from the database that has a matching name with the name provided.
     * Allow removal of Tables only to the owner of the Store, to which the Table belongs, or Admins.
     * @param tableName The unique name of the Table for removal.
     * @param storeName The unique name of the Store to which the Table belongs.
     * @param userID The ID of the user requesting the removal.
     * @return The encoded and the humanly readable result of the Table removal. No database data is returned.
     */
    public static StatementResponse removeTable(@NotNull String tableName, @NotNull String storeName, @NotNull BigInteger userID, Connection connection){
        //Add PreparedStatement batch values.
        ArrayList<ArrayList<Object>> input = new ArrayList<>();
        ArrayList<Object> user = new ArrayList<>();
        user.add(tableName);
        user.add(storeName);
        user.add(userID);
        input.add(user);

        String query = "DELETE android_schema.table"+
                " FROM android_schema.table"+
                " JOIN android_schema.store ON android_schema.store.id = android_schema.table.store"+
                " WHERE android_schema.table.name = ? AND android_schema.store.name = ? AND android_schema.store.owner = ?";
        return DatabaseAPI.executeQuery(input, query, "removeTable(String,String,Connection)",
                "remove table and its data from database",false,false,connection);
    }
}