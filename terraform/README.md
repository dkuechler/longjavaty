# Infrastructure

Terraform modules for AWS deployment.

## Architecture

- **VPC**: Multi-AZ network with public subnets for all resources. Private subnets are available but unused for RDS to avoid NAT Gateway costs.
- **RDS**: PostgreSQL 15 in public subnets (accessible via VPC or optional authorized IPs only).
- **Cost**: Optimized for ~$15/month (no NAT gateway).

## Structure

```
terraform/
├── environments/
│   └── dev/              # Development environment
└── modules/
    ├── networking/       # VPC, subnets, routing
    └── database/         # RDS PostgreSQL
```

## Usage

```bash
cd terraform/environments/dev
terraform init
terraform apply
```
