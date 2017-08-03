package NoSql.ObjectIdCreator;
import java.math.BigInteger;

public class ObjectId {
	private BigInteger id;

	public ObjectId(String str){
		id= new BigInteger(str);
	}

	public BigInteger getId() {
		return id;
	}

	public void setId(BigInteger id) {
		this.id = id;
	}
	
	public void incrementId(){
		
		id=id.add(BigInteger.ONE);

	}
}
