import { Injectable } from '@nestjs/common'

@Injectable()
export class RootApiService {
  getMessage(): object {
    return {
      msg: '__pirate__',
    }
  }
}
