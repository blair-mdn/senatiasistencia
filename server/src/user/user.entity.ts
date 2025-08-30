import { Column, Entity, PrimaryColumn, OneToMany } from 'typeorm';
import { RegistroAlumno } from '../registro/registro.entity';

@Entity('users')
export class User {
  @PrimaryColumn()
  dni: number;

  @Column()
  email: string;

  @Column()
  password: string;

  @Column()
  name: string;

  @Column()
  lastname: string;

  @Column()
  rol: string;

  @Column()
  isActive: boolean;

  @Column()
  createdAt: Date;

  // Relación One-to-Many con RegistroAluno
  @OneToMany(() => RegistroAlumno, registro => registro.user)
  registros: RegistroAlumno[];

}
