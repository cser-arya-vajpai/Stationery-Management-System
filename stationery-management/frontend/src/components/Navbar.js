import React from 'react';
import { useNavigate, Link, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const Navbar = () => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  if (!user) return null;

  const isActive = (path) => location.pathname === path;

  return (
    <nav className="navbar">
      <div>
        <div className="navbar-brand">📋 Stationery Management</div>

        <div className="navbar-links">
          {user.role === 'STUDENT' && (
            <>
              <Link to="/student/dashboard" className={isActive('/student/dashboard') ? 'active' : ''}>
                📊 Dashboard
              </Link>
              <Link to="/catalog" className={isActive('/catalog') ? 'active' : ''}>
                🛍️ Request Items
              </Link>
              <Link to="/my-requests" className={isActive('/my-requests') ? 'active' : ''}>
                📋 My Requests
              </Link>
            </>
          )}

          {user.role === 'ADMIN' && (
            <>
              <Link to="/admin/dashboard" className={isActive('/admin/dashboard') ? 'active' : ''}>
                📊 Dashboard
              </Link>
              <Link to="/admin/inventory" className={isActive('/admin/inventory') ? 'active' : ''}>
                📦 Inventory
              </Link>
              <Link to="/admin/requests" className={isActive('/admin/requests') ? 'active' : ''}>
                📋 Requests
              </Link>
              <Link to="/admin/audit-logs" className={isActive('/admin/audit-logs') ? 'active' : ''}>
                ⏱️ Audit Logs
              </Link>
            </>
          )}
        </div>
      </div>

      <div className="navbar-user">
        <span>
          {user.email}
          <br />
          <small style={{ fontSize: '10px', background: '#ffffff', color: '#db2777', padding: '1px 5px', borderRadius: '4px', marginTop: '4px', display: 'inline-block', fontWeight: 'bold' }}>
            {user.role}
          </small>
        </span>
        <button onClick={handleLogout} className="btn-logout">
          Logout
        </button>
      </div>
    </nav>
  );
};

export default Navbar;