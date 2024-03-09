import React from "react";
import { Navigate, Outlet } from "react-router-dom";
import { useRecoilState } from "recoil";
import { authAtom } from "../store/authAtom";


const PublicRoute = () => {
  const [authDto, ] = useRecoilState(authAtom);

  return !authDto.isLogin ? <Outlet /> : <Navigate to="/"/>;
};

export default PublicRoute;