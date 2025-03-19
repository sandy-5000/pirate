import mongoose from 'mongoose'

const userSchema = new mongoose.Schema({
  name: {
    type: String,
    default: '',
    maxlength: 255,
  },
  username: {
    type: String,
    required: [true, 'username is a required field'],
    validate: {
      validator: function (v) {
        return /^[a-z0-9-_.]{4,64}$/.test(v)
      },
      message: (props) => `${props.value} is not a valid username!`,
    },
  },
  public_key: {
    type: String,
    default: '',
  },
  email: {
    type: String,
    required: [true, 'email is a required field'],
    validate: {
      validator: function (v) {
        return /^[a-zA-Z0-9.]{1,64}@[a-zA-Z0-9]{2,255}.com$/.test(v)
      },
      message: (props) => `${props.value} is not a valid email!`,
    },
  },
  passwd: {
    type: String,
    required: [true, 'password is required field'],
  },
  bio: {
    type: String,
    default: '',
    maxlength: 255,
  },
  profile_image: {
    type: Number,
    default: 5,
  },
  created_on: {
    type: Date,
    default: new Date(),
  },
  friends: [
    {
      type: mongoose.Schema.Types.ObjectId,
      ref: 'user',
    },
  ],
})

userSchema.index({ username: 1 }, { unique: true })
userSchema.index({ email: 1 }, { unique: true })

export default mongoose.model('user', userSchema)
