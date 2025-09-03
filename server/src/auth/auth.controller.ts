import { Controller, Post, UseGuards, Request, Body, Patch, Get, HttpCode, HttpStatus } from '@nestjs/common';
import { AuthService } from './auth.service';
import { LocalAuthGuard } from './guards/local-auth.guard';
import { JwtAuthGuard } from './guards/jwt-auth.guard';
import { LoginDto } from './dto/login.dto';
import { ChangePasswordDto } from './dto/change-password.dto';
import { AuthResponseDto } from './dto/auth-response.dto';

@Controller('auth')
export class AuthController {
    constructor(private authService: AuthService) {}

    @UseGuards(LocalAuthGuard)
    @Post('login')
    @HttpCode(HttpStatus.OK)
    async login(@Request() req, @Body() loginDto: LoginDto): Promise<AuthResponseDto> {
        return this.authService.login(req.user);
    }

    @UseGuards(JwtAuthGuard)
    @Patch('change-password')
    async changePassword(
        @Request() req,
        @Body() changePasswordDto: ChangePasswordDto
    ): Promise<{ message: string }> {
        return this.authService.changePassword(req.user.email, changePasswordDto);
    }

    @UseGuards(JwtAuthGuard)
    @Get('profile')
    getProfile(@Request() req) {
        return req.user;
    }
}
