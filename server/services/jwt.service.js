import jwt from 'jsonwebtoken'
import dotenv from 'dotenv'
import { ERRORS } from '#~/utils/error.types'

dotenv.config()
const secret = process.env.JWT_SECRET

export default class JwtService {
  static sign(payload, expiresIn = '7d') {
    if (!secret) {
      throw new Error(ERRORS.JWT.SECRET_NOT_DEFINED)
    }
    return jwt.sign(payload, secret, { expiresIn })
  }

  static verify(token) {
    if (!secret) {
      throw new Error(ERRORS.JWT.SECRET_NOT_DEFINED)
    }
    try {
      return jwt.verify(token, secret)
    } catch (error) {
      throw new Error(ERRORS.JWT.INVALID_OR_EXPIRED)
    }
  }

  static decode(token) {
    return jwt.decode(token)
  }
}
