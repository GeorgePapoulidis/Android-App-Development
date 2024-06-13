package Server;

/**
 * Contains an encoded textual representation of all possible outcomes when accessing a Server API endpoint.
 */
public enum ServerExitCode {
    Success,
    DatabaseError,
    NullUserName,
    NullPassword,
    NullFullName,
    NullEmail,
    NullUserID,
    NullOwnerID,
    NullStoreID,
    NullStoreName,
    NullTableName,
    NullTableState,
    SmallUserName,
    SmallPassword,
    WeakPassword,
    InvalidEmail,
    InvalidXDimension,
    InvalidYDimension,
    InvalidPeopleNumber,
    UnauthorizedID,
    StoreNotFound,
    TableNotFound,
    UserIDNotFound,
    UserNameNotFound,
    UserNameTooLong,
    FullNameTooLong,
    WrongPassword,
    UserNameExists,
    EmailExists,
    StoreNameExists,
    StoreNameTooLong,
    TableNameExists,
    TableNameTooLong,

}
