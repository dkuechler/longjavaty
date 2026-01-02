terraform {
  required_version = ">= 1.0"

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }
}

provider "aws" {
  region = var.aws_region

  default_tags {
    tags = {
      Project     = var.project_name
      Environment = var.environment
      ManagedBy   = "terraform"
    }
  }
}

module "networking" {
  source = "../../modules/networking"

  project_name = var.project_name
  environment  = var.environment
  vpc_cidr     = var.vpc_cidr
}

module "database" {
  source = "../../modules/database"

  project_name = var.project_name
  environment  = var.environment

  vpc_id     = module.networking.vpc_id
  subnet_ids = module.networking.public_subnet_ids

  app_security_group_id = aws_security_group.app.id
  allowed_cidr_blocks   = var.db_allowed_cidr_blocks

  db_name     = var.db_name
  db_username = var.db_username
  db_password = var.db_password

  instance_class      = "db.t3.micro"
  allocated_storage   = 20
  publicly_accessible = var.db_publicly_accessible
}

module "app" {
  source = "../../modules/app"

  project_name = var.project_name
  environment  = var.environment

  vpc_id            = module.networking.vpc_id
  public_subnet_ids = module.networking.public_subnet_ids

  app_security_group_id = aws_security_group.app.id
  container_image       = var.container_image

  db_host     = module.database.db_address
  db_name     = var.db_name
  db_username = var.db_username
  db_password = var.db_password

  frontend_origins = var.frontend_origins
}

resource "aws_security_group" "app" {
  name        = "${var.project_name}-${var.environment}-app-sg"
  description = "Application security group"
  vpc_id      = module.networking.vpc_id

  revoke_rules_on_delete = true

  ingress {
    from_port   = 8080
    to_port     = 8080
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
    description = "Allow inbound HTTP to application port"
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = "${var.project_name}-${var.environment}-app-sg"
  }
}
