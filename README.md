# 🦉 Nalar: Premium Gamified Full-Stack Learning Platform

[![Build Status](https://img.shields.io/badge/Build-Successful-brightgreen?style=for-the-badge&logo=android)](https://github.com/Dialius/Nalar)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.0-purple?style=for-the-badge&logo=kotlin)](https://kotlinlang.org)
[![Express](https://img.shields.io/badge/Express-5.2.1-lightgrey?style=for-the-badge&logo=express)](https://expressjs.com)
[![Midtrans](https://img.shields.io/badge/Midtrans-Payment-blue?style=for-the-badge)](https://midtrans.com)

**Nalar** is a premium, state-of-the-art gamified learning mobile application designed specifically for high school students. It covers critical sciences including **Mathematics & Algebra**, **Classical Physics**, and **Stoichiometric Chemistry** through interactive learning paths, live stats tracking, a competitive leaderboard system, and robust premium VIP subscriptions powered by the **Midtrans Payment Gateway**.

---

## 🗺️ System Architecture

Nalar is split into two primary decoupled components: a modern Jetpack Compose Android Client and a high-performance Express.js REST API.

```mermaid
graph TD
    subgraph Android Client (com.davinza.nalar)
        UI[Jetpack Compose UI Layer]
        VM[MVVM ViewModels]
        UPM[UserProgressManager]
        NC[Retrofit & OkHttp API Client]
        DS[DataStore Local Session]
        FCM[Firebase Cloud Messaging]
    end

    subgraph Backend API (Express.js)
        SRV[Express Server]
        RTS[Express Routes]
        MW[Auth & Admin Middleware]
        DB[(MySQL Database)]
        MDT[Midtrans Client SDK]
    end

    UI --> VM
    VM --> UPM
    VM --> NC
    VM --> DS
    FCM <--> SRV
    NC <--> RTS
    RTS --> MW
    MW --> DB
    RTS --> MDT
```

---

## ⚡ Tech Stack & Key Features

### 📱 Android Mobile Client (`/app`)
* **Modern UI Engine**: Built completely with **Jetpack Compose**, implementing a custom premium glassmorphic and bento-style styling system.
* **MVVM Architecture**: Clean separation of concerns with lifecycle-aware ViewModels, reactive states, and Flow-based API communication.
* **Real-Time Progress Engine (`UserProgressManager`)**: Coordinates in-app streak counts (🔥), keys (🔑), active subject paths, and local quiz completions instantly.
* **Smart Bento Cards**: Responsive bento grids on the Profile page that dynamically auto-scale font sizes (e.g., dynamically sizing `"Bronze Pioneer"` league titles down to prevent screen clipping).
* **FCM Push Notifications**: Deeply integrated notification hub displaying custom visual logos and deep-linked messages from the server.
* **Libraries**: Retrofit 2, OkHttp 3, Google Play Services Auth, Firebase BOM 33.1.0, Jetpack DataStore Preferences.

### 🌐 REST API Backend (`/nalar-backend`)
* **Express.js Server**: Scalable Node.js routing and request-handling API.
* **MySQL Database**: Persistent storage for user statistics, game progress, premium transaction states, and course schemas.
* **Midtrans Sandbox Integration**: Production-ready payment lifecycle capturing pending, success, and expired transactions automatically via server webhooks.
* **Secure JWT Sessioning**: Stateless token-based JSON Web Token authentication with custom encrypted password hashing powered by `bcryptjs`.
* **Libraries**: `express`, `mysql2`, `midtrans-client`, `jsonwebtoken`, `bcryptjs`, `cors`, `dotenv`.

---

## 📂 Repository Structure

```directory
Nalar/
├── app/                           # Android Client Code base
│   ├── src/main/java/com/davinza/nalar/
│   │   ├── data/                  # Remote Api client, endpoints, and models
│   │   │   ├── local/             # SessionManager & local storage preferences
│   │   │   ├── remote/            # ApiClient & ApiService endpoints
│   │   │   └── repository/        # Repositories (Auth, Course, Gamification, Payment)
│   │   ├── di/                    # Dependency Injection factories
│   │   ├── ui/                    # UI Screen Composables & MVVM ViewModels
│   │   │   ├── auth/              # Authentication screens (SignIn, SignUp)
│   │   │   ├── components/        # Custom visual components (Pushable buttons, NalarAvatar, Shimmer)
│   │   │   ├── courses/           # Subject structures (Math, Physics, Chemistry) and progress trackers
│   │   │   ├── home/              # Dynamic dashboard, bento metrics, active mission algorithms
│   │   │   ├── leaderboard/       # Competitive ranking ladders
│   │   │   ├── premium/           # VIP upgrade screens, payment methods & instructions
│   │   │   ├── profile/           # Bento grids, dynamic font scaling, achievements
│   │   │   ├── quiz/              # Interactive kuis, feedback bottom sheets, scoring engine
│   │   │   ├── settings/          # Profile setting edits, avatar selectors
│   │   │   └── splash/            # Elegant double splash transitions
│   │   └── utils/                 # Firebase Cloud Messaging handlers
│   └── build.gradle.kts           # Kotlin App compilation Gradle file
├── nalar-backend/                 # Node.js API Backend
│   ├── database/                  # Database connections & raw sql schemas
│   ├── middleware/                # Authenticate and Auth admin middleware
│   ├── routes/                    # API Endpoints (auth, users, courses, payment, gamification)
│   ├── server.js                  # Main server entry point
│   └── package.json               # Backend dependencies & dev scripts
└── build.gradle.kts               # Parent Gradle build configuration
```

---

## 🛠️ Installation & Setup Runbook

### 1. Backend Setup (`/nalar-backend`)

#### Prerequisites
* **Node.js** (v18+ recommended)
* **MySQL Database Server**

#### Installation Steps
1. Navigate to the backend directory:
   ```bash
   cd nalar-backend
   ```
2. Install npm dependencies:
   ```bash
   npm install
   ```
3. Configure Environment Variables:
   Create a `.env` file in the root of `/nalar-backend` and populate the details:
   ```env
   PORT=5000
   DB_HOST=127.0.0.1
   DB_USER=root
   DB_PASS=your_secure_password
   DB_NAME=nalar_db
   JWT_SECRET=your_super_jwt_secret_key
   MIDTRANS_CLIENT_KEY=your_midtrans_sandbox_client_key
   MIDTRANS_SERVER_KEY=your_midtrans_sandbox_server_key
   ```
4. Run the database schema migrations located in the `/database` directory inside your MySQL server to construct the necessary tables (`users`, `purchases`, etc.).
5. Start the server in Development mode (with nodemon auto-reload):
   ```bash
   npm run dev
   ```

---

### 2. Android Client Setup (`/app`)

#### Prerequisites
* **Android Studio** (Koala or newer)
* **Java Development Kit (JDK 17)**
* **Google Services Account Configuration**

#### Installation Steps
1. Open the root folder `Nalar` in **Android Studio**.
2. Supply your Firebase Project configuration by placing your downloaded `google-services.json` inside the `/app` folder directory.
3. Configure the local properties file:
   Create a `local.properties` file in the root directory and specify the SDK path:
   ```properties
   sdk.dir=C\:\\Users\\YOUR_USERNAME\\AppData\\Local\\Android\\Sdk
   ```
4. Synchronize project Gradle files:
   Click **File > Sync Project with Gradle Files** inside Android Studio.
5. Build the application:
   Execute compile tasks in Android Studio or compile via command line:
   ```bash
   ./gradlew compileDebugKotlin
   ```
6. Run the App on a physical device or Android Emulator.

---

## 🔐 Security & Middleware Implementation

The backend implements a two-tier middleware authentication structure to safeguard endpoints:

> [!IMPORTANT]  
> **Route Guarding Rule**
> * **`requireAuth`**: Validates the incoming Bearer token in the `Authorization` header against the JSON Web Token signature to verify the student's active session.
> * **`requireAdmin`**: Scans the validated payload to ensure the user possesses the `isAdmin` flag before allowing modifications to courses, subject nodes, or payment verification statuses.

---

## 💳 Payment Gateway Integration (Midtrans)

Nalar features a robust premium monetization path utilizing the Midtrans Sandbox:

```
[Student Upgrades] ────> [API Request] ────> [Midtrans Snap Token]
                                                  │
                                                  ▼
[Instant Local Upgrade] <─── [Webhook Notify] <─── [Midtrans Secure Payment]
```

1. **Snap Token Creation**: The client initiates an upgrade intent. The Express API uses the `midtrans-client` SDK to request a unique payment URL and Snap Token from Midtrans.
2. **Dynamic UI Payment Page**: The Android client renders secure instructions and payment methods (Virtual Account, E-Wallet, QRIS) inside Compose UI.
3. **Webhook Processing**: The backend registers a secure webhook to receive real-time status updates from Midtrans. Upon notification of a `settlement` state, the user status is upgraded in MySQL, triggering a live event to update the Android app UI instantly.

---

## 📄 License
This repository is licensed under the **ISC License**. All rights reserved. Original designs and code assets are property of Dialius/Nalar project.
