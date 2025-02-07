import { Module } from '@nestjs/common'
import { ApiModule } from './api/api.module'
import { ServeStaticModule } from '@nestjs/serve-static'
import { join } from 'path'

@Module({
  imports: [
    ServeStaticModule.forRoot({
      rootPath: join(__dirname, '..', 'views'),
      exclude: ['/api*'],
    }),
    ApiModule,
  ],
  controllers: [],
  providers: [],
})
export class AppModule {}
