import express from 'express'
import bodyParser from 'body-parser'
import { validateToken } from '#~/middlewares/jwt.middleware'
import FriendsService from '#~/services/friends.service'
import UserService from '#~/services/user.service'
import JwtService from '#~/services/jwt.service'
import { ERRORS } from '#~/utils/error.types'

const app = express.Router()
app.use(bodyParser.urlencoded({ extended: false }))

app.route('/request').post(validateToken, async (req, res) => {
  try {
    const { _id = '' } = req.token_data || {}
    const { receiver_id } = req.body
    if (_id === receiver_id) {
      throw new Error(ERRORS.INVALID_REQUEST)
    }
    let request = await FriendsService.request(_id, receiver_id)
    request = JSON.parse(JSON.stringify(request))
    return res.json({ result: { ...request } })
  } catch (e) {
    return res.status(400).json({
      error: e.message,
    })
  }
})

app.route('/accept').post(validateToken, async (req, res) => {
  try {
    const { _id = '' } = req.token_data || {}
    const { sender_id } = req.body
    if (_id === sender_id) {
      throw new Error(ERRORS.INVALID_REQUEST)
    }
    let user = await FriendsService.accept(sender_id, _id)
    user = JSON.parse(JSON.stringify(user))
    const payload = UserService.payload(user)
    const token = JwtService.sign(payload)
    return res.json({ result: { ...user, passwd: '' }, token })
  } catch (e) {
    return res.status(400).json({
      error: e.message,
    })
  }
})

app.route('/reject').post(validateToken, async (req, res) => {
  try {
    const { _id = '' } = req.token_data || {}
    const { sender_id } = req.body
    if (_id === sender_id) {
      throw new Error(ERRORS.INVALID_REQUEST)
    }
    let result = await FriendsService.reject(sender_id, _id)
    return res.json({ result })
  } catch (e) {
    return res.status(400).json({
      error: e.message,
    })
  }
})

const friendsController = app
export default friendsController
