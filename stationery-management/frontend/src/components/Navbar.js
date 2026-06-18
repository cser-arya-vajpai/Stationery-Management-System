import React from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const Navbar = () => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  if (!user) return null;

  return (
    <nav className="navbar">
      <div className="navbar-brand">📋 Stationery Management</div>

      <div className="navbar-links">
        {user.role === 'STUDENT' && (
          <>
            <Link to="/catalog">Catalog</Link>
            <Link to="/my-requests">My Requests</Link>
          </>
        )}

        {user.role === 'ADMIN' && (
          <>
            <Link to="/admin/dashboard">Dashboard</Link>
            <Link to="/admin/inventory">Manage Inventory</Link>
          </>
        )}
      </div>

      <div className="navbar-user">
        <span>{user.name} ({user.role})</span>
        <button onClick={handleLogout} className="btn-logout">Logout</button>
      </div>
    </nav>
  );
};

export default Navbar;