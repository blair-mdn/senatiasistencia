import { IsNumber, IsString } from 'class-validator'

export class UpdateSalidaDto {
    // DNI del estudiante al que se le registra la salida
    @IsNumber({}, { message: 'El DNI del usuario debe ser un número válido' })
    userDni: number;

    // Hora de salida en formato HH:MM:SS
    @IsString({ message: 'La hora de salida debe ser una cadena de texto en formato HH:MM:SS' })
    horaSalida: string;
}