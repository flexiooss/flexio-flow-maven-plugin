package io.flexio.maven.report;

import io.flexio.maven.WithCoordinates;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.logging.Log;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class Report {

    private final String logFormat;

    public Report(String logFormat) {
        this.logFormat = logFormat;
    }

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

    public void appendAll(Collection<WithCoordinates> withCoordinates) {
        this.snapshotDependencies.addAll(withCoordinates);
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
                    String.format(logFormat,
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
