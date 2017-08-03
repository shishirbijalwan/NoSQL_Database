package NoSql.Exception;

public class CollectionPresentException  extends Exception{
public CollectionPresentException(){
	super("Collection already present");
}
}
