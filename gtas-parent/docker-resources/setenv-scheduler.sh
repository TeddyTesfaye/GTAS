export DB_USERNAME=$(cat /run/secrets/mysql_processor_user)
export DB_PASSWORD=$(cat /run/secrets/mysql_processor_password)
export NEO4J_USERNAME=$(cat /run/secrets/webapp_neo4j_user)
export NEO4J_PASSWORD=$(cat /run/secrets/webapp_neo4j_password)