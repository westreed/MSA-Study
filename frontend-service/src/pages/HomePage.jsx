import React from "react";
import { Link } from "react-router-dom";
import { user_user_api } from "../api/User";

function UserAPIOnClick(e) {
  e.preventDefault();
  user_user_api()
  .then(res => {
    console.log(res);
  })
  .catch(err => {
    console.log(err);
  });
}

function HomePage() {
  return (
    <>
      <p>HomePage</p>
      <Link to="/login">Login</Link>
      <Link to="/admin">Admin</Link>
      <Link to="/guest">Guest</Link>
      <button onClick={(e) => {UserAPIOnClick(e)}}>UserAPI</button>
    </>
  );
}

export default HomePage;