import React, { useState, useRef, useEffect } from "react";
import FruitList from "./FruitList";
import { v4 as uuidv4 } from "uuid";
import "./App.css";

const LOCAL_STORAGE_KEY = "fruitsApp.fruits";

function App() {
  const [fruits, setFruits] = useState([]);
  const fruitNameRef = useRef();

  useEffect(() => {
    const storedFruits = JSON.parse(localStorage.getItem(LOCAL_STORAGE_KEY));
    if (storedFruits) setFruits(storedFruits);
  }, []);

  useEffect(() => {
    localStorage.setItem(LOCAL_STORAGE_KEY, JSON.stringify(fruits));
  }, [fruits]);

  function toggleFruit(id) {
    const newFruits = [...fruits];
    const fruit = newFruits.find((fruit) => fruit.id === id);
    fruit.complete = !fruit.complete;
    setFruits(newFruits);
  }

  function handleAddFruits() {
    const name = fruitNameRef.current.value;
    if (name === "") return;
    setFruits((prevFruits) => {
      return [...prevFruits, { id: uuidv4(), name: name, complete: false }];
    });
    fruitNameRef.current.value = null;
  }

  function handleClearFruits() {
    const newFruits = fruits.filter((fruit) => !fruit.complete);
    setFruits(newFruits);
  }

  return (
    <div className="container">
      <h2>Жимсний бүртгэл</h2>
      <FruitList fruits={fruits} toggleFruit={toggleFruit} />

      <div className="input-section">
        <input
          ref={fruitNameRef}
          type="text"
          placeholder="Жимсний нэр оруулах"
        />
        <button onClick={handleAddFruits}>Жагсаалт нэмэх</button>
        <button onClick={handleClearFruits}>Арилгах</button>
      </div>

      <div className="summary">
        Нийт: {fruits.length} | Үлдсэн:{" "}
        {fruits.filter((fruit) => !fruit.complete).length}
      </div>
    </div>
  );
}

export default App;
