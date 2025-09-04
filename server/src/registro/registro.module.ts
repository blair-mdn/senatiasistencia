import { Module } from '@nestjs/common';
import { RegistroController } from './registro.controller';
import { RegistroService } from './registro.service';
import { TypeOrmModule } from '@nestjs/typeorm';
import { Registro } from './registro.entity';


@Module({
  controllers: [RegistroController],
  providers: [RegistroService],
  imports: [TypeOrmModule.forFeature([Registro])],
})
export class RegistroModule {}
