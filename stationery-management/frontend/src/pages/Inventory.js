import React, { useEffect, useState } from 'react';
import Navbar from '../components/Navbar';
import ItemCard from '../components/ItemCard';
import { getAllItems } from '../api/inventoryApi';
import { submitRequest } from '../api/requestApi';
import { useAuth } from '../context/AuthContext';
import { addAuditLog } from '../utils/auditLogger';

const Inventory = () => {
  const { user } = useAuth();
  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  // Pagination & Sorting States
  const [currentPage, setCurrentPage] = useState(0);
  const [sortBy, setSortBy] = useState('name');
  const [totalPages, setTotalPages] = useState(0);

  const [selectedItem, setSelectedItem] = useState(null);
  const [quantity, setQuantity] = useState(1);
  const [remarks, setRemarks] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const [successMsg, setSuccessMsg] = useState('');

  useEffect(() => {
    fetchItems();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [currentPage, sortBy]);

  const fetchItems = async () => {
    setLoading(true);
    try {
      // Fetches 20 items per page with specified sorting
      const response = await getAllItems(currentPage, 20, sortBy);
      setItems(response.data.content || []);
      setTotalPages(response.data.totalPages || 0);
    } catch (err) {
      setError('Failed to load inventory items.');
    } finally {
      setLoading(false);
    }
  };

  const openRequestModal = (item) => {
    setSelectedItem(item);
    setQuantity(1);
    setRemarks('');
    setSuccessMsg('');
  };

  const closeModal = () => {
    setSelectedItem(null);
  };

  const handleSubmitRequest = async (e) => {
    e.preventDefault();
    setSubmitting(true);
    setError('');

    try {
      await submitRequest({
        itemId: selectedItem.id,
        itemName: selectedItem.name,
        requestedQuantity: Number(quantity),
        remarks,
      });

      addAuditLog(user.email, user.role, 'SUBMIT_REQUEST', `Requested ${quantity} ${selectedItem.unit}(s) of ${selectedItem.name}`);

      setSuccessMsg('Request submitted successfully!');
      setTimeout(() => {
        closeModal();
        fetchItems(); // refresh page
      }, 1200);
    } catch (err) {
      setError(err.response?.data?.error || 'Failed to submit request.');
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div>
      <Navbar />
      <div className="page-container">
        <div className="page-header" style={{ display: 'flex', flexWrap: 'wrap', gap: '15px' }}>
          <h1>Stationery Catalog</h1>
          
          {/* Sorting Control */}
          <div className="filter-bar" style={{ margin: 0, display: 'flex', alignItems: 'center' }}>
            <label htmlFor="sortBy">Sort by: </label>
            <select 
              id="sortBy"
              value={sortBy} 
              onChange={(e) => { setSortBy(e.target.value); setCurrentPage(0); }}
            >
              <option value="name">Name (A-Z)</option>
              <option value="category">Category</option>
              <option value="availableQuantity">Availability</option>
            </select>
          </div>
        </div>

        {error && !selectedItem && <div className="error-message">{error}</div>}

        {loading ? (
          <p>Loading items...</p>
        ) : items.length === 0 ? (
          <p>No stationery items found.</p>
        ) : (
          <>
            <div className="item-grid">
              {items.map((item) => (
                <ItemCard
                  key={item.id}
                  item={item}
                  actionLabel="Request Item"
                  onAction={openRequestModal}
                  disabled={item.availableQuantity === 0}
                />
              ))}
            </div>

            {/* Pagination Controls */}
            {totalPages > 1 && (
              <div className="pagination-controls">
                <button 
                  className="btn-secondary btn-pagination" 
                  disabled={currentPage === 0} 
                  onClick={() => setCurrentPage(prev => prev - 1)}
                >
                  Previous
                </button>
                <span className="pagination-info">Page {currentPage + 1} of {totalPages}</span>
                <button 
                  className="btn-secondary btn-pagination" 
                  disabled={currentPage >= totalPages - 1} 
                  onClick={() => setCurrentPage(prev => prev + 1)}
                >
                  Next
                </button>
              </div>
            )}
          </>
        )}

        {selectedItem && (
          <div className="modal-overlay" onClick={closeModal}>
            <div className="modal-content" onClick={(e) => e.stopPropagation()}>
              <h2>Request: {selectedItem.name}</h2>
              {error && <div className="error-message">{error}</div>}
              {successMsg && <div className="success-message">{successMsg}</div>}

              <form onSubmit={handleSubmitRequest}>
                <div className="form-group">
                  <label>Quantity (Available: {selectedItem.availableQuantity})</label>
                  <input
                    type="number"
                    min="1"
                    max={selectedItem.availableQuantity}
                    value={quantity}
                    onChange={(e) => setQuantity(e.target.value)}
                    required
                  />
                </div>

                <div className="form-group">
                  <label>Remarks (optional)</label>
                  <textarea
                    value={remarks}
                    onChange={(e) => setRemarks(e.target.value)}
                    placeholder="Reason for request..."
                  />
                </div>

                <div className="modal-actions">
                  <button type="button" className="btn-secondary" onClick={closeModal}>
                    Cancel
                  </button>
                  <button type="submit" className="btn-primary" disabled={submitting}>
                    {submitting ? 'Submitting...' : 'Submit Request'}
                  </button>
                </div>
              </form>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default Inventory;