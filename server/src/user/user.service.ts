import { Injectable } from '@nestjs/common';
import { User } from './user.entity';
import { UserDto } from './dto/user.dto'
import { InjectRepository } from '@nestjs/typeorm';
import { Repository } from 'typeorm';
import * as bcrypt from 'bcrypt';

@Injectable()
export class UserService {
    constructor(
        @InjectRepository(User)
        private readonly userRepository: Repository<User>,
    ) {}



    // Buscar un usuario por su DNI
    async findByDni(dni: number): Promise<User | null> {
        return await this.userRepository.findOne({ where: { dni } });
    }

    // Buscar todos los usuarios
    async findAllUsers() {
        return await this.userRepository.find();
    }

    // Buscar un usuario por su correo electrónico
    async findByEmail(email: string) {
        return await this.userRepository.findOne({ where: { email } });
    }

    // Hashear contraseña
    async hashPassword(password: string): Promise<string> {
        const saltRounds = 12;
        return await bcrypt.hash(password, saltRounds);
    }

    // Validar contraseña
    async validatePassword(password: string, hashedPassword: string): Promise<boolean> {
        return await bcrypt.compare(password, hashedPassword);
    }

    // Actualizar contraseña de usuario
    async updatePassword(email: string, newPassword: string): Promise<void> {
        const hashedPassword = await this.hashPassword(newPassword);
        await this.userRepository.update({ email }, { password: hashedPassword });
    }
}
