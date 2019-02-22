package io.flexio.maven;

import io.flexio.maven.report.Report;
import io.flexio.maven.task.AllDependenciesAreReleasedCheck;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.*;
import java.util.List;

@Mojo(name = "check-deps", defaultPhase = LifecyclePhase.VERIFY)
public class DependencyVersionCheckMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}", readonly = true)
    private MavenProject project;

    @Parameter(required = false, alias = "report-to", property = "report.to")
    private File reportTo;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        if(this.project.getOriginalModel() != null && this.project.getOriginalModel().getDependencies() != null) {
            this.checkDependencies("declared", WithCoordinates.fromDependencies(this.project.getOriginalModel().getDependencies()));
        }
        if(this.project.getOriginalModel() != null && this.project.getOriginalModel().getDependencyManagement() != null && this.project.getOriginalModel().getDependencyManagement().getDependencies() != null) {
            this.checkDependencies("declared dependency management", WithCoordinates.fromDependencies(this.project.getOriginalModel().getDependencyManagement().getDependencies()));
        }
        if(this.project.getDependencies() != null) {
            this.checkDependencies("resolved", WithCoordinates.fromDependencies(this.project.getDependencies()));
        }
        if(this.project.getDependencyManagement() != null && this.project.getDependencyManagement().getDependencies() != null) {
            this.checkDependencies("resolved dependency management", WithCoordinates.fromDependencies(this.project.getDependencyManagement().getDependencies()));
        }


        if(this.project.getOriginalModel().getBuild() != null && this.project.getOriginalModel().getBuild().getPlugins() != null) {
            this.checkDependencies("plugin", WithCoordinates.fromPlugins(this.project.getOriginalModel().getBuild().getPluginManagement().getPlugins()));
        }
        if(this.project.getOriginalModel().getBuild() != null && this.project.getOriginalModel().getBuild().getPluginManagement() != null && this.project.getOriginalModel().getBuild().getPlugins() != null) {
            this.checkDependencies("plugin management plugin", WithCoordinates.fromPlugins(this.project.getOriginalModel().getBuild().getPluginManagement().getPlugins()));
        }
        if(this.project.getBuild() != null && this.project.getBuild().getPlugins() != null) {
            this.checkDependencies("resolved plugin", WithCoordinates.fromPlugins(this.project.getBuild().getPluginManagement().getPlugins()));
        }
        if(this.project.getBuild() != null && this.project.getBuild().getPluginManagement() != null && this.project.getBuild().getPlugins() != null) {
            this.checkDependencies("resolved plugin management plugin", WithCoordinates.fromPlugins(this.project.getBuild().getPluginManagement().getPlugins()));
        }
    }

    private void checkDependencies(String level, List<WithCoordinates> deps) throws MojoFailureException, MojoExecutionException {
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
