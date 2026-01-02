variable "aws_region" {
  type    = string
  default = "eu-central-1"
}

variable "environment" {
  type    = string
  default = "dev"
}

variable "project_name" {
  type    = string
  default = "longjavaty"
}

variable "vpc_cidr" {
  type    = string
  default = "10.0.0.0/16"
}

variable "db_name" {
  type    = string
  default = "longjavaty"
}

variable "db_username" {
  type    = string
  default = "longjavaty_admin"
}

variable "db_password" {
  type      = string
  sensitive = true
}

variable "db_publicly_accessible" {
  type    = bool
  default = false
}

variable "db_allowed_cidr_blocks" {
  type    = list(string)
  default = []

  validation {
    condition     = !contains(var.db_allowed_cidr_blocks, "0.0.0.0/0")
    error_message = "Do not use 0.0.0.0/0 for DB access."
  }
}

variable "container_image" {
  type        = string
  description = "Docker image for the application. If empty, uses the ECR repository created by the module."
  default     = ""
}

variable "frontend_origins" {
  type    = string
  default = "http://localhost:4200"
}

variable "spring_profiles" {
  type    = string
  default = "dev"
}
