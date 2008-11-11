package org.codehaus.mojo.sqlj;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * Cleans out generated stale resources.
 * 
 * @goal clean
 * @phase clean
 * @author <a href="mailto:david@codehaus.org">David J. M. Karlsen</a>
 */
public class CleanMojo
    extends AbstractMojo
{
    /**
     * Location for generated source files.
     * 
     * @parameter expression="${sqlj.generatedSourcesDirectory}"
     *            default-value="${project.build.directory}/generated-sources/sqlj-maven-plugin"
     * @required
     */
    private File generatedSourcesLocation;

    /**
     * {@inheritDoc}
     */
    public void execute()
        throws MojoExecutionException, MojoFailureException
    {
        try
        {
            FileUtils.cleanDirectory( generatedSourcesLocation );
        }
        catch ( IOException e )
        {
            throw new MojoFailureException( e.getMessage() );
        }
    }

}
