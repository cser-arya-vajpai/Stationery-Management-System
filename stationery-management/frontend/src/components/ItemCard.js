import React from 'react';

//Item card is an empty index card template for a stationery product
//We dont have to make a separate card design. 
//We feed it 4 inputs based on what we want it to show
//item - product's info (name, qty, category, unit)
//actionLabel - text we want to write on card's button (Like request or edit)
//onAction - What should happen when the user clicks the button
//disabled - A switch (yes or no) to freeze the button
const ItemCard = ({ item, actionLabel, onAction, disabled }) => {
  return (
    //draws the boundary/border of the card. If qty is low, it applies a warning highlight to card.
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