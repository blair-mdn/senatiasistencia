import { IsBoolean, IsDate, IsNumber, IsString, IsOptional } from 'class-validator'

export class RegistroDto {
    // ID único del registro (UUID generado automáticamente)
    @IsOptional()
    id?: string;

    // DNI del estudiante al que se le registra la asistencia
    @IsNumber({}, { message: 'El DNI del usuario debe ser un número válido' })
    userDni: number;

    // Fecha del registro de asistencia
    @IsDate({ message: 'La fecha debe ser una fecha válida en formato YYYY-MM-DD' })
    fecha: Date;

    // Hora de entrada en formato HH:MM:SS
    @IsString({ message: 'La hora de entrada debe ser una cadena de texto en formato HH:MM:SS' })
    horaEntrada: string;

    // Hora de salida en formato HH:MM:SS (opcional)
    @IsOptional()
    @IsString({ message: 'La hora de salida debe ser una cadena de texto en formato HH:MM:SS' })
    horaSalida?: string;

    // DNI del usuario que verifica/controla la asistencia
    @IsNumber({}, { message: 'El DNI del verificador debe ser un número válido' })
    verificadoPorDni: number;

    // Ubicación donde se registra la asistencia
    @IsString({ message: 'La ubicación debe ser una cadena de texto válida' })
    ubicacion: string;

    // Estado del registro (activo/inactivo) - por defecto true
    @IsOptional()
    @IsBoolean({ message: 'El estado isActive debe ser un valor booleano (true o false)' })
    isActive?: boolean;
}