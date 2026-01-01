output "vpc_id" {
  description = "VPC ID"
  value       = module.networking.vpc_id
}

output "database_endpoint" {
  description = "RDS endpoint"
  value       = module.database.db_endpoint
  # Hostname is generally not considered a secret, improving usability
}

output "database_connection_string" {
  description = "JDBC connection string"
  value       = "jdbc:postgresql://${module.database.db_address}:${module.database.db_port}/${module.database.db_name}"
  sensitive   = true
}
