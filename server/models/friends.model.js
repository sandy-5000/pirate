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
})

friendsSchema.index({ min_id: 1, max_id: 1 }, { unique: true })

export default mongoose.model('friends', friendsSchema)
