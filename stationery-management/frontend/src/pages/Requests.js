import React, { useEffect, useState } from 'react';
import Navbar from '../components/Navbar';
import { getMyRequests } from '../api/requestApi';

const statusColors = {
  PENDING: 'badge-pending',
  APPROVED: 'badge-approved',
  REJECTED: 'badge-rejected',
  FULFILLED: 'badge-fulfilled',
};

const Requests = () => {
  const [requests, setRequests] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [filter, setFilter] = useState('');
  const [sortBy, setSortBy] = useState('date'); // New state for sorting requests

  useEffect(() => {
    fetchRequests();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [filter]);

  const fetchRequests = async () => {
    setLoading(true);
    try {
      const response = await getMyRequests(filter || undefined);
      setRequests(response.data || []);
    } catch (err) {
      setError('Failed to load your requests.');
    } finally {
      setLoading(false);
    }
  };

  // Perform sorting on the requests array before rendering
  const getSortedRequests = () => {
    return [...requests].sort((a, b) => {
      if (sortBy === 'date') {
        return new Date(b.createdAt) - new Date(a.createdAt); // newest first
      }
      if (sortBy === 'status') {
        return a.status.localeCompare(b.status);
      }
      if (sortBy === 'itemName') {
        return a.itemName.localeCompare(b.itemName);
      }
      return 0;
    });
  };

  const displayedRequests = getSortedRequests();

  return (
    <div>
      <Navbar />
      <div className="page-container">
        <div className="page-header" style={{ display: 'flex', flexWrap: 'wrap', gap: '15px', justifyContent: 'space-between' }}>
          <h1>My Requests</h1>
          
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
              </select>
            </div>
          </div>
        </div>

        {error && <div className="error-message">{error}</div>}

        {loading ? (
          <p>Loading requests...</p>
        ) : displayedRequests.length === 0 ? (
          <p>No requests found.</p>
        ) : (
          <table className="data-table">
            <thead>
              <tr>
                <th>Request ID</th>
                <th>Item</th>
                <th>Quantity</th>
                <th>Status</th>
                <th>Remarks</th>
                <th>Rejection Reason</th>
                <th>Submitted On</th>
              </tr>
            </thead>
            <tbody>
              {displayedRequests.map((req) => (
                <tr key={req.id}>
                  <td>{req.requestId}</td>
                  <td>{req.itemName}</td>
                  <td>{req.requestedQuantity}</td>
                  <td>
                    <span className={`badge ${statusColors[req.status]}`}>
                      {req.status}
                    </span>
                  </td>
                  <td>{req.remarks || '-'}</td>
                  <td>{req.rejectionReason || '-'}</td>
                  <td>{new Date(req.createdAt).toLocaleString()}</td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </div>
  );
};

export default Requests;