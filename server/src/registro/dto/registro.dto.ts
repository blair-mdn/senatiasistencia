import { IsBoolean, IsDate, IsNumber, IsString, IsOptional } from 'class-validator'

export class RegistroDto {
    @IsString()
    id: string;

    @IsNumber()
    userDni: number;

    @IsDate()
    fecha: Date;

    @IsDate()
    horaEntrada: Date;

    @IsOptional()
    @IsDate()
    horaSalida?: Date;

    @IsString()
    verificadoPor: string;

    @IsString()
    ubicacion: string;

    @IsBoolean()
    isActive: boolean;
}