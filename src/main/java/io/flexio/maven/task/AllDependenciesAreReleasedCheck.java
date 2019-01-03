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
            System.out.println("DEP :: " + String.format("dependency %s:%s:%s in scope %s",
                    dependency.getGroupId(),
                    dependency.getArtifactId(),
                    dependency.getVersion(),
                    dependency.getScope()
            ));

            if(dependency.getVersion().endsWith("-SNAPSHOT")) {
                result.hasFailed(true);
                result.append(String.format("dependency %s:%s:%s in scope %s is not a released version",
                        dependency.getGroupId(),
                        dependency.getArtifactId(),
                        dependency.getVersion(),
                        dependency.getScope()
                ));
            }
        }

        return result;
    }
}
