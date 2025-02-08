import _users from '#~/models/user.model'
import { ERRORS } from '#~/utils/error.types'
import { compare, hash } from 'bcrypt'

export default class UserService {
  static async authenticate(username, passwd) {
    const user = await _users.findOne({ username })
    if (!user) {
      throw new Error(ERRORS.AUTH.USER_NOT_FOUND)
    }
    const passwordMatched = await compare(passwd, user.passwd)
    if (!passwordMatched) {
      throw new Error(ERRORS.AUTH.INVALID_CREDENTIALS)
    }
    return user
  }

  static async create({ first_name, last_name, username, email, passwd }) {
    try {
      const hashedPasswd = await hash(passwd, 12)
      const new_user = new _users({
        first_name,
        last_name,
        username,
        email,
        passwd: hashedPasswd,
      })
      const result = await new_user.save()
      return result
    } catch (e) {
      if (e.code === 11000) {
        throw new Error(ERRORS.ACCOUNT.ALREADY_EXISTS)
      }
      throw new Error(ERRORS.INTERNAL_SERVER_ERROR)
    }
  }

  static async exists({ handler, type }) {
    try {
      const user = await _users.findOne({ [type]: handler })
      return Boolean(user)
    } catch (e) {
      throw new Error(ERRORS.INTERNAL_SERVER_ERROR)
    }
  }
}
