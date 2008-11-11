package org.codehaus.mojo.sqlj;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.beanutils.MethodUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

/**
 * Generates SQLJ javacode.
 * 
 * @author <a href="mailto:david@codehaus.org">David J. M. Karlsen</a>
 * @goal sqlj
 * @phase generate-sources
 * @requiresDependencyResolution compile
 */
public class SqljMojo
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
     * Codepage for generated sources.
     * 
     * @parameter expression="${sqlj.encoding}" default-value="UTF-8"
     */
    private String encoding;

    /**
     * Show status while executing.
     * 
     * @parameter expression="${sqlj.status}" default-value="true"
     */
    private boolean status;

    /**
     * Explicit list of sqlj files to process.
     * 
     * @parameter expression="${sqlj.sqljFiles}"
     */
    private File[] sqljFiles;

    /**
     * Directories to recursively scan for .sqlj files.
     * 
     * @parameter expression="${sqlj.sqljDirectories}"
     */
    private File[] sqljDirs;

    /**
     * The enclosing project.
     * 
     * @parameter default-value="${project}"
     * @required
     * @readonly
     */
    private MavenProject mavenProject;

    /**
     * {@inheritDoc}
     */
    public void execute()
        throws MojoExecutionException, MojoFailureException
    {
        String[] arguments =
            { "-d=" + generatedSourcesLocation.getAbsolutePath(), "-encoding=" + encoding, status ? "-status" : "",
                "-compile=false", StringUtils.join( getSqljFiles().iterator(), " " ) };

        Class sqljClass;
        try
        {
            sqljClass = Class.forName( "sqlj.tools.Sqlj" );
        }
        catch ( ClassNotFoundException e )
        {
            throw new MojoFailureException( "Please add sqlj to the plugins classpath " + e.getMessage() );
        }
        catch ( Exception e )
        {
            throw new MojoFailureException( e.getMessage() );
        }

        if ( !generatedSourcesLocation.getAbsoluteFile().mkdir() )
        {
            getLog().warn( generatedSourcesLocation.getAbsolutePath() + " already exists and may contain stale sources" );
        }

        Integer returnCode = null;
        try
        {
            returnCode =
                (Integer) MethodUtils.invokeExactStaticMethod( sqljClass, "statusMain", new Object[] { arguments } );
        }
        catch ( Exception e )
        {
            throw new MojoFailureException( e.getMessage() );
        }

        if ( returnCode.intValue() != 0 )
        {
            throw new MojoExecutionException( "Bad returncode: " + returnCode );
        }

        mavenProject.getCompileSourceRoots().add( generatedSourcesLocation.getAbsolutePath() );
    }

    /**
     * Finds the union of files to generate for.
     * 
     * @return a Set of unique files.
     */
    private Set getSqljFiles()
    {
        Set files = new HashSet();

        final String[] extensions = new String[] { "sqlj" };
        for ( int i = 0; i < sqljDirs.length; i++ )
        {
            files.addAll( FileUtils.listFiles( sqljDirs[i], extensions, true ) );
        }

        Collections.addAll( files, sqljFiles );

        return files;
    }

}
