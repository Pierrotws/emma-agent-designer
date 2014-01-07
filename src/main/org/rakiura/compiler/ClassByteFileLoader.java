package org.rakiura.compiler;

import java.io.File;
import java.io.FileInputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * Supports loading of class byte code from the file system.<P>
 *
 * The <A HREF="../../compiler/ClassByteFileLoader.java">source code</A> is available.
 * 
 * @author	Graham Kirby (<A HREF="mailto:graham@dcs.st-and.ac.uk">graham@dcs.st-and.ac.uk</A>)
 * @version 1.2 2-Nov-98
 */
public class ClassByteFileLoader implements ClassByteLoader {

	private Vector classPathVector;
	
	private Hashtable properties;

	/**
	 * @param classPathVector	vector of directories forming class path with index 0 being searched first
	 * @param properties		hash table containing entries for various constants including "packageSeparator" and "compiledSuffix"
	 */
	public ClassByteFileLoader (Vector classPathVector, Hashtable properties) {
	
		this.classPathVector = classPathVector;
		this.properties =      properties;
	}

	/**
	 * Finds the byte array corresponding to the given class name.
	 *
	 * @param name	a fully qualified class name
	 * @return		an array containing the byte code for the given class, or null if it is not found
	 */
	public byte[] get (String name) {
	
		byte[] classBytes = null;

		// Get the file containing the byte code.
		File classFile = findClassFile (name);

		try {

			// Create an input stream from the file.
			FileInputStream classFileInputStream = new FileInputStream (classFile);

			// Get the file length.
			int fileLength = classFileInputStream.available ();

			// Create an array the same length as the file to hold the compiled class.
			classBytes = new byte[ fileLength ];

			// Read class definition into the array.
			classFileInputStream.read (classBytes);
			
			// Close the file.
			classFileInputStream.close ();
		}
		catch (Exception e) {}

		return classBytes;
	}

	/**
	 * Finds the .class file corresponding to the given class name.
	 *
	 * @param fullClassName	a full class name
	 * @return				the file containing the byte code for the given class
	 */
	public File findClassFile (String fullClassName) {

		File classFile = null;
		
		String packageSeparator = (String) properties.get ("packageSeparator");
		String compiledSuffix =   (String) properties.get ("compiledSuffix");
		
		Enumeration classDirs = classPathVector.elements ();
		while (classDirs.hasMoreElements ()) {

			// Create tokenizer to extract the package name components.
			StringTokenizer st = new StringTokenizer (fullClassName, packageSeparator);

			// Start at the root directory.
			File packageDir = (File) classDirs.nextElement ();

			// Get the first token.
			String s = st.nextToken();

			// Add the sub-directories for the package structure in turn, if any.
			while ( st.hasMoreTokens() ) {

				// Generate the name of the next directory.
				packageDir = new File (packageDir, s);

				// Get the next token.
				s = st.nextToken();
			}

			// Get the class file.
			classFile = new File (packageDir, s + compiledSuffix);

			// Return the class file if found.
			if ( classFile.exists() ) break;
		}
		
		return classFile;
	}
}
