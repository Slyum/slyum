package dataRecord.elements;

import classDiagram.ClassDiagram;
import dataRecord.Keyword;
import dataRecord.io.ElementVisitor;

/**
 * This class represent both import statement and package statement
 * 
 * @see ImportStmt 
 * @see PackageStmt 
 * 
 * @author Fabrizio Beretta Piccoli
 * @version 2.0 | 10-lug-2012
 *
 */
public abstract class Statement implements Element
{
	private Keyword kw;
	private String packageName;
	private int ID;

	public Statement(Keyword kw, String packageName)
	{
		this.kw = kw;
		this.packageName = packageName;
		this.ID = ClassDiagram.getElementID();
	}
	
	public Statement(Keyword kw, String packageName, int ID)
	{
		this.kw = kw;
		this.packageName = packageName;
		this.ID = ID;
	}

	public String toString()
	{
		return kw + " " + packageName + ";";
	}

	public String getPackageName()
	{
		return packageName;
	}

	public String getName()
	{
		return packageName;
	}
	
	public int getID()
	{
		return ID;
	}
	
	/**
	 * this method will be called by the writer to know
	 * how to write this object.
	 * 
	 * @see ElementVisitor
	 */
	public abstract String accept(ElementVisitor visitor);

}
