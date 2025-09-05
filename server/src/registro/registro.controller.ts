import { Controller, Post, UseGuards, Request, Response, Body, Patch, Get, HttpCode, HttpStatus } from '@nestjs/common';
import { RegistroService } from './registro.service';
import { GuardiaGuard } from '../auth/guards/guardia.guard';
import { RegistroDto } from './dto/registro.dto';
import { UpdateSalidaDto } from './dto/update-salida.dto';
import { VisitorDto } from './dto/visitor.dto';
import { UpdateSalidaVisitorDto } from './dto/update-salida-visitor.dto';

@Controller('registro')
export class RegistroController {

    constructor(private registroService: RegistroService) {}

    @UseGuards(GuardiaGuard)
    @Get('all')
    @HttpCode(HttpStatus.OK)
    async getAllRegistros(){
        return this.registroService.getAllRegistros()
    }

    @UseGuards(GuardiaGuard)
    @Post('create')
    @HttpCode(HttpStatus.CREATED)
    async createRegistro(@Body() body: RegistroDto){
        return this.registroService.createRegistro(body)
    }

    @UseGuards(GuardiaGuard)
    @Patch('updateSalida')
    @HttpCode(HttpStatus.OK)
    async updateHoraSalida(@Body() body: UpdateSalidaDto){
        return this.registroService.updateHoraSalida(body);
    }


    @UseGuards(GuardiaGuard)
    @Post('create/visitor')
    @HttpCode(HttpStatus.CREATED)
    async createRegistroVisitor(@Body() body: VisitorDto){
        return this.registroService.createRegistroVisitor(body)
    }

    @UseGuards(GuardiaGuard)
    @Patch('updateSalida/visitor')
    @HttpCode(HttpStatus.OK)
    async updateHoraSalidaVisitor(@Body() body: UpdateSalidaVisitorDto){
        return this.registroService.updateHoraSalidaVisitor(body);
    }






}
