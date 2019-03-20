package io.flexio.maven;

import io.flexio.maven.report.Report;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.*;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Mojo(name = "produces", defaultPhase = LifecyclePhase.VERIFY)
public class ProducesMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}", readonly = true)
    private MavenProject project;

    @Parameter(required = false, alias = "report-to", property = "report.to")
    private File reportTo;


    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        Set<WithCoordinates> produced = null;
        try {
            produced = WithCoordinates.loadFromReport(this.reportTo);
        } catch (IOException e) {
            throw new MojoExecutionException("failed reading existing report to file : " + this.reportTo.getAbsolutePath(), e);
        }

        produced.add(WithCoordinates.from(this.project.getArtifact()));
        for (Artifact artifact : this.project.getArtifacts()) {
            produced.add(WithCoordinates.from(artifact));
        }

        Report report = new Report("produces %s:%s:%s");
        report.appendAll(produced.stream()
                .filter(WithCoordinates::isComplete)
                .collect(Collectors.toList())
        );

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


    private void artifact(String msg, Artifact artifact) {
        System.out.printf("%s :: %s:%s || %s", msg, artifact.getGroupId(), artifact.getArtifactId(), artifact);
    }

}
