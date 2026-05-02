#!/bin/sh
set -e

echo "=== Election Monitor Startup ==="
echo "--- Available DB env vars ---"
echo "DATABASE_URL present: $([ -n "$DATABASE_URL" ] && echo YES || echo NO)"
echo "MYSQLHOST present:    $([ -n "$MYSQLHOST" ] && echo YES || echo NO)"
echo "MYSQLUSER present:    $([ -n "$MYSQLUSER" ] && echo YES || echo NO)"
echo "MYSQL_URL present:    $([ -n "$MYSQL_URL" ] && echo YES || echo NO)"
echo "---"

# ── Database URL resolution ──
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

# ── Mail configuration bridge ──
# Railway env vars: GMAIL_EMAIL, GMAIL_APP_PASSWORD
# Spring expects:   spring.mail.username, spring.mail.password
# Bridge them via SPRING_MAIL_USERNAME / SPRING_MAIL_PASSWORD so Spring picks them up.
echo "--- Mail config ---"
if [ -n "$GMAIL_EMAIL" ]; then
  export SPRING_MAIL_USERNAME="$GMAIL_EMAIL"
  echo "SPRING_MAIL_USERNAME set from GMAIL_EMAIL: $GMAIL_EMAIL"
else
  echo "GMAIL_EMAIL not set — using default from application-prod.properties"
fi

if [ -n "$GMAIL_APP_PASSWORD" ]; then
  # App password may contain spaces (e.g., "kxhf ifxv linp rais")
  # Exporting with quotes ensures the full value is passed to Spring
  export SPRING_MAIL_PASSWORD="$GMAIL_APP_PASSWORD"
  echo "SPRING_MAIL_PASSWORD set from GMAIL_APP_PASSWORD (length: $(printf '%s' "$GMAIL_APP_PASSWORD" | wc -c))"
else
  echo "GMAIL_APP_PASSWORD not set — using default from application-prod.properties"
fi
echo "---"

echo "Starting Spring Boot on port ${PORT:-8080}..."
exec java \
  -Dspring.profiles.active=prod \
  -Dserver.port="${PORT:-8080}" \
  -jar app.jar
