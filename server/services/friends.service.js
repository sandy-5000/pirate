import mongoose from 'mongoose'
import _friends from '#~/models/friends.model'
import _pendingRequests from '#~/models/pendingRequests.model'
import _users from '#~/models/user.model'
import PushTokenService from '#~/services/pushToken.service'
import { ERRORS } from '#~/utils/error.types'
import { sortIds } from '#~/utils/helper.util'
import { NOTIFICATION_TYPE } from '#~/utils/enums.constants'

export default class FriendsService {
  static async request(sender_id, receiver_id) {
    try {
      // check for consistancy
      const sender = await _users.findById(sender_id, { friends: 0 })
      if (!sender) {
        throw new Error(ERRORS.INVALID_REQUEST)
      }
      const receiver = await _users.findById(receiver_id, { friends: 0 })
      if (!receiver) {
        throw new Error(ERRORS.INVALID_REQUEST)
      }
      const request = await _pendingRequests.findOne({
        sender_id: sender_id,
        receiver_id: receiver_id,
      })
      if (request) {
        throw new Error(ERRORS.REQUEST.ALREADY_EXISTS)
      }
      const reverse = await _pendingRequests.findOne({
        sender_id: receiver_id,
        receiver_id: sender_id,
      })
      if (reverse) {
        throw new Error(ERRORS.REQUEST.USER_ALREADY_REQUESTED)
      }
      // end check
      const new_request = new _pendingRequests({ sender_id, receiver_id })
      await new_request.save()

      try {
        const tokenData = await PushTokenService.getToken(receiver_id)
        const name =
          `${sender.first_name} ${sender.last_name} (${sender.username})`.trim()
        await PushTokenService.notify(
          tokenData?.token || '',
          sender.username,
          `${name} has sent you a message request.`,
          NOTIFICATION_TYPE.MESSAGE_REQUEST,
        )
      } catch (e) {
        console.log(e)
      }

      return new_request
    } catch (e) {
      console.log(e)
      throw new Error(e.message)
    }
  }

  static async cancel(sender_id, receiver_id) {
    try {
      await _pendingRequests.deleteOne({
        sender_id,
        receiver_id,
      })
      return { flag: 'DELETED' }
    } catch (e) {
      throw new Error(e.message)
    }
  }

  static async accept(sender_id, receiver_id) {
    const session = await mongoose.startSession()
    session.startTransaction()
    try {
      // check for consistancy
      const sender = await _users.findById(sender_id)
      if (!sender) {
        throw new Error(ERRORS.INVALID_REQUEST)
      }
      const receiver = await _users.findById(receiver_id)
      if (!receiver) {
        throw new Error(ERRORS.INVALID_REQUEST)
      }
      const reverse = await _pendingRequests.findOne({
        sender_id: receiver_id,
        receiver_id: sender_id,
      })
      if (reverse) {
        throw new Error(ERRORS.INVALID_REQUEST)
      }
      const request = await _pendingRequests.findOne({
        sender_id,
        receiver_id,
      })
      if (!request) {
        throw new Error(ERRORS.INVALID_REQUEST)
      } else {
        await _pendingRequests.deleteOne(
          { sender_id, receiver_id },
          { session },
        )
      }
      const [min_id, max_id] = sortIds(sender_id, receiver_id)
      const alreadyFriends = await _friends.findOne({ min_id, max_id })
      if (alreadyFriends) {
        throw new Error(ERRORS.INVALID_REQUEST)
      }
      // end check
      sender.friends.push(receiver_id)
      receiver.friends.push(sender_id)
      await sender.save({ session })
      await receiver.save({ session })
      const new_pair = new _friends({ min_id, max_id })
      await new_pair.save({ session })
      await session.commitTransaction()

      try {
        const tokenData = await PushTokenService.getToken(sender_id)
        const name =
          `${receiver.first_name} ${receiver.last_name} (${receiver.username})`.trim()
        await PushTokenService.notify(
          tokenData?.token || '',
          receiver.username,
          `${name} has accepted your message request.`,
          NOTIFICATION_TYPE.MESSAGE_REQUEST,
        )
      } catch (e) {
        console.log(e)
      }

      return receiver
    } catch (e) {
      await session.abortTransaction()
      console.log(e)
      throw new Error(e.message)
    } finally {
      session.endSession()
    }
  }

  static async reject(sender_id, receiver_id) {
    try {
      const receiver = await _users.findById(receiver_id, {
        friends: 0,
        bio: 0,
      })
      if (!receiver) {
        throw new Error(ERRORS.INVALID_REQUEST)
      }
      const request = await _pendingRequests.findOne({
        sender_id,
        receiver_id,
      })
      if (!request) {
        throw new Error(ERRORS.INVALID_REQUEST)
      } else {
        await _pendingRequests.deleteOne({ sender_id, receiver_id })
      }

      try {
        const tokenData = await PushTokenService.getToken(sender_id)
        const name =
          `${receiver.first_name} ${receiver.last_name} (${receiver.username})`.trim()
        await PushTokenService.notify(
          tokenData?.token || '',
          receiver.username,
          `${name} has rejected your message request.`,
          NOTIFICATION_TYPE.MESSAGE_REQUEST,
        )
      } catch (e) {
        console.log(e)
      }

      return { flag: 'REJECTED' }
    } catch (e) {
      console.log(e)
      throw new Error(e.message)
    }
  }

  static async find(username) {
    try {
      const people = await _users
        .find(
          { username: { $regex: `^${username}`, $options: 'i' } },
          {
            username: 1,
            first_name: 1,
            last_name: 1,
            profile_image: 1,
          },
        )
        .limit(30)
      return people
    } catch {
      throw new Error(e.message)
    }
  }
}
