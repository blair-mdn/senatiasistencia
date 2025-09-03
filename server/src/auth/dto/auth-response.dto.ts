export class AuthResponseDto {
  access_token: string;
  user: {
    dni: number;
    email: string;
    name: string;
    lastname: string;
    rol: string;
  };
}
