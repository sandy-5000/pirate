import { Module } from '@nestjs/common'
import { ApiController } from './controllers/api.controller'
import { RootApiService } from './services/root.service'

@Module({
  imports: [],
  controllers: [ApiController],
  providers: [RootApiService],
})
export class ApiModule {}
