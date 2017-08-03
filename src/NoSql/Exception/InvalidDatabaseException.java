package NoSql.Exception;

public class InvalidDatabaseException extends Exception {
public InvalidDatabaseException(){
	super("Database name not found!!");
}
}
