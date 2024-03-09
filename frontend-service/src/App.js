import { BrowserRouter, Route, Routes } from 'react-router-dom';
import PrivateRoute from './routes/PrivateRoute';
import PublicRoute from './routes/PublicRoute';
import AnyRoute from './routes/AnyRoute';

import "./styles/index.css";

import Header from './components/Header';
import Footer from './components/Footer';
import LoginPage from './pages/LoginPage';
import HomePage from './pages/HomePage';
import AdminPage from './pages/AdminPage';
import GuestPage from './pages/GuestPage';
import { AuthTokenInterceptor } from './api/Axios';

function App() {
  return (
    <div className={`wrapper`}>
      <div className={`contentWrapper`}>
        <BrowserRouter basename={process.env.PUBLIC_URL}>
          <AuthTokenInterceptor>
          <Header/>
          <Routes>
            <Route element={<AnyRoute/>}>
              <Route path="/" element={<HomePage/>} />
            </Route>
            <Route element={<PublicRoute/>}>
              <Route path="/login" element={<LoginPage/>} />
            </Route>
            <Route element={<PrivateRoute role={"Admin"} />}>
              <Route path="/admin" element={<AdminPage/>} />
            </Route>
            <Route element={<PrivateRoute role={"Guest"} />}>
              <Route path="/guest" element={<GuestPage/>} />
            </Route>
          </Routes>
          </AuthTokenInterceptor>
        </BrowserRouter>
      </div>
      <Footer/>
    </div>
  );
}

export default App;
