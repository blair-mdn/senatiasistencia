import { Column, Entity, PrimaryColumn, OneToMany, CreateDateColumn } from 'typeorm';
import { Registro } from '../registro/registro.entity';

@Entity('users')
export class User {
  @PrimaryColumn()
  dni: number;

  @Column({ unique: true })
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

  @Column({type: 'timestamp', default: () => 'CURRENT_TIMESTAMP'})
  createdAt: Date;

  // Relación One-to-Many con Registro
  @OneToMany(() => Registro, registro => registro.user)
  registros: Registro[];

}
