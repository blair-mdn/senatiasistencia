import { Injectable, CanActivate, ExecutionContext, ForbiddenException } from '@nestjs/common';
import { JwtAuthGuard } from './jwt-auth.guard';

@Injectable()
export class GuardiaGuard extends JwtAuthGuard {
    async canActivate(context: ExecutionContext): Promise<boolean> {

        // Validar si el JWT es válido
        const jwtValid = await super.canActivate(context);
        if (!jwtValid) return false;

        // obtener el usuario del request
        const request = context.switchToHttp().getRequest();
        const user = request.user;

        // verificar que el usuario tenga rol de guardia
        if (!user || user.rol !== 'guardia') throw new ForbiddenException('Solo los guardias pueden acceder a este recurso');

        return true;
    }
}