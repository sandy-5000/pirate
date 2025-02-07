import mongoose from 'mongoose'

const userSchema = new mongoose.Schema({
  first_name: {
    type: String,
    required: true,
    validate: {
      validator: function (v) {
        return 0 < v.length && v.length < 256
      },
    },
  },
  last_name: {
    type: String,
    required: true,
    validate: {
      validator: function (v) {
        return v.length < 256
      },
    },
  },
  username: {
    type: String,
    required: true,
    validate: {
      validator: function (v) {
        return /^[a-z0-9-_.]{4,64}$/.test(v)
      },
    },
  },
  email: {
    type: String,
    required: true,
    validate: {
      validator: function (v) {
        return /^[a-zA-Z0-9\.]{1,64}@[a-zA-Z0-9]{2,255}.com$/.test(v)
      },
    },
  },
  passwd: {
    type: String,
    required: true,
  },
  created_on: {
    type: Date,
    default: new Date(),
  },
})

userSchema.index({ username: 1 }, { unique: true })
userSchema.index({ email: 1 }, { unique: true })

export default mongoose.model('user', userSchema)
