import { Column, Entity, ManyToOne, JoinColumn, PrimaryGeneratedColumn } from 'typeorm';
import { User } from '../user/user.entity'


@Entity('registro')
export class RegistroAlumno {
  @PrimaryGeneratedColumn('uuid')
  id: string;

  // Foreign Key hacia User
  @Column()
  userDni: number;

  // Relación Many-to-One con User
  @ManyToOne(() => User, user => user.registros)
  @JoinColumn({ name: 'userDni', referencedColumnName: 'dni' })
  user: User;

  @Column()
  fecha: Date;

  @Column()
  horaEntrada: Date;

  @Column({ nullable: true })
  horaSalida?: Date;

  @Column()
  verificadoPor: string;

  @Column()
  ubicacion: string;

  @Column()
  isActive: boolean;

}

