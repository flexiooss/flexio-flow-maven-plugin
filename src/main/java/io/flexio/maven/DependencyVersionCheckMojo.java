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

import java.util.LinkedList;
import java.util.List;

@Mojo(name = "check-deps", defaultPhase = LifecyclePhase.VERIFY)
public class DependencyVersionCheckMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}", readonly = true)
    private MavenProject project;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        List<Dependency> deps = new LinkedList<>();
        deps.addAll(this.project.getDependencies());
        deps.addAll(this.project.getDependencyManagement().getDependencies());
        
        Report report = new AllDependenciesAreReleasedCheck(deps).check();
        if(report.isFailure()) {
            report.log(this.getLog(), Report.LogToLevel.ERROR);
            throw new MojoFailureException("some dependencies are still in SNAPSHOT version, see logs");
        } else {
            report.log(this.getLog(), Report.LogToLevel.INFO);
        }
    }
}
