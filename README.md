# WolfWR – Wholesale Store Management System

A database-driven management system for a wholesale store chain (**WolfCity**) designed to handle **inventory, billing, discounts, transactions, and staff roles** with robust transaction handling and consistency guarantees.

---

## 🚀 Features
- **Inventory Management**
  - Add new inventory from suppliers  
  - Handle product returns with reason logging  
  - Transfer products between stores with accountability  
  - Check stock levels in real time  

- **Billing & Transactions**
  - Generate detailed **customer bills** and **supplier bills**  
  - Apply **platinum membership rewards** and cashback policies  
  - Create transactions with **discount validation** and automatic calculation  

- **Discount Management**
  - Flexible discount mechanism with time frames & max limits  
  - Transactional updates using **JDBC manual transaction handling**  
  - Safe rollback on constraint violations  

- **Staff & Role Management**
  - Entities for **cashiers, billing staff, warehouse staff, and managers**  
  - Role-based access to system operations  
  - Ensures one role per employee per store for clarity  

---

## 🛠️ Tech Stack
- **Database:** MySQL  
- **Backend:** Java (JDBC)  
- **Transaction Handling:** Manual transaction control (`commit`, `rollback`)  
- **ER & Schema Design:** Normalized relational schema with role-based constraints  

---

## 💡 Design Decisions
- **Separate Discount Table** → supports dynamic seasonal promos & easy policy changes  
- **Role-based Menus** → improves security & usability  
- **ON DUPLICATE KEY UPDATE** → efficient restocking without redundant queries  
- **Robust Error Handling** → rollback on constraint violations, ensuring data integrity  

---

## 🧪 Example Use Cases
- Manager updates discount % → rollback if invalid rate entered  
- Cashier generates customer bill with automatic discount application  
- Warehouse staff transfers stock → logged with staff ID & timestamp  
- Inventory insertion prevents duplicate product entries via transaction rollback  
