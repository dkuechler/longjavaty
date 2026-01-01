output "db_endpoint" {
  value     = aws_db_instance.postgres.endpoint
  sensitive = true
}

output "db_address" {
  value     = aws_db_instance.postgres.address
  sensitive = true
}

output "db_port" {
  value     = aws_db_instance.postgres.port
  sensitive = true
}

output "db_name" {
  value = aws_db_instance.postgres.db_name
}
