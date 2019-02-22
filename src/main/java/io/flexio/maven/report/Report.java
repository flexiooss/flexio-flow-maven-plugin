package io.flexio.maven.report;

import io.flexio.maven.WithCoordinates;
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
        void write(String line) throws IOException;
    }


    private boolean failed = false;
    private final List<WithCoordinates> snapshotDependencies = new LinkedList<>();


    public void append(WithCoordinates dependency) {
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
        for (WithCoordinates dependency : this.snapshotDependencies) {
            to.logWith(
                    String.format("dependency %s:%s:%s is not a released version",
                            dependency.groupId(),
                            dependency.artifactId(),
                            dependency.version()
                    ),
                    log
            );
        }
    }

    public void report(Reporter reporter) throws IOException {
        for (WithCoordinates dependency : this.snapshotDependencies) {
            reporter.write(String.format("%s:%s:%s\n",
                    dependency.groupId(),
                    dependency.artifactId(),
                    dependency.version()
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
