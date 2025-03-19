import mongoose from 'mongoose'

const friendsSchema = new mongoose.Schema({
  min_id: {
    type: mongoose.Schema.Types.ObjectId,
    required: [true, 'min_id is a required field'],
  },
  max_id: {
    type: mongoose.Schema.Types.ObjectId,
    required: [true, 'max_id is a required field'],
  },
  block_type: {
    type: Number,
    default: 0,
    enum: [-1, 0, 1],
  },
})

friendsSchema.index({ min_id: 1, max_id: 1 }, { unique: true })

export default mongoose.model('friends', friendsSchema)
