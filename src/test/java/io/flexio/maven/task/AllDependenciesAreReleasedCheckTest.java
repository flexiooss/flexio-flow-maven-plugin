package io.flexio.maven.task;

import io.flexio.maven.report.Report;
import org.apache.maven.model.Dependency;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class AllDependenciesAreReleasedCheckTest {

    private final List<String> logs = new LinkedList<>();
    private final Report.LogToLevel to = (line, log) -> logs.add(line);

    @Before
    public void setUp() throws Exception {
        this.logs.clear();
    }

    @Test
    public void givenNoDependency__thenCheckPasses() throws Exception {
        Report report = new AllDependenciesAreReleasedCheck(Collections.emptyList()).check();
        report.log(null, to);

        assertThat(report.isFailure(), is(false));
        assertThat(this.logs, is(empty()));
    }

    @Test
    public void givenOneDependency__whenIsSnapshot__thenCheckFails() throws Exception {
        Report report = new AllDependenciesAreReleasedCheck(Arrays.asList(
                this.createDependency("org.test", "test-dep", "1.0.0-SNAPSHOT", "runtime")
        )).check();
        report.log(null, to);

        assertThat(report.isFailure(), is(true));
        assertThat(this.logs, contains(
                "dependency org.test:test-dep:1.0.0-SNAPSHOT in scope runtime is not a released version"
        ));
    }

    @Test
    public void givenOneDependency__whenIsRleased__thenCheckSuccesses() throws Exception {
        Report report = new AllDependenciesAreReleasedCheck(Arrays.asList(
                this.createDependency("org.test", "test-dep", "1.0.0", "compile")
        )).check();
        report.log(null, to);

        assertThat(report.isFailure(), is(false));
        assertThat(this.logs, is(empty()));
    }

    private Dependency createDependency(String groupId, String artifactId, String version, String scope) {
        Dependency result = new Dependency();
        result.setGroupId(groupId);
        result.setArtifactId(artifactId);
        result.setVersion(version);
        result.setScope(scope);
        return result;
    }
}