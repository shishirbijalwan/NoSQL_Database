package NoSql.Database;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import NoSql.Collection.Collection;
import NoSql.Exception.CollectionPresentException;
import NoSql.Exception.InvalidCollection;
import NoSql.Exception.InvalidOperation;
import NoSql.Exception.InvalidSyntax;
import NoSql.ObjectIdCreator.ObjectId;

public class Database {

	private HashMap<String, Collection> collections;
	private ObjectId objectId;
	private String DBname;

	public ObjectId getObjectId() {
		return objectId;
	}

	public JSONObject QueryEngine(String str) throws InvalidCollection,InvalidSyntax,InvalidOperation,CollectionPresentException{
		String collectionName=str.substring(0, str.indexOf("."));
		String queryPart=str.substring(str.indexOf("(")+1, str.lastIndexOf(")"));
		if(!collections.containsKey(collectionName) && (!str.contains("createColletion")))
			throw new InvalidCollection();
		if(str.contains("createColletion")){


			createCollection(queryPart);
		}else if(str.contains("insert(")){

			insert(collectionName,queryPart);
		}else if(str.contains("update(")){

			update(collectionName,queryPart);


		}else if(str.contains("delete(")){

			delete(collectionName,queryPart);


		}else if(str.contains("select(")){

			JSONArray temp=	select(collectionName,queryPart);
			for(int i=0;i<temp.length();i++)
			{ JSONObject js=null;
				 try {
					js=(JSONObject)temp.get(i);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println(js.toString());

			}

		}else if(str.contains("showAll(")){
			System.out.println(str);

		}else{
			throw new InvalidOperation();
		}


		return null;
	}
	public Database(ObjectId obj,String DatabaseName){

		collections= new HashMap<>();
		DBname=DatabaseName;
		objectId=obj;
		loadDataFromFiles();
	}

	private void loadDataFromFiles(){
		FileSystem fs= new FileSystem();
		fs.searchFiles();
		ArrayList<String> listOfFiles=fs.getFileNames();
		try{
			for(String fileName:listOfFiles)
			{
				int ix1 = fileName.lastIndexOf("/");
				String folderNamePath=fileName.substring(0,ix1);
				String FolderName	=folderNamePath.substring(folderNamePath.lastIndexOf("/")+1,folderNamePath.length());
				List<String> lines = Files.readAllLines(Paths.get(fileName), StandardCharsets.UTF_8);
				JSONObject js= new JSONObject(lines.get(lines.size()-1));

				if(collections.containsKey(FolderName)){
					collections.get(FolderName).insertCollectionList(js.get("objectId").toString(), js);

				}else{
					Collection newCollection= new Collection(objectId,FolderName,DBname);
					collections.put(FolderName, newCollection);
					newCollection.insertCollectionList(js.get("objectId").toString(), js);

				}

			}}catch(Exception e){

				System.out.println(e.getMessage());
			}
	}

	public boolean createCollection(String name) throws CollectionPresentException{

		if(collections.containsKey(name))
			throw new CollectionPresentException();
		Collection newCollection= new Collection(objectId,name,DBname);
		collections.put(name, newCollection);

		(new File("./Databases/"+DBname+"/"+name)).mkdirs();

		return true;
	}
	//insert operation
	public boolean insert(String name,String data) throws InvalidSyntax{

		boolean bool=collections.get(name).insert(data);

		return bool;
	}
	// delete operation
	public boolean delete(String name,String deleteQuery) throws InvalidSyntax{
	
		boolean bool=collections.get(name).delete(deleteQuery);


		return bool;
	}
	//update
	public boolean update(String name,String updateQuery) throws InvalidSyntax{
	
		boolean bool=collections.get(name).update(updateQuery);

		return bool;
	}
	//select 
	public JSONArray select(String name,String selectQuery) throws InvalidSyntax{
	
		return collections.get(name).select(selectQuery);

	}
	//print
	public void printAll(String name){
		if(collections.containsKey(name))
			collections.get(name).printAllData();

	}

	public static void main(String[] str){
		
		ObjectId obj= new ObjectId("0");
		Database db= new Database(obj,"Databases");
		//		db.createCollection("shishir");
		//		db.createCollection("Nilesh");
		//
		//		HashMap hm= new HashMap<>();
		//		hm.put("name", "Shishir");
		//		hm.put("age", 26);
		//		hm.put("sex", "Male");
		//
		//		HashMap hm2= new HashMap<>();
		//		hm2.put("name", "Arpit");
		//		hm2.put("age", 23);
		//		hm2.put("sex", "Female");
		//
		//		JSONObject js1= new JSONObject(hm);
		//
		//		db.insert("shishir",js1.toString());
		//		db.insert("Nilesh",js1.toString());
		//
		//		JSONObject js2= new JSONObject(hm2);
		//		db.insert("shishir",js2.toString());
		//		hm2.put("Address", "123");
		//
		//		js2= new JSONObject(hm2);
		//		db.insert("shishir",js2.toString());
		//		//db.printAll("shishir");
		//	//	db.printAll("Nilesh");
		//		
		//		
		//		//db.printAll("shishir");
		//
		//		
		//		//delete
		//		HashMap hm3= new HashMap<>();
		//		//hm3.put("age", 23);
		//		hm3.put("Address", "123");
		//
		//		JSONObject js3= new JSONObject(hm3);
		//		//db.delete("shishir",js3.toString());
		//		db.printAll("shishir");
		//		
		//		
		//		//update
		//		
		//		HashMap hm4= new HashMap<>();
		//		hm4.put("name", "rohit");
		//		hm4.put("place", "delhi");
		//		JSONObject js4= new JSONObject(hm4);
		//		
		//		JSONObject mainObj = new JSONObject();
		//
		//		try {
		//			mainObj.put("where", js3);
		//			mainObj.put("set", js4);
		//		} catch (JSONException e) {
		//			// TODO Auto-generated catch block
		//			e.printStackTrace();
		//		}
		//
		//		db.update("shishir",mainObj.toString());

		// insert operation	
		//db.printAll("shishir");
		//db.QueryEngine("shishir.insert({\"sex\":\"Male\",\"name\":\"tree\",\"age\":26})");

		//delete
		//	db.QueryEngine("shishir.delete({\"name\":\"tree\"})");
		//db.printAll("shishir");
		// update
		//db.QueryEngine("shishir.update({\"where\":{\"name\":\"tree\"},\"set\":{\"name\":\"tree2\"}})");
		//db.printAll("shishir");
		// select
		//db.QueryEngine("shishir.select({\"where\":{\"sex\":\"Female\"},\"select\":{\"name\":\"1\",\"age\":\"1\"}})");
		//db.QueryEngine("shishir.select({\"where\":{},\"select\":{}})");
		//create collection
		//	db.QueryEngine("db.createColletion(Mahesh)");
		//db.QueryEngine("Mahesh.insert({\"sex\":\"Male\",\"name\":\"tree\",\"age\":26})");
		//	db.printAll("Mahesh");
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
					db.QueryEngine(Query);

				}catch(Exception e){
					System.out.println(e.getMessage());
					
				}

				
		}
		System.out.println("Quiting");

		try {
			PrintWriter writer = new PrintWriter("./Databases/ObjectId", "UTF-8");
			writer.println(db.getObjectId().getId());
			writer.close();
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}
}
