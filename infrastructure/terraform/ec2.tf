
resource "aws_volume_attachment" "ebs_att" {
  device_name = "/dev/sdf"
  volume_id   = var.volume[0]
  instance_id = aws_instance.alphaBackEnd.id
}

resource "aws_instance" "alphaBackEnd" {
  ami           = "ami-089a545a9ed9893b6"
  instance_type = "t3.medium"
  key_name = "alphaAccess"
  subnet_id = var.vpc_private_subnet_id[0]
  vpc_security_group_ids = [aws_security_group.allow_tls.id]
  associate_public_ip_address = false

  tags = {
    Name = "AlphaBackServer"
  }

#   provisioner "file" {
#     source = "./alphaAccess"
#     destination = "/home/ec2-user/alphaAccess.pem"
  
#     connection {
#       type = "ssh"
#       host = "${self.private_ip}"
#       user = "ec2-user"
#       private_key = "${file("./alphaAccess.pem")}"
#     }  
#   }
}

resource "aws_instance" "db" {
  ami           = "ami-089a545a9ed9893b6"
  instance_type = "t2.micro"
  key_name = "alphaAccess"
  subnet_id = var.vpc_private_db_subnet_id[0]
  vpc_security_group_ids = [aws_security_group.allow_tls_db.id]

  tags = {
    Name = "Alpha DB Server"
  }
}

