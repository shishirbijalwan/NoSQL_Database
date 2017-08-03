package NoSql.Exception;

public class InvalidCollection extends Exception {

	public InvalidCollection(){
		
		super("Collection name not found");
	}
}
