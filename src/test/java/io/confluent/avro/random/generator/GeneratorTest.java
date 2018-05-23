package io.confluent.avro.random.generator;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@RunWith(Parameterized.class)
public class GeneratorTest {

  private static final String SCHEMA_TEST_DIR = "test-schemas";
  private static final Random RNG = new Random();
  private final String content;

  /**
   * Run the test for each test schema.
   *
   * @return array of [filename, file-content]
   */
  @Parameterized.Parameters(name = "{0}")
  public static Collection<Object[]> data() {
    return findTestSchemas().stream()
        .map(filename -> new Object[]{filename, loadContent(filename)})
        .collect(Collectors.toCollection(ArrayList::new));
  }

  @SuppressWarnings("unused") // Used to name tests.
  public GeneratorTest(final String filename, final String content) {
    this.content = content;
  }

  @Test
  public void shouldHandleSchema() {
    final Generator generator = new Generator(content, RNG);
    generator.generate();
  }

  private static List<String> findTestSchemas() {
    final InputStream testDir =
        GeneratorTest.class.getClassLoader().getResourceAsStream(SCHEMA_TEST_DIR);

    try (BufferedReader reader = new BufferedReader(
        new InputStreamReader(testDir, StandardCharsets.UTF_8))) {

      return reader.lines()
          .filter(filename -> filename.endsWith(".json"))
          .collect(Collectors.toList());

    } catch (final IOException ioe) {
      throw new RuntimeException("failed to find test schemas", ioe);
    }
  }

  private static String loadContent(final String filename) {
    final InputStream testDir =
        GeneratorTest.class.getClassLoader().getResourceAsStream(SCHEMA_TEST_DIR + "/" + filename);

    try (BufferedReader reader = new BufferedReader(
        new InputStreamReader(testDir, StandardCharsets.UTF_8))) {

      return reader.lines().collect(Collectors.joining("\n"));
    } catch (final IOException ioe) {
      throw new RuntimeException("failed to find test test-schema " + filename, ioe);
    }
  }
}