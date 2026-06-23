import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import Navbar from '../components/Navbar';
import { getMyRequests } from '../api/requestApi';
import { useAuth } from '../context/AuthContext';

const statusColors = {
  PENDING: 'badge-pending',
  APPROVED: 'badge-approved',
  REJECTED: 'badge-rejected',
  FULFILLED: 'badge-fulfilled',
};

const StudentDashboard = () => {
  const { user } = useAuth();
  const [requests, setRequests] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  
  const [stats, setStats] = useState({
    total: 0,
    pending: 0,
    approved: 0,
    rejected: 0,
  });

  useEffect(() => {
    fetchDashboardData();
  }, []);

  const fetchDashboardData = async () => {
    setLoading(true);
    setError('');
    try {
      const response = await getMyRequests();
      const allRequests = response.data || [];
      setRequests(allRequests);

      // Compute statistics
      const total = allRequests.length;
      const pending = allRequests.filter(r => r.status === 'PENDING').length;
      const approved = allRequests.filter(r => r.status === 'APPROVED' || r.status === 'FULFILLED').length;
      const rejected = allRequests.filter(r => r.status === 'REJECTED').length;

      setStats({ total, pending, approved, rejected });
    } catch (err) {
      setError('Failed to load dashboard data.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div>
      <Navbar />
      <div className="page-container">
        
        {/* Welcome Banner */}
        <div className="welcome-banner">
          <h1>Hello, {user.name.split(' ')[0]}! 👋</h1>
          <p>Welcome back! Let's keep everything in place and make studying even more fun! 💖</p>
        </div>

        {error && <div className="error-message">{error}</div>}

        {loading ? (
          <p>Loading dashboard data...</p>
        ) : (
          <>
            {/* Overview Stats Cards */}
            <div className="stats-grid" style={{ marginBottom: '35px' }}>
              <div className="stat-card">
                <h3>🎒 Total Requests</h3>
                <p className="stat-number">{stats.total}</p>
                <Link to="/my-requests" style={{ color: '#db2777', textDecoration: 'none', fontSize: '13px', fontWeight: 'bold' }}>
                  View all
                </Link>
              </div>
              <div className="stat-card">
                <h3>🕒 Pending</h3>
                <p className="stat-number">{stats.pending}</p>
                <Link to="/my-requests?status=PENDING" style={{ color: '#db2777', textDecoration: 'none', fontSize: '13px', fontWeight: 'bold' }}>
                  View
                </Link>
              </div>
              <div className="stat-card">
                <h3>💖 Approved</h3>
                <p className="stat-number">{stats.approved}</p>
                <Link to="/my-requests?status=APPROVED" style={{ color: '#db2777', textDecoration: 'none', fontSize: '13px', fontWeight: 'bold' }}>
                  View
                </Link>
              </div>
              <div className="stat-card">
                <h3>❌ Rejected</h3>
                <p className="stat-number">{stats.rejected}</p>
                <Link to="/my-requests?status=REJECTED" style={{ color: '#db2777', textDecoration: 'none', fontSize: '13px', fontWeight: 'bold' }}>
                  View
                </Link>
              </div>
            </div>

            {/* Bottom Panels Layout */}
            <div className="dashboard-panels">
              
              {/* Panel 1: Recent Requests */}
              <div className="dashboard-panel">
                <div className="panel-header">
                  <h3>Recent Requests</h3>
                  <Link to="/my-requests" className="view-all-link">
                    View All Requests →
                  </Link>
                </div>
                {requests.length === 0 ? (
                  <div className="empty-state">
                    <span className="empty-state-icon">📋</span>
                    <p style={{ fontWeight: '600', color: 'var(--text-primary)' }}>No requests submitted yet.</p>
                    <Link to="/catalog" className="btn-primary" style={{ marginTop: '15px', textDecoration: 'none' }}>
                      Request Stationery
                    </Link>
                  </div>
                ) : (
                  <div style={{ overflowX: 'auto' }}>
                    <table className="data-table" style={{ margin: 0, boxShadow: 'none', border: 'none' }}>
                      <thead>
                        <tr>
                          <th>Items Requested</th>
                          <th>Status</th>
                          <th>Requested On</th>
                        </tr>
                      </thead>
                      <tbody>
                        {requests.slice(0, 4).map((req) => (
                          <tr key={req.id}>
                            <td>
                              <ul style={{ margin: 0, paddingLeft: '15px', listStyleType: 'disc' }}>
                                {req.items && req.items.map((item, idx) => (
                                  <li key={idx} style={{ fontSize: '12px', color: '#4b5563' }}>
                                    <strong>{item.itemName}</strong> (Qty: {item.requestedQuantity})
                                  </li>
                                ))}
                              </ul>
                            </td>
                            <td>
                              <span className={`badge ${statusColors[req.status]}`} style={{ fontSize: '11px' }}>
                                {req.status}
                              </span>
                            </td>
                            <td style={{ fontSize: '12px' }}>{new Date(req.createdAt).toLocaleDateString()}</td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  </div>
                )}
              </div>

              {/* Panel 2: Quick Actions & Announcements */}
              <div style={{ display: 'flex', flexDirection: 'column', gap: '24px' }}>
                
                {/* Quick Actions Card */}
                <div className="dashboard-panel" style={{ padding: '24px' }}>
                  <div className="panel-header" style={{ marginBottom: '16px' }}>
                    <h3 style={{ fontSize: '16px' }}>🎀 Quick Actions</h3>
                  </div>
                  <div style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
                    <Link to="/catalog" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', background: '#fff0f3', padding: '14px 20px', borderRadius: '12px', textDecoration: 'none', color: '#db2777', fontWeight: 'bold', border: '1px solid rgba(219,39,119,0.1)' }}>
                      <span>🛍️ Request New Item</span>
                      <span>→</span>
                    </Link>
                    <Link to="/my-requests" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', background: '#fff0f3', padding: '14px 20px', borderRadius: '12px', textDecoration: 'none', color: '#db2777', fontWeight: 'bold', border: '1px solid rgba(219,39,119,0.1)' }}>
                      <span>📋 View My Requests</span>
                      <span>→</span>
                    </Link>
                  </div>
                </div>

                {/* Announcements Card */}
                <div className="dashboard-panel" style={{ padding: '24px', flex: 1 }}>
                  <div className="panel-header" style={{ marginBottom: '16px' }}>
                    <h3 style={{ fontSize: '16px' }}>📢 Announcements</h3>
                  </div>
                  <div style={{ display: 'flex', flexDirection: 'column', gap: '15px' }}>
                    <div style={{ borderBottom: '1px solid rgba(219,39,119,0.06)', paddingBottom: '10px' }}>
                      <h4 style={{ fontSize: '13px', color: '#db2777', marginBottom: '4px' }}>New Stationery Items Added! <span style={{ fontSize: '9px', background: '#e11d48', color: '#fff', padding: '1px 5px', borderRadius: '4px', textTransform: 'uppercase' }}>New</span></h4>
                      <p style={{ fontSize: '12px', color: '#7c4c58' }}>Check out the new spiral notebooks and premium blue gel pens available now in the catalog.</p>
                      <small style={{ fontSize: '10px', color: '#8c606b' }}>20 May 2026</small>
                    </div>
                    <div>
                      <h4 style={{ fontSize: '13px', color: '#db2777', marginBottom: '4px' }}>Inventory Threshold Alerts</h4>
                      <p style={{ fontSize: '12px', color: '#7c4c58' }}>Some items are running low. Please submit your bulk requests early to ensure availability.</p>
                      <small style={{ fontSize: '10px', color: '#8c606b' }}>18 May 2026</small>
                    </div>
                  </div>
                </div>

              </div>

            </div>

            {/* Custom wavy styled footer */}
            <div className="wavy-footer">
                © 2026 Stationery Management System. All rights reserved.
            </div>
          </>
        )}
      </div>
    </div>
  );
};

export default StudentDashboard;