import React from "react";
import { Navigate, Outlet } from "react-router-dom";
import { useRecoilState } from "recoil";
import { authAtom } from "../store/authAtom";


const PrivateRoute = ({role}) => {
  const [authDto, ] = useRecoilState(authAtom);

  if (!authDto.isLogin) {
    alert("로그인이 필요한 서비스입니다.");
    return <Navigate to="/login" />;
  }

  if(authDto.role.toLowerCase() !== role.toLowerCase()) {
    alert("해당 서비스를 이용하실 수 없습니다.");
    return <Navigate to="/" />;
  }

  return <Outlet />;
};

export default PrivateRoute;