import React, { createContext, useContext, useState } from 'react';

const AuthContext = createContext(null); //creating an empty shared storage box named AuthContext. Means no user is logged in yet.

//Component that stores authentication details and provides it to all child components
export const AuthProvider = ({ children }) => {  
  const [user, setUser] = useState(() => {    //creates a state variable named user and  fn named setUser to update it
    const token = localStorage.getItem('token');
    const role = localStorage.getItem('role');
    const email = localStorage.getItem('email');
    const name = localStorage.getItem('name');   //retrieves token/role/email/name from local storage/browser storage
    if (token && role && email) {
      return { token, role, email, name };  //if any detail found, return to user state.
    }
    return null;
  });

  //defines a helper function login that we can call after backend verifies credentials
  const login = (authResponse) => {
    const { token, role, email, name } = authResponse;
    localStorage.setItem('token', token);  //saves login details to browser's persistent localStorage so that session survives page refreshes
    localStorage.setItem('role', role);
    localStorage.setItem('email', email);
    localStorage.setItem('name', name);
    setUser({ token, role, email, name });
  };

  //defines a helper fn logout to clear the session
  const logout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('role');
    localStorage.removeItem('email');
    localStorage.removeItem('name');
    setUser(null);
  };

  return (
    //tells react: take the variables and broadcast them to every component wrapped inside me
    <AuthContext.Provider value={{ user, login, logout }}> 
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => useContext(AuthContext);