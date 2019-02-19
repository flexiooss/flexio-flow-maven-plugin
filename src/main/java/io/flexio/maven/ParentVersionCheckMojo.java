package io.flexio.maven;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

@Mojo(name = "check-parent", defaultPhase = LifecyclePhase.VERIFY)
public class ParentVersionCheckMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}", readonly = true)
    private MavenProject project;

    @Parameter(required = false, alias = "report-to", property = "report.to")
    private File reportTo;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        this.check(this.project.getParent());
    }

    private void check(MavenProject project) throws MojoFailureException, MojoExecutionException {
        if(project.getParent().getVersion().endsWith("-SNAPSHOT")) {
            String coordinates = String.format("%s:%s:%s", project.getGroupId(), project.getArtifactId(), project.getVersion());
            String message = String.format("parent is not released : %s", coordinates);

            if(this.reportTo != null) {
                try(OutputStream out = new FileOutputStream(this.reportTo, true)) {
                    out.write(String.format("%s:%s:%s:compile\n", coordinates).getBytes("UTF-8"));
                    out.flush();

                } catch (IOException e) {
                    throw new MojoExecutionException("failed writing report to file : " + this.reportTo.getAbsolutePath(), e);
                }
            }
            this.getLog().error(message);
            throw new MojoFailureException("parent is still in SNAPSHOT version, see logs");
        }
    }
}
