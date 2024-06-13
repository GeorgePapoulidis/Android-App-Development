package Database;

/**
 * Contains a textual representation of all possible outcomes when attempting to execute a query to the database.
 */
public enum DatabaseExitCode {
    Success,
    EmptyQuery,
    InvalidParameterType,
    ConnectingTimeOut,
    ConnectingFailed,
    ConnectionValidationTimeOut,
    InvalidAPIParameters,
    PreparedStatementCreationFailed,
    InvalidHashingAlgorithm,
    InvalidKeySpec,
    ErrorSettingQueryParameters,
    QueryExecutionFailed,
    QueryExecutionTimeOut,
    ResultRetrievalFailed,
    ResultRetrievalCastingFailed,
    ErrorRollingResultSetBackwards,
    ForeignKeyViolation,
    UnmatchedConstraint,
    UnmetCondition,
    KeyConflict,
    UniqueFieldConflict
}
