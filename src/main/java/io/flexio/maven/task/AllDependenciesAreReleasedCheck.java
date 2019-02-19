package io.flexio.maven.task;

import io.flexio.maven.report.Report;
import org.apache.maven.model.Dependency;

import java.util.List;

public class AllDependenciesAreReleasedCheck {
    private List<Dependency> dependencies;

    public AllDependenciesAreReleasedCheck(List<Dependency> dependencies) {
        this.dependencies = dependencies;
    }

    public Report check() {
        Report result = new Report();

        for (Dependency dependency : this.dependencies) {
            if(dependency.getVersion().endsWith("-SNAPSHOT")) {
                result.hasFailed(true);
                result.append(dependency);
            }
        }

        return result;
    }
}
