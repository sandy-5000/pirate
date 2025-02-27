import express from 'express'
import cors from 'cors'
import mongoose from 'mongoose'
import dotenv from 'dotenv'
import admin from 'firebase-admin'
import http from 'http'
import { Server as SocketServer } from 'socket.io'
import { readFileSync } from 'fs'
import userController from '#~/controllers/user.controller'
import pushTokenController from '#~/controllers/pushToken.controller'
import friendsController from '#~/controllers/friends.controller'
import { ERRORS } from '#~/utils/error.types'
import { USER_STATUS } from '#~/utils/enums.constants'
dotenv.config()

const app = express()
const server = http.createServer(app)

app.use(express.json())
app.use(cors())

const serviceAccount = JSON.parse(readFileSync('pirate.firebase.json', 'utf-8'))
admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
})

const PORT = process.env.PORT || 5000
const HOST = process.env.HOST || '0.0.0.0'
const MONGO_DB_URL = process.env.MONGO_DB_URL

app.get('/api', (_, res) => {
  res.status(200).json({
    message: 'Api [pirate]',
  })
})

app.use('/api/user', userController)
app.use('/api/pushtoken', pushTokenController)
app.use('/api/friends', friendsController)

app.use('*', (_, res) => {
  console.log('wild-route-hit')
  res.status(403).json({
    error: ERRORS.METHOD_NOT_ALLOWED,
  })
})

const io = new SocketServer(server, {
  cors: { origin: '*' },
})

const pirateIds = new Map()
const userSockets = new Map()
const onlineUsers = new Map()
const pairsMap = new Map()
const rPairsMap = new Map()

io.on('connection', (socket) => {
  console.log('user connected:', socket.id)

  socket.on('init', ({ pirateId }) => {
    const prevSocket = userSockets.get(pirateId)
    if (prevSocket) {
      prevSocket.disconnect(true)
      pirateIds.delete(prevSocket.id)
    }
    pirateIds.set(socket.id, pirateId)
    userSockets.set(pirateId, socket)
    onlineUsers.set(pirateId, { status: USER_STATUS.ONLINE })
    const otherPirateId = rPairsMap.get(pirateId)
    if (otherPirateId) {
      const otherSocket = userSockets.get(otherPirateId)
      otherSocket?.emit('user-online-response', { isOnline: true })
    }
  })

  socket.on('enter-chat', ({ otherPirateId }) => {
    const pirateId = pirateIds.get(socket.id)
    if (pirateId) {
      pairsMap.set(pirateId, otherPirateId)
      rPairsMap.set(otherPirateId, pirateId)
    }
    const userInfo = onlineUsers.get(otherPirateId)
    const isOnline = userInfo?.status === USER_STATUS.ONLINE
    socket.emit('user-online-response', { isOnline })
  })

  socket.on('exit-chat', () => {
    const pirateId = pirateIds.get(socket.id)
    if (pirateId) {
      const otherPirateId = pairsMap.get(pirateId)
      if (otherPirateId) {
        pairsMap.delete(pirateId)
        rPairsMap.delete(otherPirateId)
      }
    }
  })

  const typingChanged = (isTyping) => {
    const pirateId = pirateIds.get(socket.id)
    if (!pirateId) {
      return
    }
    const otherPirateId = pairsMap.get(pirateId)
    if (!otherPirateId) {
      return
    }
    const otherSocket = userSockets.get(otherPirateId)
    otherSocket?.emit('typing-changed', { otherPirateId: pirateId, isTyping })
  }

  socket.on('started-typing', () => {
    typingChanged(true)
  })

  socket.on('stopped-typing', () => {
    typingChanged(false)
  })

  socket.on('disconnect', () => {
    const pirateId = pirateIds.get(socket.id)
    if (pirateId) {
      onlineUsers.delete(pirateId)
      userSockets.delete(pirateId)
    }
    pirateIds.delete(socket.id)
    console.log('user disconnected:', socket.id)
  })
})

mongoose
  .connect(MONGO_DB_URL)
  .then(() => {
    console.log('DB Connected')
    server.listen(PORT, () => console.log(`visit http://${HOST}:${PORT}`))
  })
  .catch((e) => console.log('db NOT connected', e.message))
