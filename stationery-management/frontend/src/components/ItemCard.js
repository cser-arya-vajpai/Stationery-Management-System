import React from 'react';

const ItemCard = ({ item, actionLabel, onAction, disabled }) => {
  return (
    <div className={`item-card ${item.lowStock ? 'low-stock' : ''}`}>
      <div className="item-card-header">
        <h3>{item.name}</h3>
        {item.lowStock && <span className="badge-warning">Low Stock</span>}
      </div>

      <div className="item-card-body">
        <p><strong>Category:</strong> {item.category}</p>
        <p><strong>Unit:</strong> {item.unit}</p>
        <p><strong>Available:</strong> {item.availableQuantity}</p>
        <p><strong>Minimum Required:</strong> {item.minimumQuantity}</p>
      </div>

      {actionLabel && (
        <button
          className="btn-primary"
          onClick={() => onAction(item)}
          disabled={disabled}
        >
          {actionLabel}
        </button>
      )}
    </div>
  );
};

export default ItemCard;