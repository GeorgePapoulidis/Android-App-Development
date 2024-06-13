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


public class DatabaseAPI {
    private static final int VALIDATION_TIMEOUT=10;
    public DatabaseAPI(){
    }
    public static ConnectionResponse connect(Connection connection) {
        if (connection != null){
            try {
                if (connection.isValid(VALIDATION_TIMEOUT)) {
                    return new ConnectionResponse(connection,"", DatabaseExitCode.Success);
                }
            } catch (Exception ignored) {
            }
        }

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
    private static StatementResponse executeQuery(@NotNull ArrayList<ArrayList<Object>> tableRows,
                                                  @NotNull String query, String callerMethod,
                                                  String goalOfMethod, boolean returnResults, boolean allowScroll, Connection connection){
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

            try {
                if (tableRows.isEmpty()){
                    return new StatementResponse(null,"", DatabaseExitCode.EmptyQuery);
                }
                for (ArrayList<Object> tableRow : tableRows) {
                    for (int j = 0; j < tableRow.size(); j++) {
                        Object obj = tableRow.get(j);
                        /*switch (obj) {
                            case Integer ignored1 -> statement.setInt(j + 1, (int) obj);
                            case BigInteger ignored2 -> statement.setObject(j + 1, obj, Types.BIGINT);
                            case String s -> statement.setString(j + 1, s);
                            default -> {
                                String exitMessage = "Error occurred at method DatabaseAPI." + callerMethod + ":\n";
                                exitMessage = exitMessage.concat("Preparing statement to " + goalOfMethod + ": " +
                                        "Parameter provided has unrecognized type.");
                                try {
                                    statement.close();
                                } catch (Exception ignored) {
                                }
                                return new StatementResponse(null, exitMessage, DatabaseExitCode.InvalidParameterType);
                            }
                        }*/

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
                        return new StatementResponse(null, "", DatabaseExitCode.Success);
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
    @NotNull
    private static DatabaseExitCode getConstraintExitCode(SQLException e) {
        DatabaseExitCode dec= DatabaseExitCode.QueryExecutionFailed;
        if (e.getSQLState().equals("23505")){
            dec = DatabaseExitCode.KeyConflict;
            String error= e.getMessage();
            if (error.contains("user_email_key") || error.contains("user_username_key") || error.contains("unique_name_per_store")
                    || error.contains("store_name_key")){
                dec=DatabaseExitCode.UniqueFieldConflict;
            }
        }
        if (e.getSQLState().equals("23503")){
            dec=DatabaseExitCode.ForeignKeyViolation;
        }
        return dec;
    }

    public static StatementResponse addUser(@NotNull String fullName, @NotNull String username, @NotNull String password, @NotNull String email, Connection connection){
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

        byte[] hashedPassword;
        try {
            hashedPassword = factory.generateSecret(spec).getEncoded();
        } catch (InvalidKeySpecException ex) {
            String exitMessage = "Error occurred at method DatabaseAPI.addUser():\n";
            exitMessage = exitMessage.concat("Hashing password: "+
                    "KeySpec for password was invalid.");
            return new StatementResponse(null,exitMessage, DatabaseExitCode.InvalidKeySpec);
        }

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
    public static StatementResponse addStore(@NotNull String name, @NotNull  BigInteger ownerID, int x, int y, Connection connection){
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
    public static StatementResponse addTable(@NotNull String tableName, @NotNull BigInteger storeID, int x, int y, int people,
                                             @NotNull String state, Connection connection){
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
    public static StatementResponse logIn(@NotNull String usernameConstraint, @NotNull String password, Connection connection){
        ArrayList<ArrayList<Object>> input = new ArrayList<>();
        ArrayList<Object> user = new ArrayList<>();
        user.add(usernameConstraint);
        input.add(user);

        String query = "SELECT android_schema.user.ID,android_schema.user.password_salt,android_schema.user.password_hashed " +
                "FROM android_schema.user " +
                "WHERE android_schema.user.username = ? " +
                "LIMIT 1;";
        StatementResponse statementResponse = DatabaseAPI.executeQuery(input, query, "logIn(String,String,Connection)",
                "get information of user with specified Username", true, true, connection);
        byte[] salt;
        byte[] databaseHashedPassword;
        boolean error=true;
        try {
            if (statementResponse.getStatement().getResultSet().next()){
                statementResponse.getStatement().getResultSet().getBigDecimal("ID").toBigIntegerExact();
                //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                salt = Base64.getDecoder().decode(statementResponse.getStatement().getResultSet().getString("password_salt"));
                //}
                //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                databaseHashedPassword = Base64.getDecoder().decode(statementResponse.getStatement().getResultSet().getString("password_hashed"));
                //}
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

        byte[] hashedPassword;
        try {
            hashedPassword = factory.generateSecret(spec).getEncoded();
        } catch (InvalidKeySpecException ex) {
            String exitMessage = "Error occurred at method DatabaseAPI.logIn(String,String,Connection):\n";
            exitMessage = exitMessage.concat("Hashing password: "+
                    "KeySpec for password was invalid.");
            return new StatementResponse(null,exitMessage, DatabaseExitCode.InvalidKeySpec);
        }

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
    public static StatementResponse getStore(@NotNull String storeName, Connection connection){
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
    public static StatementResponse getStores(Connection connection){
        ArrayList<ArrayList<Object>> input = new ArrayList<>();
        ArrayList<Object> store = new ArrayList<>();
        input.add(store);

        String query = "SELECT * " +
                "FROM android_schema.store ";
        return DatabaseAPI.executeQuery(input,query,"getStores(Connection)",
                "get every Store's information", true,false,connection);
    }
    public static StatementResponse getTables(@NotNull String storeName, Connection connection){
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
    public static StatementResponse getStoreOwner(@NotNull String storeName, Connection connection){
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
    public static StatementResponse removeUser(@NotNull BigInteger userID, Connection connection){
        ArrayList<ArrayList<Object>> input = new ArrayList<>();
        ArrayList<Object> user = new ArrayList<>();
        user.add(userID);
        input.add(user);

        String query = "DELETE FROM android_schema.user"+
                " WHERE android_schema.user.id = ?";
        return DatabaseAPI.executeQuery(input, query, "removeUser(BigInteger,Connection)",
                "remove user and their data from database",false,false,connection);
    }
    public static StatementResponse removeStore(@NotNull String storeName, @NotNull BigInteger userID, Connection connection){
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
    public static StatementResponse removeTable(@NotNull String tableName, @NotNull String storeName, @NotNull BigInteger userID, Connection connection){
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