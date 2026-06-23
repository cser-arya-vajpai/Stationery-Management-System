import React, { useEffect, useState } from 'react';
import Navbar from '../components/Navbar';
import { getAllItems, addItem, updateItem, deleteItem } from '../api/inventoryApi';
import { useAuth } from '../context/AuthContext';
import { addAuditLog } from '../utils/auditLogger';

const CATEGORIES = ['PAPER', 'PEN', 'PENCIL', 'NOTEBOOK', 'ERASER', 'SHARPENER', 'STAPLER', 'OTHER'];

const emptyForm = {
  name: '',
  category: 'PAPER',
  unit: '',
  availableQuantity: '',
  minimumQuantity: '',
};

const AdminInventory = () => {
  const { user } = useAuth();
  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  // Pagination & Sorting States
  const [currentPage, setCurrentPage] = useState(0);
  const [sortBy, setSortBy] = useState('name');
  const [totalPages, setTotalPages] = useState(0);

  const [showForm, setShowForm] = useState(false);
  const [editingId, setEditingId] = useState(null);
  const [formData, setFormData] = useState(emptyForm);
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    fetchItems();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [currentPage, sortBy]);

  const fetchItems = async () => {
    setLoading(true);
    try {
      const response = await getAllItems(currentPage, 20, sortBy);
      setItems(response.data.content || []);
      setTotalPages(response.data.totalPages || 0);
    } catch (err) {
      setError('Failed to load inventory items.');
    } finally {
      setLoading(false);
    }
  };

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const openAddForm = () => {
    setEditingId(null);
    setFormData(emptyForm);
    setShowForm(true);
    setError('');
    setSuccess('');
  };

  const openEditForm = (item) => {
    setEditingId(item.id);
    setFormData({
      name: item.name,
      category: item.category,
      unit: item.unit,
      availableQuantity: item.availableQuantity,
      minimumQuantity: item.minimumQuantity,
    });
    setShowForm(true);
    setError('');
    setSuccess('');
  };

  const closeForm = () => {
    setShowForm(false);
    setEditingId(null);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSubmitting(true);
    setError('');
    setSuccess('');

    const payload = {
      ...formData,
      availableQuantity: Number(formData.availableQuantity),
      minimumQuantity: Number(formData.minimumQuantity),
    };

    try {
      if (editingId) {
        await updateItem(editingId, payload);
        addAuditLog(user.email, user.role, 'UPDATE_ITEM', `Updated details for item: ${payload.name} (Qty: ${payload.availableQuantity}, Min Threshold: ${payload.minimumQuantity})`);
        setSuccess('Item updated successfully.');
      } else {
        await addItem(payload);
        addAuditLog(user.email, user.role, 'ADD_ITEM', `Added new item: ${payload.name} (Initial Qty: ${payload.availableQuantity})`);
        setSuccess('Item added successfully.');
      }
      closeForm();
      fetchItems();
    } catch (err) {
      if (err.response?.data && typeof err.response.data === 'object') {
        setError(Object.values(err.response.data).join(', '));
      } else {
        setError('Failed to save item.');
      }
    } finally {
      setSubmitting(false);
    }
  };

  const handleDelete = async (id) => {
    const targetItem = items.find(item => item.id === id);
    const itemName = targetItem ? targetItem.name : `ID ${id}`;
    if (!window.confirm(`Are you sure you want to delete ${itemName}?`)) return;
    try {
      await deleteItem(id);
      addAuditLog(user.email, user.role, 'DELETE_ITEM', `Deleted inventory item: ${itemName}`);
      fetchItems();
    } catch (err) {
      setError('Failed to delete item.');
    }
  };

  return (
    <div>
      <Navbar />
      <div className="page-container">
        <div className="page-header" style={{ display: 'flex', flexWrap: 'wrap', gap: '15px', justifyContent: 'space-between' }}>
          <h1>Manage Inventory</h1>
          <div style={{ display: 'flex', alignItems: 'center', gap: '15px' }}>
            {/* Sorting Control */}
            <div className="filter-bar" style={{ margin: 0 }}>
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
            <button className="btn-primary" onClick={openAddForm}>+ Add Item</button>
          </div>
        </div>

        {error && !showForm && <div className="error-message">{error}</div>}
        {success && <div className="success-message">{success}</div>}

        {loading ? (
          <p>Loading items...</p>
        ) : items.length === 0 ? (
          <p>No stationery items found.</p>
        ) : (
          <>
            <table className="data-table">
              <thead>
                <tr>
                  <th>Name</th>
                  <th>Category</th>
                  <th>Unit</th>
                  <th>Available</th>
                  <th>Minimum</th>
                  <th>Status</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {items.map((item) => (
                  <tr key={item.id}>
                    <td>{item.name}</td>
                    <td>{item.category}</td>
                    <td>{item.unit}</td>
                    <td>{item.availableQuantity}</td>
                    <td>{item.minimumQuantity}</td>
                    <td>
                      {item.availableQuantity <= item.minimumQuantity ? (
                        <span className="badge-warning">Low Stock</span>
                      ) : (
                        <span className="badge badge-approved">OK</span>
                      )}
                    </td>
                    <td>
                      <button className="btn-small" onClick={() => openEditForm(item)}>Edit</button>
                      <button className="btn-small btn-reject" onClick={() => handleDelete(item.id)}>Delete</button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>

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
                  onClick={() => setCurrentPage(prev => prev - 1)}
                >
                  Next
                </button>
              </div>
            )}
          </>
        )}

        {showForm && (
          <div className="modal-overlay" onClick={closeForm}>
            <div className="modal-content" onClick={(e) => e.stopPropagation()}>
              <h2>{editingId ? 'Edit Item' : 'Add New Item'}</h2>
              {error && <div className="error-message">{error}</div>}

              <form onSubmit={handleSubmit}>
                <div className="form-group">
                  <label>Name</label>
                  <input type="text" name="name" value={formData.name} onChange={handleChange} required />
                </div>

                <div className="form-group">
                  <label>Category</label>
                  <select name="category" value={formData.category} onChange={handleChange}>
                    {CATEGORIES.map((cat) => (
                      <option key={cat} value={cat}>{cat}</option>
                    ))}
                  </select>
                </div>

                <div className="form-group">
                  <label>Unit</label>
                  <input type="text" name="unit" value={formData.unit} onChange={handleChange} placeholder="e.g. Ream, Box, Piece" required />
                </div>

                <div className="form-group">
                  <label>Available Quantity</label>
                  <input type="number" name="availableQuantity" min="0" value={formData.availableQuantity} onChange={handleChange} required />
                </div>

                <div className="form-group">
                  <label>Minimum Quantity</label>
                  <input type="number" name="minimumQuantity" min="0" value={formData.minimumQuantity} onChange={handleChange} required />
                </div>

                <div className="modal-actions">
                  <button type="button" className="btn-secondary" onClick={closeForm}>Cancel</button>
                  <button type="submit" className="btn-primary" disabled={submitting}>
                    {submitting ? 'Saving...' : editingId ? 'Update Item' : 'Add Item'}
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

export default AdminInventory;