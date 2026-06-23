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

  const [cart, setCart] = useState([]);
  const [cartRemarks, setCartRemarks] = useState('');

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

  const handleAddToCart = (e) => {
    e.preventDefault();
    
    // Check if duplicate
    if (cart.some(c => c.itemId === selectedItem.id)) {
      setError('Item is already in your cart.');
      return;
    }

    const cartItem = {
      itemId: selectedItem.id,
      itemName: selectedItem.name,
      requestedQuantity: Number(quantity),
      unit: selectedItem.unit,
      availableQuantity: selectedItem.availableQuantity
    };

    setCart([...cart, cartItem]);
    closeModal();
  };

  const removeFromCart = (itemId) => {
    setCart(cart.filter(item => item.itemId !== itemId));
  };

  const handleSubmitCart = async () => {
    setSubmitting(true);
    setError('');
    setSuccessMsg('');

    try {
      await submitRequest({
        items: cart.map(i => ({
          itemId: i.itemId,
          itemName: i.itemName,
          requestedQuantity: i.requestedQuantity
        })),
        remarks: cartRemarks,
      });

      const auditDetails = cart.map(i => `${i.requestedQuantity} ${i.itemName}`).join(', ');
      addAuditLog(user.email, user.role, 'SUBMIT_REQUEST', `Requested multiple items: ${auditDetails}`);

      setSuccessMsg('Request submitted successfully!');
      setCart([]);
      setCartRemarks('');
      setTimeout(() => {
        setSuccessMsg('');
        fetchItems(); // refresh page
      }, 1500);
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
            <div style={{ display: 'flex', gap: '20px', alignItems: 'flex-start', flexWrap: 'wrap' }}>
              <div style={{ flex: 1, minWidth: '300px' }}>
                <div className="item-grid">
                  {items.map((item) => (
                    <ItemCard
                      key={item.id}
                      item={item}
                      actionLabel="Request Item"
                      onAction={openRequestModal}
                      disabled={item.availableQuantity === 0 || cart.some(c => c.itemId === item.id)}
                    />
                  ))}
                </div>
              </div>

              {cart.length > 0 && (
                <div className="card" style={{ width: '320px', padding: '15px', position: 'sticky', top: '20px', background: '#fff', borderRadius: '10px', boxShadow: '0 4px 12px rgba(219, 39, 119, 0.1)', border: '1px solid #fecdd3' }}>
                  <h3 style={{ color: '#db2777', borderBottom: '2px solid #fecdd3', paddingBottom: '8px', marginBottom: '12px', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                    🛒 Request Cart
                    <span style={{ fontSize: '12px', background: '#ff7597', color: '#fff', padding: '2px 8px', borderRadius: '20px' }}>
                      {cart.length} {cart.length === 1 ? 'item' : 'items'}
                    </span>
                  </h3>
                  
                  <div style={{ maxHeight: '250px', overflowY: 'auto', marginBottom: '15px' }}>
                    {cart.map((cartItem) => (
                      <div key={cartItem.itemId} style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '8px 0', borderBottom: '1px solid #fff0f3' }}>
                        <div>
                          <div style={{ fontWeight: 'bold', fontSize: '14px', color: '#374151' }}>{cartItem.itemName}</div>
                          <div style={{ fontSize: '12px', color: '#6b7280' }}>
                            Qty: {cartItem.requestedQuantity} {cartItem.unit}(s)
                          </div>
                        </div>
                        <button 
                          className="btn-secondary" 
                          style={{ padding: '4px 8px', fontSize: '12px', background: '#fff0f3', color: '#e11d48', border: 'none', cursor: 'pointer' }}
                          onClick={() => removeFromCart(cartItem.itemId)}
                        >
                          🗑️
                        </button>
                      </div>
                    ))}
                  </div>

                  <div className="form-group">
                    <label style={{ fontSize: '13px', fontWeight: 'bold', color: '#4b5563' }}>Remarks for Request</label>
                    <textarea
                      style={{ fontSize: '13px', minHeight: '60px' }}
                      value={cartRemarks}
                      onChange={(e) => setCartRemarks(e.target.value)}
                      placeholder="Remarks/Reason..."
                    />
                  </div>

                  {error && !selectedItem && <div className="error-message" style={{ fontSize: '12px', margin: '10px 0' }}>{error}</div>}
                  {successMsg && <div className="success-message" style={{ fontSize: '12px', margin: '10px 0' }}>{successMsg}</div>}

                  <button 
                    className="btn-primary" 
                    style={{ width: '100%', padding: '10px', marginTop: '5px' }}
                    onClick={handleSubmitCart}
                    disabled={submitting}
                  >
                    {submitting ? 'Submitting...' : 'Submit Request'}
                  </button>
                </div>
              )}
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

              <form onSubmit={handleAddToCart}>
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

                <div className="modal-actions">
                  <button type="button" className="btn-secondary" onClick={closeModal}>
                    Cancel
                  </button>
                  <button type="submit" className="btn-primary">
                    Add to Cart
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