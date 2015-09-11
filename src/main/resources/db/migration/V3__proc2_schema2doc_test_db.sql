-- db flyway migration script
-- --------------------------
-- @author man-at-home

-- stored procedure
-- -----------------

CREATE ALIAS PROC_SAMPLE_NAME2 AS $$
  String SAMPLE_NAME2(String value) {
    return "sample2 " + value;
  }
$$
;

COMMENT ON ALIAS PROC_SAMPLE_NAME2 IS 'eine einfache Beispielprozedur H2.';
