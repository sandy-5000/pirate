import _pushTokens from '#~/models/pushToken.model'
import { ERRORS } from '#~/utils/error.types'

export default class PushTokenService {
  static async update(user_id, token) {
    try {
      const result = await _pushTokens.updateOne(
        { user_id },
        { $set: { token } },
        { upsert: true },
      )
      return result
    } catch (e) {
      throw new Error(ERRORS.INTERNAL_SERVER_ERROR)
    }
  }
}
