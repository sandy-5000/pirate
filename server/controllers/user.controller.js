import express from 'express'
import bodyParser from 'body-parser'
import UserService from '#~/services/user.service'
import JwtService from '#~/services/jwt.service'
import { validateToken } from '#~/middlewares/jwt.middleware'
import { ERRORS } from '#~/utils/error.types'
import { HANDLER_TYPE, UPDATE_TYPE } from '#~/utils/enums.constants'

const app = express.Router()
app.use(bodyParser.urlencoded({ extended: false }))

app.route('/login').post(async (req, res) => {
  const { username, passwd } = req.body
  try {
    let user = await UserService.authenticate(username, passwd)
    user = JSON.parse(JSON.stringify(user))
    const payload = UserService.payload(user)
    const token = JwtService.sign(payload)
    return res.json({ result: { ...user, passwd: '' }, token })
  } catch (e) {
    if (
      e.message === ERRORS.AUTH.INVALID_CREDENTIALS ||
      e.message === ERRORS.AUTH.USER_NOT_FOUND
    ) {
      return res.status(401).json({
        error:
          ERRORS.AUTH.INVALID_CREDENTIALS + ' OR ' + ERRORS.AUTH.USER_NOT_FOUND,
      })
    }
    return res.status(500).json({
      error: ERRORS.INTERNAL_SERVER_ERROR,
    })
  }
})

app.route('/register').post(async (req, res) => {
  const { first_name, last_name, username, email, passwd } = req.body
  try {
    let user = await UserService.create({
      first_name,
      last_name,
      username,
      email,
      passwd,
    })
    user = JSON.parse(JSON.stringify(user))
    const payload = UserService.payload(user)
    const token = JwtService.sign(payload)
    return res.json({ result: { ...user, passwd: '' }, token })
  } catch (e) {
    if (e.message === ERRORS.ACCOUNT.ALREADY_EXISTS) {
      return res.status(409).json({
        error: ERRORS.ACCOUNT.ALREADY_EXISTS,
      })
    }
    return res.status(500).json({
      error: ERRORS.INTERNAL_SERVER_ERROR,
    })
  }
})

app.route('/search/:handler').get(async (req, res) => {
  const { handler } = req.params
  let { type = '' } = req.query
  if (!Object.values(HANDLER_TYPE).includes(type)) {
    type = HANDLER_TYPE.USERNAME
  }
  try {
    const flag = await UserService.exists({ handler, type })
    return res.status(200).json({ flag })
  } catch (e) {
    return res.status(500).json({
      error: ERRORS.INTERNAL_SERVER_ERROR,
    })
  }
})

app
  .route('/profile')
  .get(validateToken, async (req, res) => {
    try {
      const { _id = '' } = req.token_data || {}
      let user = await UserService.details({ _id })
      user = JSON.parse(JSON.stringify(user))
      return res.json({ result: { ...user } })
    } catch (e) {
      if (e.message === ERRORS.INTERNAL_SERVER_ERROR) {
        return res.status(500).json({
          error: ERRORS.INTERNAL_SERVER_ERROR,
        })
      }
      return res.status(400).json({
        error: e.message,
      })
    }
  })
  .patch(validateToken, async (req, res) => {
    const { type = '' } = req.query
    if (!Object.values(UPDATE_TYPE).includes(type)) {
      return res.status(400).json({ error: ERRORS.INVALID_REQUEST })
    }
    try {
      const { _id = '' } = req.token_data || {}
      let user = await UserService.update({
        _id,
        type,
        ...(req.body || {}),
      })
      user = JSON.parse(JSON.stringify(user))
      const payload = UserService.payload(user)
      const token = JwtService.sign(payload)
      return res.json({ result: { ...user, passwd: '' }, token })
    } catch (e) {
      if (e.message === ERRORS.INTERNAL_SERVER_ERROR) {
        return res.status(500).json({
          error: ERRORS.INTERNAL_SERVER_ERROR,
        })
      }
      return res.status(400).json({
        error: e.message,
      })
    }
  })

app.route('/secure').post(validateToken, async (req, res) => {
  res.json(req.token_data)
})

const userController = app
export default userController
