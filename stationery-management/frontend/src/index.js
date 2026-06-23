import React from 'react';
import ReactDOM from 'react-dom/client';
import App from './App';
import './index.css';

const root = ReactDOM.createRoot(document.getElementById('root'));  //it locates the placeholder HTML tag <div id="root"></div> in browser's defaukt template and initializes it as the base container 
root.render(    //tells react to draw the <App/>> component inside that root container
  //developer only wrapper, runs extra checks to catch potential programming bugs.
  <React.StrictMode>   
    <App />
  </React.StrictMode>
);