module "vpc" {
  source = "terraform-aws-modules/vpc/aws"
  version = "3.18.1"

  name = "DigitalAlpha"
  cidr = "192.168.0.0/16"

  azs                 = var.azs
  private_subnets     = var.vpc_private_subnets
  public_subnets      = var.vpc_public_subnets
  database_subnets    = var.vpc_private_subnets_db
  enable_nat_gateway  = true
  enable_vpn_gateway  = true

  create_database_subnet_group           = false
  create_database_subnet_route_table     = true
  create_database_internet_gateway_route = false
  enable_dns_hostnames = true

  tags = {
    Environment = "Alpha Enviroment"
  }
}