import React, { useEffect, useState } from 'react';
import Navbar from '../components/Navbar';
import { getAllItems, getLowStockItems } from '../api/inventoryApi';
import { getAllRequests, updateRequestStatus } from '../api/requestApi';

const statusColors = {
  PENDING: 'badge-pending',
  APPROVED: 'badge-approved',
  REJECTED: 'badge-rejected',
  FULFILLED: 'badge-fulfilled',
};

const Dashboard = () => {
  const [totalItems, setTotalItems] = useState(0);
  const [lowStockCount, setLowStockCount] = useState(0);
  const [requests, setRequests] = useState([]);
  const [filter, setFilter] = useState('PENDING');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const [actionTarget, setActionTarget] = useState(null);
  const [rejectionReason, setRejectionReason] = useState('');

  useEffect(() => {
    fetchDashboardData();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [filter]);

  const fetchDashboardData = async () => {
    setLoading(true);
    setError('');
    try {
      const [itemsRes, lowStockRes, requestsRes] = await Promise.all([
        getAllItems(0, 1),
        getLowStockItems(),
        getAllRequests(filter || undefined),
      ]);
      setTotalItems(itemsRes.data.totalElements ?? 0);
      setLowStockCount(lowStockRes.data.length);
      setRequests(requestsRes.data || []);
    } catch (err) {
      setError('Failed to load dashboard data.');
    } finally {
      setLoading(false);
    }
  };

  const handleApprove = async (request) => {
    try {
      await updateRequestStatus(request.id, { status: 'APPROVED' });
      fetchDashboardData();
    } catch (err) {
      setError(err.response?.data?.error || 'Failed to approve request.');
    }
  };

  const openRejectModal = (request) => {
    setActionTarget(request);
    setRejectionReason('');
  };

  const closeRejectModal = () => setActionTarget(null);

  const handleReject = async (e) => {
    e.preventDefault();
    try {
      await updateRequestStatus(actionTarget.id, {
        status: 'REJECTED',
        rejectionReason,
      });
      closeRejectModal();
      fetchDashboardData();
    } catch (err) {
      setError(err.response?.data?.error || 'Failed to reject request.');
    }
  };

  const handleFulfill = async (request) => {
    try {
      await updateRequestStatus(request.id, { status: 'FULFILLED' });
      fetchDashboardData();
    } catch (err) {
      setError(err.response?.data?.error || 'Failed to mark as fulfilled.');
    }
  };

  return (
    <div>
      <Navbar />
      <div className="page-container">
        <h1>Admin Dashboard</h1>

        {error && <div className="error-message">{error}</div>}

        <div className="stats-grid">
          <div className="stat-card">
            <h3>Total Items</h3>
            <p className="stat-number">{totalItems}</p>
          </div>
          <div className="stat-card stat-card-warning">
            <h3>Low Stock Items</h3>
            <p className="stat-number">{lowStockCount}</p>
          </div>
        </div>

        <h2>Student Requests</h2>
        <div className="filter-bar">
          <label>Filter by status: </label>
          <select value={filter} onChange={(e) => setFilter(e.target.value)}>
            <option value="">All</option>
            <option value="PENDING">Pending</option>
            <option value="APPROVED">Approved</option>
            <option value="REJECTED">Rejected</option>
            <option value="FULFILLED">Fulfilled</option>
          </select>
        </div>

        {loading ? (
          <p>Loading requests...</p>
        ) : requests.length === 0 ? (
          <p>No requests found.</p>
        ) : (
          <table className="data-table">
            <thead>
              <tr>
                <th>Request ID</th>
                <th>Student</th>
                <th>Item</th>
                <th>Quantity</th>
                <th>Status</th>
                <th>Submitted On</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {requests.map((req) => (
                <tr key={req.id}>
                  <td>{req.requestId}</td>
                  <td>{req.studentEmail}</td>
                  <td>{req.itemName}</td>
                  <td>{req.requestedQuantity}</td>
                  <td>
                    <span className={`badge ${statusColors[req.status]}`}>
                      {req.status}
                    </span>
                  </td>
                  <td>{new Date(req.createdAt).toLocaleString()}</td>
                  <td>
                    {req.status === 'PENDING' && (
                      <>
                        <button className="btn-small btn-approve" onClick={() => handleApprove(req)}>
                          Approve
                        </button>
                        <button className="btn-small btn-reject" onClick={() => openRejectModal(req)}>
                          Reject
                        </button>
                      </>
                    )}
                    {req.status === 'APPROVED' && (
                      <button className="btn-small btn-approve" onClick={() => handleFulfill(req)}>
                        Mark Fulfilled
                      </button>
                    )}
                    {(req.status === 'REJECTED' || req.status === 'FULFILLED') && '-'}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}

        {actionTarget && (
          <div className="modal-overlay" onClick={closeRejectModal}>
            <div className="modal-content" onClick={(e) => e.stopPropagation()}>
              <h2>Reject Request: {actionTarget.requestId}</h2>
              <form onSubmit={handleReject}>
                <div className="form-group">
                  <label>Rejection Reason</label>
                  <textarea
                    value={rejectionReason}
                    onChange={(e) => setRejectionReason(e.target.value)}
                    required
                    placeholder="Explain why this request is being rejected..."
                  />
                </div>
                <div className="modal-actions">
                  <button type="button" className="btn-secondary" onClick={closeRejectModal}>
                    Cancel
                  </button>
                  <button type="submit" className="btn-primary">Confirm Reject</button>
                </div>
              </form>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default Dashboard;