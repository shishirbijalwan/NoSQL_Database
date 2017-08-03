package NoSql.Database;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileSystem {
	private ArrayList<String> fileNames;

	public FileSystem(){
		
		fileNames= new ArrayList<>();
	}
	public ArrayList<String> getFileNames() {
		return fileNames;
	}

	public void setFileNames(ArrayList<String> fileNames) {
		this.fileNames = fileNames;
	}
	
	public void searchFiles(){
		
		fileNames.clear();
		searchhelper("./Databases/");
	}
	private void searchhelper(String path){
		File folder = new File(path);
		File[] listOfFiles = folder.listFiles();
		for(File file:listOfFiles){
			if(file.isFile() && file.getName().endsWith(".data"))
			fileNames.add(file.getPath());
		}
		File[] directories = new File(path).listFiles(File::isDirectory);
		for(File file:directories){
			searchhelper(file.toString());
		}
		
	}
	
	public static void main(String[] str){
		FileSystem fs= new FileSystem();
		fs.searchFiles();
		ArrayList<String> arr= fs.getFileNames();
		
		for(String string:arr){
			System.out.println(string);
			
			try {
				List<String> lines = Files.readAllLines(Paths.get(string), StandardCharsets.UTF_8);
				System.out.println(lines.get(lines.size()-1));
				JSONObject js= new JSONObject(lines.get(lines.size()-1));
				System.out.println(js.get("objectId").toString());

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

}
