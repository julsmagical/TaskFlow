export interface Role {
    id: String;
    name: String;
}

export interface User{
    id: String;
    username: String;
    fullname: String;
    email: String;
    password: String;
    role: Role;
    createdAt: String;
}