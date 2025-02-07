import { Controller, Post, HttpCode, HttpStatus, Body } from '@nestjs/common'
import { AuthService } from '../services/auth.service'

@Controller('api/user/login')
export class UserController {
  constructor(private readonly authService: AuthService) {}

  @Post()
  @HttpCode(HttpStatus.OK)
  async login(
    @Body('username') username: string,
    @Body('passwd') passwd: string,
  ): Promise<object> {
    return this.authService.login(username, passwd)
  }
}
