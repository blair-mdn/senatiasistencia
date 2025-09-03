import { Injectable, UnauthorizedException, BadRequestException } from '@nestjs/common';
import { UserService } from '../user/user.service';
import { JwtService } from '@nestjs/jwt';
import { User } from '../user/user.entity';
import { AuthResponseDto } from './dto/auth-response.dto';
import { ChangePasswordDto } from './dto/change-password.dto';

@Injectable()
export class AuthService {
    constructor(
        private userService: UserService,
        private jwtService: JwtService,
    ) {}

    // Validar credenciales del usuario
    async validateUser(email: string, password: string): Promise<any> {
        const user = await this.userService.findByEmail(email);
        
        if (!user) return null;
        if (!user.isActive) throw new UnauthorizedException('Usuario inactivo');

        const isPasswordValid = await this.userService.validatePassword(password, user.password);
        
        if (user && isPasswordValid) {
            // Remover la contraseña del objeto retornado
            const { password, ...result } = user;
            return result;
        }
        
        return null;
    }

    // Login del usuario
    async login(user: User): Promise<AuthResponseDto> {
        const payload = { 
            sub: user.dni, 
            email: user.email,
            name: user.name,
            lastname: user.lastname,
            rol: user.rol,
        };
        
        return {
            access_token: this.jwtService.sign(payload),
            user: {
                dni: user.dni,
                email: user.email,
                name: user.name,
                lastname: user.lastname,
                rol: user.rol,
            }
        };
    }

    // Cambiar contraseña
    async changePassword(userEmail: string, changePasswordDto: ChangePasswordDto): Promise<{ message: string }> {
        const user = await this.userService.findByEmail(userEmail);
        console.log(userEmail);
        if (!user) throw new BadRequestException('Usuario no encontrado');

        // Validar la contraseña actual
        const isCurrentPasswordValid = await this.userService.validatePassword(
            changePasswordDto.currentPassword,
            user.password
        );

        if (!isCurrentPasswordValid) throw new UnauthorizedException('La contraseña actual es incorrecta');


        // Verificar que la nueva contraseña sea diferente
        const isSamePassword = await this.userService.validatePassword(
            changePasswordDto.newPassword,
            user.password
        );

        if (isSamePassword) throw new BadRequestException('La nueva contraseña debe ser diferente a la actual');

        // Actualizar la contraseña
        await this.userService.updatePassword(user.email, changePasswordDto.newPassword);

        return { message: 'Contraseña actualizada correctamente' };
    }
}
