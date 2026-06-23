package com.stationery.inventory.model;

//defined the set of allowed stationery categories.
//We're using enum so that categories are enforced and this prevents spelling mistakes and helps in filtering items on the frontend. 

public enum Category {
    PAPER,
    PEN,
    PENCIL,
    NOTEBOOK,
    ERASER,
    SHARPENER,
    STAPLER,
    OTHER
}