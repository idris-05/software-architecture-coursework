#!/usr/bin/env bash

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
# shellcheck disable=SC1091
source "$SCRIPT_DIR/common.sh"

require_command "$ASADMIN_BIN"

if "$ASADMIN_BIN" list-jdbc-connection-pools | grep -qx "$JDBC_POOL_NAME"; then
    echo "JDBC connection pool '$JDBC_POOL_NAME' already exists."
else
    echo "Creating JDBC connection pool '$JDBC_POOL_NAME'..."
    "$ASADMIN_BIN" create-jdbc-connection-pool \
        --datasourceclassname org.postgresql.ds.PGSimpleDataSource \
        --restype javax.sql.DataSource \
        --property "serverName=${DB_HOST}:portNumber=${DB_PORT}:databaseName=${DB_NAME}:user=${DB_USER}:password=${DB_PASSWORD}" \
        "$JDBC_POOL_NAME"
fi

if "$ASADMIN_BIN" list-jdbc-resources | grep -qx "$JDBC_RESOURCE_NAME"; then
    echo "JDBC resource '$JDBC_RESOURCE_NAME' already exists."
else
    echo "Creating JDBC resource '$JDBC_RESOURCE_NAME'..."
    "$ASADMIN_BIN" create-jdbc-resource \
        --connectionpoolid "$JDBC_POOL_NAME" \
        "$JDBC_RESOURCE_NAME"
fi

echo "Pinging JDBC pool '$JDBC_POOL_NAME'..."
"$ASADMIN_BIN" ping-connection-pool "$JDBC_POOL_NAME"

echo "JDBC setup complete."
