import express from 'express'
import bodyParser from 'body-parser'
import { validateToken } from '#~/middlewares/jwt.middleware'

const app = express.Router()
app.use(bodyParser.urlencoded({ extended: false }))

app.route('/secure').post(validateToken, async (req, res) => {
  res.json(req.token_data)
})

const userController = app
export default userController
