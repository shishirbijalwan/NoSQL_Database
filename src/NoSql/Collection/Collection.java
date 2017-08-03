package NoSql.Collection;
	import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import NoSql.Exception.InvalidSyntax;
import NoSql.ObjectIdCreator.ObjectId;

public class Collection {

	private BigInteger objectId;
	private ObjectId   objectIdGetter;
	private String collectionName;
	private HashMap<BigInteger,JSONObject> collectionlist;
	private TreeMap<Object, ArrayList<BigInteger>> indexMap;
	private boolean indexFlag;
	private String indexColumn;
	private String databaseName;

	public Collection(ObjectId bigInt,String name,String DatabaseName){

		objectIdGetter=bigInt;
		collectionlist = new HashMap<>();
		indexMap= new TreeMap<>();
		indexFlag=false;
		collectionName=name;
		databaseName=DatabaseName;
	}

	public void insertCollectionList(String bigInt,JSONObject js){
		BigInteger	objectId= new BigInteger(bigInt);

		collectionlist.put(objectId, js);
	}
	public boolean insert(String data) throws InvalidSyntax{

		try{
			JSONObject temp= new JSONObject(data);
			objectId=objectIdGetter.getId();
			temp.put("objectId", objectId);
			collectionlist.put(objectId, temp);
			objectIdGetter.incrementId();
			   PrintWriter writer = new PrintWriter("./Databases/"+databaseName+"/"+collectionName+"/"+objectId.toString()+".data", "UTF-8");
			    writer.println(temp.toString());
			    writer.close();

		}catch(Exception ex){
			throw new InvalidSyntax();

		}

		System.out.println("Rows inserted 1");

		return true;
	}

	public void indexing(String columnName){

		try{
			for (Map.Entry currentvar : collectionlist.entrySet()) {
				JSONObject temp=(JSONObject)currentvar.getValue();
				if(indexMap.containsKey(temp.get(columnName))){
					indexMap.get(temp.get(columnName)).add((BigInteger)currentvar.getKey());

				}else{
					ArrayList<BigInteger> localList= new ArrayList<>();
					localList.add((BigInteger)currentvar.getKey());
					indexMap.put(temp.get(columnName), localList);

				}
				System.out.println("Key is "+currentvar.getKey()+"  and value "+currentvar.getValue());
			}}
		catch(Exception e){
			System.out.println(e.getMessage());

		}
		indexFlag=true;
		indexColumn=columnName;
	}

	public ArrayList<BigInteger> getObjectId(String wherePart){
		ArrayList<BigInteger> selectedObjectId= new ArrayList<>();

		try{
			JSONObject temp= new JSONObject(wherePart);

			String[] columns= JSONObject.getNames(temp);

			if(indexFlag && columns.length==1 && Arrays.asList(columns).contains(indexColumn) ){
				for(BigInteger bigint:indexMap.get(temp.get(indexColumn))){
					selectedObjectId.add(bigint);

				}

			}else{
				temp= new JSONObject(wherePart);
				if(temp.length()==0)
				{
					//System.out.println("where clause empty");

					for (Map.Entry currentvar : collectionlist.entrySet()) {

						selectedObjectId.add((BigInteger)currentvar.getKey());
					}

					return selectedObjectId;
				}
				Iterator it= temp.keys();
				ArrayList<String> keyList= new ArrayList<>();
				while(it.hasNext()){

					keyList.add((String) it.next());

				}

				Iterator iter = collectionlist.entrySet().iterator();
				while (iter.hasNext())
				{

					Map.Entry<BigInteger,JSONObject> entry= (Map.Entry<BigInteger,JSONObject>) iter.next();
					JSONObject jsonObject= (JSONObject) entry.getValue();


					boolean flag=true;
					for(String keyName:keyList){


						if(!jsonObject.has(keyName)){
							flag=false;
							break;

						}
    if(temp.get(keyName) instanceof JSONObject){
    	JSONObject comparisonJS= (JSONObject)temp.get(keyName);
    	String []str=JSONObject.getNames(comparisonJS);
    	  double userProvided=Double.parseDouble(comparisonJS.get(str[0]).toString());
    	  double dataValue=Double.parseDouble(jsonObject.get(keyName).toString());
    	if(str[0].equals(">") ){
       if(userProvided>dataValue)
			flag=false;

    	}else{

        	   if(userProvided <dataValue)
       			flag=false;
    	}
    	
    }else{
    	if(!(temp.get(keyName).equals(jsonObject.get(keyName)))){
			flag=false;
		}
    	
    }
						
					
					}
					if(flag)
						selectedObjectId.add((BigInteger)entry.getKey());
				}


			}

		}catch(Exception ex){
			System.out.println(ex.getMessage());

		}

		return selectedObjectId;
	}

	//update
	public boolean update(String updateQuery) throws InvalidSyntax{

		//getting key to be updated
		ArrayList<BigInteger> affectedObjectId= new ArrayList<>();
		try {

			JSONObject jsonObj= new JSONObject(updateQuery);
			if(jsonObj.has("where")){

				JSONObject whereObject= jsonObj.getJSONObject("where");
				affectedObjectId=getObjectId(whereObject.toString());

				JSONObject setObject= jsonObj.getJSONObject("set");

				for(BigInteger select:affectedObjectId){
					String[] updatedKeyNames= JSONObject.getNames(setObject);

					for(String key:updatedKeyNames)
						collectionlist.get(select).put(key, setObject.get(key));

				    Files.write(Paths.get("./Databases/"+databaseName+"/"+collectionName+"/"+select.toString()+".data"), collectionlist.get(select).toString().getBytes(), StandardOpenOption.APPEND);

				}
			}




		} catch (Exception e) {
			throw new InvalidSyntax();
		}


System.out.println("Rows updated "+affectedObjectId.size());
		return true;
	}

	// delete record	
	public boolean delete(String deleteQuery) throws InvalidSyntax{
		ArrayList<BigInteger> selectedIds;
		try{


			 selectedIds=getObjectId(deleteQuery);

			for(BigInteger key:selectedIds){
				collectionlist.remove(key);
			    Files.delete(Paths.get("./Databases/"+databaseName+"/"+collectionName+"/"+key.toString()+".data"));

				
			}

		}catch(Exception ex){
			throw new InvalidSyntax();

		}

		System.out.println("Rows deleted "+selectedIds.size());

		return true;
	}

	//select
	public JSONArray select(String updateQuery) throws InvalidSyntax{

		//getting key to be updated
		JSONArray returnArray= new JSONArray();

		ArrayList<BigInteger> affectedObjectId= new ArrayList<>();
		try {

			JSONObject jsonObj= new JSONObject(updateQuery);
			//getting object id which needs to be updated
			if(jsonObj.has("where")){

				JSONObject whereObject= jsonObj.getJSONObject("where");
				affectedObjectId=getObjectId(whereObject.toString());

				JSONObject selectObject= jsonObj.getJSONObject("select");
			
				if(selectObject.length()==0)
				{
					for(BigInteger select:affectedObjectId){

						returnArray.put(collectionlist.get(select));
					}
					return returnArray;
				}

				for(BigInteger select:affectedObjectId){
					String[] updatedKeyNames= JSONObject.getNames(selectObject);
					JSONObject returnObject= new JSONObject();
					for(String key:updatedKeyNames){

						returnObject.put(key, collectionlist.get(select).get(key));


					}
					returnArray.put(returnObject);

				}
			}




		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new InvalidSyntax();
		}



		return returnArray;
	}

	public void printAllData(){

		for (Map.Entry currentvar : collectionlist.entrySet()) {

			System.out.println("Key is "+currentvar.getKey()+"  and value "+currentvar.getValue());
		}

	}

	public static void main(String[] str){
		try{
		BigInteger	objectId= new BigInteger("0");
ObjectId obj= new ObjectId("0");
		Collection col= new Collection(obj,"name","Databases");
		HashMap hm= new HashMap<>();
		hm.put("name", "Shishir");
		hm.put("age", 26);
	
		hm.put("sex", "Male");

		HashMap hm2= new HashMap<>();
		hm2.put("name", "Arpit");
		hm2.put("age", 23);
		hm2.put("sex", "Female");

		JSONObject js1= new JSONObject(hm);

		col.insert(js1.toString());

		JSONObject js2= new JSONObject(hm2);
		col.insert(js2.toString());
		hm2.put("Address", "123");

		js2= new JSONObject(hm2);
		col.insert(js2.toString());

		col.printAllData();
		HashMap hm3= new HashMap<>();
		hm3.put("age", 23);
		hm3.put("Address", "123");

		JSONObject js3= new JSONObject(hm3);

		ArrayList<BigInteger> selectedId= col.getObjectId(js3.toString());

		for(BigInteger select:selectedId){
			System.out.println(select);

		}
		System.out.println("After delete");
		//col.delete(js3.toString());
		col.printAllData();

		JSONArray ja = new JSONArray();
		ja.put(js3);
		JSONObject mainObj = new JSONObject();

		HashMap hm4= new HashMap<>();
		hm4.put("name", "rohit");
		hm4.put("place", "delhi");
		JSONObject js4= new JSONObject(hm4);
		JSONArray ja2 = new JSONArray();
		ja2.put(js4);
		try {
			mainObj.put("where", js3);
			mainObj.put("set", js4);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}




		System.out.println(mainObj);
		System.out.println("Calling update");

		col.update(mainObj.toString());
		col.printAllData();

		//select

		System.out.println("Calling select");
		HashMap hmselectwhere= new HashMap<>();
		hmselectwhere.put("sex", "Female");
		JSONObject jsUpdatewhere= new JSONObject(hmselectwhere);



		HashMap hmselectColumn= new HashMap<>();
		hmselectColumn.put("name", "1");

		hmselectColumn.put("sex", "1");


		JSONObject jsselecteName= new JSONObject(hmselectColumn);



		JSONObject mainObjUpdate = new JSONObject();

		try {
			mainObjUpdate.put("where", jsUpdatewhere);
			mainObjUpdate.put("select", jsselecteName);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		col.select(mainObjUpdate.toString());

		//
		System.out.println("Calling indexing");

		col.indexing("name");


		HashMap hmindexSearch= new HashMap<>();
		hmindexSearch.put("name", "rohit");
		JSONObject jssearch= new JSONObject(hmindexSearch);


		ArrayList<BigInteger> selectedId2= col.getObjectId(jssearch.toString());

		for(BigInteger select:selectedId2){
			System.out.println(select);

		}

		}catch(Exception e){
			
			
		}
	}


}
