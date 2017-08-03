package NoSql.QueryEngine;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.TreeMap;

import org.json.JSONArray;
import org.json.JSONObject;

import NoSql.Database.Database;
import NoSql.Exception.InvalidDatabaseException;
import NoSql.ObjectIdCreator.ObjectId;

public class QueryEngine {
	private static QueryEngine instance=null;
	private ObjectId objectId;
	public ObjectId getObjectId() {
		return objectId;
	}

	private HashMap<String, Database> dataBaseCollection;
	private Database currentDatabase;
	private QueryEngine(){


		try {
			dataBaseCollection= new HashMap<>();
			List<String> lines = Files.readAllLines(Paths.get("./Databases/ObjectId"), StandardCharsets.UTF_8);
			//System.out.println("Object id="+ lines.get(0));
			objectId= new ObjectId(lines.get(0));
			loadData();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	static public QueryEngine getInstance(){
		if(instance!=null)
			return instance;
		else{
			instance= new QueryEngine();
			return instance;
		}
	}

	public void loadData(){

		File[] directories = new File("./Databases/").listFiles(File::isDirectory);
		for(File file:directories){
			//System.out.println(file.getName());
			Database db= new Database(objectId, file.getName());
			dataBaseCollection.put(file.getName(), db);

		}

	}

	public void useDatabase(String name){

		if(dataBaseCollection.containsKey(name)){
			currentDatabase=dataBaseCollection.get(name);
		}else{

			Database db= new Database(objectId, name);
			dataBaseCollection.put(name, db);
			new File("./Databases/"+name).mkdirs();
			currentDatabase=db;
		}

	}

	public JSONObject queryDatabase(String str) throws Exception{
		if(str.contains("Create database ")){
			String lastWord = str.substring(str.lastIndexOf(" ")+1);
			Database db= new Database(objectId, lastWord);
			dataBaseCollection.put(lastWord, db);
			new File("./Databases/"+lastWord).mkdirs();
			currentDatabase=db;

			return null;
		}else{
			String dbName = str.substring(0,str.indexOf("."));
			String Query = str.substring(str.indexOf(".")+1);


			if(dataBaseCollection.containsKey(dbName))
				return dataBaseCollection.get(dbName).QueryEngine(Query);
			else
				throw new InvalidDatabaseException();
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		QueryEngine qEngine=QueryEngine.getInstance();


		System.out.println("Database started");


		while(true){
			String Query;
			System.out.print(">>");

			Scanner in= new Scanner(System.in);
			Query=	in.nextLine();
			if(Query.equals("quit")){
				break;

			}
			try{
				qEngine.queryDatabase(Query);

			}catch(Exception e){
				System.out.println(e.getMessage());

			}


		}
		System.out.println("Quiting");

		try {
			PrintWriter writer = new PrintWriter("./Databases/ObjectId", "UTF-8");
			writer.println(qEngine.getObjectId().getId());
			writer.close();
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
