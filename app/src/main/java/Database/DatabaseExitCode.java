package Database;

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
