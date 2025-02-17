import mongoose from 'mongoose'

const pushTokenSchema = new mongoose.Schema({
  user_id: {
    type: mongoose.Schema.Types.ObjectId,
    required: [true, 'user_id is a required field'],
  },
  token: {
    type: String,
    required: [true, 'token is a required field'],
  },
})

pushTokenSchema.index({ user_id: 1 }, { unique: true })

export default mongoose.model('push_token', pushTokenSchema)
