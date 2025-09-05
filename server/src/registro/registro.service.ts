import { Injectable, BadRequestException } from '@nestjs/common';
import { Registro } from './registro.entity';
import { InjectRepository } from '@nestjs/typeorm';
import { Repository } from 'typeorm';
import { Request, Response } from 'express';
import { RegistroDto } from './dto/registro.dto';
import { UpdateSalidaDto } from './dto/update-salida.dto';
import { VisitorDto } from './dto/visitor.dto';
import { UpdateSalidaVisitorDto } from './dto/update-salida-visitor.dto';

@Injectable()
export class RegistroService {

    constructor(
        @InjectRepository(Registro)
        private registroRepository: Repository<Registro>,
    ) {}

    // Obtener todos los registros
    async getAllRegistros(): Promise<Registro[]>{
        try {
            const registros= await this.registroRepository.find({})
            return registros
        }catch (error) {
            if (error.code === '404') throw new BadRequestException('No se encontraron registros');

            throw new BadRequestException('Error al obtener los registros: ' + error.message);
        }
    }

    // Crear un nuevo registro
    async createRegistro(data: RegistroDto): Promise<Registro> {
        try {
            // Convertir la fecha string a Date object si es necesario
            const registroData = {
                ...data,
                fecha: new Date(data.fecha)
            };
            
            const newRegistro = this.registroRepository.create(registroData);
            const savedRegistro = await this.registroRepository.save(newRegistro);
            return savedRegistro;
        } catch (error) {
            if (error.code === '23503') throw new BadRequestException('El DNI del usuario o verificador no existe en el sistema');
            if (error.code === '23505') throw new BadRequestException('Ya existe un registro con estos datos');
            if (error.code === '23502') throw new BadRequestException('Faltan campos obligatorios en el registro');
            
            throw new BadRequestException('Error al crear el registro: ' + error.message);
        }
    }

    // Actualizar la hora de salida de un registro existente
    async updateHoraSalida(data: UpdateSalidaDto): Promise<Registro> {
        try {
            // Buscar el ultimo registro activo del estudiante por su DNI
            const registro = await this.registroRepository.findOne({
                where: { userDni: data.userDni, horaSalida: null },
                order: { fecha: 'DESC', horaEntrada: 'DESC' },
            });

            if (!registro) throw new BadRequestException('No se encontró un registro activo para el estudiante con el DNI proporcionado');


            registro.horaSalida = data.horaSalida;
            return await this.registroRepository.save(registro);
        } catch (error) {
            throw new BadRequestException('Error al actualizar la hora de salida: ' + error.message);
        }
    }

    async createRegistroVisitor(data: VisitorDto): Promise<Registro> {
        try{
            console.log('Datos recibidos:', data);
            
            // Convertir la fecha string a Date object si es necesario
            const registroData = {
                ...data,
                fecha: new Date(data.fecha)
            };
            
            const newRegistro = this.registroRepository.create(registroData);
            const savedRegistro = await this.registroRepository.save(newRegistro);
            return savedRegistro;
        } catch (error) {
            if (error.code === '23503') throw new BadRequestException('El DNI del verificador no existe en el sistema');
            if (error.code === '23505') throw new BadRequestException('Ya existe un registro con estos datos');
            if (error.code === '23502') throw new BadRequestException('Faltan campos obligatorios en el registro');
            
            throw new BadRequestException('Error al crear el registro: ' + error.message);
    
        }


    }

    async updateHoraSalidaVisitor(data: UpdateSalidaVisitorDto): Promise<Registro> {
        try {
            // Buscar el ultimo registro activo del visitante por su DNI
            const registro = await this.registroRepository.findOne({
                where: { visitorDni: data.visitorDni, horaSalida: null },
                order: { fecha: 'DESC', horaEntrada: 'DESC' },
            });

            if (!registro) throw new BadRequestException('No se encontró un registro activo para el visitante con el DNI proporcionado');

            registro.horaSalida = data.horaSalida;
            return await this.registroRepository.save(registro);
        } catch (error) {
            throw new BadRequestException('Error al actualizar la hora de salida: ' + error.message);
        }

    }





















}
