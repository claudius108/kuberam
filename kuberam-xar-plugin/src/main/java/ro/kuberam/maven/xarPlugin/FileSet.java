package ro.kuberam.maven.xarPlugin;

//---------------------------------/
//- Imported classes and packages -/
//---------------------------------/

import java.util.Date;

/**
 * 
 * A fileSet allows the inclusion of groups of files into the assembly.
 * 
 * 
 * @version $Revision$ $Date$
 */
public class FileSet extends SetBase implements java.io.Serializable {

	// --------------------------/
	// - Class/Member Variables -/
	// --------------------------/

	/**
	 * 
	 * Sets the absolute or relative location from the module's directory. For
	 * example, "src/main/bin" would select this subdirectory of the project in
	 * which this dependency is defined.
	 * 
	 */
	private String directory;

	/**
	 * 
	 * 
	 * Sets the line-endings of the files in this fileSet. Valid values:
	 * <ul>
	 * <li><b>"keep"</b> - Preserve all line endings</li>
	 * <li><b>"unix"</b> - Use Unix-style line endings</li>
	 * <li><b>"lf"</b> - Use a single line-feed line endings</li>
	 * <li><b>"dos"</b> - Use DOS-style line endings</li>
	 * <li><b>"crlf"</b> - Use Carraige-return, line-feed line endings</li>
	 * </ul>
	 * 
	 * 
	 */
	private String lineEnding;

	/**
	 * 
	 * Whether to filter symbols in the files as they are copied, using
	 * properties from the build configuration.
	 * 
	 */
	private boolean filtered = false;

	// -----------/
	// - Methods -/
	// -----------/

	/**
	 * Get Sets the absolute or relative location from the module's directory.
	 * For example, "src/main/bin" would select this subdirectory of the project
	 * in which this dependency is defined.
	 * 
	 * 
	 * @return String
	 */
	public String getDirectory() {
		return this.directory;
	} // -- String getDirectory()

	/**
	 * Get
	 * 
	 * Sets the line-endings of the files in this fileSet. Valid values:
	 * <ul>
	 * <li><b>"keep"</b> - Preserve all line endings</li>
	 * <li><b>"unix"</b> - Use Unix-style line endings</li>
	 * <li><b>"lf"</b> - Use a single line-feed line endings</li>
	 * <li><b>"dos"</b> - Use DOS-style line endings</li>
	 * <li><b>"crlf"</b> - Use Carraige-return, line-feed line endings</li>
	 * </ul>
	 * 
	 * 
	 * 
	 * @return String
	 */
	public String getLineEnding() {
		return this.lineEnding;
	} // -- String getLineEnding()

	/**
	 * Get Whether to filter symbols in the files as they are copied, using
	 * properties from the build configuration.
	 * 
	 * 
	 * @return boolean
	 */
	public boolean isFiltered() {
		return this.filtered;
	} // -- boolean isFiltered()

	/**
	 * Set Sets the absolute or relative location from the module's directory.
	 * For example, "src/main/bin" would select this subdirectory of the project
	 * in which this dependency is defined.
	 * 
	 * 
	 * @param directory
	 */
	public void setDirectory(String directory) {
		this.directory = directory;
	} // -- void setDirectory( String )

	/**
	 * Set Whether to filter symbols in the files as they are copied, using
	 * properties from the build configuration.
	 * 
	 * 
	 * @param filtered
	 */
	public void setFiltered(boolean filtered) {
		this.filtered = filtered;
	} // -- void setFiltered( boolean )

	/**
	 * Set
	 * 
	 * Sets the line-endings of the files in this fileSet. Valid values:
	 * <ul>
	 * <li><b>"keep"</b> - Preserve all line endings</li>
	 * <li><b>"unix"</b> - Use Unix-style line endings</li>
	 * <li><b>"lf"</b> - Use a single line-feed line endings</li>
	 * <li><b>"dos"</b> - Use DOS-style line endings</li>
	 * <li><b>"crlf"</b> - Use Carraige-return, line-feed line endings</li>
	 * </ul>
	 * 
	 * 
	 * 
	 * @param lineEnding
	 */
	public void setLineEnding(String lineEnding) {
		this.lineEnding = lineEnding;
	} // -- void setLineEnding( String )

}