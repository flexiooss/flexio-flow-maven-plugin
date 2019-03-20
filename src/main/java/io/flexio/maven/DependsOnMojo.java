package io.flexio.maven;

import io.flexio.maven.report.Report;
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
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Mojo(name = "depends-on", defaultPhase = LifecyclePhase.VERIFY)
public class DependsOnMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}", readonly = true)
    private MavenProject project;

    @Parameter(required = false, alias = "report-to", property = "report.to")
    private File reportTo;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        Set<WithCoordinates> all = null;
        try {
            all = WithCoordinates.loadFromReport(this.reportTo);
        } catch (IOException e) {
            throw new MojoExecutionException("failed reading existing report to file : " + this.reportTo.getAbsolutePath(), e);
        }

        all.addAll(this.collect()
                .stream()
                .filter(WithCoordinates::isComplete)
                .collect(Collectors.toList())
        );

        Report report = new Report("depends on %s:%s:%s");
        report.appendAll(all);

        if(this.reportTo != null) {
            try(OutputStream out = new FileOutputStream(this.reportTo, false)) {
                report.report(line -> out.write(line.getBytes("UTF-8")));
                out.flush();
            } catch (IOException e) {
                throw new MojoExecutionException("failed writing report to file : " + this.reportTo.getAbsolutePath(), e);
            }
        }

        report.log(this.getLog(), Report.LogToLevel.INFO);
    }

    private HashSet<WithCoordinates> collect() {
        HashSet<WithCoordinates> all = new HashSet<>();

        if(this.project.getOriginalModel() != null && this.project.getOriginalModel().getDependencies() != null) {
            all.addAll(WithCoordinates.fromDependencies(this.project.getOriginalModel().getDependencies()));
        }
        if(this.project.getOriginalModel() != null && this.project.getOriginalModel().getDependencyManagement() != null && this.project.getOriginalModel().getDependencyManagement().getDependencies() != null) {
            all.addAll(WithCoordinates.fromDependencies(this.project.getOriginalModel().getDependencyManagement().getDependencies()));
        }
        if(this.project.getDependencies() != null) {
            all.addAll(WithCoordinates.fromDependencies(this.project.getDependencies()));
        }
        if(this.project.getDependencyManagement() != null && this.project.getDependencyManagement().getDependencies() != null) {
            all.addAll(WithCoordinates.fromDependencies(this.project.getDependencyManagement().getDependencies()));
        }


        if(this.project.getOriginalModel().getBuild() != null && this.project.getOriginalModel().getBuild().getPlugins() != null) {
            all.addAll(WithCoordinates.fromPlugins(this.project.getOriginalModel().getBuild().getPlugins()));
        }
        if(this.project.getOriginalModel().getBuild() != null && this.project.getOriginalModel().getBuild().getPluginManagement() != null && this.project.getOriginalModel().getBuild().getPlugins() != null) {
            all.addAll(WithCoordinates.fromPlugins(this.project.getOriginalModel().getBuild().getPluginManagement().getPlugins()));
        }
        if(this.project.getBuild() != null && this.project.getBuild().getPlugins() != null) {
            all.addAll(WithCoordinates.fromPlugins(this.project.getBuild().getPluginManagement().getPlugins()));
        }
        if(this.project.getBuild() != null && this.project.getBuild().getPluginManagement() != null && this.project.getBuild().getPlugins() != null) {
            all.addAll(WithCoordinates.fromPlugins(this.project.getBuild().getPluginManagement().getPlugins()));
        }

        return all;
    }
}
