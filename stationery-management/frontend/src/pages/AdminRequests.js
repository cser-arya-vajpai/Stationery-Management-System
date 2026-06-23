import React, { useEffect, useState } from 'react';
import Navbar from '../components/Navbar';
import { getAllRequests, updateRequestStatus } from '../api/requestApi';
import { addAuditLog } from '../utils/auditLogger';
import { useAuth } from '../context/AuthContext';

const statusColors = {
  PENDING: 'badge-pending',
  APPROVED: 'badge-approved',
  REJECTED: 'badge-rejected',
  FULFILLED: 'badge-fulfilled',
};

const AdminRequests = () => {
  const { user } = useAuth();
  const [requests, setRequests] = useState([]);
  const [filter, setFilter] = useState('');
  const [sortBy, setSortBy] = useState('date');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const [actionTarget, setActionTarget] = useState(null);
  const [rejectionReason, setRejectionReason] = useState('');

  useEffect(() => {
    fetchRequests();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [filter]);

  const fetchRequests = async () => {
    setLoading(true);
    setError('');
    try {
      const requestsRes = await getAllRequests(filter || undefined);
      setRequests(requestsRes.data || []);
    } catch (err) {
      setError('Failed to load student requests.');
    } finally {
      setLoading(false);
    }
  };

  const handleApprove = async (request) => {
    try {
      await updateRequestStatus(request.id, { status: 'APPROVED' });
      const itemsDetail = request.items?.map(i => `${i.requestedQuantity} ${i.itemName}`).join(', ') || '';
      addAuditLog(user.email, user.role, 'APPROVE_REQUEST', `Approved request ID: ${request.requestId} for ${itemsDetail}`);
      fetchRequests();
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
      const itemsDetail = actionTarget.items?.map(i => `${i.requestedQuantity} ${i.itemName}`).join(', ') || '';
      addAuditLog(user.email, user.role, 'REJECT_REQUEST', `Rejected request ID: ${actionTarget.requestId} for ${itemsDetail}. Reason: ${rejectionReason}`);
      closeRejectModal();
      fetchRequests();
    } catch (err) {
      setError(err.response?.data?.error || 'Failed to reject request.');
    }
  };

  const handleFulfill = async (request) => {
    try {
      await updateRequestStatus(request.id, { status: 'FULFILLED' });
      const itemsDetail = request.items?.map(i => `${i.requestedQuantity} ${i.itemName}`).join(', ') || '';
      addAuditLog(user.email, user.role, 'FULFILL_REQUEST', `Marked request ID: ${request.requestId} for ${itemsDetail} as fulfilled`);
      fetchRequests();
    } catch (err) {
      setError(err.response?.data?.error || 'Failed to mark as fulfilled.');
    }
  };

  // Perform sorting on the requests list
  const getSortedRequests = () => {
    return [...requests].sort((a, b) => {
      if (sortBy === 'date') {
        return new Date(b.createdAt) - new Date(a.createdAt); // Newest first
      }
      if (sortBy === 'status') {
        return a.status.localeCompare(b.status);
      }
      if (sortBy === 'itemName') {
        const nameA = a.items?.[0]?.itemName || '';
        const nameB = b.items?.[0]?.itemName || '';
        return nameA.localeCompare(nameB);
      }
      if (sortBy === 'quantity') {
        const qtyA = a.items?.reduce((sum, item) => sum + item.requestedQuantity, 0) || 0;
        const qtyB = b.items?.reduce((sum, item) => sum + item.requestedQuantity, 0) || 0;
        return qtyB - qtyA; // Highest total quantity first
      }
      return 0;
    });
  };

  const displayedRequests = getSortedRequests();

  return (
    <div>
      <Navbar />
      <div className="page-container">
        <div className="page-header" style={{ display: 'flex', flexWrap: 'wrap', gap: '15px', justifyContent: 'space-between', borderBottom: '1px solid rgba(219, 39, 119, 0.15)', paddingBottom: '16px' }}>
          <h1>Student Requests Management</h1>

          <div style={{ display: 'flex', gap: '15px' }}>
            {/* Filter controls */}
            <div className="filter-bar" style={{ margin: 0 }}>
              <label htmlFor="filterStatus">Filter by status: </label>
              <select id="filterStatus" value={filter} onChange={(e) => setFilter(e.target.value)}>
                <option value="">All</option>
                <option value="PENDING">Pending</option>
                <option value="APPROVED">Approved</option>
                <option value="REJECTED">Rejected</option>
                <option value="FULFILLED">Fulfilled</option>
              </select>
            </div>

            {/* Sorting controls */}
            <div className="filter-bar" style={{ margin: 0 }}>
              <label htmlFor="sortRequests">Sort by: </label>
              <select id="sortRequests" value={sortBy} onChange={(e) => setSortBy(e.target.value)}>
                <option value="date">Date Requested</option>
                <option value="status">Status</option>
                <option value="itemName">Item Name</option>
                <option value="quantity">Quantity</option>
              </select>
            </div>
          </div>
        </div>

        {error && <div className="error-message">{error}</div>}

        {loading ? (
          <p>Loading requests...</p>
        ) : displayedRequests.length === 0 ? (
          <p>No student requests found matching filters.</p>
        ) : (
          <table className="data-table">
            <thead>
              <tr>
                <th>Request ID</th>
                <th>Student Email</th>
                <th>Requested Items</th>
                <th>Status</th>
                <th>Submitted On</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {displayedRequests.map((req) => (
                <tr key={req.id}>
                  <td>{req.requestId}</td>
                  <td>{req.studentEmail}</td>
                  <td>
                    <ul style={{ margin: 0, paddingLeft: '15px', listStyleType: 'disc' }}>
                      {req.items && req.items.map((item, idx) => (
                        <li key={idx} style={{ fontSize: '13px', color: '#4b5563' }}>
                          <strong>{item.itemName}</strong> (Qty: {item.requestedQuantity})
                        </li>
                      ))}
                    </ul>
                  </td>
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

export default AdminRequests;