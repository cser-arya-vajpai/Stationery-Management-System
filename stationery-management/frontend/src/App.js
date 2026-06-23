import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext';
import PrivateRoute from './components/PrivateRoute';

import Login from './pages/Login';
import Register from './pages/Register';
import Inventory from './pages/Inventory';
import Requests from './pages/Requests';
import Dashboard from './pages/Dashboard';
import AdminInventory from './pages/AdminInventory';
import AdminRequests from './pages/AdminRequests';
import AuditLogs from './pages/AuditLogs';
import StudentDashboard from './pages/StudentDashboard';

//decides where a user should be sent:
const HomeRedirect = () => {    //creates a component named HomeRediret
  //Get the current logged-in user's information from the authentication system.
  const { user } = useAuth();   //reaches into shared storage box, extracts current logged-in user data. useAuth is a custom react hook.
  if (!user) return <Navigate to="/login" replace />; //if user = null then redirect to login page
  if (user.role === 'ADMIN') return <Navigate to="/admin/dashboard" replace />;   //if role = admin, redirect to admin page
  return <Navigate to="/student/dashboard" replace />;  //otherwise return to student dashboard 
};

function App() {     //main react component
  return (           //we will return JSX here
    <AuthProvider>   
      <BrowserRouter>
        <Routes>
          <Route path="/" element={<HomeRedirect />} />
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />

          {/* Student Routes */}
          <Route
            path="/student/dashboard"
            element={
              <PrivateRoute allowedRole="STUDENT">
                <StudentDashboard />
              </PrivateRoute>
            }
          />
          <Route
            path="/catalog"
            element={
              <PrivateRoute allowedRole="STUDENT">
                <Inventory />
              </PrivateRoute>
            }
          />
          <Route
            path="/my-requests"
            element={
              <PrivateRoute allowedRole="STUDENT">
                <Requests />
              </PrivateRoute>
            }
          />

          {/* Admin Routes */}
          <Route
            path="/admin/dashboard"
            element={
              <PrivateRoute allowedRole="ADMIN">
                <Dashboard />
              </PrivateRoute>
            }
          />
          <Route
            path="/admin/inventory"
            element={
              <PrivateRoute allowedRole="ADMIN">
                <AdminInventory />
              </PrivateRoute>
            }
          />
          <Route
            path="/admin/requests"
            element={
              <PrivateRoute allowedRole="ADMIN">
                <AdminRequests />
              </PrivateRoute>
            }
          />
          <Route
            path="/admin/audit-logs"
            element={
              <PrivateRoute allowedRole="ADMIN">
                <AuditLogs />
              </PrivateRoute>
            }
          />

          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
}

export default App;