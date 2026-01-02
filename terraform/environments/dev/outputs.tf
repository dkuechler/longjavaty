output "vpc_id" {
  description = "VPC ID"
  value       = module.networking.vpc_id
}

output "database_endpoint" {
  description = "RDS endpoint"
  value       = module.database.db_endpoint
  sensitive   = true
}

output "database_connection_string" {
  description = "JDBC connection string"
  value       = "jdbc:postgresql://${module.database.db_address}:${module.database.db_port}/${module.database.db_name}"
  sensitive   = true
}

output "ecs_cluster_name" {
  value = module.app.cluster_name
}

output "ecs_service_name" {
  value = module.app.service_name
}

output "ecr_repository_url" {
  value = module.app.ecr_repository_url
}
