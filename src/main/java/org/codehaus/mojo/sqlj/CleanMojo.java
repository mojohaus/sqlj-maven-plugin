package org.codehaus.mojo.sqlj;

import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

/**
 * Cleans out generated stale resources.
 * 
 * @author <a href="mailto:david@codehaus.org">David J. M. Karlsen</a>
 */
@Mojo( name = "clean", defaultPhase = LifecyclePhase.CLEAN )
public class CleanMojo
    extends AbstractSqljMojo
{

    /**
     * {@inheritDoc}
     */
    public void execute()
        throws MojoExecutionException, MojoFailureException
    {
        try
        {
            FileUtils.deleteDirectory( getGeneratedSourcesDirectory() );
            FileUtils.deleteDirectory( getGeneratedResourcesDirectory() );
        }
        catch ( IOException e )
        {
            throw new MojoFailureException( e.getMessage() );
        }
    }

}
