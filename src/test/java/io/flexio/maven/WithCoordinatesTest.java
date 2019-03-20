package io.flexio.maven;

import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.model.Plugin;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.util.HashSet;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.*;

public class WithCoordinatesTest {

    @Test
    public void set() throws Exception {
        HashSet<WithCoordinates> coords = new HashSet<>();
        coords.add(WithCoordinates.from("a:b:c"));

        coords.add(WithCoordinates.from(new DefaultArtifact("a", "b", "c", "", "", "", null)));
        coords.add(WithCoordinates.from("a:b:c"));

        assertThat(coords.toString(), coords, hasSize(1));
    }
}