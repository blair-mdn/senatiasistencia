import { Column, Entity, ManyToOne, JoinColumn, PrimaryGeneratedColumn } from 'typeorm';
import { User } from '../user/user.entity'


@Entity('registro')
export class Registro {
  @PrimaryGeneratedColumn('uuid')
  id: string;

  // DNI del estudiante (nullable para visitantes)
  @Column({ nullable: true })
  userDni?: number;

  // Relación Many-to-One con User (estudiante) - opcional
  @ManyToOne(() => User, user => user.registros, { nullable: true })
  @JoinColumn({ name: 'userDni', referencedColumnName: 'dni' })
  user?: User;

  // DNI del visitante (nullable para estudiantes)
  @Column({ nullable: true })
  visitorDni?: number;

  // Área visitada (solo para visitantes)
  @Column({ nullable: true })
  areaVisited?: string;

  // Asunto de visita (solo para visitantes)
  @Column({ nullable: true })
  asuntoVisita?: string;

  @Column({ type: 'date', transformer: {
    to: (value: string | Date) => {
      if (typeof value === 'string') {
        return new Date(value);
      }
      return value;
    },
    from: (value: Date) => value
  }})
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

