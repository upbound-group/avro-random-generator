package io.confluent.avro.random.generator;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import io.confluent.avro.random.generator.util.ResourceUtil;
import org.apache.avro.generic.GenericRecord;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;
import java.util.stream.IntStream;


public class IterationTest {

  private static final Random RNG = new Random();
  private static final String ITERATION_SCHEMA =
      ResourceUtil.loadContent("test-schemas/iteration.json");

  private Generator generator;

  @Before
  public void setUp() {
    generator = new Generator(ITERATION_SCHEMA, RNG);
  }

  @Test
  public void shouldCreateIteratorPerFieldEvenIfSameSchema() {
    final GenericRecord first = (GenericRecord) generator.generate();
    final GenericRecord second = (GenericRecord) generator.generate();

    assertThat(first.get("long_iteration"), is(-50L));
    assertThat(((GenericRecord) first.get("nested")).get("long_iteration"), is(-50L));

    assertThat(second.get("long_iteration"), is(-3L));
    assertThat(((GenericRecord) second.get("nested")).get("long_iteration"), is(-3L));
  }

  @Test
  public void shouldBeginIterationAtInitialValue() {
    final GenericRecord generated = (GenericRecord) generator.generate();
    assertThat(generated.get("int_iteration_offset"), is(40));
    assertThat(generated.get("long_iteration_offset"), is(0L));
    assertThat(generated.get("float_iteration_offset"), is(0.0f));
    assertThat(generated.get("double_iteration_offset"), is(5.0));
  }

  @Test
  public void shouldSupportStringIteration() {
    final GenericRecord first = (GenericRecord) generator.generate();
    final GenericRecord second = (GenericRecord) generator.generate();
    final GenericRecord third = (GenericRecord) generator.generate();

    assertThat(first.get("string_iteration"), is("1"));
    assertThat(second.get("string_iteration"), is("2"));
    assertThat(third.get("string_iteration"), is("1"));
  }

  @Test
  public void shouldSupportPrefixAndSuffix() {
    final GenericRecord generated = (GenericRecord) generator.generate();
    assertThat(generated.get("prefixed_suffixed_string_iteration"), is("pre-0-post"));
  }

  @Test
  public void shouldGenerateFromSameSchemaOnMultipleThreads() {
    IntStream.range(0, 10).parallel()
        .forEach(idx -> {
          final Generator generator = new Generator(ITERATION_SCHEMA, new Random());
          generator.generate();
        });
  }
}
