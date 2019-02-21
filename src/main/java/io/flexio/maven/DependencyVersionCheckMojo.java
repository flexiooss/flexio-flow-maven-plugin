package io.flexio.maven;

import io.flexio.maven.report.Report;
import io.flexio.maven.task.AllDependenciesAreReleasedCheck;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

@Mojo(name = "check-deps", defaultPhase = LifecyclePhase.VERIFY)
public class DependencyVersionCheckMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}", readonly = true)
    private MavenProject project;

    @Parameter(required = false, alias = "report-to", property = "report.to")
    private File reportTo;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        this.checkDependencies("declared", this.project.getOriginalModel().getDependencies());
        this.checkDependencies("declared dependency management", this.project.getOriginalModel().getDependencyManagement().getDependencies());
        this.checkDependencies("resolved", this.project.getDependencies());
        this.checkDependencies("resolved dependency management", this.project.getDependencyManagement().getDependencies());
    }

    private void checkDependencies(String level, List<Dependency> deps) throws MojoFailureException, MojoExecutionException {
        Report report = new AllDependenciesAreReleasedCheck(deps, this.getLog()).check();

        if(this.reportTo != null) {
            try(OutputStream out = new FileOutputStream(this.reportTo, true)) {
                report.report(line -> out.write(line.getBytes("UTF-8")));
                out.flush();
            } catch (IOException e) {
                throw new MojoExecutionException("failed writing report to file : " + this.reportTo.getAbsolutePath(), e);
            }
        }

        if(report.isFailure()) {
            report.log(this.getLog(), Report.LogToLevel.ERROR);
            throw new MojoFailureException("some " + level + " dependencies are still in SNAPSHOT version, see logs");
        } else {
            report.log(this.getLog(), Report.LogToLevel.INFO);
        }
    }
}
