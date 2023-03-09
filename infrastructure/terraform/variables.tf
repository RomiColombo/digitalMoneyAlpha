variable "vpc_public_subnets" {
  description = "List of public subnets"
  type        = list(string)
  default     = ["192.168.1.0/24"]
}
variable "vpc_private_subnets" {
  description = "List of private subnets"
  type        = list(string)
  default     = ["192.168.2.0/24"]
}

variable "vpc_private_subnet_id" {
  description = "List of private subnets"
  type        = list(string)
  default     = ["subnet-0eef594bcb935d40a"]
}

variable "vpc_private_db_subnet_id" {
  description = "List of private subnets"
  type        = list(string)
  default     = ["subnet-05407a9a1576ebc0c"]
}
variable "vpc_private_subnets_db" {
  description = "List of private db subnets"
  type        = list(string)
  default     = ["192.168.3.0/24"]
}

variable "azs" {
  description = "List of azs"
  type        = list(string)
  default     = ["us-east-2a"]
}

variable "vpc_id" {
  description = "List of vpcs ids"
  type        = list(string)
  default     = ["vpc-0d09c7fd1af724949"]
}

variable "volume" {
  description = "List of vpcs ids"
  type        = list(string)
  default     = ["vol-0a8cf03a89707a2f4"]
}

variable "instance_snapshot" {
  description = "List of vpcs ids"
  type        = list(string)
  default     = ["snap-0ef9e0ff559888458"]
}
