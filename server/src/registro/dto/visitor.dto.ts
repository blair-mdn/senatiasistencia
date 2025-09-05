import { IsBoolean, IsDateString, IsNumber, IsString, IsOptional } from 'class-validator'

export class VisitorDto {
    // ID único del registro (UUID generado automáticamente)
    @IsOptional()
    id?: string;

    // DNI del visitante al que se le registra la entrada
    @IsNumber({}, { message: 'El DNI del visitante debe ser un número válido' })
    visitorDni: number;

    // Fecha del registro de asistencia
    @IsDateString({}, { message: 'La fecha debe ser una fecha válida en formato YYYY-MM-DD' })
    fecha: string;

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

    // Área visitada
    @IsString({ message: 'El área visitada debe ser una cadena de texto válida' })
    areaVisited: string;

    // Asunto de visita
    @IsOptional()
    @IsString({ message: 'El asunto de la visita debe ser una cadena de texto válida' })
    asuntoVisita?: string;


    // Estado del registro (activo/inactivo) - por defecto true
    @IsOptional()
    @IsBoolean({ message: 'El estado isActive debe ser un valor booleano (true o false)' })
    isActive?: boolean;
}