import admin from 'firebase-admin'
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

  static async getToken(user_id) {
    try {
      const result = await _pushTokens.findOne({ user_id })
      if (!result) {
        throw new Error(ERRORS.AUTH.USER_NOT_FOUND)
      }
      return result
    } catch (e) {
      if (e.message === ERRORS.AUTH.USER_NOT_FOUND) {
        throw new Error(ERRORS.AUTH.USER_NOT_FOUND)
      }
      throw new Error(ERRORS.INTERNAL_SERVER_ERROR)
    }
  }

  static async notify(token, title, body, sender_id, username, type = '') {
    const message = {
      notification: {
        title,
        body,
      },
      data: {
        sender_id,
        username,
        type,
      },
      token,
    }
    try {
      const response = await admin.messaging().send(message)
      return response
    } catch (e) {
      throw new Error(e.message)
    }
  }
}
