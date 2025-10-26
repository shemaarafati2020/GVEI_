# 🌱 Green Vehicle Exchange Initiative (GVEI) Desktop Application

![Java](https://img.shields.io/badge/Language-Java-blue)
![MySQL](https://img.shields.io/badge/Database-MySQL-orange)
![Swing](https://img.shields.io/badge/UI-Swing%2FAWT-green)

---

## 🚀 Overview

The **GVEI Desktop Application** is a Java-based system to support Rwanda’s **Green Vehicle Exchange Initiative**, allowing citizens to exchange old fuel-powered vehicles for electric vehicles with government support.

It includes **citizen** and **admin** dashboards to manage vehicles, exchange offers, and generate reports.

---

## ✨ Features

### User Management
- Citizen registration and login.
- Admin login for system management.
- Role-based access control (citizen/admin).

### Vehicle Management
- Citizens can register vehicles:
  - Plate number, type, fuel type, manufacture year, mileage.
- List and view owned vehicles.
- Export vehicle data to **CSV**.

### Exchange Management
- Eligibility check based on:
  - Age > 5 years
  - Fuel type = Petrol/Diesel
- Citizens can apply for exchange offers.
- Admins can approve/reject offers.
- Offers display exchange value, subsidy percentage, and status.
- Export offer data to CSV.

### Reporting & Analytics
- Admin dashboard shows:
  - Approved exchanges.
  - Total subsidies paid.
  - Estimated carbon reduction (tons/year).
- Simple charts using **AWT Canvas**.

### UI
- Built with **Java Swing & AWT**.
- Logout button on top-right returns to login without closing the app.

---

## 🗂️ Project Structure

