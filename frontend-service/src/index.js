import React from 'react';
import ReactDOM from 'react-dom/client';
import reportWebVitals from './reportWebVitals';

import { RecoilRoot } from 'recoil';
import { CookiesProvider } from 'react-cookie';
import App from './App';

function Index() {
  return (
    <RecoilRoot>
      <CookiesProvider>
        <App/>
      </CookiesProvider>
    </RecoilRoot>
  );
}

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(<Index/>);

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
reportWebVitals();
