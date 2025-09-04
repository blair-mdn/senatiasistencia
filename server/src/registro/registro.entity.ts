import { Column, Entity, ManyToOne, JoinColumn, PrimaryGeneratedColumn } from 'typeorm';
import { User } from '../user/user.entity'


@Entity('registro')
export class Registro {
  @PrimaryGeneratedColumn('uuid')
  id: string;

  // Foreign Key hacia User (estudiante)
  @Column()
  userDni: number;

  // Relación Many-to-One con User (estudiante)
  @ManyToOne(() => User, user => user.registros)
  @JoinColumn({ name: 'userDni', referencedColumnName: 'dni' })
  user: User;

  @Column({ type: 'date' })
  fecha: Date;

  @Column({ type: 'time' })
  horaEntrada: string;

  @Column({ type: 'time', nullable: true })
  horaSalida?: string;

  // DNI del usuario que verifica la asistencia
  @Column()
  verificadoPorDni: number;

  // Relación Many-to-One con User (quien verifica)
  @ManyToOne(() => User)
  @JoinColumn({ name: 'verificadoPorDni', referencedColumnName: 'dni' })
  verificadoPor: User;

  @Column()
  ubicacion: string;

  @Column({ default: true })
  isActive: boolean;

}

