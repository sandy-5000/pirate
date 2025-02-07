import express from 'express'
import cors from 'cors'
import mongoose from 'mongoose'
import dotenv from 'dotenv'
import userController from '#~/controllers/user.controller'
dotenv.config()

const app = express()
app.use(express.json())
app.use(cors())

const PORT = process.env.PORT || 5000
const HOST = process.env.HOST || '0.0.0.0'
const MONGO_DB_URL = process.env.MONGO_DB_URL

app.get('/api', (_, res) => {
  res.status(200).json({
    message: 'Api [pirate]',
  })
})

app.use('/api/user', userController)

app.use('*', (_, res) =>
  res.status(403).json({
    message: 'Method Not Allowed',
  }),
)

mongoose
  .connect(MONGO_DB_URL)
  .then(() => {
    console.log('DB Connected')
    app.listen(PORT, () => console.log(`visit http://${HOST}:${PORT}`))
  })
  .catch((e) => console.log('db NOT connected', e.message))
