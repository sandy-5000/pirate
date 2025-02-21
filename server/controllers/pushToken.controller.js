import express from 'express'
import bodyParser from 'body-parser'
import PushTokenService from '#~/services/pushToken.service'
import UserService from '#~/services/user.service'
import { validateToken } from '#~/middlewares/jwt.middleware'
import { NOTIFICATION_TYPE } from '#~/utils/enums.constants'

const app = express.Router()
app.use(bodyParser.urlencoded({ extended: false }))

app.route('/update').put(validateToken, async (req, res) => {
  const { token } = req.body
  try {
    const { _id = '' } = req.token_data || {}
    await PushTokenService.update(_id, token)
    return res.status(200).json({
      updated: true,
    })
  } catch (e) {
    return res.status(500).json({
      error: ERRORS.INTERNAL_SERVER_ERROR,
    })
  }
})

app.route('/message/:pirateId').post(validateToken, async (req, res) => {
  const { pirateId } = req.params
  const { message } = req.body
  const { username = '' } = req.token_data || {}
  try {
    const user_id = await UserService.userId({ username: pirateId })
    const tokenData = await PushTokenService.getToken(user_id)
    const result = await PushTokenService.notify(
      tokenData?.token || '',
      username,
      message,
      tokenData?._id || '',
      NOTIFICATION_TYPE.MESSAGE,
    )
    return res.status(200).json({
      result,
    })
  } catch (e) {
    return res.status(500).json({
      error: ERRORS.INTERNAL_SERVER_ERROR,
    })
  }
})

const pushTokenController = app
export default pushTokenController
