import { IsDate, IsNumber, IsString, IsEmail, IsBoolean } from 'class-validator'

export class UserDto {
  @IsNumber()
  dni: number;

  @IsEmail()
  email: string;

  @IsString()
  password: string;

  @IsString()
  name: string;

  @IsString()
  lastname: string;

  @IsString()
  rol: string;

  @IsBoolean()
  isActive: boolean;

  @IsDate()
  createdAt: Date;

}
