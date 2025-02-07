import {
  Injectable,
  NotFoundException,
  ConflictException,
} from '@nestjs/common'
import { compare, hash } from 'bcrypt'
import { JwtService } from '@nestjs/jwt'

@Injectable()
export class AuthService {
  private users = [
    {
      username: 'sandyblaze',
      passwd: '$2b$12$j9tmf0nsClLt2lswD7hHi.sUdqOmCV/O4SFQX6LFjcbkfECVYPSCm',
    },
  ]

  constructor(private jwtService: JwtService) {}

  async login(username: string, passwd: string): Promise<object> {
    const user = this.users.find((user) => user.username === username)
    if (!user) {
      throw new NotFoundException('User not found or incorrect credentials')
    }
    const isPasswordValid = await compare(passwd, user.passwd)
    if (!isPasswordValid) {
      throw new NotFoundException('User not found or incorrect credentials')
    }
    const payload = { sub: user.username, username: user.username }
    const access_token = await this.jwtService.signAsync(payload)
    return { message: 'Login successful', user: { ...user, access_token } }
  }

  async register(username: string, passwd: string): Promise<object> {
    const existingUser = this.users.find((user) => user.username === username)
    if (existingUser) {
      throw new ConflictException('User already exists')
    }
    const hashedPasswd: string = await hash(passwd, 12)
    const newUser = { username, passwd: hashedPasswd }
    this.users.push(newUser)
    return { message: 'User registered successfully', user: newUser }
  }
}
