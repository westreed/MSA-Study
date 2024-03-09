import {Cookies} from 'react-cookie';

const cookies = new Cookies();

export const setCookie = (name, value, options=null) => {
  /** options = path, expires, secure, httpOnly
   * path => 쿠키의 값을 저장하는 서버경로 default: "/", 쿠키가 접근할 수 있는 경로를 의미한다.
   * expires => 만료시간, Date 객체를 받는다.
   * secure => https로 통신할 때만 쿠키가 저장된다.
   * httpOnly => js 같은 비정상적인 접근을 막는다.
   */
 	return cookies.set(name, value, {...options});
}

export const getCookie = (name) => {
 return cookies.get(name);
}