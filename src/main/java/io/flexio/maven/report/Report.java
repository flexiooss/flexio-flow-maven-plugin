package io.flexio.maven.report;

import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.logging.Log;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class Report {

    @FunctionalInterface
    public interface LogToLevel {

        LogToLevel INFO = (line, log) -> log.info(line);
        LogToLevel ERROR = (line, log) -> log.error(line);

        void logWith(String line, Log log);
    }

    @FunctionalInterface
    public interface Reporter {
        void writeLine(String line) throws IOException;
    }


    private boolean failed = false;
    private final List<Dependency> snapshotDependencies = new LinkedList<>();


    public void append(Dependency dependency) {
        this.snapshotDependencies.add(dependency);
    }

    public Report hasFailed(boolean failure) {
        this.failed = failure;
        return this;
    }

    public boolean isFailure() {
        return this.failed;
    }

    public void log(Log log, LogToLevel to) {
        for (Dependency dependency : this.snapshotDependencies) {
            to.logWith(
                    String.format("dependency %s:%s:%s in scope %s is not a released version",
                            dependency.getGroupId(),
                            dependency.getArtifactId(),
                            dependency.getVersion(),
                            dependency.getScope()
                    ),
                    log
            );
        }
    }

    public void report(Reporter reporter) throws IOException {
        for (Dependency dependency : this.snapshotDependencies) {
            reporter.writeLine(String.format("%s:%s:%s:%s",
                    dependency.getGroupId(),
                    dependency.getArtifactId(),
                    dependency.getVersion(),
                    dependency.getScope()
            ));
        }
    }

    @Override
    public String toString() {
        return "Report{" +
                "failed=" + failed +
                ", snapshotDependencies=" + snapshotDependencies +
                '}';
    }
}
