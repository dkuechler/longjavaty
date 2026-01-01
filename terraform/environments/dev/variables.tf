variable "aws_region" {
  type    = string
  default = "us-east-1"
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

variable "db_allowed_cidr_blocks" {
  type    = list(string)
  default = []
}
