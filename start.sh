#!/bin/sh
set -e

echo "=== Election Monitor Startup ==="
echo "--- Available DB env vars ---"
echo "DATABASE_URL present: $([ -n "$DATABASE_URL" ] && echo YES || echo NO)"
echo "MYSQLHOST present:    $([ -n "$MYSQLHOST" ] && echo YES || echo NO)"
echo "MYSQLUSER present:    $([ -n "$MYSQLUSER" ] && echo YES || echo NO)"
echo "MYSQL_URL present:    $([ -n "$MYSQL_URL" ] && echo YES || echo NO)"
echo "---"

if [ -n "$DATABASE_URL" ]; then
  echo "Using DATABASE_URL..."
  JDBC_URL=$(echo "$DATABASE_URL" | sed 's|^mysql://|jdbc:mysql://|')
  export SPRING_DATASOURCE_URL="${JDBC_URL}?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&createDatabaseIfNotExist=true"
  echo "SPRING_DATASOURCE_URL built from DATABASE_URL"
elif [ -n "$MYSQL_URL" ]; then
  echo "Using MYSQL_URL..."
  JDBC_URL=$(echo "$MYSQL_URL" | sed 's|^mysql://|jdbc:mysql://|')
  export SPRING_DATASOURCE_URL="${JDBC_URL}?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&createDatabaseIfNotExist=true"
  echo "SPRING_DATASOURCE_URL built from MYSQL_URL"
elif [ -n "$MYSQLHOST" ]; then
  echo "Using MYSQL* individual vars..."
  export SPRING_DATASOURCE_URL="jdbc:mysql://${MYSQLHOST}:${MYSQLPORT:-3306}/${MYSQLDATABASE:-election_monitor_db}?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&createDatabaseIfNotExist=true"
  export SPRING_DATASOURCE_USERNAME="${MYSQLUSER}"
  export SPRING_DATASOURCE_PASSWORD="${MYSQLPASSWORD}"
  echo "SPRING_DATASOURCE_URL built from MYSQL* vars"
else
  echo "ERROR: No DB variables found! Add DATABASE_URL reference variable in Railway."
  echo "Go to: Backend Service > Variables > New Variable"
  echo "  Name: DATABASE_URL"
  echo "  Value: \${{MySQL.DATABASE_URL}}"
fi

echo "Starting Spring Boot on port ${PORT:-8080}..."
exec java \
  -Dspring.profiles.active=prod \
  -Dserver.port="${PORT:-8080}" \
  -jar app.jar

