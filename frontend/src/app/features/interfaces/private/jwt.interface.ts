export interface JwtPayload {
  sub: string;
  role: string;
  iss: string;
  exp: number;
  iat: number;
}
