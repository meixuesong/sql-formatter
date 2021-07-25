package com.github.vertical_blank.sqlformatter;

import static org.junit.jupiter.api.Assertions.*;

import com.github.vertical_blank.sqlformatter.languages.Dialect;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled
public class SqlFormatterTest {

  @Test
  public void simple() {
    String format =
        SqlFormatter.format(
            "SELECT foo, bar, CASE baz WHEN 'one' THEN 1 WHEN 'two' THEN 2 ELSE 3 END FROM table");
    assertEquals(
        format,
        "SELECT\n"
            + "  foo,\n"
            + "  bar,\n"
            + "  CASE\n"
            + "    baz\n"
            + "    WHEN 'one' THEN 1\n"
            + "    WHEN 'two' THEN 2\n"
            + "    ELSE 3\n"
            + "  END\n"
            + "FROM\n"
            + "  table");
  }

  @Test
  public void withIndent() {
    String format =
        SqlFormatter.format(
            "SELECT foo, bar, CASE baz WHEN 'one' THEN 1 WHEN 'two' THEN 2 ELSE 3 END FROM table",
            "    ");
    assertEquals(
        format,
        "SELECT\n"
            + "    foo,\n"
            + "    bar,\n"
            + "    CASE\n"
            + "        baz\n"
            + "        WHEN 'one' THEN 1\n"
            + "        WHEN 'two' THEN 2\n"
            + "        ELSE 3\n"
            + "    END\n"
            + "FROM\n"
            + "    table");
  }

  @Test
  public void withNamedParams() {
    Map<String, String> namedParams = new HashMap<>();
    namedParams.put("foo", "'bar'");

    String format =
        SqlFormatter.of(Dialect.TSql).format("SELECT * FROM tbl WHERE foo = @foo", namedParams);
    assertEquals(format, "SELECT\n" + "  *\n" + "FROM\n" + "  tbl\n" + "WHERE\n" + "  foo = 'bar'");
  }

  @Test
  public void withFatArrow() {
    String format =
        SqlFormatter.extend(config -> config.plusOperators("=>"))
            .format("SELECT * FROM tbl WHERE foo => '123'");
    assertEquals(
        format, "SELECT\n" + "  *\n" + "FROM\n" + "  tbl\n" + "WHERE\n" + "  foo => '123'");
  }

  @Test
  public void withIndexedParams() {
    String format = SqlFormatter.format("SELECT * FROM tbl WHERE foo = ?", Arrays.asList("'bar'"));
    assertEquals(format, "SELECT\n" + "  *\n" + "FROM\n" + "  tbl\n" + "WHERE\n" + "  foo = 'bar'");
  }
}
