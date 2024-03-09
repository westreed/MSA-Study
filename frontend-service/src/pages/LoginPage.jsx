import React, { useState } from "react";
import { user_login_api } from "../api/User";
import { setCookie } from "../utils/useCookie";
import { decodeJWT } from "../utils/useJWT";
import { useRecoilState } from "recoil";
import { Auth, authAtom } from "../store/authAtom";

function LoginOnClick(e, inputs, setAuthDto) {
  e.preventDefault();
  

  user_login_api({"username":inputs.username, "password":inputs.password})
    .then((res) => {
      setCookie("access_token", res.headers['authorization']);
      setCookie("refresh_token", res.headers["authorization-refresh"]);
      const payload = decodeJWT(res.headers['authorization']);
      setAuthDto(new Auth(payload.name, payload.role, true));
    })
    .catch((err) => {
      console.log(err);
      alert(err.response.data.error);
    });
}

function LoginPage() {
  const [, setAuthDto] = useRecoilState(authAtom);
  const [inputs, setInputs] = useState({
    username: "",
    password: ""
  });

  return (
    <div style={{maxWidth:"50vw"}}>
      <p>LoginPage</p>
      <p>username</p>
      <input onChange={(e) => setInputs({...inputs, "username": e.target.value})} className="username" type="text" placeholder="username" value={inputs.username} />
      <p>password</p>
      <input onChange={(e) => setInputs({...inputs, "password": e.target.value})} className="password" type="password" placeholder="password" value={inputs.password} />
      <br/>
      <button onClick={(e) => LoginOnClick(e, inputs, setAuthDto)}>로그인</button>
    </div>
  );
}

export default LoginPage;