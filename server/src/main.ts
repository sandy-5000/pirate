import { NestFactory } from '@nestjs/core'
import { AppModule } from './app.module'
import { NestExpressApplication } from '@nestjs/platform-express'
import { join } from 'path'
import { Request, Response, NextFunction } from 'express'

async function bootstrap() {
  const app = await NestFactory.create<NestExpressApplication>(AppModule)

  app.useStaticAssets(join(__dirname, '..', 'views'))

  app.set('views', join(__dirname, '..', 'views'))
  app.set('view engine', 'html')

  app.use((req: Request, res: Response, next: NextFunction) => {
    if (!req.url.startsWith('/api')) {
      res.sendFile(join(__dirname, '..', 'views', 'index.html'), (err) => {
        if (err) {
          console.error('Error sending file:', err)
          res.status(500).send('Internal Server Error')
        }
      })
    } else {
      next()
    }
  })

  await app.listen(process.env.PORT ?? 5000)
}

void bootstrap()
