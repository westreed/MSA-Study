import axios from "axios";
import { useNavigate } from "react-router-dom";
import { authAtom } from "../store/authAtom";
import { useRecoilState } from "recoil";
import { useEffect } from "react";
import { getCookie } from "../utils/useCookie";

const REFRESH_URL = '/user/reissue';


export const AXIOS_INSTANCE = axios.create({
  baseURL: `${process.env.REACT_APP_API_ENDPOINT}`,
  withCredentials: true
});

export const AuthTokenInterceptor = ({children}) => {
  const navigate = useNavigate();

  // 유저의 로그인 여부를 관리하기 위한 전역상태변수
  const [authDto, setAuthDto] = useRecoilState(authAtom);

  useEffect(() => {
    requestAuthTokenInjector();
    requestRejectHandler();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [])

  const requestAuthTokenInjector = () => {
    AXIOS_INSTANCE.interceptors.request.use((requestConfig) => {
      if(!requestConfig.headers) return requestConfig;

      const token = getCookie("access_token");
      // if(!token){
      //   const error = new Error('다시 로그인을 해주세요.');
      //   resetUserData();
      //   return Promise.reject(error);
      // }
      if(token){
        requestConfig.headers['Authorization'] = "Bearer " + token;
      }
      return requestConfig;
    });
  }

  const requestRejectHandler = () => {
    AXIOS_INSTANCE.interceptors.response.use(
      (res) => res,
      async(err) => {
        // Network Error
        if(!err.response?.status) return Promise.reject(err);

        const {config, response: {status}} = err;

        if(config.url === REFRESH_URL){
          alert("다시 로그인을 해주세요.");
          // toast.info("다시 로그인을 해주세요.");
          // resetUserData();
          return Promise.reject(err);
        }

        if(status === 401){
          // await jwt_refresh_api()
          // return API_INSTANCE_TO_INJECT_TOKEN(config);
        }

        return Promise.reject(err);
      }
    );
  }

  return children;
}