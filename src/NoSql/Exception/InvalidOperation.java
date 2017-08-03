package NoSql.Exception;

public class InvalidOperation extends Exception {

	public InvalidOperation(){
		
		super("Operation allowed are:Insert,Update,Delete and Select");
	}
}
