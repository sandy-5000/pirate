import { Controller, Get, HttpCode, HttpStatus } from '@nestjs/common'
import { RootApiService } from '../services/root.service'

@Controller('api')
export class ApiController {
  constructor(private readonly rootApiService: RootApiService) {}

  @Get()
  @HttpCode(HttpStatus.OK)
  freezer(): object {
    return this.rootApiService.getMessage()
  }
}
