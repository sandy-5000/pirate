import _users from '#~/models/user.model'
import _friends from '#~/models/friends.model'
import _pendingRequests from '#~/models/pendingRequests.model'
import { UPDATE_TYPE, FRIENDS_TYPE } from '#~/utils/enums.constants'
import { ERRORS } from '#~/utils/error.types'
import { sortIds } from '#~/utils/helper.util'
import { compare, hash } from 'bcrypt'

export default class UserService {
  static payload(user) {
    const payload = {
      _id: user._id,
      name: user.name,
      username: user.username,
      email: user.email,
      profile_image: user.profile_image,
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

  static async create({ name, username, email, passwd }) {
    try {
      const hashedPasswd = await hash(passwd, 12)
      const new_user = new _users({
        name,
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
      name: 1,
      username: 1,
      email: 1,
      bio: 1,
      profile_image: 1,
    })
    if (!user) {
      throw new Error(ERRORS.AUTH.USER_NOT_FOUND)
    }
    return user
  }

  static async getPublicKey(_id) {
    const user = await _users.findById(_id, { public_key: 1 })
    if (!user) {
      throw new Error(ERRORS.AUTH.USER_NOT_FOUND)
    }
    return user.public_key
  }

  static async updatePublicKey(_id, public_key) {
    const user = await _users.findById(_id, { public_key: 1 })
    if (!user) {
      throw new Error(ERRORS.AUTH.USER_NOT_FOUND)
    }
    user.public_key = public_key
    await user.save()
    return user.public_key
  }

  static async update({
    _id,
    type,
    name,
    email,
    old_passwd,
    new_passwd,
    profile_image,
  }) {
    const user = await _users.findById(_id, {
      name: 1,
      username: 1,
      email: 1,
      passwd: 1,
      bio: 1,
      profile_image: 1,
    })
    if (!user) {
      throw new Error(ERRORS.AUTH.USER_NOT_FOUND)
    }
    switch (type) {
      case UPDATE_TYPE.DISPLAY_NAME:
        user.name = name
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
      case UPDATE_TYPE.PROFILE_IMAGE:
        user.profile_image = profile_image
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
          select: '_id username name profile_image',
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
      const pendingRequests = await _pendingRequests
        .find({ sender_id: _id })
        .populate({
          path: 'receiver_id',
          select: '_id username name profile_image',
        })
        .exec()
      return pendingRequests
    } catch (e) {
      throw new Error(e.message)
    }
  }

  static async messageRequests({ _id }) {
    try {
      const messageRequests = await _pendingRequests
        .find({ receiver_id: _id })
        .populate({
          path: 'sender_id',
          select: '_id username name profile_image',
        })
        .exec()
      return messageRequests
    } catch (e) {
      throw new Error(e.message)
    }
  }

  static async friendType(sender_id, receiver_id) {
    try {
      const [min_id, max_id] = sortIds(sender_id, receiver_id)
      const alreadyFriends = await _friends.findOne({ min_id, max_id })
      if (alreadyFriends) {
        let block_type = alreadyFriends.block_type
        if (block_type === 0) {
          return { type: FRIENDS_TYPE.FRIENDS }
        }
        if (min_id === receiver_id) {
          block_type *= -1
        }
        if (block_type === 1) {
          return { type: FRIENDS_TYPE.SENDER_BLOCKED }
        }
        if (block_type === -1) {
          return { type: FRIENDS_TYPE.RECEIVER_BLOCKED }
        }
      }
      const request = await _pendingRequests.findOne({
        sender_id,
        receiver_id,
      })
      if (request) {
        return { type: FRIENDS_TYPE.REQUEST_SENT }
      }
      const reverse = await _pendingRequests.findOne({
        sender_id: receiver_id,
        receiver_id: sender_id,
      })
      if (reverse) {
        return { type: FRIENDS_TYPE.REQUEST_RECEIVED }
      }
      const sender = await _users.findById(sender_id, { _id: 1 })
      if (!sender) {
        throw new Error(ERRORS.INVALID_REQUEST)
      }
      const receiver = await _users.findById(receiver_id, { _id: 1 })
      if (!receiver) {
        throw new Error(ERRORS.INVALID_REQUEST)
      }
      if (!alreadyFriends) {
        return { type: FRIENDS_TYPE.NOT_FRIENDS }
      }
      throw new Error(ERRORS.INVALID_REQUEST)
    } catch (e) {
      throw new Error(e.message)
    }
  }
}
