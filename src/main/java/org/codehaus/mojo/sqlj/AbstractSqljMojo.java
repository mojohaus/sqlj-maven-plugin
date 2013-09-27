package org.codehaus.mojo.sqlj;

import java.io.File;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * Common superclass for sharing configuration attributes.
 * 
 * @author karltdav
 */
public abstract class AbstractSqljMojo
    extends AbstractMojo
{
    /**
     * Location for generated source files.
     */
    @Parameter( property = "sqlj.generatedSourcesDirectory", defaultValue = "${project.build.directory}/generated-sources/sqlj" )
    private File generatedSourcesDirectory;
    
    /**
     * Location for generated .ser files.
     */
    @Parameter( property = "sqlj.generatedResourcesDirectory", defaultValue = "${project.build.directory}/generated-resources/sqlj" )
    private File generatedResourcesDirectory;
    
    protected File getGeneratedSourcesDirectory()
    {
        return generatedSourcesDirectory;
    }
    
    protected File getGeneratedResourcesDirectory()
    {
        return generatedResourcesDirectory;
    }

}
