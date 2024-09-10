package tools;

import java.io.File;
import java.util.ArrayList;

import javax.swing.filechooser.FileFilter;

public class ErgoFileFilter extends FileFilter 
{
	ArrayList<String> extensions = new ArrayList<String>();
	String description = "";

	public boolean accept(File file) {
		String fileName = file.getAbsolutePath();
		String extension = "";
		
		if(!file.isDirectory() && fileName.lastIndexOf(".") != -1)
			extension = fileName.substring(fileName.lastIndexOf("."),fileName.length());
		else 
			return true;
		
		if(extensions.contains(extension.toLowerCase()))
			return true;
		
		return false;
	}

	public void addExtension(String extension)
	{
		int pos = extension.indexOf(".");
		if(pos > 0)
			extension = extension.substring(pos,extension.length());
		extensions.add(extension.toLowerCase());
	}
	
	public String getDescription()
	{
		return description;
	}
	
	public void setDescription(String newDescription)
	{
		description = newDescription;
	}
}
