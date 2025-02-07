import { Module } from '@nestjs/common'
import { JwtModule } from '@nestjs/jwt'

import { ApiController } from './controllers/api.controller'
import { UserController } from './controllers/user.controller'

import { RootApiService } from './services/root.service'
import { AuthService } from './services/auth.service'
import { JwtService } from '@nestjs/jwt'

@Module({
  imports: [
    JwtModule.register({
      secret: 'bC8s9dTq3oXzA!jI8Wv#f5YdU2JwP$k4LqVbZ7eF9S',
      signOptions: { expiresIn: '7d' },
    }),
  ],
  controllers: [ApiController, UserController],
  providers: [RootApiService, AuthService, JwtService],
})
export class ApiModule {}
