import mongoose from 'mongoose'

const pendingRequestsSchema = new mongoose.Schema({
  sender_id: {
    type: mongoose.Schema.Types.ObjectId,
    required: [true, 'sender_id is a required field'],
  },
  receiver_id: {
    type: mongoose.Schema.Types.ObjectId,
    required: [true, 'receiver_id is a required field'],
  },
})

pendingRequestsSchema.index({ sender_id: 1, receiver_id: 1 }, { unique: true })
pendingRequestsSchema.index({ receiver_id: 1, sender_id: 1 }, { unique: true })

export default mongoose.model('pending_requests', pendingRequestsSchema)
