package com.github.vertical_blank.sqlformatter;

import com.github.vertical_blank.sqlformatter.languages.Dialect;
import org.junit.Ignore;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Ignore
public class PlJSqlFormatterTest {

  @Test
  public void simple() {
    String format =
        SqlFormatter.of(Dialect.PlSql).format(
            "SELECT foo, bar, CASE baz WHEN 'one' THEN 1 WHEN 'two' THEN 2 ELSE 3 END FROM table");
    assertThat(format).isEqualTo("SELECT foo, bar, CASE\n" +
            "  baz\n" +
            "  WHEN 'one' THEN 1\n" +
            "  WHEN 'two' THEN 2\n" +
            "  ELSE 3\n" +
            "END\n" +
            "FROM table");
  }

    @Test
    void should_update() {
      String format =
              SqlFormatter.of(Dialect.PlSql).format(
                      "update table1 set a = 1, b = 2 where id = 3;");
      assertThat(format).isEqualTo("update table1\n" +
              "set\n" +
              "  a = 1, b = 2\n" +
              "  where id = 3;");
    }

  @Test
  void should_delete() {
    String format =
            SqlFormatter.of(Dialect.PlSql).format(
                    "delete table1 where a = 1 and b = 2 and exists (select f1, f2 from t2 where t2.a = table1.a)");
    assertThat(format).isEqualTo("delete table1\n" +
            "where a = 1\n" +
            "and b = 2\n" +
            "and exists (\n" +
            "  select f1, f2\n" +
            "  from t2\n" +
            "  where t2.a = table1.a\n" +
            ")");
  }

  @Test
  void should_insert() {
    String format =
            SqlFormatter.of(Dialect.PlSql).format(
                    "insert into abcd(a, b, c) values(1, 2, 3);");
    assertThat(format).isEqualTo("insert into abcd(a, b, c)\nvalues (1, 2, 3);");
  }

  @Test
  void name() {
    String result = SqlFormatter.of(Dialect.PlSql).format("INSERT INTO T_POLICY_FEE ( FEE_ID, CHANGE_ID )\n" +
            "SELECT\n" +
            "    V_FEE_ID, BRANCH_ID,\n" +
            "    (\n" +
            "        SELECT organ_id\n" +
            "        FROM t_contract_master t\n" +
            "        WHERE t.policy_id = tpf.policy_id\n" +
            "    ),\n" +
            "    (\n" +
            "        SELECT\n" +
            "            dept_id\n" +
            "        FROM\n" +
            "            t_contract_master t\n" +
            "        WHERE\n" +
            "            t.policy_id = tpf.policy_id\n" +
            "    ),\n" +
            "    AGENT_ID,\n" +
            "    POLICY_ID,\n" +
            "    POLICY_TYPE,\n" +
            "    SEND_CODE,\n" +
            "    MONEY_ID,\n" +
            "    MONEY_ID,\n" +
            "    NULL\n" +
            "FROM\n" +
            "    T_POLICY_FEE TPF\n" +
            "WHERE\n" +
            "    TPF.CHANGE_ID = I_CHANGE_ID\n" +
            "    AND TPF.PAYMENT_ID IS NULL\n" +
            "    AND TPF.RECEIV_STATUS = 2\n" +
            "    AND TPF.FEE_STATUS = 1\n" +
            "    AND EXISTS (\n" +
            "        SELECT\n" +
            "            1\n" +
            "        FROM\n" +
            "            T_POLICY_FEE TPF1\n" +
            "        WHERE\n" +
            "            TPF1.CHANGE_ID = I_CHANGE_ID\n" +
            "    )\n" +
            "    AND EXISTS (\n" +
            "        SELECT\n" +
            "            1\n" +
            "        FROM\n" +
            "            T_FEE_TYPE FT\n" +
            "        WHERE\n" +
            "            FT.ACTUAL_TYPE IN ('1', '2')\n" +
            "            AND FT.TYPE_ID = TPF.FEE_TYPE\n" +
            "            AND FT.VAT_FLAG <> '1'\n" +
            "    )\n" +
            "    AND ROWNUM < 2;");
    assertThat(result).isEqualTo("insert into abcd(a, b, c)\nvalues (1, 2, 3);");
  }

  @Test
  public void withIndent() {
    String format =
        SqlFormatter.of(Dialect.PlSql).format(
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
        SqlFormatter.of(Dialect.PlSql).format("SELECT * FROM tbl WHERE foo = @foo", namedParams);
    assertEquals(format, "SELECT\n" + "  *\n" + "FROM\n" + "  tbl\n" + "WHERE\n" + "  foo = 'bar'");
  }

  @Test
  public void withFatArrow() {
    String format =
        SqlFormatter.of(Dialect.PlSql).extend(config -> config.plusOperators("=>"))
            .format("SELECT * FROM tbl WHERE foo => '123'");
    assertEquals(
        format, "SELECT\n" + "  *\n" + "FROM\n" + "  tbl\n" + "WHERE\n" + "  foo => '123'");
  }

  @Test
  public void withIndexedParams() {
    String format = SqlFormatter.of(Dialect.PlSql).format("SELECT * FROM tbl WHERE foo = ?", Arrays.asList("'bar'"));
    assertEquals(format, "SELECT\n" + "  *\n" + "FROM\n" + "  tbl\n" + "WHERE\n" + "  foo = 'bar'");
  }
}
