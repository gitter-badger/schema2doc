-- db flyway migration script
-- --------------------------
-- @author man-at-home

-- stored procedure
-- -----------------

CREATE ALIAS SAMPLE_NAME AS $$
  String TRANSLATE_NAME(String value) {
    return "sample " + value;
  }
$$
