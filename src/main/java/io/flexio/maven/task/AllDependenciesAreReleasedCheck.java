package io.flexio.maven.task;

import io.flexio.maven.report.Report;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.logging.Log;

import java.util.List;
import java.util.Optional;

public class AllDependenciesAreReleasedCheck {
    private List<Dependency> dependencies;
    private Optional<Log> log;

    public AllDependenciesAreReleasedCheck(List<Dependency> dependencies, Log log) {
        this.dependencies = dependencies;
        this.log = Optional.ofNullable(log);
    }

    public Report check() {
        Report result = new Report();

        for (Dependency dependency : this.dependencies) {
            if(dependency.getVersion() != null) {
                if (dependency.getVersion().endsWith("-SNAPSHOT")) {
                    result.hasFailed(true);
                    result.append(dependency);
                }
            } else {
                this.log.ifPresent(log1 -> log1.debug("dependency has no version : " + dependency));
            }
        }

        return result;
    }
}
