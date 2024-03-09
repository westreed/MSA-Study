import { atom } from "recoil";
import { recoilPersist } from "recoil-persist";

const {persistAtom} = recoilPersist({
  key: 'auth',
  storage: sessionStorage
});

export class Auth {
  constructor(username=null, role=null, isLogin=false) {
    this.username = username;
    this.role = role;
    this.isLogin = isLogin;
  }
}

export const authAtom = atom({
  key: "authAtom",
  /** @type {Auth} */
  default: new Auth(),
  effects_UNSTABLE: [persistAtom],
});