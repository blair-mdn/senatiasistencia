import { Module } from '@nestjs/common';
import { AppService } from './app.service';
import { UserController } from './user/user.controller';
import { UserModule } from './user/user.module';
import { TypeOrmModule } from '@nestjs/typeorm';
import { ConfigModule, ConfigService } from '@nestjs/config'
import { RegistroService } from './registro/registro.service';



@Module({
  
  imports: [
    ConfigModule.forRoot({ 
      isGlobal: true,
      envFilePath: '../.env' 
    }),
    TypeOrmModule.forRootAsync({
      imports: [ConfigModule],
      inject: [ConfigService],
      useFactory: (configService: ConfigService) => {
        const POSTGRESPassword = configService.get<string>('POSTGRES_PASSWORD');
        const POSTGRESHost = configService.get<string>('POSTGRES_HOST');
        const POSTGRESUser = configService.get<string>('POSTGRES_USER');
        const POSTGRESDB = configService.get<string>('POSTGRES_DB');
        const POSTGRESPort = configService.get<number>('POSTGRES_PORT');

        // Validación de las variables de entorno
        if (!POSTGRESPassword) {
          throw new Error('POSTGRES_PASSWORD no está definido o está vacío. Revisa el archivo .env.');
        }
        if (!POSTGRESHost) {
          throw new Error('POSTGRES_HOST no está definido o está vacío. Revisa el archivo .env.');
        }
        if (!POSTGRESUser) {
          throw new Error('POSTGRES_USER no está definido o está vacío. Revisa el archivo .env.');
        }
        if (!POSTGRESDB) {
          throw new Error('POSTGRES_DB no está definido o está vacío. Revisa el archivo .env.');
        }
        if (!POSTGRESPort) {
          throw new Error('POSTGRES_PORT no está definido o está vacío. Revisa el archivo .env.');
        }

        return {
          type: 'postgres',
          host: POSTGRESHost,
          port: POSTGRESPort,
          username: POSTGRESUser,
          password: POSTGRESPassword,
          database: POSTGRESDB,
          entities: [__dirname + '/**/*.entity{.ts,.js}'],
          synchronize: true, // Set to false in production
          logging: true, // Enable logging for debugging
        };
      },
    }),
    UserModule,
  ],
  
  controllers: [ UserController],
  providers: [AppService, RegistroService],
  
})
export class AppModule {}
