import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import Navbar from '../components/Navbar';
import { getAllItems, getLowStockItems } from '../api/inventoryApi';
import { getAllRequests } from '../api/requestApi';

const Dashboard = () => {
  const [totalItems, setTotalItems] = useState(0);
  const [lowStockCount, setLowStockCount] = useState(0);
  const [lowStockList, setLowStockList] = useState([]);
  const [pendingRequests, setPendingRequests] = useState([]);
  const [pendingCount, setPendingCount] = useState(0);
  const [approvedCount, setApprovedCount] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    fetchDashboardData();
  }, []);

  const fetchDashboardData = async () => {
    setLoading(true);
    setError('');
    try {
      const [itemsRes, lowStockRes, requestsRes] = await Promise.all([
        getAllItems(0, 100),
        getLowStockItems(),
        getAllRequests(),
      ]);

      setTotalItems(itemsRes.data.totalElements ?? 0);
      
      const lowStockData = lowStockRes.data || [];
      setLowStockCount(lowStockData.length);
      setLowStockList(lowStockData);

      const allRequests = requestsRes.data || [];
      
      // Filter out pending requests for the display panel
      const pending = allRequests.filter((r) => r.status === 'PENDING');
      setPendingRequests(pending);
      setPendingCount(pending.length);
      
      // Calculate approved requests count
      const approved = allRequests.filter((r) => r.status === 'APPROVED');
      setApprovedCount(approved.length);

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
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: '15px' }}>
            <div>
              <h1>Admin Dashboard</h1>
              <p>Welcome to the Stationery Management Control Center</p>
            </div>
            <button className="refresh-btn" onClick={fetchDashboardData}>
              🔄 Refresh Data
            </button>
          </div>
        </div>

        {error && <div className="error-message">{error}</div>}

        {loading ? (
          <p>Loading dashboard data...</p>
        ) : (
          <>
            {/* Overview Stats Cards */}
            <div className="stats-grid" style={{ marginBottom: '35px' }}>
              <div className="stat-card stat-card-info">
                <h3>📦 Total Items</h3>
                <p className="stat-number">{totalItems}</p>
              </div>
              <div className="stat-card stat-card-warning">
                <h3>⚠️ Low Stock Items</h3>
                <p className="stat-number">{lowStockCount}</p>
              </div>
              <div className="stat-card stat-card-pending">
                <h3>🕒 Pending Requests</h3>
                <p className="stat-number">{pendingCount}</p>
              </div>
              <div className="stat-card stat-card-success">
                <h3>✅ Approved Requests</h3>
                <p className="stat-number">{approvedCount}</p>
              </div>
            </div>

            {/* Bottom Panels Layout */}
            <div className="dashboard-panels">
              
              {/* Panel 1: Recent Pending Requests */}
              <div className="dashboard-panel">
                <div className="panel-header">
                  <h3>Recent Pending Requests</h3>
                  <Link to="/admin/requests" className="view-all-link">
                    View All →
                  </Link>
                </div>
                {pendingRequests.length === 0 ? (
                  <div className="empty-state">
                    <span className="empty-state-icon">✅</span>
                    <p style={{ fontWeight: '600', color: 'var(--text-primary)' }}>All student requests have been processed!</p>
                  </div>
                ) : (
                  <div style={{ overflowX: 'auto' }}>
                    <table className="data-table" style={{ margin: 0, boxShadow: 'none', border: 'none' }}>
                      <thead>
                        <tr>
                          <th>Item</th>
                          <th>Qty</th>
                          <th>Student</th>
                        </tr>
                      </thead>
                      <tbody>
                        {pendingRequests.slice(0, 3).map((req) => (
                          <tr key={req.id}>
                            <td style={{ fontWeight: '600' }}>{req.itemName}</td>
                            <td>{req.requestedQuantity}</td>
                            <td style={{ fontSize: '12px' }}>{req.studentEmail}</td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  </div>
                )}
              </div>

              {/* Panel 2: Low Stock Alerts */}
              <div className="dashboard-panel">
                <div className="panel-header">
                  <h3>Low Stock Alerts</h3>
                  <Link to="/admin/inventory" className="view-all-link">
                    Manage Inventory →
                  </Link>
                </div>
                {lowStockList.length === 0 ? (
                  <div className="empty-state">
                    <span className="empty-state-icon">✅</span>
                    <p style={{ fontWeight: '600', color: 'var(--text-primary)' }}>All items have sufficient stock levels.</p>
                  </div>
                ) : (
                  <div style={{ overflowX: 'auto' }}>
                    <table className="data-table" style={{ margin: 0, boxShadow: 'none', border: 'none' }}>
                      <thead>
                        <tr>
                          <th>Item Name</th>
                          <th>Stock Left</th>
                          <th>Min Threshold</th>
                        </tr>
                      </thead>
                      <tbody>
                        {lowStockList.slice(0, 3).map((item) => (
                          <tr key={item.id}>
                            <td style={{ fontWeight: '600', color: 'var(--danger)' }}>{item.name}</td>
                            <td>{item.availableQuantity} {item.unit}(s)</td>
                            <td>{item.minimumQuantity} {item.unit}(s)</td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  </div>
                )}
              </div>

            </div>
          </>
        )}
      </div>
    </div>
  );
};

export default Dashboard;