import _users from '#~/models/user.model'
import _friends from '#~/models/friends.model'
import _pendingRequests from '#~/models/pendingRequests.model'
import { UPDATE_TYPE } from '#~/utils/enums.constants'
import { ERRORS } from '#~/utils/error.types'
import { compare, hash } from 'bcrypt'

export default class UserService {
  static payload(user) {
    const payload = {
      _id: user._id,
      first_name: user.first_name,
      last_name: user.last_name,
      username: user.username,
      email: user.email,
    }
    return payload
  }

  static async authenticate(username, passwd) {
    const user = await _users.findOne({ username }, { friends: 0 })
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
        friends: [],
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

  static async userId({ username }) {
    const user = await _users.findOne(
      { username },
      {
        _id: 1,
      },
    )
    if (!user) {
      throw new Error(ERRORS.AUTH.USER_NOT_FOUND)
    }
    return user
  }

  static async details({ _id }) {
    const user = await _users.findById(_id, {
      first_name: 1,
      last_name: 1,
      email: 1,
      bio: 1,
    })
    if (!user) {
      throw new Error(ERRORS.AUTH.USER_NOT_FOUND)
    }
    return user
  }

  static async update({
    _id,
    type,
    first_name,
    last_name,
    email,
    old_passwd,
    new_passwd,
  }) {
    const user = await _users.findById(_id, {
      first_name: 1,
      last_name: 1,
      username: 1,
      email: 1,
      passwd: 1,
      bio: 1,
    })
    if (!user) {
      throw new Error(ERRORS.AUTH.USER_NOT_FOUND)
    }
    switch (type) {
      case UPDATE_TYPE.DISPLAY_NAME:
        user.first_name = first_name
        user.last_name = last_name
        break
      case UPDATE_TYPE.EMAIL:
        user.email = email
        break
      case UPDATE_TYPE.PASSWORD:
        const passwordMatched = await compare(old_passwd, user.passwd)
        if (!passwordMatched) {
          throw new Error(ERRORS.AUTH.INVALID_CREDENTIALS)
        }
        const hashedPasswd = await hash(new_passwd, 12)
        user.passwd = hashedPasswd
        break
    }
    await user.save()
    return user
  }

  static async exists({ handler, type }) {
    try {
      const user = await _users.findOne({ [type]: handler })
      return Boolean(user)
    } catch (e) {
      throw new Error(ERRORS.INTERNAL_SERVER_ERROR)
    }
  }

  static async friends({ _id }) {
    try {
      const friendsList = await _users
        .findById(_id, { friends: 1 })
        .populate({
          path: 'friends',
          select: 'username _id first_name last_name',
        })
        .exec()
      if (!friendsList) {
        throw new Error(ERRORS.INVALID_REQUEST)
      }
      return friendsList
    } catch (e) {
      throw new Error(e.message)
    }
  }

  static async pendingRequests({ _id }) {
    try {
      const pendingRequests = await _pendingRequests.find({ sender_id: _id })
      return pendingRequests
    } catch (e) {
      throw new Error(e.message)
    }
  }

  static async messageRequests({ _id }) {
    try {
      const messageRequests = await _pendingRequests.find({ receiver_id: _id })
      return messageRequests
    } catch (e) {
      throw new Error(e.message)
    }
  }
}
