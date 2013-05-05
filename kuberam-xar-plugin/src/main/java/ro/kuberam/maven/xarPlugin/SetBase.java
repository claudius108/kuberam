package ro.kuberam.maven.xarPlugin;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

import java.util.Date;

/**
 * Class SetBase.
 * 
 * @version $Revision$ $Date$
 */
public class SetBase
    implements java.io.Serializable
{

      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * 
     *           When specified as true, any include/exclude
     * patterns which aren't used to filter an actual
     *           artifact during assembly creation will cause the
     * build to fail with an error. This is meant
     *           to highlight obsolete inclusions or exclusions, or
     * else signal that the assembly descriptor
     *           is incorrectly configured.
     *           
     */
    private boolean useStrictFiltering = false;

    /**
     * 
     *             Whether standard exclusion patterns, such as
     * those matching CVS and Subversion
     *             metadata files, should be used when calculating
     * the files affected by this set.
     *             For backward compatibility, the default value is
     * true.
     *           
     */
    private boolean useDefaultExcludes = true;

    /**
     * 
     *             Sets the output directory relative to the root
     *             of the root directory of the assembly. For
     * example,
     *             "log" will put the specified files in the log
     * directory.
     *           
     */
    private String outputDirectory;

    /**
     * Field includes.
     */
    private java.util.List/*<String>*/ includes;

    /**
     * Field excludes.
     */
    private java.util.List/*<String>*/ excludes;

    /**
     * 
     *             
     *             Similar to a UNIX permission, sets the file mode
     * of the files included.
     *             Format: (User)(Group)(Other) where each
     * component is a sum of Read = 4,
     *             Write = 2, and Execute = 1.  For example, the
     * value 0644
     *             translates to User read-write, Group and Other
     * read-only.
     *             <a
     * href="http://www.onlamp.com/pub/a/bsd/2000/09/06/FreeBSD_Basics.html">(more
     * on unix-style permissions)</a>
     *           	
     *           
     */
    private String fileMode;

    /**
     * 
     *             
     *             Similar to a UNIX permission, sets the directory
     * mode of the directories
     *             included. Format: (User)(Group)(Other) where
     * each component is a sum of
     *             Read = 4, Write = 2, and Execute = 1.  For
     * example, the value
     *             0755 translates to User read-write, Group and
     * Other read-only.
     *             <a
     * href="http://www.onlamp.com/pub/a/bsd/2000/09/06/FreeBSD_Basics.html">(more
     * on unix-style permissions)</a>
     *           	
     *           
     */
    private String directoryMode;


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method addExclude.
     * 
     * @param string
     */
    public void addExclude( String string )
    {
        if ( !(string instanceof String) )
        {
            throw new ClassCastException( "SetBase.addExcludes(string) parameter must be instanceof " + String.class.getName() );
        }
        getExcludes().add( string );
    } //-- void addExclude( String )

    /**
     * Method addInclude.
     * 
     * @param string
     */
    public void addInclude( String string )
    {
        if ( !(string instanceof String) )
        {
            throw new ClassCastException( "SetBase.addIncludes(string) parameter must be instanceof " + String.class.getName() );
        }
        getIncludes().add( string );
    } //-- void addInclude( String )

    /**
     * Get 
     *             
     *             Similar to a UNIX permission, sets the directory
     * mode of the directories
     *             included. Format: (User)(Group)(Other) where
     * each component is a sum of
     *             Read = 4, Write = 2, and Execute = 1.  For
     * example, the value
     *             0755 translates to User read-write, Group and
     * Other read-only.
     *             <a
     * href="http://www.onlamp.com/pub/a/bsd/2000/09/06/FreeBSD_Basics.html">(more
     * on unix-style permissions)</a>
     *           	
     *           
     * 
     * @return String
     */
    public String getDirectoryMode()
    {
        return this.directoryMode;
    } //-- String getDirectoryMode()

    /**
     * Method getExcludes.
     * 
     * @return List
     */
    public java.util.List/*<String>*/ getExcludes()
    {
        if ( this.excludes == null )
        {
            this.excludes = new java.util.ArrayList/*<String>*/();
        }

        return this.excludes;
    } //-- java.util.List/*<String>*/ getExcludes()

    /**
     * Get 
     *             
     *             Similar to a UNIX permission, sets the file mode
     * of the files included.
     *             Format: (User)(Group)(Other) where each
     * component is a sum of Read = 4,
     *             Write = 2, and Execute = 1.  For example, the
     * value 0644
     *             translates to User read-write, Group and Other
     * read-only.
     *             <a
     * href="http://www.onlamp.com/pub/a/bsd/2000/09/06/FreeBSD_Basics.html">(more
     * on unix-style permissions)</a>
     *           	
     *           
     * 
     * @return String
     */
    public String getFileMode()
    {
        return this.fileMode;
    } //-- String getFileMode()

    /**
     * Method getIncludes.
     * 
     * @return List
     */
    public java.util.List/*<String>*/ getIncludes()
    {
        if ( this.includes == null )
        {
            this.includes = new java.util.ArrayList/*<String>*/();
        }

        return this.includes;
    } //-- java.util.List/*<String>*/ getIncludes()

    /**
     * Get 
     *             Sets the output directory relative to the root
     *             of the root directory of the assembly. For
     * example,
     *             "log" will put the specified files in the log
     * directory.
     *           
     * 
     * @return String
     */
    public String getOutputDirectory()
    {
        return this.outputDirectory;
    } //-- String getOutputDirectory()

    /**
     * Get 
     *             Whether standard exclusion patterns, such as
     * those matching CVS and Subversion
     *             metadata files, should be used when calculating
     * the files affected by this set.
     *             For backward compatibility, the default value is
     * true.
     *           
     * 
     * @return boolean
     */
    public boolean isUseDefaultExcludes()
    {
        return this.useDefaultExcludes;
    } //-- boolean isUseDefaultExcludes()

    /**
     * Get 
     *           When specified as true, any include/exclude
     * patterns which aren't used to filter an actual
     *           artifact during assembly creation will cause the
     * build to fail with an error. This is meant
     *           to highlight obsolete inclusions or exclusions, or
     * else signal that the assembly descriptor
     *           is incorrectly configured.
     *           
     * 
     * @return boolean
     */
    public boolean isUseStrictFiltering()
    {
        return this.useStrictFiltering;
    } //-- boolean isUseStrictFiltering()

    /**
     * Method removeExclude.
     * 
     * @param string
     */
    public void removeExclude( String string )
    {
        if ( !(string instanceof String) )
        {
            throw new ClassCastException( "SetBase.removeExcludes(string) parameter must be instanceof " + String.class.getName() );
        }
        getExcludes().remove( string );
    } //-- void removeExclude( String )

    /**
     * Method removeInclude.
     * 
     * @param string
     */
    public void removeInclude( String string )
    {
        if ( !(string instanceof String) )
        {
            throw new ClassCastException( "SetBase.removeIncludes(string) parameter must be instanceof " + String.class.getName() );
        }
        getIncludes().remove( string );
    } //-- void removeInclude( String )

    /**
     * Set 
     *             
     *             Similar to a UNIX permission, sets the directory
     * mode of the directories
     *             included. Format: (User)(Group)(Other) where
     * each component is a sum of
     *             Read = 4, Write = 2, and Execute = 1.  For
     * example, the value
     *             0755 translates to User read-write, Group and
     * Other read-only.
     *             <a
     * href="http://www.onlamp.com/pub/a/bsd/2000/09/06/FreeBSD_Basics.html">(more
     * on unix-style permissions)</a>
     *           	
     *           
     * 
     * @param directoryMode
     */
    public void setDirectoryMode( String directoryMode )
    {
        this.directoryMode = directoryMode;
    } //-- void setDirectoryMode( String )

    /**
     * Set 
     *             
     *             When &lt;exclude&gt; subelements are present,
     * they define a set of
     *             files and directory to exclude. If none is
     * present, then
     *             &lt;excludes&gt; represents no exclusions.
     *             
     *           
     * 
     * @param excludes
     */
    public void setExcludes( java.util.List/*<String>*/ excludes )
    {
        this.excludes = excludes;
    } //-- void setExcludes( java.util.List )

    /**
     * Set 
     *             
     *             Similar to a UNIX permission, sets the file mode
     * of the files included.
     *             Format: (User)(Group)(Other) where each
     * component is a sum of Read = 4,
     *             Write = 2, and Execute = 1.  For example, the
     * value 0644
     *             translates to User read-write, Group and Other
     * read-only.
     *             <a
     * href="http://www.onlamp.com/pub/a/bsd/2000/09/06/FreeBSD_Basics.html">(more
     * on unix-style permissions)</a>
     *           	
     *           
     * 
     * @param fileMode
     */
    public void setFileMode( String fileMode )
    {
        this.fileMode = fileMode;
    } //-- void setFileMode( String )

    /**
     * Set 
     *             
     *             When &lt;include&gt; subelements are present,
     * they define a set of
     *             files and directory to include. If none is
     * present, then
     *             &lt;includes&gt; represents all valid values.
     *             
     *           
     * 
     * @param includes
     */
    public void setIncludes( java.util.List/*<String>*/ includes )
    {
        this.includes = includes;
    } //-- void setIncludes( java.util.List )

    /**
     * Set 
     *             Sets the output directory relative to the root
     *             of the root directory of the assembly. For
     * example,
     *             "log" will put the specified files in the log
     * directory.
     *           
     * 
     * @param outputDirectory
     */
    public void setOutputDirectory( String outputDirectory )
    {
        this.outputDirectory = outputDirectory;
    } //-- void setOutputDirectory( String )

    /**
     * Set 
     *             Whether standard exclusion patterns, such as
     * those matching CVS and Subversion
     *             metadata files, should be used when calculating
     * the files affected by this set.
     *             For backward compatibility, the default value is
     * true.
     *           
     * 
     * @param useDefaultExcludes
     */
    public void setUseDefaultExcludes( boolean useDefaultExcludes )
    {
        this.useDefaultExcludes = useDefaultExcludes;
    } //-- void setUseDefaultExcludes( boolean )

    /**
     * Set 
     *           When specified as true, any include/exclude
     * patterns which aren't used to filter an actual
     *           artifact during assembly creation will cause the
     * build to fail with an error. This is meant
     *           to highlight obsolete inclusions or exclusions, or
     * else signal that the assembly descriptor
     *           is incorrectly configured.
     *           
     * 
     * @param useStrictFiltering
     */
    public void setUseStrictFiltering( boolean useStrictFiltering )
    {
        this.useStrictFiltering = useStrictFiltering;
    } //-- void setUseStrictFiltering( boolean )


}
