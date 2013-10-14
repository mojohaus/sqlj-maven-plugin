package org.codehaus.mojo.sqlj;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.beanutils.MethodUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.Scanner;
import org.sonatype.plexus.build.incremental.BuildContext;

/**
 * Translates SQLJ source code using the SQLJ Translator.
 * 
 * @author <a href="mailto:david@codehaus.org">David J. M. Karlsen</a>
 */
@Mojo( name = "sqlj", defaultPhase = LifecyclePhase.GENERATE_SOURCES, requiresDependencyResolution = ResolutionScope.COMPILE )
public class SqljMojo
    extends AbstractSqljMojo
{

    /**
     * Codepage for generated sources.
     */
    @Parameter( property = "sqlj.encoding", defaultValue = "${project.build.sourceEncoding}" )
    private String encoding;

    /**
     * Show status while executing.
     */
    @Parameter( property = "sqlj.status", defaultValue = "true" )
    private boolean status;

    /**
     * Explicit list of SQLJ files to process.
     */
    @Parameter( property = "sqlj.sqljFiles" )
    private File[] sqljFiles;

    /**
     * Directories to recursively scan for SQLJ files (only files with .sqlj extension are included).
     */
    @Parameter( property = "sqlj.sqljDirectories" )
    private File[] sqljDirs;

    /**
     * The enclosing project.
     */
    @Component
    private MavenProject mavenProject;

    /**
     * For m2e compatibility.
     */
    @Component
    private BuildContext buildContext;

    /**
     * {@inheritDoc}
     */
    public void execute()
        throws MojoExecutionException, MojoFailureException
    {
        if ( !checkSqljDirAndFileDeclarations() )
        {
            String msg = "Plugin configuration contains invalid SQLJ directory or file declaration(s).";
            throw new MojoExecutionException( msg );
        }

        if ( StringUtils.isEmpty( encoding ) )
        {
            encoding = SystemUtils.FILE_ENCODING;
            getLog().warn( "No encoding given, falling back to system default value: " + encoding );
        }

        try
        {
            FileUtils.forceMkdir( getGeneratedResourcesDirectory().getAbsoluteFile() );
            FileUtils.forceMkdir( getGeneratedSourcesDirectory().getAbsoluteFile() );
        }
        catch ( IOException e )
        {
            throw new MojoFailureException( e.getMessage() );
        }

        Set<File> sqljFiles = getStaleSqljFiles();
        boolean translationPerformed;
        if ( !sqljFiles.isEmpty() )
        {
            for ( File file : sqljFiles )
            {
                buildContext.removeMessages( file );
                try
                {
                    translate( file );
                }
                catch ( MojoExecutionException e )
                {
                    buildContext.addMessage( file, 0, 0, "Error translating SQLJ file", BuildContext.SEVERITY_ERROR, e );
                    throw e;
                }
            }
            translationPerformed = true;
            final int numberOfFiles = sqljFiles.size();
            getLog().info( "Translated " + numberOfFiles + " SQLJ file" + ( numberOfFiles > 0 ? "s." : "." ) );
        }
        else
        {
            getLog().info( "No updated SQLJ files found - skipping SQLJ translation." );
            translationPerformed = false;
        }

        Resource resource = new Resource();
        resource.setDirectory( getGeneratedResourcesDirectory().getAbsolutePath() );
        mavenProject.addResource( resource );
        if ( translationPerformed )
        {
            buildContext.refresh( getGeneratedResourcesDirectory() );
        }
        mavenProject.addCompileSourceRoot( getGeneratedSourcesDirectory().getAbsolutePath() );
        if ( translationPerformed )
        {
            buildContext.refresh( getGeneratedSourcesDirectory() );
        }
    }

    private boolean checkSqljDirAndFileDeclarations()
    {
        boolean isOk = true;

        for ( File sqljDir : sqljDirs )
        {
            if ( !sqljDir.exists() )
            {
                getLog().error( "Declared SQLJ directory does not exist: " + sqljDir );
                isOk = false;
            }
            if ( !sqljDir.isDirectory() )
            {
                getLog().error( "Declared SQLJ directory is not a directory: " + sqljDir );
                isOk = false;
            }
        }
        for ( File sqljFile : sqljFiles )
        {
            if ( !sqljFile.exists() )
            {
                getLog().error( "Declared SQLJ file does not exist: " + sqljFile );
                isOk = false;
            }
            if ( !sqljFile.isFile() )
            {
                getLog().error( "Declared SQLJ file is not a file: " + sqljFile );
                isOk = false;
            }
        }

        return isOk;
    }

    /**
     * Executes the SQLJ Translator on the given file.
     * 
     * @param file the file to translate
     * @throws MojoFailureException in case of failure.
     * @throws MojoExecutionException in case of execution failure.
     */
    private void translate( File file )
        throws MojoFailureException, MojoExecutionException
    {
        Class sqljClass;
        try
        {
            sqljClass = Class.forName( "sqlj.tools.Sqlj" );
        }
        catch ( ClassNotFoundException e )
        {
            throw new MojoFailureException( "Please add SQLJ Translator to the plugin's classpath: " + e.getMessage() );
        }
        catch ( Exception e )
        {
            throw new MojoFailureException( e.getMessage() );
        }

        String[] arguments =
            { "-dir=" + getGeneratedSourcesDirectory().getAbsolutePath(),
                "-d=" + getGeneratedResourcesDirectory().getAbsolutePath(), "-encoding=" + encoding,
                status ? "-status" : "", "-compile=false", file.getAbsolutePath() };

        Integer returnCode = null;
        try
        {
            if ( getLog().isDebugEnabled() )
            {
                getLog().debug( "Performing SQLJ translation on " + file );
            }
            returnCode =
                (Integer) MethodUtils.invokeExactStaticMethod( sqljClass, "statusMain", new Object[] { arguments } );
        }
        catch ( Exception e )
        {
            throw new MojoFailureException( e.getMessage() );
        }

        if ( returnCode.intValue() != 0 )
        {
            throw new MojoExecutionException( "Can't translate file (return code: " + returnCode + ")" );
        }
    }

    /**
     * Returns a set of SQLJ source files, based on configured directories and files, that has been modified and
     * therefore needs to be re-translated.
     */
    private Set<File> getStaleSqljFiles()
        throws MojoExecutionException
    {
        Set<File> staleFiles = new HashSet<File>();

        // Check for modified files in configured directory declarations
        final String[] sqljIncludes = new String[] { "**/*.sqlj" };
        for ( File sqljDir : sqljDirs )
        {
            if ( getLog().isDebugEnabled() )
            {
                getLog().debug( "Checking for updated SQLJ files in directory: " + sqljDir );
            }
            Scanner modifiedScanner = this.buildContext.newScanner( sqljDir );
            modifiedScanner.setIncludes( sqljIncludes );
            modifiedScanner.setExcludes( null );
            modifiedScanner.scan();

            String[] modifiedFiles = modifiedScanner.getIncludedFiles();
            for ( String path : modifiedFiles )
            {
                File file = new File( sqljDir, path );
                if ( getLog().isDebugEnabled() )
                {
                    getLog().debug( "Updated SQLJ file found: " + file );
                }
                if ( !staleFiles.add( file ) )
                {
                    getLog().warn( "Duplicated declaration of SQLJ source detected in plugin configuration: " + file );
                }
            }
        }

        // Check for modified files in configured file declarations
        for ( File sqljFile : sqljFiles )
        {
            if ( getLog().isDebugEnabled() )
            {
                getLog().debug( "Checking if SQLJ file has been updated: " + sqljFile );
            }
            Scanner modifiedScanner = this.buildContext.newScanner( sqljFile.getParentFile() );
            modifiedScanner.setIncludes( new String[] { sqljFile.getName() } );
            modifiedScanner.setExcludes( null );
            modifiedScanner.scan();

            String[] modifiedFiles = modifiedScanner.getIncludedFiles();
            if ( modifiedFiles.length == 1 )
            {
                String path = modifiedFiles[0];
                File file = new File( sqljFile.getParentFile(), path );
                if ( file.compareTo( sqljFile ) == 0 )
                {
                    if ( getLog().isDebugEnabled() )
                    {
                        getLog().debug( "Updated SQLJ file found: " + sqljFile );
                    }
                    if ( !staleFiles.add( file ) )
                    {
                        getLog().warn( "Duplicated declaration of SQLJ source detected in plugin configuration: "
                                           + file );
                    }
                }
                else
                {
                    // Should never happen...
                    getLog().error( "Unexpected SQLJ file returned; aborting..." );
                    throw new MojoExecutionException( "Unexpected SQLJ file returned when examining " + sqljFile + ": "
                        + file );
                }
            }
            else if ( modifiedFiles.length > 1 )
            {
                // Should never happen...
                getLog().error( "Unexpected list of SQLJ files returned; aborting..." );
                throw new MojoExecutionException( "Unexpected list of SQLJ files returned when examining " + sqljFile
                    + ": " + modifiedFiles );
            }
        }

        // Ignoring checking for deleted files. User should run "mvn clean" to handle that, consistent with
        // how it works if Java source files are deleted.

        return staleFiles;
    }
}
